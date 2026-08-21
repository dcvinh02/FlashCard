package com.deskapp.flashcard.service;

import com.deskapp.flashcard.model.Flashcard;
import com.deskapp.flashcard.repository.FlashcardRepository;

import java.util.List;

public class StudyService {
    private final FlashcardRepository repository;

    public StudyService() {
        this.repository = new FlashcardRepository();
    }

    public List<Flashcard> getCardsToStudy() {
        return repository.findUnmemorized();
    }

    // Thuật toán kiểm tra lỏng (Loose checking)
    public boolean isAnswerCorrect(String userAnswer, String expectedAnswer) {
        if (userAnswer == null || expectedAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }

        // 1. Chuẩn hóa: Đưa về chữ thường, thay nhiều khoảng trắng liên tiếp thành 1 khoảng trắng
        String normUser = userAnswer.trim().toLowerCase().replaceAll("\\s+", " ");
        String normExpected = expectedAnswer.trim().toLowerCase().replaceAll("\\s+", " ");

        // 2. Kiểm tra khớp toàn bộ (trường hợp user gõ đủ "dồi dào, phong phú")
        if (normExpected.equals(normUser)) {
            return true;
        }

        // 3. Kiểm tra từng phần tử cách nhau bởi dấu phẩy
        String[] parts = normExpected.split(",");
        for (String part : parts) {
            if (part.trim().equals(normUser)) {
                return true;
            }
        }

        return false;
    }

    // Cập nhật điểm và lưu vào SQLite
    public void updateScore(Flashcard card, boolean isCorrect) {
        int delta = isCorrect ? 1 : -1;
        int newScore = card.getScore() + delta;
        card.setScore(newScore);
        repository.updateScore(card.getId(), newScore);
    }
}