package com.bookrealm.library.repository;

import com.bookrealm.library.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    /** 某本书的全部章节(按 seq 排序),不返回已删 */
    List<Chapter> findByBookIdAndIsDeleteOrderBySeq(Long bookId, Integer isDelete);
}
