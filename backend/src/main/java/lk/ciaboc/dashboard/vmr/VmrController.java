package lk.ciaboc.dashboard.vmr;

import com.mongodb.MongoException;
import jakarta.servlet.http.HttpServletRequest;
import lk.ciaboc.dashboard.api.ApiResponse;
import lk.ciaboc.dashboard.auth.AuthService;
import lk.ciaboc.dashboard.auth.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/vmr")
public class VmrController {
    private static final Logger LOG = LoggerFactory.getLogger(VmrController.class);
    private static final Set<String> REPORT_ROLES = Set.of("COMMISSION", "DIRECTOR_GENERAL", "DIRECTOR");
    private final VmrAtlasSource source;
    private final AuthService auth;

    VmrController(VmrAtlasSource source, AuthService auth) {
        this.source = source;
        this.auth = auth;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<VmrOverview>> overview(
            @RequestParam(defaultValue = "12") int months,
            @RequestParam(defaultValue = "true") boolean compare,
            HttpServletRequest request,
            Authentication authentication) {
        if (!allowed(request, authentication)) {
            return error(HttpStatus.FORBIDDEN, "A dashboard role with reception access is required.");
        }
        if (!List.of(3, 6, 12).contains(months)) {
            return error(HttpStatus.BAD_REQUEST, "Choose a period of 3, 6, or 12 months.");
        }
        if (!source.configured()) {
            return error(HttpStatus.SERVICE_UNAVAILABLE, "Atlas reporting is not configured.");
        }
        try {
            return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                    .body(ApiResponse.success(source.overview(months, compare)));
        } catch (VmrAtlasSource.MissingCollectionsException exception) {
            return error(HttpStatus.SERVICE_UNAVAILABLE, "The RMS collections could not be identified in Atlas.");
        } catch (MongoException | IllegalArgumentException exception) {
            LOG.warn("Vehicle and reception feed unavailable ({})", exception.getClass().getSimpleName());
            return error(HttpStatus.SERVICE_UNAVAILABLE, "The Atlas reporting source is unavailable.");
        }
    }

    private boolean allowed(HttpServletRequest request, Authentication authentication) {
        var session = request.getSession(false);
        if (session == null || authentication == null
                || !(session.getAttribute(AuthService.SSO_SUBJECT_SESSION_KEY) instanceof String subject)
                || !(session.getAttribute(AuthService.SELECTED_USER_ROLE_SESSION_KEY) instanceof Long roleId)) {
            return false;
        }
        if (roleId == -1L) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_TEMPORARY_LOCAL".equals(authority.getAuthority()));
        }
        return auth.findAuthorizedRole(subject, roleId).map(UserRole::getRole)
                .map(role -> REPORT_ROLES.contains(role.getCode())).orElse(false);
    }

    private static ResponseEntity<ApiResponse<VmrOverview>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).cacheControl(CacheControl.noStore()).body(ApiResponse.error(message));
    }
}
