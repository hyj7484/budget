package com.app.application.budget.auth.dto.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SignupRequest {
    @Email @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String displayName;

    private String locale;       // default: ko-KR
    private String timezone;     // default: Asia/Tokyo
    private String currencyCode; // default: JPY
    private String ledgerName;   // default: "기본 가계부"
}
