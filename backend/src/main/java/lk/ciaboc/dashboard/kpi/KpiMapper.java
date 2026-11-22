package lk.ciaboc.dashboard.kpi;

import org.springframework.stereotype.Component;

@Component
public class KpiMapper {
    public KpiDto toDto(DimKpi entity) {
        return new KpiDto(
                entity.getKpiId(),
                entity.getTitle(),
                entity.getFormulaVersion(),
                entity.getOwner(),
                entity.getReportingGrain(),
                entity.getClassification()
        );
    }
}
