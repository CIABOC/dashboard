package lk.ciaboc.dashboard.api;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/executive")
public class ExecutiveOverviewController {
    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        return ApiResponse.success(Map.of(
                "dataStatus", "FOUNDATION_ONLY",
                "sourceStatus", "NOT_CONNECTED",
                "message", "Approved source feeds are not connected in Phase 01."
        ));
    }
}
