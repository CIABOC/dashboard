package lk.ciaboc.dashboard.kpi;

import java.util.List;
import lk.ciaboc.dashboard.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/kpis")
public class KpiController {
    private final KpiService service;

    public KpiController(KpiService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<KpiDto>> findAll() {
        return ApiResponse.success(service.findAll());
    }
}
