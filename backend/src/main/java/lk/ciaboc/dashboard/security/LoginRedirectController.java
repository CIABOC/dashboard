package lk.ciaboc.dashboard.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class LoginRedirectController {
    private final String loginUrl;

    public LoginRedirectController(@Value("${dashboard.frontend.login-url:http://localhost:3000/login}") String loginUrl) {
        this.loginUrl = loginUrl;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(loginUrl)).build();
    }
}
