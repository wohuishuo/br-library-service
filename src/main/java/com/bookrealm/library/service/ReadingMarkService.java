package com.bookrealm.library.service;

import com.bookrealm.library.common.ErrorCode;
import com.bookrealm.library.dto.ReadingMarkDtos;
import com.bookrealm.library.entity.Paragraph;
import com.bookrealm.library.entity.ReadingComment;
import com.bookrealm.library.entity.ReadingCommentLike;
import com.bookrealm.library.entity.ReadingMark;
import com.bookrealm.library.exception.BusinessException;
import com.bookrealm.library.repository.ParagraphRepository;
import com.bookrealm.library.repository.ReadingCommentLikeRepository;
import com.bookrealm.library.repository.ReadingCommentRepository;
import com.bookrealm.library.repository.ReadingMarkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReadingMarkService {

    private final ReadingMarkRepository markRepo;
    private final ReadingCommentRepository commentRepo;
    private final ReadingCommentLikeRepository likeRepo;
    private final ParagraphRepository paragraphRepo;

    public ReadingMarkService(
        ReadingMarkRepository markRepo,
        ReadingCommentRepository commentRepo,
        ReadingCommentLikeRepository likeRepo,
        ParagraphRepository paragraphRepo
    ) {
        this.markRepo = markRepo;
        this.commentRepo = commentRepo;
        this.likeRepo = likeRepo;
        this.paragraphRepo = paragraphRepo;
    }

    @Transactional
    public ReadingMarkDtos.MarkItem save(Long userId, ReadingMarkDtos.SaveMarkRequest request) {
        Paragraph paragraph = paragraphRepo.findById(request.paragraphId())
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "段落不存在"));
        ReadingMark mark = markRepo.findByUserIdAndParagraphIdAndIsDelete(userId, request.paragraphId(), 0)
            .orElseGet(ReadingMark::new);
        mark.setUserId(userId);
        mark.setBookId(request.bookId());
        mark.setChapterId(request.chapterId());
        mark.setParagraphId(request.paragraphId());
        mark.setParagraphSeq(paragraph.getSeq());
        mark.setMarkType(normalizeType(request.markType()));
        mark.setNote(cleanNote(request.note()));
        mark.setIsDelete(0);
        return toItem(markRepo.save(mark));
    }

    @Transactional(readOnly = true)
    public List<ReadingMarkDtos.MarkItem> listChapter(Long userId, Long chapterId) {
        return markRepo.findByUserIdAndChapterIdAndIsDeleteOrderByParagraphSeq(userId, chapterId, 0)
            .stream().map(this::toItem).toList();
    }

    @Transactional(readOnly = true)
    public List<ReadingMarkDtos.MarkItem> listBook(Long userId, Long bookId) {
        return markRepo.findByUserIdAndBookIdAndIsDeleteOrderByUpdateTimeDesc(userId, bookId, 0)
            .stream().map(this::toItem).toList();
    }

    @Transactional(readOnly = true)
    public List<ReadingMarkDtos.MarkItem> listMine(Long userId) {
        return markRepo.findByUserIdAndIsDeleteOrderByUpdateTimeDesc(userId, 0)
            .stream().map(this::toItem).toList();
    }

    @Transactional
    public void delete(Long userId, Long id) {
        ReadingMark mark = markRepo.findByIdAndIsDelete(id, 0)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "标记不存在"));
        if (!mark.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "不能删除他人的标记");
        }
        mark.setIsDelete(1);
        markRepo.save(mark);
    }

    @Transactional
    public ReadingMarkDtos.CommentItem saveComment(Long userId, ReadingMarkDtos.SaveCommentRequest request) {
        Paragraph paragraph = paragraphRepo.findById(request.paragraphId())
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "段落不存在"));
        String content = cleanContent(request.content());
        ReadingComment comment = new ReadingComment();
        comment.setUserId(userId);
        comment.setBookId(request.bookId());
        comment.setChapterId(request.chapterId());
        comment.setParagraphId(request.paragraphId());
        comment.setParagraphSeq(paragraph.getSeq());
        comment.setContent(content);
        return toCommentItem(commentRepo.save(comment), userId);
    }

    @Transactional(readOnly = true)
    public List<ReadingMarkDtos.CommentItem> listParagraphComments(Long paragraphId, Long userId) {
        return commentRepo.findByParagraphIdAndIsDeleteOrderByLikeCountDescUpdateTimeDesc(paragraphId, 0)
            .stream().map(comment -> toCommentItem(comment, userId)).toList();
    }

    @Transactional(readOnly = true)
    public List<ReadingMarkDtos.CommentItem> listBookComments(Long bookId, Long userId) {
        return commentRepo.findByBookIdAndIsDeleteOrderByUpdateTimeDesc(bookId, 0)
            .stream().map(comment -> toCommentItem(comment, userId)).toList();
    }

    @Transactional(readOnly = true)
    public List<ReadingMarkDtos.CommentItem> listMyComments(Long userId) {
        return commentRepo.findByUserIdAndIsDeleteOrderByUpdateTimeDesc(userId, 0)
            .stream().map(comment -> toCommentItem(comment, userId)).toList();
    }

    @Transactional(readOnly = true)
    public ReadingMarkDtos.ParagraphInteraction paragraphInteraction(Long paragraphId, Long userId) {
        List<ReadingMarkDtos.MarkItem> marks = new ArrayList<>();
        if (userId != null) {
            marks = markRepo.findByParagraphIdAndIsDeleteOrderByUpdateTimeDesc(paragraphId, 0)
                .stream()
                .filter(mark -> mark.getUserId().equals(userId))
                .map(this::toItem)
                .toList();
        }
        List<ReadingMarkDtos.CommentItem> comments = listParagraphComments(paragraphId, userId);
        return new ReadingMarkDtos.ParagraphInteraction(paragraphId, marks, comments);
    }

    @Transactional
    public ReadingMarkDtos.CommentItem likeComment(Long commentId, Long userId) {
        ReadingComment comment = commentRepo.findById(commentId)
            .filter(item -> item.getIsDelete() == 0)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "评论不存在"));
        ReadingCommentLike like = likeRepo.findByCommentIdAndUserId(commentId, userId)
            .orElseGet(ReadingCommentLike::new);
        like.setCommentId(commentId);
        like.setUserId(userId);
        like.setIsDelete(0);
        likeRepo.save(like);
        refreshLikeCount(comment);
        return toCommentItem(comment, userId);
    }

    @Transactional
    public ReadingMarkDtos.CommentItem unlikeComment(Long commentId, Long userId) {
        ReadingComment comment = commentRepo.findById(commentId)
            .filter(item -> item.getIsDelete() == 0)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "评论不存在"));
        likeRepo.findByCommentIdAndUserId(commentId, userId).ifPresent(like -> {
            like.setIsDelete(1);
            likeRepo.save(like);
        });
        refreshLikeCount(comment);
        return toCommentItem(comment, userId);
    }

    @Transactional
    public void deleteComment(Long userId, Long id) {
        ReadingComment comment = commentRepo.findByIdAndIsDelete(id, 0)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "评论不存在"));
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "不能删除他人的评论");
        }
        comment.setIsDelete(1);
        commentRepo.save(comment);
    }

    private String normalizeType(String markType) {
        if (markType == null || markType.isBlank()) {
            return "highlight";
        }
        return markType.length() > 24 ? markType.substring(0, 24) : markType;
    }

    private String cleanNote(String note) {
        if (note == null || note.isBlank()) {
            return null;
        }
        return note.length() > 2000 ? note.substring(0, 2000) : note.trim();
    }

    private String cleanContent(String content) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }
        String cleaned = content.trim();
        return cleaned.length() > 1000 ? cleaned.substring(0, 1000) : cleaned;
    }

    private void refreshLikeCount(ReadingComment comment) {
        comment.setLikeCount(likeRepo.countByCommentIdAndIsDelete(comment.getId(), 0));
        commentRepo.save(comment);
    }

    private ReadingMarkDtos.MarkItem toItem(ReadingMark mark) {
        return new ReadingMarkDtos.MarkItem(
            mark.getId(),
            mark.getUserId(),
            mark.getBookId(),
            mark.getChapterId(),
            mark.getParagraphId(),
            mark.getParagraphSeq(),
            mark.getMarkType(),
            mark.getNote(),
            mark.getUpdateTime()
        );
    }

    private ReadingMarkDtos.CommentItem toCommentItem(ReadingComment comment, Long viewerUserId) {
        boolean likedByMe = viewerUserId != null
            && likeRepo.existsByCommentIdAndUserIdAndIsDelete(comment.getId(), viewerUserId, 0);
        return new ReadingMarkDtos.CommentItem(
            comment.getId(),
            comment.getUserId(),
            comment.getBookId(),
            comment.getChapterId(),
            comment.getParagraphId(),
            comment.getParagraphSeq(),
            comment.getContent(),
            comment.getLikeCount(),
            likedByMe,
            comment.getUpdateTime()
        );
    }
}
