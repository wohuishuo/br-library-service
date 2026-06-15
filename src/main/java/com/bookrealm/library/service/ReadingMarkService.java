package com.bookrealm.library.service;

import com.bookrealm.library.common.ErrorCode;
import com.bookrealm.library.dto.ReadingMarkDtos;
import com.bookrealm.library.entity.Paragraph;
import com.bookrealm.library.entity.ReadingMark;
import com.bookrealm.library.exception.BusinessException;
import com.bookrealm.library.repository.ParagraphRepository;
import com.bookrealm.library.repository.ReadingMarkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReadingMarkService {

    private final ReadingMarkRepository markRepo;
    private final ParagraphRepository paragraphRepo;

    public ReadingMarkService(ReadingMarkRepository markRepo, ParagraphRepository paragraphRepo) {
        this.markRepo = markRepo;
        this.paragraphRepo = paragraphRepo;
    }

    @Transactional
    public ReadingMarkDtos.MarkItem save(ReadingMarkDtos.SaveMarkRequest request) {
        Paragraph paragraph = paragraphRepo.findById(request.paragraphId())
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "段落不存在"));
        ReadingMark mark = markRepo.findByUserIdAndParagraphIdAndIsDelete(request.userId(), request.paragraphId(), 0)
            .orElseGet(ReadingMark::new);
        mark.setUserId(request.userId());
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

    @Transactional
    public void delete(Long userId, Long id) {
        ReadingMark mark = markRepo.findByIdAndUserIdAndIsDelete(id, userId, 0)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "标记不存在"));
        mark.setIsDelete(1);
        markRepo.save(mark);
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
}
