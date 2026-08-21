package com.deskapp.flashcard.controller;

import com.deskapp.flashcard.service.FlashcardService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Optional;

public class AddCardController {

    @FXML private VBox emptyStateContainer;
    @FXML private VBox formContainer;
    @FXML private TextField txtEnglish;
    @FXML private TextField txtVietnamese;
    @FXML private TextArea txtNote;
    @FXML private Label lblError;
    @FXML private Label lblSuccess;

    private FlashcardService flashcardService;

    @FXML
    public void initialize() {
        flashcardService = new FlashcardService();
    }

    @FXML
    private void showForm() {
        emptyStateContainer.setVisible(false);
        formContainer.setVisible(true);
        txtEnglish.requestFocus();
    }

    @FXML
    private void handleSave() {
        // Ẩn các thông báo cũ
        lblError.setVisible(false); lblError.setManaged(false);
        lblSuccess.setVisible(false); lblSuccess.setManaged(false);

        try {
            boolean isSaved = flashcardService.saveFlashcard(txtEnglish.getText(), txtVietnamese.getText(), txtNote.getText());
            if (isSaved) {
                // Hiện thông báo thành công
                lblSuccess.setText("✓ Đã thêm từ: " + txtEnglish.getText());
                lblSuccess.setVisible(true); lblSuccess.setManaged(true);
                clearForm();

                // Đợi 1.5 giây rồi tự động đóng form, quay về trạng thái dấu +
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(e -> {
                    lblSuccess.setVisible(false); lblSuccess.setManaged(false);
                    formContainer.setVisible(false);
                    emptyStateContainer.setVisible(true);
                });
                pause.play();
            }
        } catch (IllegalArgumentException e) {
            // Hiện lỗi validation (đỏ) trực tiếp trên giao diện
            lblError.setText("⚠ " + e.getMessage());
            lblError.setVisible(true); lblError.setManaged(true);
        }
    }

    @FXML
    private void handleCancel() {
        // Kiểm tra xem người dùng đã gõ gì chưa
        boolean hasData = !txtEnglish.getText().trim().isEmpty()
                || !txtVietnamese.getText().trim().isEmpty()
                || !txtNote.getText().trim().isEmpty();

        if (hasData) {
            // Dialog xác nhận chuẩn UX
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận hủy");
            alert.setHeaderText("Bạn có muốn thoát mà không lưu?");
            alert.setContentText("Dữ liệu bạn vừa nhập sẽ bị mất.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                closeForm();
            }
        } else {
            closeForm(); // Nếu chưa nhập gì thì đóng luôn
        }
    }

    private void closeForm() {
        clearForm();
        lblError.setVisible(false); lblError.setManaged(false);
        lblSuccess.setVisible(false); lblSuccess.setManaged(false);
        formContainer.setVisible(false);
        emptyStateContainer.setVisible(true);
    }

    private void clearForm() {
        txtEnglish.clear();
        txtVietnamese.clear();
        txtNote.clear();
    }
}