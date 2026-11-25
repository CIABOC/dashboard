package lk.ciaboc.dashboard.vmr;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record VmrOverview(Instant fetchedAt, LocalDate today, LocalDate from, LocalDate previousFrom,
                          List<VisitDay> visits, List<VehicleDay> vehicles) {
    public record VisitDay(LocalDate day, long total, long completed, long declined, long cancelled) {}
    public record VehicleDay(LocalDate day, long entries, long exits, long denials) {}
}
