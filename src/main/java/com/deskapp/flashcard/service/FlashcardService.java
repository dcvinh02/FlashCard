package com.deskapp.flashcard.service;

import com.deskapp.flashcard.model.Flashcard;
import com.deskapp.flashcard.repository.FlashcardRepository;

import java.util.List;

public class FlashcardService {
    private final FlashcardRepository repository;

    public FlashcardService() {
        this.repository = new FlashcardRepository();
    }

    public boolean saveFlashcard(String english, String vietnamese, String note) throws IllegalArgumentException {
        validateInput(english, vietnamese);
        Flashcard card = new Flashcard(english.trim(), vietnamese.trim(), note != null ? note.trim() : "");
        return repository.insert(card);
    }

    public boolean updateFlashcard(Flashcard card, String english, String vietnamese, String note) throws IllegalArgumentException {
        validateInput(english, vietnamese);
        card.setEnglish(english.trim());
        card.setVietnamese(vietnamese.trim());
        card.setNote(note != null ? note.trim() : "");
        return repository.update(card);
    }

    private void validateInput(String english, String vietnamese) {
        if (english == null || english.trim().isEmpty()) {
            throw new IllegalArgumentException("Từ tiếng Anh không được để trống.");
        }
        if (vietnamese == null || vietnamese.trim().isEmpty()) {
            throw new IllegalArgumentException("Nghĩa tiếng Việt không được để trống.");
        }
    }

    public List<Flashcard> getAllCards() {
        return repository.findAll();
    }

    public List<Flashcard> searchCards(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCards();
        }
        return repository.search(keyword);
    }

    public boolean deleteCard(int id) {
        return repository.delete(id);
    }

    public boolean toggleMemorized(Flashcard card) {
        boolean newState = !card.isMemorized();
        boolean success = repository.updateMemorized(card.getId(), newState);
        if (success) {
            card.setMemorized(newState);
        }
        return success;
    }
}