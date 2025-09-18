// Book.java
public class Book {
    public int bookId;
    public long goodreadsBookId;
    public long bestBookId;
    public long workId;
    public int booksCount;
    public String isbn;
    public String isbn13;
    public String authors;
    public int originalPublicationYear;
    public String originalTitle;
    public String title;
    public String languageCode;
    public double averageRating;
    public int ratingsCount;
    public int workRatingsCount;
    public int workTextReviewsCount;
    public int ratings1;
    public int ratings2;
    public int ratings3;
    public int ratings4;
    public int ratings5;
    public String imageUrl;
    public String smallImageUrl;

    public Book(String[] tokens) {
        bookId = parseInt(tokens[0]);
        goodreadsBookId = parseLong(tokens[1]);
        bestBookId = parseLong(tokens[2]);
        workId = parseLong(tokens[3]);
        booksCount = parseInt(tokens[4]);
        isbn = tokens[5];
        isbn13 = tokens[6];
        authors = tokens[7];
        originalPublicationYear = parseInt(tokens[8]);
        originalTitle = tokens[9];
        title = tokens[10];
        languageCode = tokens[11];
        averageRating = parseDouble(tokens[12]);
        ratingsCount = parseInt(tokens[13]);
        workRatingsCount = parseInt(tokens[14]);
        workTextReviewsCount = parseInt(tokens[15]);
        ratings1 = parseInt(tokens[16]);
        ratings2 = parseInt(tokens[17]);
        ratings3 = parseInt(tokens[18]);
        ratings4 = parseInt(tokens[19]);
        ratings5 = parseInt(tokens[20]);
        imageUrl = tokens[21];
        smallImageUrl = tokens[22];
    }

    public String summary(boolean useEmoji) {
        String bookIcon = useEmoji ? "\uD83D\uDCDA" : "[BOOK]";
        String starIcon = useEmoji ? "\u2B50" : "*";
        return String.format("%s %s by %s (%d) — %s %.2f (%d ratings)",
            bookIcon, title, authors, originalPublicationYear, starIcon, averageRating, ratingsCount);
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s.trim()); } catch (Exception e) { return 0L; }
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }
}
