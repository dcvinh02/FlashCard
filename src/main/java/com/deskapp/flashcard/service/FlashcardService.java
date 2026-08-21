package com.deskapp.flashcard.service;

import com.deskapp.flashcard.model.Flashcard;
import com.deskapp.flashcard.repository.FlashcardRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
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

    // ==========================================
    // TÍNH NĂNG JSON (MỚI) - ỔN ĐỊNH TUYỆT ĐỐI
    // ==========================================

    public int importFromJson(File file) throws Exception {
        int importedCount = 0;

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            // Định nghĩa kiểu List<Flashcard> để Gson biết cách ép kiểu
            Type listType = new TypeToken<List<Flashcard>>() {}.getType();
            List<Flashcard> cardsToImport = gson.fromJson(reader, listType);

            if (cardsToImport != null) {
                for (Flashcard card : cardsToImport) {
                    if (card.getEnglish() != null && !card.getEnglish().isEmpty() &&
                            card.getVietnamese() != null && !card.getVietnamese().isEmpty()) {

                        // Khởi tạo thẻ mới để làm sạch dữ liệu
                        Flashcard cleanCard = new Flashcard(card.getEnglish().trim(), card.getVietnamese().trim(), card.getNote());
                        if (repository.insert(cleanCard)) {
                            importedCount++;
                        }
                    }
                }
            }
        }
        return importedCount;
    }

    public void exportToJson(File file) throws Exception {
        List<Flashcard> cards = getAllCards();

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            // GsonBuilder với setPrettyPrinting() để file JSON xuất ra đẹp, dễ đọc
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(cards, writer);
        }
    }
}