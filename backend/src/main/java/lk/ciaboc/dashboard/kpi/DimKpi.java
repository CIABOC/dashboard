package lk.ciaboc.dashboard.kpi;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "dim_kpi")
public class DimKpi {
    @Id
    @Column(name = "kpi_id")
    private String kpiId;

    @Column(nullable = false)
    private String title;

    @Column(name = "formula_version", nullable = false)
    private String formulaVersion;

    private String owner;

    @Column(name = "reporting_grain")
    private String reportingGrain;

    @Column(nullable = false)
    private String classification;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    protected DimKpi() {}

    public String getKpiId() { return kpiId; }
    public String getTitle() { return title; }
    public String getFormulaVersion() { return formulaVersion; }
    public String getOwner() { return owner; }
    public String getReportingGrain() { return reportingGrain; }
    public String getClassification() { return classification; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public LocalDate getEffectiveTo() { return effectiveTo; }
}
