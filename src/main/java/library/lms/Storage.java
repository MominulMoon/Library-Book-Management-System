package library.lms;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private static final String BOOKS_FILE = "books.csv";

    public static List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        File file = new File(BOOKS_FILE);
        // If file doesn't exist, return empty list (caller can populate defaults)
        if (!file.exists()) {
            return books;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Simple CSV parsing
                // Format: ID,Title,Author,Status
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    // Unescape commas if we decided to escape them
                    String id = unescape(parts[0]);
                    String title = unescape(parts[1]);
                    String author = unescape(parts[2]);
                    String status = unescape(parts[3]);
                    books.add(new Book(id, title, author, status));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading books: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    public static void saveBooks(List<Book> books) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : books) {
                String line = String.format("%s,%s,%s,%s",
                        escape(b.getId()),
                        escape(b.getTitle()),
                        escape(b.getAuthor()),
                        escape(b.getStatus()));
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper to replace commas so they don't break CSV split
    private static String escape(String input) {
        if (input == null)
            return "";
        return input.replace(",", ";;");
    }

    private static String unescape(String input) {
        if (input == null)
            return "";
        return input.replace(";;", ",");
    }
}
