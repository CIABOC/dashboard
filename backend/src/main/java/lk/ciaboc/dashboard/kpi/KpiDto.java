package lk.ciaboc.dashboard.kpi;

public record KpiDto(
        String kpiId,
        String title,
        String formulaVersion,
        String owner,
        String reportingGrain,
        String classification
) {
}
