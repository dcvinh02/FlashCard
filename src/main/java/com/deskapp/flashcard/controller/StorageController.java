package com.deskapp.flashcard.controller;

import com.deskapp.flashcard.model.Flashcard;
import com.deskapp.flashcard.service.FlashcardService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Optional;

public class StorageController {

    @FXML private TextField txtSearch;
    @FXML private VBox listContainer;

    @FXML private VBox popupNoteOverlay;
    @FXML private Label lblPopupEnglish;
    @FXML private Label lblPopupVietnamese;
    @FXML private Label lblPopupNote;

    @FXML private VBox popupEditOverlay;
    @FXML private TextField txtEditEnglish;
    @FXML private TextField txtEditVietnamese;
    @FXML private TextArea txtEditNote;
    @FXML private Label lblEditError;

    private FlashcardService flashcardService;
    private Flashcard currentEditingCard;

    @FXML
    public void initialize() {
        flashcardService = new FlashcardService();
        loadList(flashcardService.getAllCards());

        // Bắt sự kiện gõ phím vào ô Tìm kiếm
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            loadList(flashcardService.searchCards(newValue));
        });
    }

    private void loadList(List<Flashcard> cards) {
        listContainer.getChildren().clear();

        if (cards.isEmpty()) {
            Label lblEmpty = new Label("Chưa có từ vựng nào. Hãy sang Tab Thêm để tạo nhé!");
            lblEmpty.setStyle("-fx-text-fill: #A0A0B0; -fx-font-size: 16px; -fx-padding: 20;");
            listContainer.getChildren().add(lblEmpty);
            return;
        }

        for (Flashcard card : cards) {
            HBox row = createCardRow(card);
            listContainer.getChildren().add(row);
        }
    }

    // Hàm tự động sinh giao diện cho 1 hàng (row)
    private HBox createCardRow(Flashcard card) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("list-row");

        // 1. Chữ Tiếng Anh & Tiếng Việt
        VBox textContainer = new VBox(5);
        Label lblEng = new Label(card.getEnglish());
        lblEng.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #303040;");
        Label lblVie = new Label(card.getVietnamese());
        lblVie.setStyle("-fx-font-size: 14px; -fx-text-fill: #6E6E80;");
        textContainer.getChildren().addAll(lblEng, lblVie);

        // Spacer để đẩy các nút về bên phải
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 2. Badge Điểm số
        Label lblScore = new Label(String.valueOf(card.getScore()));
        lblScore.getStyleClass().add("badge-score");

        // 3. Nút Xem Note
        Button btnNote = new Button("📄");
        btnNote.getStyleClass().add("btn-icon");
        btnNote.setTooltip(new Tooltip("Xem ghi chú"));
        btnNote.setOnAction(e -> showNotePopup(card));

        // 4. Nút Đã Nhớ (Bookmark)
        Button btnMemo = new Button(card.isMemorized() ? "🔖 ✓" : "🔖");
        btnMemo.getStyleClass().add("btn-icon");
        if(card.isMemorized()) btnMemo.setStyle("-fx-text-fill: #78C99A;"); // Đổi màu xanh nếu đã nhớ
        btnMemo.setTooltip(new Tooltip(card.isMemorized() ? "Bỏ đánh dấu nhớ" : "Đánh dấu đã nhớ"));
        btnMemo.setOnAction(e -> {
            if(flashcardService.toggleMemorized(card)) {
                // Refresh lại list sau khi toggle
                loadList(flashcardService.searchCards(txtSearch.getText()));
            }
        });

        // 5. Menu 3 chấm (Sửa / Xóa)
        MenuButton btnMore = new MenuButton("⋯");
        btnMore.getStyleClass().add("btn-icon");

        MenuItem editItem = new MenuItem("✎ Sửa từ");
        editItem.setOnAction(e -> showEditPopup(card));

        MenuItem deleteItem = new MenuItem("🗑 Xóa từ");
        deleteItem.setStyle("-fx-text-fill: #E77C83;");
        deleteItem.setOnAction(e -> handleDelete(card));

        btnMore.getItems().addAll(editItem, deleteItem);

        // Ráp tất cả vào row
        row.getChildren().addAll(textContainer, spacer, lblScore, btnNote, btnMemo, btnMore);
        return row;
    }

    // --- LOGIC POPUPS & XÓA ---

    private void showNotePopup(Flashcard card) {
        lblPopupEnglish.setText(card.getEnglish() + " (" + card.getScore() + " điểm)");
        lblPopupVietnamese.setText(card.getVietnamese());
        lblPopupNote.setText(card.getNote().isEmpty() ? "(Không có ghi chú)" : card.getNote());
        popupNoteOverlay.setVisible(true);
    }

    private void showEditPopup(Flashcard card) {
        currentEditingCard = card;
        txtEditEnglish.setText(card.getEnglish());
        txtEditVietnamese.setText(card.getVietnamese());
        txtEditNote.setText(card.getNote());
        lblEditError.setVisible(false); lblEditError.setManaged(false);
        popupEditOverlay.setVisible(true);
    }

    @FXML
    private void saveEdit() {
        try {
            boolean success = flashcardService.updateFlashcard(currentEditingCard,
                    txtEditEnglish.getText(), txtEditVietnamese.getText(), txtEditNote.getText());
            if (success) {
                closePopups();
                loadList(flashcardService.searchCards(txtSearch.getText())); // Refresh
            }
        } catch (IllegalArgumentException e) {
            lblEditError.setText("⚠ " + e.getMessage());
            lblEditError.setVisible(true); lblEditError.setManaged(true);
        }
    }

    private void handleDelete(Flashcard card) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa từ: " + card.getEnglish());
        alert.setContentText("Bạn có chắc chắn muốn xóa từ này không?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (flashcardService.deleteCard(card.getId())) {
                loadList(flashcardService.searchCards(txtSearch.getText())); // Refresh
            }
        }
    }

    @FXML
    private void closePopups() {
        popupNoteOverlay.setVisible(false);
        popupEditOverlay.setVisible(false);
        currentEditingCard = null;
    }
}