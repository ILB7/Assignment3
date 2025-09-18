import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Task11Loader {
    private static final Pattern CSV_PATTERN = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

    public static List<Book> loadBooks(String filePath, List<Book> container) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = CSV_PATTERN.split(line, -1);
                if (fields.length == 23) {
                    container.add(new Book(fields));
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
        }
        return container;
    }
}