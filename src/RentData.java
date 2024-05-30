import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.io.*;
import java.time.LocalDate;

/**
 * Manages rental records for a library system.
 * This class handles adding, removing, and querying rental records for books.
 */
public class RentData implements Serializable {
    private List<Rent> Rents = new ArrayList<>();
    
    /**
     * Adds a new rental record to the system.
     * 
     * @param record The rental record to be added.
     * @return true if the record is successfully added, false otherwise.
     */
    public boolean addRent(Rent record) {
        return Rents.add(record);
    }

    /**
     * Retrieves all rental records.
     * 
     * @return A list of all rental records.
     */
    public List<Rent> getRents() {
        return new ArrayList<>(Rents); // Return a copy to avoid external modifications
    }
    
    // Rent a book
    /**
     * Rents a book to a user.
     * 
     * @param isbn The ISBN of the book to be rented.
     * @param username The username of the user renting the book.
     * @param bookData The data source containing book records.
     * @return true if the book is successfully rented, false otherwise.
     */
    public boolean rentBook(String isbn, String username, BookData bookData) {
        Book book = bookData.getBookByISBN(isbn);
        if (book != null && book.getCopies() > 0) {
            LocalDate rentDate = LocalDate.now();
            LocalDate returnDate = rentDate.plusDays(5);
            Rent rent = new Rent(isbn, username, rentDate, returnDate);
            
            // Update book copies
            book.setCopies(book.getCopies() - 1);
            bookData.saveBooksToFile("medialab/books.ser"); //update the books
            
            // Add rent record
            Rents.add(rent);
            saveRentsToFile("medialab/rents.ser"); //update the rents
            return true;
        }
        return false;
    }
    
    // Return a book
    /**
     * Returns a rented book.
     * 
     * @param isbn The ISBN of the book being returned.
     * @param username The username of the user returning the book.
     * @param bookData The data source containing book records.
     * @return true if the book is successfully returned, false otherwise.
     */
    public boolean returnBook(String isbn, String username, BookData bookData) {
        Iterator<Rent> iterator = Rents.iterator();
        while (iterator.hasNext()) {
            Rent rent = iterator.next();
            if (rent.getIsbn().equals(isbn) && rent.getUsername().equals(username)) {
                // Increment book copies
                Book book = bookData.getBookByISBN(isbn);
                if (book != null) {
                    book.setCopies(book.getCopies() + 1);
                    bookData.saveBooksToFile("medialab/books.ser"); // Save the updated book info
                } else {
                    return false; // Book data not found, cannot proceed
                }

                // Remove the rent record
                iterator.remove();
                saveRentsToFile("medialab/rents.ser"); // Save the updated rents
                return true;
            }
        }
        return false; // Rent record not found
    }
    
    //Return all rented books
    /**
     * Retrieves all rented books.
     * 
     * @return A list of all rented books.
     */
    public List<Rent> getRentedBooks() {
        return new ArrayList<>(Rents); // Return a copy of the rents list to avoid modification
    }
    
    //Find Rents by certain username method
    /**
     * Finds rental records for a specific user.
     * 
     * @param username The username of the user whose rental records are being queried.
     * @return A list of rental records for the specified user.
     */
    public List<Rent> getRentedBooksForUser(String username) {
        // Filter the rents list to find entries matching the given username
        return Rents.stream()
                .filter(rent -> rent.getUsername().equals(username))
                .collect(Collectors.toList());
    }
    
    //Remove Rents by isbn
    /**
     * Removes rental records based on a book's ISBN.
     * 
     * @param isbn The ISBN of the book whose rental records are to be removed.
     * @return true if the records are successfully removed, false otherwise.
     */
    public boolean removeRentsByISBN(String isbn) {
        Rents.removeIf(rent -> rent.getIsbn().equals(isbn));
        saveRentsToFile("medialab/rents.ser"); // Update the rent records file
        return true;
    }


    // Saves the rent records to a file
    /**
     * Saves rental records to a file.
     * 
     * @param filename The name of the file where rental records are saved.
     */
    public void saveRentsToFile(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(Rents);
        } catch (IOException e) {
            System.err.println("Error saving rent records: " + e.getMessage());
        }
    }

    // Loads the rent records from a file
    /**
     * Loads rental records from a file.
     * 
     * @param filename The name of the file from which rental records are loaded.
     */
    @SuppressWarnings("unchecked")
    public void loadRentsFromFile(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            Object object = in.readObject();
            if (object instanceof List) {
                Rents = (List<Rent>) object;
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing rent data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading rent records: " + e.getMessage());
        }
    }
}
