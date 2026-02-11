package com.app.application.budget.auth.dto.login;

import java.util.UUID;

public record LoginResponse(UUID userId, UUID ledgerId) {}