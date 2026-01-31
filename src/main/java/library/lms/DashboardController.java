package library.lms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class DashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox dashboardHome;

    @FXML
    private Label totalBooksLabel; // Placeholder if you bind these later
    @FXML
    private Label totalStudentsLabel;
    @FXML
    private Label issuedBooksLabel;

    @FXML
    private VBox chartContainer;

    // Dummy Data Lists
    private ObservableList<Book> bookList;
    private ObservableList<Student> studentList;
    private ObservableList<IssuedBook> issuedBookList;

    @FXML
    public void initialize() {
        // Initialize Dummy Data
        // Initialize Dummy Data or Load from Storage
        java.util.List<Book> loadedBooks = Storage.loadBooks();
        if (loadedBooks.isEmpty()) {
            bookList = FXCollections.observableArrayList(
                    new Book("B001", "The Great Gatsby", "F. Scott Fitzgerald", "Available"),
                    new Book("B002", "1984", "George Orwell", "Available"),
                    new Book("B003", "To Kill a Mockingbird", "Harper Lee", "Issued"),
                    new Book("B004", "Pride and Prejudice", "Jane Austen", "Available"),
                    new Book("B005", "The Catcher in the Rye", "J.D. Salinger", "Available"));
            Storage.saveBooks(bookList);
        } else {
            bookList = FXCollections.observableArrayList(loadedBooks);
        }

        studentList = FXCollections.observableArrayList(
                new Student("S001", "Alice Smith", "CSE"),
                new Student("S002", "Bob Jones", "EEE"),
                new Student("S003", "Charlie Brown", "ME"),
                new Student("S004", "Diana Prince", "CSE"));

        issuedBookList = FXCollections.observableArrayList(
                new IssuedBook("I001", "To Kill a Mockingbird", "Bob Jones", LocalDate.now().minusDays(5)));

        // Show Dashboard by default
        showDashboard(null);
    }

    @FXML
    private void showDashboard(ActionEvent event) {
        // Refresh Stats
        refreshStats();

        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardHome);

        // Update stats (simple count)
        // Note: In a real app, bind these or update dynamically
    }

    private void refreshStats() {
        // Simple counts
        int totalBooks = bookList.size();
        int totalStudents = studentList.size();
        long issuedCount = bookList.stream().filter(b -> "Issued".equalsIgnoreCase(b.getStatus())).count();

        totalBooksLabel.setText(String.valueOf(totalBooks));
        totalStudentsLabel.setText(String.valueOf(totalStudents));
        issuedBooksLabel.setText(String.valueOf(issuedCount));

        // Create/Update Chart
        long availableCount = totalBooks - issuedCount;

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Available", availableCount),
                new PieChart.Data("Issued", issuedCount));

        PieChart chart = new PieChart(pieData);
        chart.setTitle("Books Status");
        chart.setLabelsVisible(true);
        chart.setLegendVisible(true);

        // Clear previous chart if any and add new one
        // Keeping the title label if it exists in FXML
        if (chartContainer.getChildren().size() > 1) {
            chartContainer.getChildren().remove(1); // Remove old chart, keep title
        } else if (chartContainer.getChildren().isEmpty()) {
            // If totally empty, just add
        }

        // Alternatively, just clear all except title or rebuild
        Label chartTitle = (Label) chartContainer.getChildren().get(0); // Assuming first child is Label
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(chartTitle);
        chartContainer.getChildren().add(chart);
    }

    @FXML
    private void showBooks(ActionEvent event) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(createBookView());
    }

    @FXML
    private void showStudents(ActionEvent event) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(createStudentView());
    }

    @FXML
    private void showIssuedBooks(ActionEvent event) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(createIssuedBooksView());
    }

    // --- VIEW GENERATORS ---

    private VBox createBookView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));

        Label title = new Label("Books Management");
        title.getStyleClass().add("page-title");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Title, Author, or ID...");

        // Wrap the ObservableList in a FilteredList (initially display all data)
        FilteredList<Book> filteredData = new FilteredList<>(bookList, p -> true);

        // Set the filter Predicate whenever the filter changes.
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                // If filter text is empty, display all books.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (book.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches title.
                } else if (book.getAuthor().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches author.
                } else if (book.getId().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches ID.
                }
                return false; // Does not match.
            });
        });

        // Wrap the FilteredList in a SortedList.
        // This is so the TableView sorts the FilteredList, not the original list.
        SortedList<Book> sortedData = new SortedList<>(filteredData);

        TableView<Book> table = new TableView<>();

        // Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Book, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, titleCol, authorCol, statusCol);

        // Buttons
        HBox buttonBox = new HBox(10);

        Button addBtn = new Button("Add Book");
        addBtn.setOnAction(e -> handleAddBook());

        Button issueBtn = new Button("Issue Selected Book");
        issueBtn.setOnAction(e -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "No book selected.");
                return;
            }
            if ("Issued".equals(selected.getStatus())) {
                showAlert("Error", "This book is already issued.");
                return;
            }
            handleIssueBook(selected);
        });

        buttonBox.getChildren().addAll(addBtn, issueBtn);

        layout.getChildren().addAll(title, searchField, buttonBox, table);
        return layout;
    }

    private VBox createStudentView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));

        Label title = new Label("Student Management");
        title.getStyleClass().add("page-title");

        TableView<Student> table = new TableView<>();
        table.setItems(studentList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Student, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        table.getColumns().addAll(idCol, nameCol, deptCol);

        layout.getChildren().addAll(title, table);
        return layout;
    }

    private VBox createIssuedBooksView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));

        Label title = new Label("Issued Books");
        title.getStyleClass().add("page-title");

        TableView<IssuedBook> table = new TableView<>();
        table.setItems(issuedBookList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<IssuedBook, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<IssuedBook, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));

        TableColumn<IssuedBook, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        TableColumn<IssuedBook, LocalDate> dateCol = new TableColumn<>("Issue Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("issueDate"));

        table.getColumns().addAll(idCol, bookCol, studentCol, dateCol);

        layout.getChildren().addAll(title, table);
        return layout;
    }

    // --- LOGIC ---

    private void handleAddBook() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter Book Details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField idField = new TextField();
        idField.setPromptText("Book ID");
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        layout.getChildren().addAll(
                new Label("Book ID:"), idField,
                new Label("Title:"), titleField,
                new Label("Author:"), authorField);

        dialog.getDialogPane().setContent(layout);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                if (idField.getText().isEmpty() || titleField.getText().isEmpty()) {
                    return null;
                }
                return new Book(idField.getText(), titleField.getText(), authorField.getText(), "Available");
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();

        result.ifPresent(book -> {
            bookList.add(book);
            Storage.saveBooks(bookList);
            showAlert("Success", "Book added successfully!");
        });
    }

    private void handleIssueBook(Book book) {
        // Simple Dialog to select a student ID
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Issue Book");
        dialog.setHeaderText("Issuing: " + book.getTitle());
        dialog.setContentText("Enter Student ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(studentId -> {
            // Find student
            Student foundStudent = studentList.stream()
                    .filter(s -> s.getId().equalsIgnoreCase(studentId))
                    .findFirst()
                    .orElse(null);

            if (foundStudent == null) {
                showAlert("Error", "Student not found!");
            } else {
                // Issue the book
                book.setStatus("Issued");
                bookList.set(bookList.indexOf(book), book); // Trigger update
                Storage.saveBooks(bookList);

                IssuedBook record = new IssuedBook(
                        "I" + (issuedBookList.size() + 1),
                        book.getTitle(),
                        foundStudent.getName(),
                        LocalDate.now());
                issuedBookList.add(record);

                showAlert("Success", "Book issued successfully to " + foundStudent.getName());
                showBooks(null); // Refresh view
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) contentArea.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login Page");
        stage.show();
    }
}
