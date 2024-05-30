import java.io.Serializable;
import java.time.LocalDate;

public class Rent implements Serializable {
    private String isbn;
    private String username;
    private LocalDate rentDate;
    private LocalDate returnDate;

    public Rent(String isbn, String username, LocalDate rentDate, LocalDate returnDate) {
        this.isbn = isbn;
        this.username = username;
        this.rentDate = rentDate;
        this.returnDate = rentDate.plusDays(5);
    }

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDate getRentDate() {
		return rentDate;
	}

	public void setRentDate(LocalDate rentDate) {
		this.rentDate = rentDate;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}
}
