package com.daelim.sfa.dto.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TransferResult<T> {

    // RAPID API 스펙에 있는 데이터 입니다
    private LocalDateTime updatedAt;
    private T data;
}
