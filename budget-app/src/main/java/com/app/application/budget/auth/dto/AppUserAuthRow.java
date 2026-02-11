package com.app.application.budget.auth.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppUserAuthRow {
    private UUID id;
    private String email;
    private String passwordHash;
    private String displayName;

}
