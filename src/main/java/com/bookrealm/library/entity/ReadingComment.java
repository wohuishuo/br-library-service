package com.bookrealm.library.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "reading_comments",
    indexes = {
        @Index(name = "idx_comment_book", columnList = "bookId,isDelete,updateTime"),
        @Index(name = "idx_comment_paragraph", columnList = "paragraphId,isDelete,updateTime"),
        @Index(name = "idx_comment_user", columnList = "userId,isDelete,updateTime")
    }
)
public class ReadingComment {

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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Long likeCount = 0L;

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
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getLikeCount() { return likeCount; }
    public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }
    public LocalDateTime getCreateTime() { return createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public Integer getIsDelete() { return isDelete; }
    public void setIsDelete(Integer isDelete) { this.isDelete = isDelete; }
}
