import java.util.*;

public class Task11 {
    public static void main(String[] args) {
        String filePath = "C:/SENG300/books.csv";
        boolean useEmoji = true;

        List<Book> arrayListBooks = Task11Loader.loadBooks(filePath, new ArrayList<>());
        List<Book> linkedListBooks = Task11Loader.loadBooks(filePath, new LinkedList<>());

        System.out.println("✅ ArrayList loaded: " + arrayListBooks.size() + " books");
        printTop10(arrayListBooks, useEmoji, "ArrayList");

        System.out.println("\n✅ LinkedList loaded: " + linkedListBooks.size() + " books");
        printTop10(linkedListBooks, useEmoji, "LinkedList");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n📚 Menu:");
            System.out.println("1. Search ArrayList by book_id (binary)");
            System.out.println("2. Search LinkedList by book_id (linear)");
            System.out.println("3. Search by ISBN");
            System.out.println("4. Sort by authors");
            System.out.println("5. Sort by publication year");
            System.out.println("6. Benchmark search performance");
            System.out.println("7. Add a new book");
            System.out.println("8. Edit book information");
            System.out.println("9. Delete a book");
            System.out.println("10. Exit");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    sortByBookId(arrayListBooks);
                    System.out.print("Enter book_id: ");
                    int id1 = Integer.parseInt(scanner.nextLine());
                    Book result1 = binarySearchByBookId(arrayListBooks, id1);
                    System.out.println(result1 != null ? "\n✅ Found:\n" + result1.summary(useEmoji) : "❌ Not found.");
                    break;
                case "2":
                    System.out.print("Enter book_id: ");
                    int id2 = Integer.parseInt(scanner.nextLine());
                    Book result2 = linearSearchByBookId(linkedListBooks, id2);
                    System.out.println(result2 != null ? "\n✅ Found:\n" + result2.summary(useEmoji) : "❌ Not found.");
                    break;
                case "3":
                    System.out.print("Enter ISBN: ");
                    String isbn = scanner.nextLine();
                    Book result3 = searchByIsbn(arrayListBooks, isbn);
                    System.out.println(result3 != null ? "\n✅ Found:\n" + result3.summary(useEmoji) : "❌ Not found.");
                    break;
                case "4":
                    System.out.print("Sort by authors (asc/desc): ");
                    String dirA = scanner.nextLine();
                    sortByAuthors(arrayListBooks, dirA.equalsIgnoreCase("desc"));
                    printTop10(arrayListBooks, useEmoji, "Sorted by Authors (" + dirA + ")");
                    break;
                case "5":
                    System.out.print("Sort by year (asc/desc): ");
                    String dirY = scanner.nextLine();
                    sortByYear(arrayListBooks, dirY.equalsIgnoreCase("desc"));
                    printTop10(arrayListBooks, useEmoji, "Sorted by Year (" + dirY + ")");
                    break;
                case "6":
                    System.out.print("Enter number of book_ids to test (e.g., 500 or 1000): ");
                    int sampleSize = Integer.parseInt(scanner.nextLine());
                    benchmarkSearch(arrayListBooks, linkedListBooks, sampleSize);
                    break;
                case "7":
                    addNewBook(arrayListBooks, scanner);
                    break;
                case "8":
                    editBook(arrayListBooks, scanner);
                    break;
                case "9":
                    deleteBook(arrayListBooks, scanner);
                    break;
                case "10":
                    System.out.println("👋 Exiting. Goodbye!");
                    return;
                default:
                    System.out.println("⚠️ Invalid choice. Try again.");
            }
        }
    }

    public static void printTop10(List<Book> books, boolean useEmoji, String label) {
        System.out.println("\n🔝 Top 10 from " + label + ":");
        for (int i = 0; i < Math.min(10, books.size()); i++) {
            System.out.println(books.get(i).summary(useEmoji));
        }
    }

    public static void sortByBookId(List<Book> books) {
        books.sort(Comparator.comparingInt(b -> b.bookId));
    }

    public static Book binarySearchByBookId(List<Book> books, int targetId) {
        int left = 0, right = books.size() - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            int midId = books.get(mid).bookId;
            if (midId == targetId) return books.get(mid);
            else if (midId < targetId) left = mid + 1;
            else right = mid - 1;
        }
        return null;
    }

    public static Book linearSearchByBookId(List<Book> books, int targetId) {
        for (Book book : books) {
            if (book.bookId == targetId) return book;
        }
        return null;
    }

    public static Book searchByIsbn(List<Book> books, String isbn) {
        for (Book book : books) {
            if (book.isbn != null && book.isbn.equalsIgnoreCase(isbn.trim())) return book;
        }
        return null;
    }

    public static void sortByAuthors(List<Book> books, boolean descending) {
        books.sort((b1, b2) -> {
            int cmp = b1.authors.compareToIgnoreCase(b2.authors);
            return descending ? -cmp : cmp;
        });
    }

    public static void sortByYear(List<Book> books, boolean descending) {
        books.sort((b1, b2) -> {
            int cmp = Integer.compare(b1.originalPublicationYear, b2.originalPublicationYear);
            return descending ? -cmp : cmp;
        });
    }

    public static List<Integer> getRandomBookIds(List<Book> books, int count) {
        List<Integer> ids = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int index = rand.nextInt(books.size());
            ids.add(books.get(index).bookId);
        }
        return ids;
    }

    public static void benchmarkSearch(List<Book> arrayListBooks, List<Book> linkedListBooks, int sampleSize) {
        sortByBookId(arrayListBooks);
        List<Integer> testIds = getRandomBookIds(arrayListBooks, sampleSize);

        int hitsBinary = 0;
        long startBinary = System.nanoTime();
        for (int id : testIds) {
            if (binarySearchByBookId(arrayListBooks, id) != null) hitsBinary++;
        }
        long endBinary = System.nanoTime();

        int hitsLinear = 0;
        long startLinear = System.nanoTime();
        for (int id : testIds) {
            if (linearSearchByBookId(linkedListBooks, id) != null) hitsLinear++;
        }
        long endLinear = System.nanoTime();

        System.out.println("\n📊 Search Benchmark (" + sampleSize + " IDs):");
        System.out.printf("🔍 Binary Search (ArrayList): %.2f ms total, %.2f µs avg, %d hits\n",
            (endBinary - startBinary) / 1e6, (endBinary - startBinary) / (double) sampleSize / 1e3, hitsBinary);
        System.out.printf("🔍 Linear Search (LinkedList): %.2f ms total, %.2f µs avg, %d hits\n",
            (endLinear - startLinear) / 1e6, (endLinear - startLinear) / (double) sampleSize / 1e3, hitsLinear);
    }

    public static void addNewBook(List<Book> books, Scanner scanner) {
        System.out.println("\n📘 Add New Book:");
        System.out.print("Enter book_id: ");
        int bookId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter title: ");
        String title = scanner.nextLine();

        System.out.print("Enter authors: ");
        String authors = scanner.nextLine();

        System.out.print("Enter publication year: ");
        int year = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter average rating: ");
        double rating = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter ratings count: ");
        int ratingsCount = Integer.parseInt(scanner.nextLine());

        String[] fields = new String[23];
        fields[0] = String.valueOf(bookId);
        fields[1] = fields[2] = fields[3] = "0";
        fields[4] = "1";
        fields[5] = fields[6] = "N/A";
        fields[7] = authors;
        fields[8] = String.valueOf(year);
        fields[9] = fields[10] = title;
        fields[11] = "en";
        fields[12] = String.valueOf(rating);
        fields[13] = String.valueOf(ratingsCount);
        for (int i = 14; i <= 20; i++) fields[i] = "0";
        fields[21] = fields[22] = "N/A";

        Book newBook = new Book(fields);
        books.add(newBook);
        System.out.println("✅ Book added:\n" + newBook.summary(true));
    }

    public static void editBook(List<Book> books, Scanner scanner) {
        System.out.print("\n✏️ Enter book_id to edit: ");
        int id = Integer.parseInt(scanner.nextLine());
        Book book = linearSearchByBookId(books, id);
        if (book == null) {
            System.out.println("❌ Book not found.");
            return;
        }

        System.out.println("Current title: " + book.title);
        System.out.print("New title (leave blank to keep): ");
        String newTitle = scanner.nextLine();
        if (!newTitle.isEmpty()) book.title = newTitle;

        System.out.println("Current authors: " + book.authors);
        System.out.print("New authors (leave blank to keep): ");
        String newAuthors = scanner.nextLine();
        if (!newAuthors.isEmpty()) book.authors = newAuthors;

        System.out.println("✅ Book updated:\n" + book.summary(true));
    }

    public static void deleteBook(List<Book> books, Scanner scanner) {
        System.out.print("\n🗑️ Enter book_id to delete: ");
        int id = Integer.parseInt(scanner.nextLine());
        Iterator<Book> iterator = books.iterator();
        while (iterator.hasNext()) {
            Book book = iterator.next();
            if (book.bookId == id) {
                iterator.remove();
                System.out.println("✅ Book deleted.");
                return;
            }
        }
        System.out.println("❌ Book not found.");
    }
}
