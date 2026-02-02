package model;

import java.util.Objects;

public final class Book {
    private final String id;
    private final String title;
    private final BookStatus status;

    public Book(String id, String title, BookStatus status) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = Objects.requireNonNull(title, "title");
        this.status = Objects.requireNonNull(status, "status");
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BookStatus getStatus() {
        return status;
    }
}

