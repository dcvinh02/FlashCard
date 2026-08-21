package com.deskapp.flashcard.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnAddTab;

    @FXML
    private Button btnStorageTab;

    @FXML
    private Button btnStudyTab;

    @FXML
    public void initialize() {
        // Mặc định khi mở app sẽ vào Tab Lưu Trữ (hoặc Tab Thêm tùy bạn)
        // Hiện tại ta sẽ đổi màu active cho nút Lưu Trữ
        updateNavButtons(btnStorageTab);
    }

    @FXML
    private void switchToAddTab() {
        updateNavButtons(btnAddTab);
        // Tạm thời hiển thị text giả lập
        showPlaceholder("Giao diện: THÊM FLASHCARD\n(Sẽ làm ở Phase 5)");
    }

    @FXML
    private void switchToStorageTab() {
        updateNavButtons(btnStorageTab);
        // Tạm thời hiển thị text giả lập
        showPlaceholder("Giao diện: KHO LƯU TRỮ\n(Sẽ làm ở Phase 6)");
    }

    @FXML
    private void switchToStudyTab() {
        updateNavButtons(btnStudyTab);
        // Tạm thời hiển thị text giả lập
        showPlaceholder("Giao diện: HỌC TỪ VỰNG\n(Sẽ làm ở Phase 7)");
    }

    // Logic thay đổi CSS class để làm nổi bật nút đang chọn
    private void updateNavButtons(Button activeButton) {
        btnAddTab.getStyleClass().remove("nav-button-active");
        btnStorageTab.getStyleClass().remove("nav-button-active");
        btnStudyTab.getStyleClass().remove("nav-button-active");

        if (!activeButton.getStyleClass().contains("nav-button-active")) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    // Hàm tạm để render nội dung vào giữa màn hình
    private void showPlaceholder(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #9B7DE8; -fx-font-weight: bold; -fx-text-alignment: center;");
        contentArea.getChildren().clear();
        contentArea.getChildren().add(label);
    }
}