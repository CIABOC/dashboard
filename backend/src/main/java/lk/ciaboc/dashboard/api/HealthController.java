package lk.ciaboc.dashboard.api;

import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.success(Map.of(
                "status", "UP",
                "service", "ciaboc-dashboard",
                "timestamp", Instant.now().toString()
        ));
    }
}
