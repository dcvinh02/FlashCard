package com.deskapp.flashcard.repository;

import com.deskapp.flashcard.model.Flashcard;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlashcardRepository {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public boolean insert(Flashcard card) {
        String sql = "INSERT INTO flashcards (english, vietnamese, note, score, memorized, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String now = LocalDateTime.now().format(FORMATTER);
            pstmt.setString(1, card.getEnglish().trim());
            pstmt.setString(2, card.getVietnamese().trim());
            pstmt.setString(3, card.getNote() != null ? card.getNote().trim() : "");
            pstmt.setInt(4, card.getScore());
            pstmt.setInt(5, card.isMemorized() ? 1 : 0);
            pstmt.setString(6, now);
            pstmt.setString(7, now);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        card.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm Flashcard: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Flashcard card) {
        String sql = "UPDATE flashcards SET english = ?, vietnamese = ?, note = ?, score = ?, memorized = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String now = LocalDateTime.now().format(FORMATTER);
            pstmt.setString(1, card.getEnglish().trim());
            pstmt.setString(2, card.getVietnamese().trim());
            pstmt.setString(3, card.getNote() != null ? card.getNote().trim() : "");
            pstmt.setInt(4, card.getScore());
            pstmt.setInt(5, card.isMemorized() ? 1 : 0);
            pstmt.setString(6, now);
            pstmt.setInt(7, card.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật Flashcard: " + e.getMessage());
        }
        return false;
    }

    public boolean updateScore(int id, int newScore) {
        String sql = "UPDATE flashcards SET score = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newScore);
            pstmt.setString(2, LocalDateTime.now().format(FORMATTER));
            pstmt.setInt(3, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật điểm: " + e.getMessage());
        }
        return false;
    }

    public boolean updateMemorized(int id, boolean memorized) {
        String sql = "UPDATE flashcards SET memorized = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memorized ? 1 : 0);
            pstmt.setString(2, LocalDateTime.now().format(FORMATTER));
            pstmt.setInt(3, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái đã nhớ: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM flashcards WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa Flashcard: " + e.getMessage());
        }
        return false;
    }

    public List<Flashcard> findAll() {
        List<Flashcard> list = new ArrayList<>();
        String sql = "SELECT * FROM flashcards ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToFlashcard(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách Flashcard: " + e.getMessage());
        }
        return list;
    }

    public List<Flashcard> findUnmemorized() {
        List<Flashcard> list = new ArrayList<>();
        String sql = "SELECT * FROM flashcards WHERE memorized = 0 ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToFlashcard(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy từ chưa nhớ: " + e.getMessage());
        }
        return list;
    }

    public List<Flashcard> search(String keyword) {
        List<Flashcard> list = new ArrayList<>();
        String sql = "SELECT * FROM flashcards WHERE english LIKE ? OR vietnamese LIKE ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String queryParam = "%" + keyword.trim() + "%";
            pstmt.setString(1, queryParam);
            pstmt.setString(2, queryParam);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFlashcard(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm Flashcard: " + e.getMessage());
        }
        return list;
    }

    public Optional<Flashcard> findById(int id) {
        String sql = "SELECT * FROM flashcards WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFlashcard(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm Flashcard theo ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    private Flashcard mapResultSetToFlashcard(ResultSet rs) throws SQLException {
        return new Flashcard(
                rs.getInt("id"),
                rs.getString("english"),
                rs.getString("vietnamese"),
                rs.getString("note"),
                rs.getInt("score"),
                rs.getInt("memorized") == 1,
                rs.getString("created_at"),
                rs.getString("updated_at")
        );
    }
}