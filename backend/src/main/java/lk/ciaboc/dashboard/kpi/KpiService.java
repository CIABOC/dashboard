package lk.ciaboc.dashboard.kpi;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class KpiService {
    private final DimKpiRepository repository;
    private final KpiMapper mapper;

    public KpiService(DimKpiRepository repository, KpiMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<KpiDto> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }
}
