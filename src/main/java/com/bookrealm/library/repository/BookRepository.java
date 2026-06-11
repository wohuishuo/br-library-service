package com.bookrealm.library.repository;

import com.bookrealm.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    /** 按书名或作者模糊搜索,不返回已删 */
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.tags WHERE b.isDelete = 0 AND (b.title LIKE CONCAT('%',:q,'%') OR b.author LIKE CONCAT('%',:q,'%'))")
    Page<Book> search(@Param("q") String q, Pageable pageable);

    /** 按书名或作者 + 标签过滤 */
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.tags JOIN b.tags t WHERE b.isDelete = 0 " +
           "AND (b.title LIKE CONCAT('%',:q,'%') OR b.author LIKE CONCAT('%',:q,'%')) AND t.name = :tag")
    Page<Book> searchByTag(@Param("q") String q, @Param("tag") String tag, Pageable pageable);
}
