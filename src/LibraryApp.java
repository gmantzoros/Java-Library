import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibraryApp extends Application {

    private UserData userData;
    private BookData bookData;
    private RentData rentData;
    private ReviewData reviewData;
    private final String filename1 = "medialab/users.ser";
    private final String filename2 = "medialab/books.ser";
    private final String filename3 = "medialab/rents.ser";
    private final String filename4 = "medialab/reviews.ser";

    @Override
    public void start(Stage primaryStage) {
    	//Initialize Variables
        userData = new UserData();
        bookData = new BookData();
        rentData = new RentData();
        reviewData = new ReviewData();
        // Load existing user data
        userData.loadUsersFromFile(filename1);
        bookData.loadBooksFromFile(filename2);
        rentData.loadRentsFromFile(filename3);
        reviewData.loadReviewsFromFile(filename4);

        primaryStage.setTitle("NTUA Library");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label userName = new Label("Username:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        TextField pwBox = new TextField();
        grid.add(pwBox, 1, 2);

        Button btnLogin = new Button("Login");
        Button btnRegister = new Button("Register");
        grid.add(btnLogin, 1, 3);
        grid.add(btnRegister, 1, 4);

        btnLogin.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            
            // Check if the credentials are for the admin
            if (username.equals("medialab") && password.equals("medialab_2024")) {
                // Show the admin main window
                showAdminMainWindow(primaryStage);
            } else {
                // Use the authenticate method to check credentials for a normal user
                boolean isAuthenticated = userData.authenticate(username, password);

                if (isAuthenticated) {
                    // Authentication successful, get the User object
                    User loggedInUser = userData.getUserByUsername(username);
                    // Show the normal user main window
                    showLibraryMainWindow(loggedInUser, primaryStage);
                } else {
                    // Show login failed message for normal users
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Login failed. Please check your credentials.", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

        btnRegister.setOnAction(e -> showRegistrationForm(primaryStage));

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            // Save user data before exiting
            userData.saveUsersToFile(filename1);
        });
    }
    
    //Register Form
    private void showRegistrationForm(Stage primaryStage) {
        Stage registerStage = new Stage();
        registerStage.setTitle("Register New User");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        TextField nameField = new TextField();
        TextField surnameField = new TextField();
        TextField emailField = new TextField();
        TextField adtField = new TextField();
        TextField usernameField = new TextField();
        TextField passwordField = new TextField();

        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Surname:"), 0, 2);
        grid.add(surnameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("ADT:"), 0, 4);
        grid.add(adtField, 1, 4);
        grid.add(new Label("Username:"), 0, 5);
        grid.add(usernameField, 1, 5);
        grid.add(new Label("Password:"), 0, 6);
        grid.add(passwordField, 1, 6);

        Button btnRegister = new Button("Register");
        grid.add(btnRegister, 1, 7);
        btnRegister.setOnAction(e -> registerUser(
                nameField.getText(), 
                surnameField.getText(), 
                emailField.getText(), 
                adtField.getText(), 
                usernameField.getText(), 
                passwordField.getText(),
                registerStage // Pass the stage to close it upon successful registration
        ));

        Scene scene = new Scene(grid, 400, 450);
        registerStage.setScene(scene);
        registerStage.initOwner(primaryStage); // Set the primary stage as the owner of this dialog
        registerStage.show();
    }


    private void registerUser(String name, String surname, String email, String adt, String username, String password, Stage registerStage) {
        try {
            User newUser = new User(name, surname, email, adt, username, password);
            
            boolean success = userData.addUser(newUser);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration successful!", ButtonType.OK);
                alert.showAndWait();
                registerStage.close(); // Close the registration form upon successful registration
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Registration failed. Username already exists.", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid ADT. Please enter a valid number.", ButtonType.OK);
            alert.showAndWait();
        }
    }


    @SuppressWarnings("unused")
	private void loginUser(String username, String password) {
        boolean authenticated = userData.authenticate(username, password);
        if (authenticated) {
            System.out.println("Login successful.");
        } else {
            System.out.println("Login failed. Incorrect username or password.");
        }
    }
    
    //Main user library window
    @SuppressWarnings("unchecked")
	private void showLibraryMainWindow(User user, Stage primaryStage) {
        // Set up the main library window layout
        BorderPane borderPane = new BorderPane();
        //add the tab pane
        TabPane tabPane = new TabPane();
        
        //Rent Tab
        Tab rentTab = new Tab("Rent");
        VBox layoutBook = new VBox(10);
        layoutBook.setPadding(new Insets(10, 10, 10, 10));
        ObservableList<Book> booksObservableList = FXCollections.observableArrayList(bookData.getAllBooks());
        
        //Setup the search functionality
        // Create a FilteredList wrapping the ObservableList
        FilteredList<Book> filteredBooks = new FilteredList<>(booksObservableList, p -> true); // Initially, show all books

        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Title, Author, or Year...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredBooks.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all books if search query is empty
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (book.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (book.getAuthor().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return String.valueOf(book.getYear()).contains(lowerCaseFilter);
            });
        });
        
        TableView<Book> booksTableView = new TableView<>();

        // Define columns for the book attributes
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> publisherColumn = new TableColumn<>("Publisher");
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Book, Integer> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Book, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Book, Integer> copiesColumn = new TableColumn<>("Copies");
        copiesColumn.setCellValueFactory(new PropertyValueFactory<>("copies"));

        TableColumn<Book, String> starsColumn = new TableColumn<>("Stars");
        starsColumn.setCellValueFactory(cellData -> {
            Book book = cellData.getValue();
            float stars = book.getStars(reviewData); // Dynamically calculate stars based on reviews
            // Return a SimpleStringProperty with formatted stars value
            return new SimpleStringProperty(String.format("%.2f", stars));
        });

        // Add columns to the TableView
        booksTableView.getColumns().add(titleColumn);
        booksTableView.getColumns().add(authorColumn);
        booksTableView.getColumns().add(publisherColumn);
        booksTableView.getColumns().add(isbnColumn);
        booksTableView.getColumns().add(yearColumn);
        booksTableView.getColumns().add(categoryColumn);
        booksTableView.getColumns().add(copiesColumn);
        booksTableView.getColumns().add(starsColumn);

        // Set the items for the TableView
        booksTableView.setItems(filteredBooks);

        // Buttons for managing categories
        Button rentButtonBook = new Button("Rent Selected Book");
        rentButtonBook.setDisable(true); // Initially disabled
        //Enable rent button only when a book is selected
        booksTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            rentButtonBook.setDisable(newSelection == null);
        });
        //rent button functionality
        rentButtonBook.setOnAction(e -> {
            Book selectedBook = booksTableView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                //rent book to user in session
                boolean success = rentData.rentBook(selectedBook.getIsbn(), user.getUsername(), bookData);
                
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Book rented successfully!");
                    rentData.saveRentsToFile(filename3); //save added rent
                    alert.showAndWait();
                    
                    // Refresh the books list to show the updated copies count
                    booksObservableList.setAll(bookData.getAllBooks());
                    booksTableView.refresh();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to rent the book. It may no longer be available.");
                    alert.showAndWait();
                }
            }
        });
        
        HBox buttonLayoutBook = new HBox(10, rentButtonBook);
        buttonLayoutBook.setAlignment(Pos.CENTER);
        
        // Add components to the layout
        layoutBook.getChildren().addAll(searchField, booksTableView, buttonLayoutBook);
        // Set the layout as the content of categoriesTab
        rentTab.setContent(layoutBook);
        //End of Rent Tab
        
        // Return Tab
        Tab returnTab = new Tab("Return");
        VBox layoutReturn = new VBox(10);
        layoutReturn.setPadding(new Insets(10, 10, 10, 10));

        ObservableList<Rent> rentedBooksObservableList = FXCollections.observableArrayList(rentData.getRentedBooksForUser(user.getUsername()));

        TableView<Rent> rentedBooksTableView = new TableView<>();
        rentedBooksTableView.setItems(rentedBooksObservableList);

        // Define columns
        TableColumn<Rent, String> titleColumnReturn = new TableColumn<>("Title");
        titleColumnReturn.setCellValueFactory(cellData -> {
            String isbn = cellData.getValue().getIsbn();
            Book book = bookData.getBookByISBN(isbn);
            return new SimpleStringProperty(book != null ? book.getTitle() : "Unknown");
        });

        TableColumn<Rent, String> authorColumnReturn = new TableColumn<>("Author");
        authorColumnReturn.setCellValueFactory(cellData -> {
            String isbn = cellData.getValue().getIsbn();
            Book book = bookData.getBookByISBN(isbn);
            return new SimpleStringProperty(book != null ? book.getAuthor() : "Unknown");
        });

        TableColumn<Rent, LocalDate> rentDateColumn = new TableColumn<>("Rent Date");
        rentDateColumn.setCellValueFactory(new PropertyValueFactory<>("rentDate"));

        TableColumn<Rent, LocalDate> returnDateColumn = new TableColumn<>("Return Date");
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        TableColumn<Rent, String> isbnColumnReturn = new TableColumn<>("ISBN");
        isbnColumnReturn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        // Add columns to the TableView
        rentedBooksTableView.getColumns().addAll(titleColumnReturn, authorColumnReturn, isbnColumnReturn, rentDateColumn, returnDateColumn);

        // "Return Selected Book" Button
        Button returnButton = new Button("Return Selected Book");
        returnButton.setDisable(true);
        rentedBooksTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            returnButton.setDisable(newSelection == null);
        });
        
        //Leave a review on selected book button
        Button leaveReviewButton = new Button("Review Selected Book");
        leaveReviewButton.setDisable(true); // Initially disabled, enabled only when a book is selected
        //Enable Button when book is selected
        rentedBooksTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isBookSelected = newSelection != null;
            leaveReviewButton.setDisable(!isBookSelected);
            // Keep the return button logic as is
        });

        // Implement return button functionality
        returnButton.setOnAction(e -> {
            Rent selectedRent = rentedBooksTableView.getSelectionModel().getSelectedItem();
            if (selectedRent != null) {
                boolean success = rentData.returnBook(selectedRent.getIsbn(), user.getUsername(), bookData);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Book returned successfully!");
                    alert.showAndWait();
                    rentedBooksObservableList.setAll(rentData.getRentedBooksForUser(user.getUsername()));
                    rentedBooksTableView.refresh();
                    rentData.saveRentsToFile(filename3); //save removed rent
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to return the book.");
                    alert.showAndWait();
                }
            }
        });

        HBox buttonLayoutReturn = new HBox(10, returnButton, leaveReviewButton);
        buttonLayoutReturn.setAlignment(Pos.CENTER);

        layoutReturn.getChildren().addAll(rentedBooksTableView, buttonLayoutReturn);
        returnTab.setContent(layoutReturn);
        
        //Implement Logic for review Button
        leaveReviewButton.setOnAction(e -> {
            Rent selectedRent = rentedBooksTableView.getSelectionModel().getSelectedItem();
            if (selectedRent != null) {
                // Show a dialog to input review text and stars
                Dialog<Review> dialog = new Dialog<>();
                dialog.setTitle("Leave a Review");
                dialog.setHeaderText("Review for " + selectedRent.getIsbn());

                // Set the button types
                ButtonType submitButtonType = new ButtonType("Submit", ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

                // Create fields for review input
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField starsField = new TextField();
                starsField.setPromptText("Stars");
                TextArea reviewTextArea = new TextArea();
                reviewTextArea.setPromptText("Review");

                grid.add(new Label("Stars (1-5):"), 0, 0);
                grid.add(starsField, 1, 0);
                grid.add(new Label("Review:"), 0, 1);
                grid.add(reviewTextArea, 1, 1);

                dialog.getDialogPane().setContent(grid);

                // Convert the result to a Review when the submit button is clicked
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == submitButtonType) {
                        try {
                            int stars = Integer.parseInt(starsField.getText());
                            String reviewText = reviewTextArea.getText();
                            return new Review(user.getUsername(), selectedRent.getIsbn(), stars, reviewText);
                        } catch (NumberFormatException ex) {
                            // Handle invalid number input
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Stars must be a number between 1 and 5.");
                            alert.showAndWait();
                            return null;
                        }
                    }
                    return null;
                });

                Optional<Review> result = dialog.showAndWait();
                result.ifPresent(review -> {
                    reviewData.addReview(review);
                    reviewData.saveReviewsToFile(filename4);
                    
                    // Recalculate the book's stars after the new review
                    Book reviewedBook = bookData.getBookByISBN(review.getIsbn()); // Find the book
                    if (reviewedBook != null) {
                        float newStars = reviewedBook.getStars(reviewData); // Recalculate stars
                        
                        // Update the book rating
                        reviewedBook.setStars(newStars); 
                        bookData.saveBooksToFile(filename2); //save the book rating in file
                        
                        // Update the UI
                        booksObservableList.setAll(bookData.getAllBooks()); // Refresh the list showing in the UI
                        booksTableView.refresh();
                    }

                    // Confirm Review
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Review submitted successfully!");
                    alert.showAndWait();
                });
            }
        });
        //End of Return Tab

        // Adding tabs to the TabPane
        tabPane.getTabs().addAll(rentTab, returnTab);

        // Prevent tabs from being closed by the user
        tabPane.getTabs().forEach(tab -> tab.setClosable(false));

        borderPane.setCenter(tabPane);
        
        //Refresh Tabs when clicked so they always stay up to date
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab == rentTab) {
                // Refresh the books list in the Rent tab
                booksObservableList.setAll(bookData.getAllBooks());
                booksTableView.refresh();
            } else if (newTab == returnTab) {
                // Refresh the rented books list in the Return tab
                rentedBooksObservableList.setAll(rentData.getRentedBooksForUser(user.getUsername()));
                rentedBooksTableView.refresh();
            }
        });

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("User Dashboard");
        primaryStage.show();
    }
    
    //Main admin library window
    @SuppressWarnings("unchecked")
	private void showAdminMainWindow(Stage primaryStage) {
        // Set up the admin main window layout
        BorderPane borderPane = new BorderPane();
        //add the tab pane
        TabPane tabPane = new TabPane();

        // Books Tab
        Tab booksTab = new Tab("Books");
        VBox layoutBook = new VBox(10);
        layoutBook.setPadding(new Insets(10, 10, 10, 10));
        ObservableList<Book> booksObservableList = FXCollections.observableArrayList(bookData.getAllBooks());
        TableView<Book> booksTableView = new TableView<>();

        // Define columns for the book attributes
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> publisherColumn = new TableColumn<>("Publisher");
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Book, Integer> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Book, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Book, Integer> copiesColumn = new TableColumn<>("Copies");
        copiesColumn.setCellValueFactory(new PropertyValueFactory<>("copies"));

        TableColumn<Book, String> starsColumn = new TableColumn<>("Stars");
        starsColumn.setCellValueFactory(cellData -> {
            Book book = cellData.getValue();
            float stars = book.getStars(reviewData); // Dynamically calculate stars based on reviews
            // Return a SimpleStringProperty with formatted stars value
            return new SimpleStringProperty(String.format("%.2f", stars));
        });

        // Add columns to the TableView
        booksTableView.getColumns().add(titleColumn);
        booksTableView.getColumns().add(authorColumn);
        booksTableView.getColumns().add(publisherColumn);
        booksTableView.getColumns().add(isbnColumn);
        booksTableView.getColumns().add(yearColumn);
        booksTableView.getColumns().add(categoryColumn);
        booksTableView.getColumns().add(copiesColumn);
        booksTableView.getColumns().add(starsColumn);

        // Set the items for the TableView
        booksTableView.setItems(booksObservableList);

        // Buttons for managing categories
        Button addButtonBook = new Button("Add Book");
        Button modifyButtonBook = new Button("Modify Selected Book");
        Button deleteButtonBook = new Button("Delete Selected Book");
        HBox buttonLayoutBook = new HBox(10, addButtonBook, deleteButtonBook, modifyButtonBook);
        buttonLayoutBook.setAlignment(Pos.CENTER);
        
        // Add components to the layout
        layoutBook.getChildren().addAll(booksTableView, buttonLayoutBook);
        
        //Add book button functionality
        addButtonBook.setOnAction(e -> {
            Dialog<Book> dialog = new Dialog<>();
            dialog.setTitle("Add New Book");
            dialog.setHeaderText("Enter Book Details");
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Fields for book details
            TextField titleField = new TextField();
            TextField authorField = new TextField();
            TextField publisherField = new TextField();
            TextField isbnField = new TextField();
            TextField yearField = new TextField();
            TextField copiesField = new TextField();
            
            // Fetch categories dynamically from BookData
            Set<String> categoriesSet = bookData.getCategories();
            ObservableList<String> categories = FXCollections.observableArrayList(categoriesSet);
            ComboBox<String> categoryComboBox = new ComboBox<>(categories);

            // GridPane for layout
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("Title:"), 0, 0);
            grid.add(titleField, 1, 0);
            grid.add(new Label("Author:"), 0, 1);
            grid.add(authorField, 1, 1);
            grid.add(new Label("Publisher:"), 0, 2);
            grid.add(publisherField, 1, 2);
            grid.add(new Label("ISBN:"), 0, 3);
            grid.add(isbnField, 1, 3);
            grid.add(new Label("Year:"), 0, 4);
            grid.add(yearField, 1, 4);
            grid.add(new Label("Copies:"), 0, 5);
            grid.add(copiesField, 1, 5);
            grid.add(new Label("Category:"), 0, 6);
            grid.add(categoryComboBox, 1, 6);
            dialogPane.setContent(grid);

            // Convert the result to a Book object when the OK button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    try {
                        // Create new Book object with the provided details
                        return new Book(
                            titleField.getText(),
                            authorField.getText(),
                            publisherField.getText(),
                            isbnField.getText(),
                            Integer.parseInt(yearField.getText()),
                            categoryComboBox.getValue(),
                            Integer.parseInt(copiesField.getText())
                        );
                    } catch (NumberFormatException ex) {
                        // Handle invalid numerical input
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Year and Copies must be valid numbers.");
                        alert.showAndWait();
                        return null;
                    }
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<Book> result = dialog.showAndWait();
            result.ifPresent(book -> {
                bookData.addBook(book); // Add the book to BookData
                bookData.saveBooksToFile(filename2); //save added book
                booksObservableList.add(book); // Add the book to the ObservableList so it updates
            });
        });
        
        //Delete book functionality
        deleteButtonBook.setOnAction(e -> {
            Book selectedBook = booksTableView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                // Confirm deletion with the user
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this book?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    // Remove the book from the data model and the TableView
                    bookData.deleteBook(selectedBook.getIsbn(), rentData); // Assuming removeBook method exists and uses ISBN as key
                    booksObservableList.remove(selectedBook); // Remove from the ObservableList backing the TableView
                    bookData.saveBooksToFile(filename2);
                }
            } else {
                // No book selected
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a book to delete.");
                alert.showAndWait();
            }
        });
        
      //Modify book functionality
        modifyButtonBook.setOnAction(e -> {
            Book selectedBook = booksTableView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                Dialog<Book> dialog = new Dialog<>();
                dialog.setTitle("Modify Book");
                dialog.setHeaderText("Edit Book Details");
                ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField titleField = new TextField(selectedBook.getTitle());
                TextField authorField = new TextField(selectedBook.getAuthor());
                TextField publisherField = new TextField(selectedBook.getPublisher());
                TextField isbnField = new TextField(selectedBook.getIsbn());
                isbnField.setEditable(false); // ISBN should not be editable
                TextField yearField = new TextField(String.valueOf(selectedBook.getYear()));
                TextField copiesField = new TextField(String.valueOf(selectedBook.getCopies()));

                ObservableList<String> categories = FXCollections.observableArrayList(bookData.getCategories());
                ComboBox<String> categoryComboBox = new ComboBox<>(categories);
                categoryComboBox.setValue(selectedBook.getCategory());

                grid.add(new Label("Title:"), 0, 0);
                grid.add(titleField, 1, 0);
                grid.add(new Label("Author:"), 0, 1);
                grid.add(authorField, 1, 1);
                grid.add(new Label("Publisher:"), 0, 2);
                grid.add(publisherField, 1, 2);
                grid.add(new Label("ISBN:"), 0, 3);
                grid.add(isbnField, 1, 3);
                grid.add(new Label("Year:"), 0, 4);
                grid.add(yearField, 1, 4);
                grid.add(new Label("Copies:"), 0, 5);
                grid.add(copiesField, 1, 5);
                grid.add(new Label("Category:"), 0, 6);
                grid.add(categoryComboBox, 1, 6);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == saveButtonType) {
                        try {
                            selectedBook.setTitle(titleField.getText());
                            selectedBook.setAuthor(authorField.getText());
                            selectedBook.setPublisher(publisherField.getText());
                            selectedBook.setYear(Integer.parseInt(yearField.getText()));
                            selectedBook.setCopies(Integer.parseInt(copiesField.getText()));
                            selectedBook.setCategory(categoryComboBox.getValue());
                            return selectedBook;
                        } catch (NumberFormatException ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numbers for year and copies.");
                            alert.showAndWait();
                            return null;
                        }
                    }
                    return null;
                });

                Optional<Book> result = dialog.showAndWait();

                result.ifPresent(book -> {
                    bookData.updateBook(book.getIsbn(), book); // Update the book details in your data model
                    booksTableView.refresh(); // Refresh the TableView to show the updated book details
                    bookData.saveBooksToFile(filename2); //save added book
                });
            }
        });


        // Set the layout as the content of categoriesTab
        booksTab.setContent(layoutBook);
        // End of Books tab

        // Categories Tab
        Tab categoriesTab = new Tab("Categories");
        // Create the layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));

        // ListView for displaying categories
        ListView<String> categoriesListView = new ListView<>();
        categoriesListView.getItems().addAll(bookData.getCategories()); // Populate ListView

        // Buttons for managing categories
        Button addButtonCat = new Button("Add Category");
        Button modifyButtonCat = new Button("Modify Selected Category");
        Button deleteButtonCat = new Button("Delete Selected Category");
        HBox buttonLayoutCat = new HBox(10, addButtonCat, deleteButtonCat, modifyButtonCat);
        buttonLayoutCat.setAlignment(Pos.CENTER);
        
        //Add functionality to buttons
        //Delete Button
        deleteButtonCat.setOnAction(e -> {
            String selectedCategory = categoriesListView.getSelectionModel().getSelectedItem();
            if (selectedCategory != null && bookData.deleteCategory(selectedCategory)) {
                categoriesListView.getItems().remove(selectedCategory); // Update UI
            } else {
                // Optionally, show an alert if the deletion was unsuccessful
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not delete the selected category.", ButtonType.OK);
                alert.showAndWait();
            }
        });
        //Modify Button
        modifyButtonCat.setOnAction(e -> {
            String selectedCategory = categoriesListView.getSelectionModel().getSelectedItem();
            if (selectedCategory != null) {
                TextInputDialog dialog = new TextInputDialog(selectedCategory);
                dialog.setTitle("Update Category");
                dialog.setHeaderText("Update the selected category:");
                dialog.setContentText("Enter new category name:");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(newCategory -> {
                    if (!newCategory.trim().isEmpty() && !selectedCategory.equals(newCategory)) {
                    	//Update the data in file
                        bookData.updateCategory(selectedCategory, newCategory);
                        bookData.saveBooksToFile(filename2);
                        // Update the ListView
                        categoriesListView.getItems().remove(selectedCategory);
                        categoriesListView.getItems().add(newCategory);
                    }
                });
            } else {
                // Optionally, show an alert if no category is selected
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a category to update.", ButtonType.OK);
                alert.showAndWait();
            }
        });
        //Add Button
        addButtonCat.setOnAction(e -> {
            // Create a dialog to enter the new category name
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add New Category");
            dialog.setHeaderText("Add a new book category:");
            dialog.setContentText("Category name:");

            // Traditional way to get the response value.
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                if (!name.trim().isEmpty() && !bookData.getCategories().contains(name)) {
                    bookData.addCategory(name); // Add the category to your data model
                    categoriesListView.getItems().add(name); // Update UI to reflect the new category
                    bookData.saveBooksToFile(filename2);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid category name or it already exists.", ButtonType.OK);
                    alert.showAndWait();
                }
            });
        });


        // Add components to the layout
        layout.getChildren().addAll(categoriesListView, buttonLayoutCat);

        // Set the layout as the content of categoriesTab
        categoriesTab.setContent(layout);
        //End of Categories Tab
        
        // Users Tab
        // userData initialized and loaded from file
        ObservableList<User> usersList = FXCollections.observableArrayList(userData.loadUsers(filename1));

        // Create TableView and define columns
        TableView<User> usersTable = new TableView<>();
        usersTable.setItems(usersList);

        // Add columns for each user attribute
        TableColumn<User, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> surnameColumn = new TableColumn<>("Surname");
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> adtColumn = new TableColumn<>("ADT");
        adtColumn.setCellValueFactory(new PropertyValueFactory<>("ADT"));

        // Add all columns to the table
        usersTable.getColumns().addAll(nameColumn, surnameColumn, emailColumn, adtColumn);

        // Initialize buttons for user actions
        Button modifyButton = new Button("Modify Selected User");
        Button deleteButton = new Button("Delete Selected User");
        
        // Define action for the Modify button
        modifyButton.setOnAction(event -> {
            User selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
            	// Create the custom dialog
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Edit User");
                dialog.setHeaderText("Modify the user details:");

                // Add buttons to the dialog
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                // Create the username and password labels and fields
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField nameField = new TextField();
                nameField.setPromptText("Name");
                nameField.setText(selectedUser.getName());

                TextField surnameField = new TextField();
                surnameField.setPromptText("Surname");
                surnameField.setText(selectedUser.getSurname());

                TextField emailField = new TextField();
                emailField.setPromptText("Email");
                emailField.setText(selectedUser.getEmail());

                TextField adtField = new TextField();
                adtField.setPromptText("ADT");
                adtField.setText(selectedUser.getADT());

                grid.add(new Label("Name:"), 0, 0);
                grid.add(nameField, 1, 0);
                grid.add(new Label("Surname:"), 0, 1);
                grid.add(surnameField, 1, 1);
                grid.add(new Label("Email:"), 0, 2);
                grid.add(emailField, 1, 2);
                grid.add(new Label("ADT:"), 0, 3);
                grid.add(adtField, 1, 3);

                dialog.getDialogPane().setContent(grid);

                // Request focus on the name field by default
                Platform.runLater(nameField::requestFocus);

                // Convert the result to a user when the OK button is clicked
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == ButtonType.OK) {
                        selectedUser.setName(nameField.getText());
                        selectedUser.setSurname(surnameField.getText());
                        selectedUser.setEmail(emailField.getText());
                        selectedUser.setADT(adtField.getText());
                        // Save changes
                        userData.usersByUsername.put(selectedUser.getUsername(), selectedUser); //modify the user
                        userData.saveUsersToFile(filename1); // Assuming saveUsersToFile() handles serialization
                        return null;
                    }
                    return null;
                });

                dialog.showAndWait();

                // Refresh the list to show updated details
                usersList.setAll(userData.loadUsers(filename1)); // Reload the updated users
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Modification Error", ButtonType.OK);
                alert.showAndWait();
            }
        });

        // Define action for the Delete button
        deleteButton.setOnAction(event -> {
            User selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                usersList.remove(selectedUser);
                // Implement the user deletion logic here
                userData.deleteUser(selectedUser.getUsername(), filename1);
                userData.saveUsersToFile(filename1); // Ensure you have a method in userData to handle saving to file
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Deletion Error", ButtonType.OK);
                alert.showAndWait();
            }
        });

        // Layout for buttons
        HBox buttonLayout = new HBox(10, modifyButton, deleteButton);
        buttonLayout.setAlignment(Pos.CENTER);

        // Layout for the Users tab
        VBox usersTabLayout = new VBox(10); // Adjust spacing as needed
        usersTabLayout.getChildren().addAll(usersTable, buttonLayout); // Add buttons if needed

        Tab usersTab = new Tab("Users", usersTabLayout);
        //end of users tab 

        // Rents Tab
        Tab rentsTab = new Tab("Rents");
        // Creating a TableView for displaying rented books
        TableView<Rent> rentsTableView = new TableView<>();
        rentsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Make columns fill available space

        // Define columns for the TableView
        TableColumn<Rent, String> userColumnRent = new TableColumn<>("User");
        userColumnRent.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Rent, String> titleColumnRent = new TableColumn<>("Title");
        titleColumnRent.setCellValueFactory(cellData -> {
            String isbn = cellData.getValue().getIsbn();
            Book book = bookData.getBookByISBN(isbn);
            return new SimpleStringProperty(book != null ? book.getTitle() : "N/A");
        });

        TableColumn<Rent, String> authorColumnRent = new TableColumn<>("Author");
        authorColumnRent.setCellValueFactory(cellData -> {
            String isbn = cellData.getValue().getIsbn();
            Book book = bookData.getBookByISBN(isbn);
            return new SimpleStringProperty(book != null ? book.getAuthor() : "N/A");
        });

        TableColumn<Rent, String> isbnColumnRent = new TableColumn<>("ISBN");
        isbnColumnRent.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Rent, LocalDate> rentalDateColumn = new TableColumn<>("Rental Date");
        rentalDateColumn.setCellValueFactory(new PropertyValueFactory<>("rentDate"));

        // Add columns to the TableView
        rentsTableView.getColumns().addAll(userColumnRent, titleColumnRent, authorColumnRent, isbnColumnRent, rentalDateColumn);

        // Populate the TableView
        ObservableList<Rent> rentalRecords = FXCollections.observableArrayList();
        // Assume getRentedBooks() returns a List<RentalRecord> with all rented books info
        rentalRecords.addAll(rentData.getRentedBooks());
        rentsTableView.setItems(rentalRecords);
        
        //Button to end rent
        Button endRentButton = new Button("End Rent");
        endRentButton.setDisable(true); // Initially disabled until a book is selected
        
        //enable button when a rented book is selected
        rentsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            endRentButton.setDisable(newSelection == null);
        });

        endRentButton.setOnAction(e -> {
            Rent selectedRent = rentsTableView.getSelectionModel().getSelectedItem();
            if (selectedRent != null) {
                // End the rent for the selected book
                boolean success = rentData.removeRentsByISBN(selectedRent.getIsbn());
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Rent ended successfully!");
                    alert.showAndWait();
                    
                    // Refresh the rents list to reflect the change
                    rentData.saveRentsToFile(filename3);
                    rentalRecords.remove(selectedRent); // Remove from the ObservableList
                    rentsTableView.refresh();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to end the rent.");
                    alert.showAndWait();
                }
            }
        });


        // Layout with VBox
        HBox hbox = new HBox(endRentButton);
        hbox.setAlignment(Pos.CENTER); // Center the HBox which contains the button
        VBox vbox = new VBox(10); // 10 is the spacing between the TableView and the button
        vbox.getChildren().addAll(rentsTableView, hbox);

        // Set the VBox as the content of the rents tab
        rentsTab.setContent(vbox);
        // End of Rents Tab
        
        //Reviews Tab
        Tab reviewsTab = new Tab("Reviews");
     // Create a TableView to display reviews
        TableView<Review> reviewsTableView = new TableView<>();
        ObservableList<Review> reviewsObservableList = FXCollections.observableArrayList(reviewData.getAllReviews());

        // Define columns for the TableView
        TableColumn<Review, String> usernameColumnReview = new TableColumn<>("Username");
        usernameColumnReview.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Review, String> isbnColumnReview = new TableColumn<>("ISBN");
        isbnColumnReview.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Review, Integer> starsColumnReview = new TableColumn<>("Stars");
        starsColumnReview.setCellValueFactory(new PropertyValueFactory<>("stars"));

        TableColumn<Review, String> reviewTextColumnReview = new TableColumn<>("Review");
        reviewTextColumnReview.setCellValueFactory(new PropertyValueFactory<>("reviewText"));

        // Add columns to the TableView
        reviewsTableView.getColumns().addAll(usernameColumnReview, isbnColumnReview, starsColumnReview, reviewTextColumnReview);

        // Set the items for the TableView
        reviewsTableView.setItems(reviewsObservableList);

        // Set the TableView as the content of the reviews tab
        reviewsTab.setContent(reviewsTableView);
        //End of reviews tab
        // Adding tabs to the TabPane
        tabPane.getTabs().addAll(booksTab, categoriesTab, usersTab, rentsTab, reviewsTab);

        // Prevent tabs from being closed by the user
        tabPane.getTabs().forEach(tab -> tab.setClosable(false));

        borderPane.setCenter(tabPane);

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
