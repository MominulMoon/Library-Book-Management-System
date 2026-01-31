package library.lms;

import java.time.LocalDate;

public class IssuedBook {
    private String id;
    private String bookTitle;
    private String studentName;
    private LocalDate issueDate;

    public IssuedBook(String id, String bookTitle, String studentName, LocalDate issueDate) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.studentName = studentName;
        this.issueDate = issueDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
}
