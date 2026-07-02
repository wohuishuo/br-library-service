package com.bookrealm.library.controller;

import com.bookrealm.library.auth.AuthenticatedUser;
import com.bookrealm.library.auth.CurrentUser;
import com.bookrealm.library.common.BaseResponse;
import com.bookrealm.library.common.ResultUtils;
import com.bookrealm.library.dto.ReadingMarkDtos;
import com.bookrealm.library.service.ReadingMarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "阅读标记", description = "段落级划线与笔记")
@RestController
public class ReadingMarkController {

    private final ReadingMarkService markService;

    public ReadingMarkController(ReadingMarkService markService) {
        this.markService = markService;
    }

    @Operation(summary = "保存划线/笔记")
    @PostMapping("/marks")
    public BaseResponse<ReadingMarkDtos.MarkItem> save(
        @CurrentUser AuthenticatedUser user,
        @Valid @RequestBody ReadingMarkDtos.SaveMarkRequest request
    ) {
        return ResultUtils.success(markService.save(user.userId(), request));
    }

    @Operation(summary = "查询某章节的划线/笔记")
    @GetMapping("/chapters/{chapterId}/marks")
    public BaseResponse<List<ReadingMarkDtos.MarkItem>> listChapter(
        @PathVariable Long chapterId,
        @CurrentUser AuthenticatedUser user
    ) {
        return ResultUtils.success(markService.listChapter(user.userId(), chapterId));
    }

    @Operation(summary = "查询某书的划线/笔记")
    @GetMapping("/books/{bookId}/marks")
    public BaseResponse<List<ReadingMarkDtos.MarkItem>> listBook(
        @PathVariable Long bookId,
        @CurrentUser AuthenticatedUser user
    ) {
        return ResultUtils.success(markService.listBook(user.userId(), bookId));
    }

    @Operation(summary = "查询我的划线/笔记")
    @GetMapping("/users/{userId}/marks")
    public BaseResponse<List<ReadingMarkDtos.MarkItem>> listMine(
        @PathVariable("userId") Long ignoredUserId,
        @CurrentUser AuthenticatedUser user
    ) {
        return ResultUtils.success(markService.listMine(user.userId()));
    }

    @Operation(summary = "删除划线/笔记")
    @DeleteMapping("/marks/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id, @CurrentUser AuthenticatedUser user) {
        markService.delete(user.userId(), id);
        return ResultUtils.success(true);
    }

    @Operation(summary = "发布段评")
    @PostMapping("/comments")
    public BaseResponse<ReadingMarkDtos.CommentItem> saveComment(
        @CurrentUser AuthenticatedUser user,
        @Valid @RequestBody ReadingMarkDtos.SaveCommentRequest request
    ) {
        return ResultUtils.success(markService.saveComment(user.userId(), request));
    }

    @Operation(summary = "查询某段互动")
    @GetMapping("/paragraphs/{paragraphId}/interactions")
    public BaseResponse<ReadingMarkDtos.ParagraphInteraction> paragraphInteraction(
        @PathVariable Long paragraphId,
        @CurrentUser(required = false) AuthenticatedUser user
    ) {
        return ResultUtils.success(markService.paragraphInteraction(paragraphId, optionalUserId(user)));
    }

    @Operation(summary = "查询某段段评")
    @GetMapping("/paragraphs/{paragraphId}/comments")
    public BaseResponse<List<ReadingMarkDtos.CommentItem>> listParagraphComments(
        @PathVariable Long paragraphId,
        @CurrentUser(required = false) AuthenticatedUser user
    ) {
        return ResultUtils.success(markService.listParagraphComments(paragraphId, optionalUserId(user)));
    }

    @Operation(summary = "查询某书段评")
    @GetMapping("/books/{bookId}/comments")
    public BaseResponse<List<ReadingMarkDtos.CommentItem>> listBookComments(
        @PathVariable Long bookId,
        @CurrentUser(required = false) AuthenticatedUser user
    ) {
        return ResultUtils.success(markService.listBookComments(bookId, optionalUserId(user)));
    }

    @Operation(summary = "查询我的段评")
    @GetMapping("/users/{userId}/comments")
    public BaseResponse<List<ReadingMarkDtos.CommentItem>> listMyComments(
        @PathVariable("userId") Long ignoredUserId,
        @CurrentUser AuthenticatedUser user
    ) {
        return ResultUtils.success(markService.listMyComments(user.userId()));
    }

    @Operation(summary = "点赞段评")
    @PostMapping("/comments/{id}/like")
    public BaseResponse<ReadingMarkDtos.CommentItem> likeComment(@PathVariable Long id, @CurrentUser AuthenticatedUser user) {
        return ResultUtils.success(markService.likeComment(id, user.userId()));
    }

    @Operation(summary = "取消点赞段评")
    @DeleteMapping("/comments/{id}/like")
    public BaseResponse<ReadingMarkDtos.CommentItem> unlikeComment(@PathVariable Long id, @CurrentUser AuthenticatedUser user) {
        return ResultUtils.success(markService.unlikeComment(id, user.userId()));
    }

    @Operation(summary = "删除自己的段评")
    @DeleteMapping("/comments/{id}")
    public BaseResponse<Boolean> deleteComment(@PathVariable Long id, @CurrentUser AuthenticatedUser user) {
        markService.deleteComment(user.userId(), id);
        return ResultUtils.success(true);
    }

    private Long optionalUserId(AuthenticatedUser user) {
        return user == null ? null : user.userId();
    }
}
