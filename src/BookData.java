import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.*;

public class BookData implements Serializable {
    private Map<String, Book> booksByISBN = new HashMap<>(); //Added to manage books
    public Set<String> categories = new HashSet<>(); // Added to manage categories

    //Add Book function
    public boolean addBook(Book book) {
        if (booksByISBN.containsKey(book.getIsbn())) {
            return false; // Book already exists
        }
        booksByISBN.put(book.getIsbn(), book);
        categories.add(book.getCategory()); // Also add the book's category to the set of categories
        return true;
    }
    
    //Delete book function
    public boolean deleteBook(String isbn, RentData rentData) {
        if (!booksByISBN.containsKey(isbn)) {
            System.out.println("Book with ISBN " + isbn + " does not exist.");
            return false;
        }

        // Remove the book
        booksByISBN.remove(isbn);
        System.out.println("Book with ISBN " + isbn + " was successfully deleted.");

        // Notify RentData to remove any rents associated with this book
        rentData.removeRentsByISBN(isbn);
        
        return true;
    }

    public Book getBookByISBN(String isbn) {
        return booksByISBN.get(isbn);
    }

    public boolean deleteCategory(String category) {
        if (!categories.contains(category)) {
            System.out.println("Category does not exist.");
            return false;
        }

        // Remove all books associated with this category
        booksByISBN.entrySet().removeIf(entry -> entry.getValue().getCategory().equals(category));

        // Remove the category itself
        categories.remove(category);
        
        return true;
    }
    
    //Update Book Method
    public void updateBook(String isbn, Book updatedBook) {
        // Check if the book with the given ISBN exists
        if (booksByISBN.containsKey(isbn)) {
            // Update the book in the map
            booksByISBN.put(isbn, updatedBook);
        } else {
            // handle the case where the book does not exist
            System.out.println("Book with ISBN " + isbn + " not found.");
        }
    }
    
    public void updateCategory(String oldCategory, String newCategory) {
    	boolean categoryExists = false;
        oldCategory = oldCategory.trim();
        newCategory = newCategory.trim();
        
        //Change the category name in books
        for (Book book : booksByISBN.values()) {
            // Use trim() to remove whitespace and equalsIgnoreCase for case-insensitive comparison
            if (book.getCategory().trim().equalsIgnoreCase(oldCategory)) {
                book.setCategory(newCategory);
                categoryExists = true;
            }
        }
        
        // Check if the old category exists
        // Change category name in data 
        if (categories.contains(oldCategory)) {
            // Remove the old category
            categories.remove(oldCategory);
            // Add the new category
            categories.add(newCategory);
            System.out.println("Category updated successfully from '" + oldCategory + "' to '" + newCategory + "'");
        } else {
            System.out.println("Category not found. Searched for: '" + oldCategory + "'");
        }

        if (!categoryExists) {
            System.out.println("Category not found in book data. Searched for: '" + oldCategory + "'");
        } else {
            System.out.println("Category in book data updated successfully to: '" + newCategory + "'");
        }
    }
    
    //method to get all books
    public List<Book> getAllBooks() {
        return new ArrayList<>(booksByISBN.values()); // Return a list of all book values in the map
    }


    public void saveBooksToFile(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(new Object[]{booksByISBN, categories}); // Save both books and categories
        } catch (IOException e) {
            System.err.println("Error saving books and categories: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadBooksFromFile(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            Object[] data = (Object[]) in.readObject(); // Expect an array because we're saving an array
            if (data.length == 2) { // Make sure the array has two elements
                booksByISBN = (Map<String, Book>) data[0];
                categories = (Set<String>) data[1];
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing book data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading books and categories: " + e.getMessage());
        }
    }
    
    // Utility methods for categories (you can add these as needed)
    public Set<String> getCategories() {
        return categories;
    }

    public void addCategory(String category) {
        categories.add(category);
    }
}
