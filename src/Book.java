import java.util.List;
import java.io.Serializable;

public class Book implements Serializable {
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private int year;
    private String category;
    private int copies;
    private transient float stars = 0f;

    public Book(String title, String author, String publisher, String isbn, int year, String category, int copies) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.year = year;
        this.category = category;
        this.copies = copies;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getCopies() {
		return copies;
	}

	public void setCopies(int copies) {
		this.copies = copies;
	}
	
    // getStars method to dynamically calculate average from reviews
    public float getStars(ReviewData reviewData) {
        List<Review> reviews = reviewData.getReviewsByBook(this.isbn);
        if (reviews.isEmpty()) {
            return stars; // Returns the default if no reviews
        } else {
            float sum = 0;
            for (Review review : reviews) {
                sum += review.getStars();
            }
            return sum / reviews.size(); // Returns the calculated average
        }
    }
    
    public void setStars(float stars) {
    	this.stars = stars;
    }
}
