package com.bookrealm.library.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class ReadingMarkDtos {
    private ReadingMarkDtos() {}

    public record SaveMarkRequest(
        @NotNull Long userId,
        @NotNull Long bookId,
        @NotNull Long chapterId,
        @NotNull Long paragraphId,
        @NotNull Integer paragraphSeq,
        String markType,
        String note
    ) {}

    public record MarkItem(
        Long id,
        Long userId,
        Long bookId,
        Long chapterId,
        Long paragraphId,
        Integer paragraphSeq,
        String markType,
        String note,
        LocalDateTime updateTime
    ) {}
}
