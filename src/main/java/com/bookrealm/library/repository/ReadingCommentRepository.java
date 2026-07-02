package com.bookrealm.library.repository;

import com.bookrealm.library.entity.ReadingComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingCommentRepository extends JpaRepository<ReadingComment, Long> {
    List<ReadingComment> findByParagraphIdAndIsDeleteOrderByLikeCountDescUpdateTimeDesc(Long paragraphId, Integer isDelete);
    List<ReadingComment> findByBookIdAndIsDeleteOrderByUpdateTimeDesc(Long bookId, Integer isDelete);
    List<ReadingComment> findByUserIdAndIsDeleteOrderByUpdateTimeDesc(Long userId, Integer isDelete);
    Optional<ReadingComment> findByIdAndIsDelete(Long id, Integer isDelete);
    Optional<ReadingComment> findByIdAndUserIdAndIsDelete(Long id, Long userId, Integer isDelete);
}
