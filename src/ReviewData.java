import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class ReviewData implements Serializable {
    private List<Review> reviews = new ArrayList<>();

    // Add a review
    public boolean addReview(Review review) {
        return reviews.add(review);
    }
    
    // Method to get all reviews
    public List<Review> getAllReviews() {
        return new ArrayList<>(reviews); // Return a copy of the reviews list to avoid modification outside
    }

    // Get all reviews for a specific book
    public List<Review> getReviewsByBook(String isbn) {
        List<Review> bookReviews = new ArrayList<>();
        for (Review review : reviews) {
            if (review.getIsbn().equals(isbn)) {
                bookReviews.add(review);
            }
        }
        return bookReviews;
    }

    // Save reviews to a file
    public void saveReviewsToFile(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(reviews);
        } catch (IOException e) {
            System.err.println("Error saving reviews: " + e.getMessage());
        }
    }

    // Load reviews from a file
    @SuppressWarnings("unchecked")
    public void loadReviewsFromFile(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            Object object = in.readObject();
            if (object instanceof List) {
                reviews = (List<Review>) object;
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing review data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading reviews: " + e.getMessage());
        }
    }
}
