package com.app.application.budget.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.application.budget.auth.dto.login.LoginRequest;
import com.app.application.budget.auth.dto.login.LoginResponse;
import com.app.application.budget.auth.dto.signup.SignupRequest;
import com.app.application.budget.auth.dto.signup.SignupResponse;

import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        try {
            SignupResponse res = authService.signup(req);
            return ResponseEntity.ok(res);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미 사용 중인 이메일입니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest req) {
        return authService.login(req);
    }
}
