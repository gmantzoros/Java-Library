import java.io.Serializable;

public class Review implements Serializable {
    private String username;
    private String isbn;
    private int stars;
    private String reviewText;

    // Constructor
    public Review(String username, String isbn, int stars, String reviewText) {
        this.username = username;
        this.isbn = isbn;
        this.stars = stars;
        this.reviewText = reviewText;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public String getReviewText() {
		return reviewText;
	}

	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
}
