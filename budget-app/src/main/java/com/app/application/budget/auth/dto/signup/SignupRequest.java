package com.app.application.budget.auth.dto.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SignupRequest {
    @Email(message = "이메일 형식이 올바르지 않습니다.") @NotBlank(message = "이메일은 필수입니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
    @NotBlank(message = "이름은 필수입니다.")
    private String displayName;

    private String locale;       // default: ko-KR
    private String timezone;     // default: Asia/Tokyo
    private String currencyCode; // default: JPY
    private String ledgerName;   // default: "기본 가계부"
}
