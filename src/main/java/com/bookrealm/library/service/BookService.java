package com.bookrealm.library.service;

import com.bookrealm.library.dto.*;
import com.bookrealm.library.entity.*;
import com.bookrealm.library.exception.BusinessException;
import com.bookrealm.library.common.ErrorCode;
import com.bookrealm.library.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepo;
    private final ChapterRepository chapterRepo;
    private final ParagraphRepository paraRepo;

    public BookService(BookRepository bookRepo, ChapterRepository chapterRepo, ParagraphRepository paraRepo) {
        this.bookRepo = bookRepo;
        this.chapterRepo = chapterRepo;
        this.paraRepo = paraRepo;
    }

    /** 书籍列表(分页+搜索+标签过滤) */
    @Transactional(readOnly = true)
    public BookListResponse list(String q, String tag, int page, int size) {
        PageRequest pr = PageRequest.of(page, size);
        Page<Book> result = (tag != null && !tag.isBlank())
            ? bookRepo.searchByTag(q != null ? q : "", tag, pr)
            : bookRepo.search(q != null ? q : "", pr);

        List<BookListResponse.BookItem> items = result.getContent().stream()
            .map(b -> new BookListResponse.BookItem(
                b.getId(), b.getTitle(), b.getAuthor(), b.getCoverUrl(), b.getIntro(),
                b.getTags().stream().map(Tag::getName).toList()))
            .toList();
        return new BookListResponse(items, result.getTotalElements(), page, size);
    }

    /** 书籍详情(含章节目录) */
    @Transactional(readOnly = true)
    public BookDetailResponse detail(Long id) {
        Book b = bookRepo.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "书籍不存在"));
        List<Chapter> chapters = chapterRepo.findByBookIdAndIsDeleteOrderBySeq(id, 0);
        // fetch tags eagerly
        b.getTags().size();
        List<BookDetailResponse.ChapterItem> chItems = chapters.stream()
            .map(c -> new BookDetailResponse.ChapterItem(c.getId(), c.getSeq(), c.getTitle()))
            .toList();
        return new BookDetailResponse(b.getId(), b.getTitle(), b.getAuthor(),
            b.getCoverUrl(), b.getIntro(),
            b.getTags().stream().map(Tag::getName).toList(), chItems);
    }

    /** 章节详情(含段落列表) */
    @Transactional(readOnly = true)
    public ChapterDetailResponse chapterDetail(Long chapterId) {
        Chapter c = chapterRepo.findById(chapterId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "章节不存在"));
        List<Paragraph> paras = paraRepo.findByChapterIdAndIsDeleteOrderBySeq(chapterId, 0);
        List<ChapterDetailResponse.ParagraphItem> pItems = paras.stream()
            .map(p -> new ChapterDetailResponse.ParagraphItem(p.getId(), p.getSeq(), p.getContent()))
            .toList();
        return new ChapterDetailResponse(c.getId(), c.getBookId(), c.getSeq(), c.getTitle(), pItems);
    }
}
