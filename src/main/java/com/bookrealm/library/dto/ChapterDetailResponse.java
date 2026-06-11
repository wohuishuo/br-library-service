package com.bookrealm.library.dto;

import java.util.List;

public class ChapterDetailResponse {
    private Long id; private Long bookId; private Integer seq; private String title;
    private List<ParagraphItem> paragraphs;

    public ChapterDetailResponse(Long id, Long bookId, Integer seq, String title, List<ParagraphItem> paragraphs) {
        this.id = id; this.bookId = bookId; this.seq = seq; this.title = title; this.paragraphs = paragraphs;
    }
    public Long getId() { return id; }
    public Long getBookId() { return bookId; }
    public Integer getSeq() { return seq; }
    public String getTitle() { return title; }
    public List<ParagraphItem> getParagraphs() { return paragraphs; }

    public static class ParagraphItem {
        private Long id; private Integer seq; private String content;
        public ParagraphItem(Long id, Integer seq, String content) { this.id = id; this.seq = seq; this.content = content; }
        public Long getId() { return id; }
        public Integer getSeq() { return seq; }
        public String getContent() { return content; }
    }
}
