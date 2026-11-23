package lk.ciaboc.dashboard.security;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.ciaboc.dashboard.auth.AuthService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Dev-only controller that simulates SSO login by setting the session subject
 * to the seeded dev user, bypassing the need for a real OAuth2 provider.
 */
@Profile("dev")
@RestController
public class DevAuthController {
    private static final String DEV_SUBJECT = "dev-user-001";

    private final AuthService auth;

    public DevAuthController(AuthService auth) {
        this.auth = auth;
    }

    @GetMapping("/oauth2/authorization/ciaboc")
    public void devSsoLogin(HttpSession session, HttpServletResponse response) throws IOException {
        session.setAttribute(AuthService.SSO_SUBJECT_SESSION_KEY, DEV_SUBJECT);
        session.removeAttribute(AuthService.SELECTED_USER_ROLE_SESSION_KEY);

        var roles = auth.assignedRoles(DEV_SUBJECT);
        if (roles.size() == 1) {
            session.setAttribute("CIABOC_PENDING_USER_ROLE", roles.get(0).getId());
            response.sendRedirect("http://localhost:3000/login?role=" + roles.get(0).getId());
        } else if (roles.isEmpty()) {
            response.sendRedirect("http://localhost:3000/login?error=no-role");
        } else {
            response.sendRedirect("http://localhost:3000/login");
        }
    }
}
