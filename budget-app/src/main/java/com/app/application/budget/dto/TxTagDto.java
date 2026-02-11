package com.app.application.budget.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TxTagDto {
    /**
     * 거래 태그 매핑 ID
     */
    private UUID txId;
    
    /**
     * 태그 ID
     */
    private UUID tagId;
    
}
