package com.bookrealm.library.dto;

import java.util.List;

public class BookListResponse {
    private List<BookItem> items;
    private long total;
    private int page;
    private int size;

    public BookListResponse(List<BookItem> items, long total, int page, int size) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public List<BookItem> getItems() { return items; }
    public long getTotal() { return total; }
    public int getPage() { return page; }
    public int getSize() { return size; }

    public static class BookItem {
        private Long id;
        private String title;
        private String author;
        private String coverUrl;
        private String intro;
        private List<String> tags;

        public BookItem(Long id, String title, String author, String coverUrl, String intro, List<String> tags) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.coverUrl = coverUrl;
            this.intro = intro;
            this.tags = tags;
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getCoverUrl() { return coverUrl; }
        public String getIntro() { return intro; }
        public List<String> getTags() { return tags; }
    }
}
