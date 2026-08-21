package com.deskapp.flashcard.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Button btnAddTab;
    @FXML private Button btnStorageTab;
    @FXML private Button btnStudyTab;

    @FXML
    public void initialize() {
        // Vừa mở app lên sẽ vào thẳng Tab Thêm
        switchToAddTab();
    }

    @FXML
    private void switchToAddTab() {
        updateNavButtons(btnAddTab);
        loadTab("/fxml/tab_add.fxml"); // Load giao diện thật
    }

    @FXML
    private void switchToStorageTab() {
        updateNavButtons(btnStorageTab);
        loadTab("/fxml/tab_storage.fxml");
    }

    @FXML
    private void switchToStudyTab() {
        updateNavButtons(btnStudyTab);
        loadTab("/fxml/tab_study.fxml");
    }

    private void updateNavButtons(Button activeButton) {
        btnAddTab.getStyleClass().remove("nav-button-active");
        btnStorageTab.getStyleClass().remove("nav-button-active");
        btnStudyTab.getStyleClass().remove("nav-button-active");

        if (!activeButton.getStyleClass().contains("nav-button-active")) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    // Hàm load FXML động vào vùng Center
    private void loadTab(String fxmlPath) {
        try {
            Parent tabContent = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(tabContent);
        } catch (IOException e) {
            e.printStackTrace();
            showPlaceholder("LỖI: Không thể tải giao diện " + fxmlPath);
        }
    }

    private void showPlaceholder(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #9B7DE8; -fx-font-weight: bold; -fx-text-alignment: center;");
        contentArea.getChildren().clear();
        contentArea.getChildren().add(label);
    }
}