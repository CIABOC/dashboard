package lk.ciaboc.dashboard.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class SsoSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthService auth;

    public SsoSuccessHandler(AuthService auth) {
        this.auth = auth;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (!(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
            response.sendRedirect("http://localhost:3000/login?error=sso");
            return;
        }
        String subject = oidcUser.getSubject();
        var user = auth.findBySubject(subject);
        if (user.isEmpty() || !user.get().isActive()) {
            auth.audit(subject, "SSO_AUTHENTICATION", "DENIED", "USER", subject,
                    "No active dashboard account");
            response.sendRedirect("http://localhost:3000/login?error=not-authorized");
            return;
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(AuthService.SSO_SUBJECT_SESSION_KEY, subject);
        session.removeAttribute(AuthService.SELECTED_USER_ROLE_SESSION_KEY);
        auth.audit(subject, "SSO_AUTHENTICATION", "SUCCESS", "USER", subject,
                "Existing SSO authentication accepted");

        var roles = auth.assignedRoles(subject);
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
