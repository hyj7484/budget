package com.app.application.budget.auth;

import com.app.application.budget.auth.dto.AppUserAuthRow;
import com.app.application.budget.auth.dto.login.LoginRequest;
import com.app.application.budget.auth.dto.login.LoginResponse;
import com.app.application.budget.auth.dto.signup.SignupRequest;
import com.app.application.budget.auth.dto.signup.SignupResponse;
import com.app.application.budget.auth.mapper.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final AppUserMapper appUserMapper;
    private final LedgerMapper ledgerMapper;
    private final LedgerMemberMapper ledgerMemberMapper;
    private final UserSettingMapper userSettingMapper;
    private final CategoryMapper categoryMapper;
    private final PaymentMethodMapper paymentMethodMapper;

    @Transactional
    public SignupResponse signup(SignupRequest req) {
        // 1) 최소 검증
        String locale = nvl(req.getLocale(), "ko-KR");
        String tz = nvl(req.getTimezone(), "Asia/Tokyo");
        String currency = nvl(req.getCurrencyCode(), "JPY");
        String ledgerName = nvl(req.getLedgerName(), "기본 가계부");

        // 2) 사용자 생성
        String hash = passwordEncoder.encode(req.getPassword());
        UUID userId = appUserMapper.insertAndReturnId(
                req.getEmail(),
                hash,
                req.getDisplayName(),
                locale,
                tz,
                currency
        );


        // 3) 기본 원장 생성 (owner=user)
        UUID ledgerId = ledgerMapper.insertAndReturnId(
                userId,
                ledgerName,
                currency,
                tz
        );

        // 4) 원장 멤버(OWNER)
        ledgerMemberMapper.insert(ledgerId, userId, "OWNER");

        // 5) user_setting 생성 (default_ledger_id 지정)
        userSettingMapper.insert(userId, ledgerId, locale, tz, currency, "WEEKLY");

        // 6) 기본 결제수단 시드
        // type: CARD/CASH/BANK/TRANSIT
        paymentMethodMapper.insert(ledgerId, "CASH", "현금", null, null);
        paymentMethodMapper.insert(ledgerId, "BANK", "주계좌", "BANK", null);
        paymentMethodMapper.insert(ledgerId, "CARD", "카드", "CARD", "0000");

        // 7) 기본 카테고리 시드 (MVP용)
        seedDefaultCategories(ledgerId);

        return new SignupResponse(userId, ledgerId);
    }

    private void seedDefaultCategories(UUID ledgerId) {
        // EXPENSE
        List<CategorySeed> expense = List.of(
                new CategorySeed("EXPENSE", null, "식비", "🍚", 10),
                new CategorySeed("EXPENSE", null, "카페/간식", "☕", 20),
                new CategorySeed("EXPENSE", null, "교통", "🚃", 30),
                new CategorySeed("EXPENSE", null, "쇼핑", "🛍️", 40),
                new CategorySeed("EXPENSE", null, "생활", "🏠", 50),
                new CategorySeed("EXPENSE", null, "의료", "🏥", 60),
                new CategorySeed("EXPENSE", null, "구독", "📦", 70),
                new CategorySeed("EXPENSE", null, "여가", "🎮", 80),
                new CategorySeed("EXPENSE", null, "여행", "🧳", 90),
                new CategorySeed("EXPENSE", null, "기타", "🧾", 99)
        );

        // INCOME
        List<CategorySeed> income = List.of(
                new CategorySeed("INCOME", null, "급여", "💴", 10),
                new CategorySeed("INCOME", null, "기타수입", "➕", 20)
        );

        for (CategorySeed c : expense) {
            categoryMapper.insertRoot(ledgerId, c.kind, c.name, c.icon, c.sortOrder);
        }
        for (CategorySeed c : income) {
            categoryMapper.insertRoot(ledgerId, c.kind, c.name, c.icon, c.sortOrder);
        }
    }

    private static String nvl(String v, String def) {
        return (v == null || v.isBlank()) ? def : v.trim();
    }

    private record CategorySeed(String kind, UUID parentId, String name, String icon, int sortOrder) {}

    public LoginResponse login(LoginRequest req) {
        AppUserAuthRow user = appUserMapper.findAuthByEmail(req.getEmail());

        UUID ledgerId = ledgerMapper.findDefaultLedgerId(user.getId());
        if (ledgerId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ledger not found");
        }

        return new LoginResponse(user.getId(), ledgerId);
    }
}
