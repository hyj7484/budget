package com.app.application.budget.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.application.budget.auth.dto.login.LoginRequest;
import com.app.application.budget.auth.dto.login.LoginResponse;
import com.app.application.budget.auth.dto.signup.SignupRequest;
import com.app.application.budget.auth.dto.signup.SignupResponse;

import jakarta.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        SignupResponse res = authService.signup(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest req) {
        return authService.login(req);
    }
}
