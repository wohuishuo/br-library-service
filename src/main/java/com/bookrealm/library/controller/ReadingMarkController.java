package com.bookrealm.library.controller;

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
    public BaseResponse<ReadingMarkDtos.MarkItem> save(@Valid @RequestBody ReadingMarkDtos.SaveMarkRequest request) {
        return ResultUtils.success(markService.save(request));
    }

    @Operation(summary = "查询某章节的划线/笔记")
    @GetMapping("/chapters/{chapterId}/marks")
    public BaseResponse<List<ReadingMarkDtos.MarkItem>> listChapter(@PathVariable Long chapterId, @RequestParam Long userId) {
        return ResultUtils.success(markService.listChapter(userId, chapterId));
    }

    @Operation(summary = "查询某书的划线/笔记")
    @GetMapping("/books/{bookId}/marks")
    public BaseResponse<List<ReadingMarkDtos.MarkItem>> listBook(@PathVariable Long bookId, @RequestParam Long userId) {
        return ResultUtils.success(markService.listBook(userId, bookId));
    }

    @Operation(summary = "删除划线/笔记")
    @DeleteMapping("/marks/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id, @RequestParam Long userId) {
        markService.delete(userId, id);
        return ResultUtils.success(true);
    }
}
