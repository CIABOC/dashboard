package lk.ciaboc.dashboard.vmr;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.PreDestroy;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
class VmrAtlasSource {
    private static final String REPORTING_ZONE = "Asia/Colombo";
    private final String uri;
    private final String database;
    private final String visits;
    private final String movements;
    private MongoClient client;
    private MongoDatabase resolved;

    VmrAtlasSource(@Value("${dashboard.vmr.atlas.uri}") String uri,
                   @Value("${dashboard.vmr.atlas.database}") String database,
                   @Value("${dashboard.vmr.atlas.visits}") String visits,
                   @Value("${dashboard.vmr.atlas.movements}") String movements) {
        this.uri = uri;
        this.database = database;
        this.visits = visits;
        this.movements = movements;
    }

    boolean configured() {
        return !uri.isBlank();
    }

    VmrOverview overview(int months, boolean compare) {
        LocalDate today = LocalDate.now(ZoneId.of(REPORTING_ZONE));
        LocalDate from = today.plusDays(1).minusMonths(months);
        LocalDate previousFrom = compare ? from.minusMonths(months) : from;
        MongoDatabase source = reportingDatabase();

        Document state = new Document("$toUpper", "$status");
        Document approved = expression("$ne", field("entry_approved_at", "entryApprovedAt"), null);
        Document completed = expression("$eq", state, "COMPLETED");
        var visitDays = readDays(source, visits, field("visit_date", "visitDate"), previousFrom, today,
                new Document("total", new Document("$sum", 1))
                        .append("completed", countIf(expression("$and", completed, approved)))
                        .append("declined", countIf(expression("$or", expression("$eq", state, "DECLINED"),
                                expression("$and", completed, expression("$not", approved)))))
                        .append("cancelled", countIf(expression("$eq", state, "CANCELLED"))),
                row -> new VmrOverview.VisitDay(day(row), number(row, "total"), number(row, "completed"),
                        number(row, "declined"), number(row, "cancelled")));

        Document direction = new Document("$toUpper", "$direction");
        Document decision = new Document("$toUpper", "$decision");
        var vehicleDays = readDays(source, movements, field("occurred_at", "occurredAt"), previousFrom, today,
                new Document("entries", countIf(expression("$and", expression("$eq", direction, "ENTRY"),
                        expression("$eq", decision, "ADMITTED"))))
                        .append("exits", countIf(expression("$eq", direction, "EXIT")))
                        .append("denials", countIf(expression("$eq", decision, "DENIED"))),
                row -> new VmrOverview.VehicleDay(day(row), number(row, "entries"), number(row, "exits"),
                        number(row, "denials")));
        return new VmrOverview(Instant.now(), today, from, previousFrom, visitDays, vehicleDays);
    }

    private static <T> List<T> readDays(MongoDatabase source, String collection, Document date, LocalDate from,
                                        LocalDate to, Document counts, Function<Document, T> mapper) {
        var timestamp = new Document("$convert", new Document("input", date).append("to", "date")
                .append("onError", null).append("onNull", null));
        var day = new Document("$dateToString", new Document("date", timestamp)
                .append("format", "%Y-%m-%d").append("timezone", REPORTING_ZONE));
        var group = new Document("_id", "$reportingDay");
        group.putAll(counts);
        var pipeline = List.of(
                new Document("$set", new Document("reportingDay", day)),
                new Document("$match", new Document("reportingDay",
                        new Document("$gte", from.toString()).append("$lte", to.toString()))),
                new Document("$group", group),
                new Document("$sort", new Document("_id", 1)));
        var results = new ArrayList<T>();
        for (Document row : source.getCollection(collection).aggregate(pipeline)) {
            results.add(mapper.apply(row));
        }
        return results;
    }

    private synchronized MongoDatabase reportingDatabase() {
        if (resolved != null) {
            return resolved;
        }
        if (client == null) {
            var settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(uri))
                    .applyToClusterSettings(cluster -> cluster.serverSelectionTimeout(5, TimeUnit.SECONDS))
                    .timeout(8, TimeUnit.SECONDS).build();
            client = MongoClients.create(settings);
        }
        MongoDatabase candidate = client.getDatabase(database);
        var names = candidate.listCollectionNames().into(new ArrayList<>());
        if (visits.equals(movements) || !names.contains(visits) || !names.contains(movements)) {
            throw new MissingCollectionsException();
        }
        resolved = candidate;
        return resolved;
    }

    private static Document field(String snakeCase, String camelCase) {
        return expression("$ifNull", "$" + snakeCase, "$" + camelCase, null);
    }

    private static Document countIf(Document condition) {
        return new Document("$sum", expression("$cond", condition, 1, 0));
    }

    private static Document expression(String operator, Object... operands) {
        return new Document(operator, Arrays.asList(operands));
    }

    private static LocalDate day(Document row) {
        return LocalDate.parse(row.getString("_id"));
    }

    private static long number(Document row, String key) {
        return row.get(key, Number.class).longValue();
    }

    @PreDestroy
    synchronized void close() {
        if (client != null) {
            client.close();
        }
    }

    static class MissingCollectionsException extends RuntimeException {}

}
