package com.bookrealm.library.repository;

import com.bookrealm.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    /** 按书名或作者模糊搜索(匹配 title 或 author),不返回已删 */
    @Query("SELECT b FROM Book b WHERE b.isDelete = 0 AND (b.title LIKE %:q% OR b.author LIKE %:q%)")
    List<Book> search(@Param("q") String q);

    /** 按书名或作者模糊搜索 + 按标签过滤 */
    @Query("SELECT DISTINCT b FROM Book b JOIN b.tags t WHERE b.isDelete = 0 " +
           "AND (b.title LIKE %:q% OR b.author LIKE %:q%) " +
           "AND t.name = :tag")
    List<Book> searchByTag(@Param("q") String q, @Param("tag") String tag);
}
