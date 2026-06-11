package com.bookrealm.library.repository;

import com.bookrealm.library.entity.Paragraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {

    /** 某章的全部段落(按 seq 排序),不返回已删 */
    List<Paragraph> findByChapterIdAndIsDeleteOrderBySeq(Long chapterId, Integer isDelete);
}
