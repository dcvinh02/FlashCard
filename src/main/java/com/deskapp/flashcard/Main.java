package com.deskapp.flashcard;

import com.deskapp.flashcard.model.Flashcard;
import com.deskapp.flashcard.repository.DatabaseConnection;
import com.deskapp.flashcard.repository.FlashcardRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

public class Main extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        // 1. Tự động tạo SQLite Database & Table
        DatabaseConnection.initializeDatabase();

        // 2. Chạy tự kiểm tra CRUD nhanh
        FlashcardRepository repo = new FlashcardRepository();
        List<Flashcard> cards = repo.findAll();
        if (cards.isEmpty()) {
            System.out.println("-> Database trống, đang thêm 3 từ mẫu chuẩn thiết kế...");
            repo.insert(new Flashcard("Abundant", "dồi dào, phong phú", "Thường dùng để chỉ số lượng lớn"));
            repo.insert(new Flashcard("Benevolent", "nhân từ, tốt bụng", "Trái nghĩa với malevolent"));
            repo.insert(new Flashcard("Candid", "thẳng thắn, chân thật", "Từ thường gặp trong IELTS"));
            System.out.println("-> Thêm 3 từ mẫu thành công!");
        } else {
            System.out.println("-> Database đã có sẵn " + cards.size() + " flashcards.");
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/fxml/main.fxml");
        if (fxmlLocation == null) {
            System.err.println("LỖI NGHIÊM TRỌNG: Không tìm thấy file /fxml/main.fxml");
            System.exit(1);
        }

        Parent root = FXMLLoader.load(fxmlLocation);
        Scene scene = new Scene(root, 1000, 700);

        URL cssLocation = getClass().getResource("/css/style.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        }

        primaryStage.setTitle("Flash Card DeskApp");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}