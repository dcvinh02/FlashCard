package com.deskapp.flashcard.controller;

import com.deskapp.flashcard.model.Flashcard;
import com.deskapp.flashcard.service.StudyService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;

public class StudyController {

    @FXML private VBox emptyState;
    @FXML private VBox studyState;
    @FXML private VBox studyCard;

    @FXML private Label lblScore;
    @FXML private Label lblQuestionLang;
    @FXML private Label lblQuestionWord;
    @FXML private Label lblAnswerLang;

    @FXML private TextField txtAnswer;
    @FXML private Label lblFeedback;
    @FXML private Button btnSkip;
    @FXML private Button btnCheck;

    private StudyService studyService;
    private List<Flashcard> studyList;
    private Flashcard currentCard;

    private boolean isEngToVie = true;
    private final Random random = new Random();

    @FXML
    public void initialize() {
        studyService = new StudyService();
        loadStudyList();
    }

    private void loadStudyList() {
        studyList = studyService.getCardsToStudy();
        if (studyList.isEmpty()) {
            studyState.setVisible(false);
            emptyState.setVisible(true);
        } else {
            emptyState.setVisible(false);
            studyState.setVisible(true);
            loadRandomCard();
        }
    }

    private void loadRandomCard() {
        studyCard.getStyleClass().removeAll("card-correct", "card-error");

        txtAnswer.clear();
        txtAnswer.getStyleClass().removeAll("input-correct", "input-error");
        txtAnswer.setDisable(false);

        // CHỈ ẨN ĐI (Giữ nguyên không gian), không rút thẻ ra khỏi bố cục nữa
        lblFeedback.setVisible(false);
        lblFeedback.setText("");

        btnSkip.setDisable(false);
        btnCheck.setDisable(false);
        txtAnswer.requestFocus();

        if (studyList.size() > 1) {
            Flashcard nextCard;
            do {
                nextCard = studyList.get(random.nextInt(studyList.size()));
            } while (currentCard != null && nextCard.getId() == currentCard.getId());
            currentCard = nextCard;
        } else {
            currentCard = studyList.get(0);
        }

        lblScore.setText(String.valueOf(currentCard.getScore()));

        if (isEngToVie) {
            lblQuestionLang.setText("Tiếng Anh");
            lblQuestionWord.setText(currentCard.getEnglish());
            lblAnswerLang.setText("Nhập nghĩa tiếng Việt");
        } else {
            lblQuestionLang.setText("Tiếng Việt");
            lblQuestionWord.setText(currentCard.getVietnamese());
            lblAnswerLang.setText("Nhập tiếng Anh");
        }
    }

    @FXML
    private void handleSwap() {
        isEngToVie = !isEngToVie;
        loadRandomCard();
    }

    @FXML
    private void handleSkip() {
        studyService.updateScore(currentCard, false);
        loadRandomCard();
    }

    @FXML
    private void handleCheck() {
        String userAnswer = txtAnswer.getText();
        if (userAnswer.trim().isEmpty()) return;

        String expectedAnswer = isEngToVie ? currentCard.getVietnamese() : currentCard.getEnglish();
        boolean isCorrect = studyService.isAnswerCorrect(userAnswer, expectedAnswer);

        txtAnswer.setDisable(true);
        btnSkip.setDisable(true);
        btnCheck.setDisable(true);

        studyService.updateScore(currentCard, isCorrect);
        lblScore.setText(String.valueOf(currentCard.getScore()));

        // Hiện chữ lên (Không làm giật Layout vì chỗ đã được giữ sẵn)
        lblFeedback.setVisible(true);

        PauseTransition pause;

        if (isCorrect) {
            txtAnswer.getStyleClass().add("input-correct");
            studyCard.getStyleClass().add("card-correct");
            lblFeedback.getStyleClass().setAll("feedback-text-correct");

            if (expectedAnswer.contains(",")) {
                lblFeedback.setText("✓ Chính xác! Đáp án đầy đủ: " + expectedAnswer);
            } else {
                lblFeedback.setText("✓ Chính xác!");
            }

            pause = new PauseTransition(Duration.seconds(2));
        } else {
            txtAnswer.getStyleClass().add("input-error");
            studyCard.getStyleClass().add("card-error");
            lblFeedback.getStyleClass().setAll("feedback-text-error");
            lblFeedback.setText("✗ Sai rồi. Đáp án đúng: " + expectedAnswer);
            pause = new PauseTransition(Duration.seconds(5));
        }

        pause.setOnFinished(e -> loadRandomCard());
        pause.play();
    }
}