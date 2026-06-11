package com.bookrealm.library.controller;

import com.bookrealm.library.common.BaseResponse;
import com.bookrealm.library.common.ResultUtils;
import com.bookrealm.library.dto.BookDetailResponse;
import com.bookrealm.library.dto.BookListResponse;
import com.bookrealm.library.dto.ChapterDetailResponse;
import com.bookrealm.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Tag(name = "书库", description = "书籍与章节查询")
@RestController
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) { this.bookService = bookService; }

    @Operation(summary = "书籍列表(分页+搜索)")
    @GetMapping("/books")
    public BaseResponse<BookListResponse> listBooks(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String tag,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return ResultUtils.success(bookService.list(q, tag, page, size));
    }

    @Operation(summary = "书籍详情")
    @GetMapping("/books/{id}")
    public BaseResponse<BookDetailResponse> getBook(@PathVariable Long id) {
        return ResultUtils.success(bookService.detail(id));
    }

    @Operation(summary = "某书的章节目录")
    @GetMapping("/books/{id}/chapters")
    public BaseResponse<List<BookDetailResponse.ChapterItem>> listChapters(@PathVariable Long id) {
        // 复用 detail 里的章节列表
        return ResultUtils.success(bookService.detail(id).getChapters());
    }

    @Operation(summary = "章节详情(含段落)")
    @GetMapping("/chapters/{id}")
    public BaseResponse<ChapterDetailResponse> getChapter(@PathVariable Long id) {
        return ResultUtils.success(bookService.chapterDetail(id));
    }
}
