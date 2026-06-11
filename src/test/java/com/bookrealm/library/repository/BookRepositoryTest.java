package com.bookrealm.library.repository;

import com.bookrealm.library.entity.Book;
import com.bookrealm.library.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private TagRepository tagRepo;

    @Test
    void search_shouldFindByTitle() {
        Page<Book> result = bookRepo.search("西游记", PageRequest.of(0, 10));
        assertFalse(result.isEmpty());
        assertTrue(result.getContent().stream().anyMatch(b -> b.getTitle().contains("西游记")));
    }

    @Test
    void searchByTag_shouldFilterCorrectly() {
        Page<Book> result = bookRepo.searchByTag("", "散文", PageRequest.of(0, 10));
        assertFalse(result.isEmpty());
        result.getContent().forEach(b -> {
            Set<Tag> tags = b.getTags();
            assertTrue(tags.stream().anyMatch(t -> "散文".equals(t.getName())));
        });
    }
}
