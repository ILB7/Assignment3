// BookDemo.java
import java.io.*;
import java.util.*;

public class BookDemo {
    public static void main(String[] args) {
        boolean useEmoji = true;
        String filePath = "C:/SENG300/books.csv";
        List<Book> books = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = splitCSV(line);
                if (tokens.length == 23) {
                    books.add(new Book(tokens));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        System.out.println("✅ Loaded " + books.size() + " books.\n");

        for (Book book : books) {
            System.out.println(book.summary(useEmoji));
        }
    }

    private static String[] splitCSV(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
