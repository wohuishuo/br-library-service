package com.bookrealm.library.config;

import com.bookrealm.library.entity.Book;
import com.bookrealm.library.entity.Chapter;
import com.bookrealm.library.entity.Paragraph;
import com.bookrealm.library.entity.Tag;
import com.bookrealm.library.repository.BookRepository;
import com.bookrealm.library.repository.ChapterRepository;
import com.bookrealm.library.repository.ParagraphRepository;
import com.bookrealm.library.repository.TagRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final BookRepository bookRepo;
    private final ChapterRepository chapterRepo;
    private final ParagraphRepository paraRepo;
    private final TagRepository tagRepo;

    public DataSeeder(BookRepository bookRepo, ChapterRepository chapterRepo,
                      ParagraphRepository paraRepo, TagRepository tagRepo) {
        this.bookRepo = bookRepo;
        this.chapterRepo = chapterRepo;
        this.paraRepo = paraRepo;
        this.tagRepo = tagRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (bookRepo.count() > 0) {
            log.info("Seed data already present, skipping.");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        InputStream in = getClass().getClassLoader().getResourceAsStream("seed/books.json");
        if (in == null) {
            log.warn("seed/books.json not found, skipping seed.");
            return;
        }

        List<Map<String, Object>> books = mapper.readValue(in, new TypeReference<>() {});
        int totalParagraphs = 0;

        for (Map<String, Object> bm : books) {
            Book book = new Book(
                (String) bm.get("title"),
                (String) bm.get("author"),
                (String) bm.get("coverUrl"),
                (String) bm.get("intro")
            );

            // tags
            @SuppressWarnings("unchecked")
            List<String> tagNames = (List<String>) bm.get("tags");
            Set<Tag> tags = new HashSet<>();
            if (tagNames != null) {
                for (String tn : tagNames) {
                    Tag tag = tagRepo.findByName(tn).orElseGet(() -> tagRepo.save(new Tag(tn)));
                    tags.add(tag);
                }
            }
            book.setTags(tags);
            book = bookRepo.save(book);

            // chapters
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> chapters = (List<Map<String, Object>>) bm.get("chapters");
            if (chapters != null) {
                for (Map<String, Object> cm : chapters) {
                    int seq = (int) cm.get("seq");
                    Chapter ch = new Chapter(book.getId(), seq, (String) cm.get("title"));
                    ch = chapterRepo.save(ch);

                    // paragraphs
                    @SuppressWarnings("unchecked")
                    List<String> paras = (List<String>) cm.get("paragraphs");
                    if (paras != null) {
                        for (int i = 0; i < paras.size(); i++) {
                            paraRepo.save(new Paragraph(ch.getId(), i + 1, paras.get(i)));
                            totalParagraphs++;
                        }
                    }
                }
            }
        }

        log.info("Seeded {} books, {} paragraphs total.", books.size(), totalParagraphs);
    }
}
