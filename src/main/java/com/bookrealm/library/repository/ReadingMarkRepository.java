package com.bookrealm.library.repository;

import com.bookrealm.library.entity.ReadingMark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingMarkRepository extends JpaRepository<ReadingMark, Long> {
    List<ReadingMark> findByUserIdAndChapterIdAndIsDeleteOrderByParagraphSeq(Long userId, Long chapterId, Integer isDelete);
    List<ReadingMark> findByUserIdAndBookIdAndIsDeleteOrderByUpdateTimeDesc(Long userId, Long bookId, Integer isDelete);
    Optional<ReadingMark> findByIdAndUserIdAndIsDelete(Long id, Long userId, Integer isDelete);
    Optional<ReadingMark> findByUserIdAndParagraphIdAndIsDelete(Long userId, Long paragraphId, Integer isDelete);
}
