package com.bookrealm.library.dto;

import java.util.List;

public class BookDetailResponse {
    private Long id;
    private String title;
    private String author;
    private String coverUrl;
    private String intro;
    private List<String> tags;
    private List<ChapterItem> chapters;

    public BookDetailResponse(Long id, String title, String author, String coverUrl,
                              String intro, List<String> tags, List<ChapterItem> chapters) {
        this.id = id; this.title = title; this.author = author; this.coverUrl = coverUrl;
        this.intro = intro; this.tags = tags; this.chapters = chapters;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCoverUrl() { return coverUrl; }
    public String getIntro() { return intro; }
    public List<String> getTags() { return tags; }
    public List<ChapterItem> getChapters() { return chapters; }

    /** 章节摘要(无段落),用于列表与书籍详情 */
    public static class ChapterItem {
        private Long id; private Integer seq; private String title;
        public ChapterItem(Long id, Integer seq, String title) { this.id = id; this.seq = seq; this.title = title; }
        public Long getId() { return id; }
        public Integer getSeq() { return seq; }
        public String getTitle() { return title; }
    }
}
