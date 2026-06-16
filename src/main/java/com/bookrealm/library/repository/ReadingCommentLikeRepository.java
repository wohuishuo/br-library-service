package com.bookrealm.library.repository;

import com.bookrealm.library.entity.ReadingCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadingCommentLikeRepository extends JpaRepository<ReadingCommentLike, Long> {
    Optional<ReadingCommentLike> findByCommentIdAndUserId(Long commentId, Long userId);
    boolean existsByCommentIdAndUserIdAndIsDelete(Long commentId, Long userId, Integer isDelete);
    long countByCommentIdAndIsDelete(Long commentId, Integer isDelete);
}
