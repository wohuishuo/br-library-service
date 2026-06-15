package com.bookrealm.library.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "reading_marks",
    indexes = {
        @Index(name = "idx_mark_user_chapter", columnList = "userId,chapterId"),
        @Index(name = "idx_mark_user_paragraph", columnList = "userId,paragraphId")
    }
)
public class ReadingMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private Long chapterId;

    @Column(nullable = false)
    private Long paragraphId;

    @Column(nullable = false)
    private Integer paragraphSeq;

    @Column(length = 24, nullable = false)
    private String markType = "highlight";

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Column(nullable = false)
    private Integer isDelete = 0;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
    public Long getParagraphId() { return paragraphId; }
    public void setParagraphId(Long paragraphId) { this.paragraphId = paragraphId; }
    public Integer getParagraphSeq() { return paragraphSeq; }
    public void setParagraphSeq(Integer paragraphSeq) { this.paragraphSeq = paragraphSeq; }
    public String getMarkType() { return markType; }
    public void setMarkType(String markType) { this.markType = markType; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDateTime getCreateTime() { return createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public Integer getIsDelete() { return isDelete; }
    public void setIsDelete(Integer isDelete) { this.isDelete = isDelete; }
}
