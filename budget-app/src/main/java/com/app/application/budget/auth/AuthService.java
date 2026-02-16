package com.app.application.budget.auth;

import com.app.application.budget.auth.dto.AppUserAuthRow;
import com.app.application.budget.auth.dto.login.LoginRequest;
import com.app.application.budget.auth.dto.login.LoginResponse;
import com.app.application.budget.auth.dto.signup.SignupRequest;
import com.app.application.budget.auth.dto.signup.SignupResponse;
import com.app.application.budget.domain.enums.CategoryKind;
import com.app.application.budget.dto.CategoryDto;
import com.app.application.budget.mapper.*;

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
        String currency = nvl(req.getCurrencyCode(), "KRW");
        String ledgerName = nvl(req.getLedgerName(), "기본 가계부");

        // 2) 사용자 생성
        // 패스워드 해싱
        String hash = passwordEncoder.encode(req.getPassword());
        // 사용자 생성 및 ID 반환
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
        List<CategoryDto> category = List.of(
                // EXPENSE
                createCategoryDto(CategoryKind.EXPENSE, "식비", "🍚", 10),
                createCategoryDto(CategoryKind.EXPENSE, "카페/간식", "☕", 20),
                createCategoryDto(CategoryKind.EXPENSE, "교통", "🚃", 30),
                createCategoryDto(CategoryKind.EXPENSE, "쇼핑", "🛍️", 40),
                createCategoryDto(CategoryKind.EXPENSE, "생활", "🏠", 50),
                createCategoryDto(CategoryKind.EXPENSE, "의료", "🏥", 60),
                createCategoryDto(CategoryKind.EXPENSE, "구독", "📦", 70),
                createCategoryDto(CategoryKind.EXPENSE, "여가", "🎮", 80),
                createCategoryDto(CategoryKind.EXPENSE, "여행", "🧳", 90),
                createCategoryDto(CategoryKind.EXPENSE, "기타", "🧾", 99),
                // INCOME
                createCategoryDto(CategoryKind.INCOME, "급여", "💴", 10),
                createCategoryDto(CategoryKind.INCOME, "기타수입", "➕", 20)
        );

        for (CategoryDto c : category) {
            categoryMapper.insertRoot(ledgerId, c.getKind(), c.getName(), c.getIcon(), c.getSortOrder());
        }
    }

    // 값이 null이거나 빈 문자열이면 기본값 반환, 그렇지 않으면 trim된 값 반환
    private static String nvl(String v, String def) {
        return (v == null || v.isBlank()) ? def : v.trim();
    }

    // 카테고리 DTO 생성
    private CategoryDto createCategoryDto(CategoryKind kind, String name, String icon, int sortOrder){
        CategoryDto dto = new CategoryDto();
        dto.setKind(kind);
        dto.setName(name);
        dto.setIcon(icon);
        dto.setSortOrder(sortOrder);
        return dto;
    }

    // 로그인: 이메일로 사용자 조회 -> 패스워드 검증 -> 기본 원장 ID 조회 -> 응답 반환
    public LoginResponse login(LoginRequest req) {
        AppUserAuthRow user = appUserMapper.findAuthByEmail(req.getEmail());
        
        // 패스워드 일치 체크
        if (user == null || !verifyPassword(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "패스워드가 올바르지 않습니다.");
        }

        UUID ledgerId = ledgerMapper.findDefaultLedgerId(user.getId());
        if (ledgerId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ledger not found");
        }

        return new LoginResponse(user.getId(), ledgerId);
    }

    private boolean verifyPassword(String rawPassword, String hash) {
        return passwordEncoder.matches(rawPassword, hash);
    }
}
