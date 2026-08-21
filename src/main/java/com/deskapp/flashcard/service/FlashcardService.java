package com.deskapp.flashcard.service;

import com.deskapp.flashcard.model.Flashcard;
import com.deskapp.flashcard.repository.FlashcardRepository;

import java.io.*;
import org.apache.commons.csv.*;
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

    public int importFromCsv(File file) throws Exception {
        int importedCount = 0;

        // Tự động phát hiện ký tự phân cách (, hoặc ;) do Excel trên Mac/Windows quy định
        char delimiter = ',';
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String firstLine = br.readLine();
            if (firstLine != null && firstLine.contains(";") && !firstLine.contains(",")) {
                delimiter = ';';
            }
        }

        // Đọc file với định dạng phân cách đã được xác định tự động
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withDelimiter(delimiter)
                     .withFirstRecordAsHeader()
                     .withTrim())) {

            for (CSVRecord record : csvParser) {
                String eng = record.isMapped("English") ? record.get("English") : null;
                String vie = record.isMapped("Vietnamese") ? record.get("Vietnamese") : null;
                String note = record.isMapped("Note") ? record.get("Note") : "";

                if (eng != null && !eng.isEmpty() && vie != null && !vie.isEmpty()) {
                    Flashcard card = new Flashcard(eng.trim(), vie.trim(), note.trim());
                    if (repository.insert(card)) {
                        importedCount++;
                    }
                }
            }
        }
        return importedCount;
    }

    public void exportToCsv(File file) throws Exception {
        List<Flashcard> cards = getAllCards();
        // Ghi file với mã hóa UTF-8 BOM để Excel có thể đọc tiếng Việt hoàn hảo
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(osw, CSVFormat.DEFAULT.withHeader("English", "Vietnamese", "Note", "Score", "Memorized"))) {

            // Ghi Byte Order Mark (BOM) cho Excel
            fos.write(0xef); fos.write(0xbb); fos.write(0xbf);

            for (Flashcard card : cards) {
                printer.printRecord(
                        card.getEnglish(),
                        card.getVietnamese(),
                        card.getNote(),
                        card.getScore(),
                        card.isMemorized() ? "Yes" : "No"
                );
            }
            printer.flush();
        }
    }
}
