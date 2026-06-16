package com.bookrealm.library.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

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

    public record SaveCommentRequest(
        @NotNull Long userId,
        @NotNull Long bookId,
        @NotNull Long chapterId,
        @NotNull Long paragraphId,
        String content
    ) {}

    public record CommentItem(
        Long id,
        Long userId,
        Long bookId,
        Long chapterId,
        Long paragraphId,
        Integer paragraphSeq,
        String content,
        Long likeCount,
        Boolean likedByMe,
        LocalDateTime updateTime
    ) {}

    public record ParagraphInteraction(
        Long paragraphId,
        List<MarkItem> marks,
        List<CommentItem> comments
    ) {}
}
