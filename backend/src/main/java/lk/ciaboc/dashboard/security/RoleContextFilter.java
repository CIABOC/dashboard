package lk.ciaboc.dashboard.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ciaboc.dashboard.auth.AuthService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RoleContextFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        boolean publicApi = path.equals("/api/v1/health")
                || path.equals("/api/v1/auth/session")
                || path.startsWith("/api/v1/auth/role/")
                || path.equals("/api/v1/auth/logout");
        if (path.startsWith("/api/") && !publicApi
                && request.getSession(false) != null
                && request.getSession(false).getAttribute(AuthService.SELECTED_USER_ROLE_SESSION_KEY) == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"timestamp\":\"\",\"success\":false,\"data\":null,\"error\":\"Verified role context required\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
