import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class LibraryAppSwing extends JFrame {
    private List<Book> books;
    private boolean usingArrayList = true;
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> sortBox, listTypeBox;
    private JButton searchButton, testPerfButton, addButton, deleteButton, editButton;

    public LibraryAppSwing() {
        setTitle("Library Book Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        books = new ArrayList<>();

        loadBooksFromCSV("books2.0.csv"); ////Change the name of this to check the other csv files
        initComponents();
    }

    private void initComponents() {
        String[] columnNames = {"ID", "Title", "Authors", "ISBN", "Year"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        searchField = new JTextField(15);
        searchButton = new JButton("Search by ID/ISBN");
        sortBox = new JComboBox<>(new String[] {
            "Sort by Author Asc", "Sort by Author Desc",
            "Sort by Year Asc", "Sort by Year Desc"
        });
        listTypeBox = new JComboBox<>(new String[] {"ArrayList", "LinkedList"});
        testPerfButton = new JButton("Test Performance");
        addButton = new JButton("Add Book");
        deleteButton = new JButton("Delete Book");
        editButton = new JButton("Edit Book");

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(sortBox);
        topPanel.add(listTypeBox);
        topPanel.add(testPerfButton);
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchBook());
        sortBox.addActionListener(e -> sortBooks());
        listTypeBox.addActionListener(e -> switchListType());
        testPerfButton.addActionListener(e -> testSearchPerformance());
        addButton.addActionListener(e -> addBookDialog());
        deleteButton.addActionListener(e -> deleteSelectedBook());
        editButton.addActionListener(e -> editSelectedBook());

        showTop10();
    }

    private void switchListType() {
        usingArrayList = listTypeBox.getSelectedItem().equals("ArrayList");
        loadBooksFromCSV("books.csv");
        showTop10();
    }

    private void loadBooksFromCSV(String filename) {
        books = usingArrayList ? new ArrayList<>() : new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);

                if (parts.length >= 23) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        String title = parts[10].replaceAll("\"", "");
                        String authors = parts[7].replaceAll("\"", "");
                        String isbn = parts[5].replaceAll("\"", "");
                        int year = (int) Double.parseDouble(parts[8]);
                        books.add(new Book(id, title, authors, isbn, year));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

    private void showTop10() {
        model.setRowCount(0);
        for (int i = 0; i < Math.min(10, books.size()); i++) {
            addBookToTable(books.get(i));
        }
    }

    private void addBookToTable(Book b) {
        model.addRow(new Object[] { b.id, b.title, b.authors, b.isbn, b.year });
    }

    private void searchBook() {
        String query = searchField.getText().trim();
        model.setRowCount(0);

        Book found = null;
        if (usingArrayList) {
            books.sort(Comparator.comparingInt(b -> b.id));
            try {
                int id = Integer.parseInt(query);
                found = binarySearch(books, id);
            } catch (NumberFormatException e) {
                found = books.stream().filter(b -> b.isbn.equalsIgnoreCase(query)).findFirst().orElse(null);
            }
        } else {
            for (Book b : books) {
                if (String.valueOf(b.id).equals(query) || b.isbn.equalsIgnoreCase(query)) {
                    found = b;
                    break;
                }
            }
        }

        if (found != null) {
            addBookToTable(found);
        } else {
            JOptionPane.showMessageDialog(this, "Book not found!");
        }
    }

    private Book binarySearch(List<Book> list, int id) {
        int left = 0, right = list.size() - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (list.get(mid).id == id) return list.get(mid);
            if (list.get(mid).id < id) left = mid + 1;
            else right = mid - 1;
        }
        return null;
    }

    private void sortBooks() {
        String selection = (String) sortBox.getSelectedItem();
        if (selection.contains("Author")) {
            books.sort(Comparator.comparing(b -> b.authors.toLowerCase()));
        } else if (selection.contains("Year")) {
            books.sort(Comparator.comparingInt(b -> b.year));
        }
        if (selection.contains("Desc")) Collections.reverse(books);
        showTop10();
    }

    private void testSearchPerformance() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            int randomId = 1 + new Random().nextInt(books.size());
            if (usingArrayList) binarySearch(books, randomId);
            else {
                for (Book b : books) if (b.id == randomId) break;
            }
        }
        long duration = System.nanoTime() - start;
        JOptionPane.showMessageDialog(this, (usingArrayList ? "Binary" : "Linear") +
                " search time (ns): " + duration);
    }

    private void addBookDialog() {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField yearField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("ID:")); panel.add(idField);
        panel.add(new JLabel("Title:")); panel.add(titleField);
        panel.add(new JLabel("Author:")); panel.add(authorField);
        panel.add(new JLabel("ISBN:")); panel.add(isbnField);
        panel.add(new JLabel("Year:")); panel.add(yearField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Book b = new Book(
                    Integer.parseInt(idField.getText()),
                    titleField.getText(), authorField.getText(),
                    isbnField.getText(), Integer.parseInt(yearField.getText())
                );
                books.add(b);
                showTop10();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }

    private void deleteSelectedBook() {
        int selected = table.getSelectedRow();
        if (selected >= 0) {
            int id = (int) model.getValueAt(selected, 0);
            books.removeIf(b -> b.id == id);
            showTop10();
        }
    }

    private void editSelectedBook() {
        int selected = table.getSelectedRow();
        if (selected < 0) return;
        int id = (int) model.getValueAt(selected, 0);
        Book book = books.stream().filter(b -> b.id == id).findFirst().orElse(null);
        if (book == null) return;

        JTextField titleField = new JTextField(book.title);
        JTextField authorField = new JTextField(book.authors);
        JTextField isbnField = new JTextField(book.isbn);
        JTextField yearField = new JTextField(String.valueOf(book.year));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Title:")); panel.add(titleField);
        panel.add(new JLabel("Author:")); panel.add(authorField);
        panel.add(new JLabel("ISBN:")); panel.add(isbnField);
        panel.add(new JLabel("Year:")); panel.add(yearField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            book.title = titleField.getText();
            book.authors = authorField.getText();
            book.isbn = isbnField.getText();
            book.year = Integer.parseInt(yearField.getText());
            showTop10();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryAppSwing().setVisible(true));
    }

    static class Book {
        int id; String title, authors, isbn; int year;
        public Book(int id, String title, String authors, String isbn, int year) {
            this.id = id; this.title = title; this.authors = authors; this.isbn = isbn; this.year = year;
        }
    }
}
