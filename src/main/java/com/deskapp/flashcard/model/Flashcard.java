package com.deskapp.flashcard.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Flashcard {
    private int id;
    private String english;
    private String vietnamese;
    private String note;
    private int score;
    private boolean memorized;
    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor khi lấy từ Database lên
    public Flashcard(int id, String english, String vietnamese, String note, int score, boolean memorized, String createdAt, String updatedAt) {
        this.id = id;
        this.english = english;
        this.vietnamese = vietnamese;
        this.note = note != null ? note : "";
        this.score = score;
        this.memorized = memorized;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor tiện lợi khi tạo mới một thẻ (score mặc định 20, memorized = false)
    public Flashcard(String english, String vietnamese, String note) {
        this(0, english, vietnamese, note, 20, false,
                LocalDateTime.now().format(FORMATTER),
                LocalDateTime.now().format(FORMATTER));
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getVietnamese() {
        return vietnamese;
    }

    public void setVietnamese(String vietnamese) {
        this.vietnamese = vietnamese;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note != null ? note : "";
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isMemorized() {
        return memorized;
    }

    public void setMemorized(boolean memorized) {
        this.memorized = memorized;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flashcard flashcard = (Flashcard) o;
        return id == flashcard.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Flashcard{" +
                "id=" + id +
                ", english='" + english + '\'' +
                ", vietnamese='" + vietnamese + '\'' +
                ", score=" + score +
                ", memorized=" + memorized +
                '}';
    }
}