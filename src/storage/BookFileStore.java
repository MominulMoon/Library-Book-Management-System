package storage;

import model.Book;
import model.BookStatus;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple line-based storage format:
 * id|title|status
 * Example:
 * B001|Clean Code|AVAILABLE
 */
public final class BookFileStore {
    private final Path filePath;

    public BookFileStore(Path filePath) {
        this.filePath = filePath;
    }

    public Path getFilePath() {
        return filePath;
    }

    public List<Book> loadAll() throws IOException {
        ensureExists();
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        List<Book> books = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line == null ? "" : line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;

            String[] parts = trimmed.split("\\|", -1);
            if (parts.length < 3) continue; // skip invalid lines

            String id = parts[0].trim();
            String title = parts[1].trim();
            String statusRaw = parts[2].trim().toUpperCase();
            if (id.isEmpty() || title.isEmpty() || statusRaw.isEmpty()) continue;

            BookStatus status;
            try {
                status = BookStatus.valueOf(statusRaw);
            } catch (IllegalArgumentException ex) {
                continue;
            }
            books.add(new Book(id, title, status));
        }
        return books;
    }

    public void append(Book book) throws IOException {
        ensureExists();
        try (BufferedWriter w = Files.newBufferedWriter(
                filePath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND
        )) {
            w.write(book.getId() + "|" + book.getTitle() + "|" + book.getStatus().name());
            w.newLine();
        }
    }

    private void ensureExists() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) Files.createDirectories(parent);
        if (!Files.exists(filePath)) Files.createFile(filePath);
    }
}

