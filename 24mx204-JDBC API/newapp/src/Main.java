import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class Main {
    // Database credentials
    static final String DB_URL = "jdbc:mysql://localhost:3306/bookstore";
    static final String USER = "root";
    static final String PASS = "WJ28@krhps";

    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField titleField, priceField, authorIdField;
    private JTextField bookIdField; // New field for updating & deleting books

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().frame.setVisible(true));
    }

    public Main() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Bookstore Manager");
        frame.setBounds(100, 100, 800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 5, 5));

        inputPanel.add(new JLabel("Book ID (For Update/Delete):"));
        bookIdField = new JTextField();
        inputPanel.add(bookIdField);

        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        inputPanel.add(new JLabel("Author ID:"));
        authorIdField = new JTextField();
        inputPanel.add(authorIdField);

        // Buttons
        JButton insertButton = new JButton("Insert Book");
        insertButton.addActionListener(e -> insertBook());
        inputPanel.add(insertButton);

        JButton updateButton = new JButton("Update Book");
        updateButton.addActionListener(e -> updateBook());
        inputPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Book");
        deleteButton.addActionListener(e -> deleteBook());
        inputPanel.add(deleteButton);

        JButton loadButton = new JButton("Load Books");
        loadButton.addActionListener(e -> loadBooks());
        inputPanel.add(loadButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Book ID", "Title", "Price", "Author Name"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        loadBooks(); // Load books on startup
    }

    // 🟢 Insert Book
    private void insertBook() {
        String title = titleField.getText();
        String priceText = priceField.getText();
        String authorIdText = authorIdField.getText();

        if (title.isEmpty() || priceText.isEmpty() || authorIdText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields are required!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO books (title, price, author_id) VALUES (?, ?, ?)")) {
            ps.setString(1, title);
            ps.setDouble(2, Double.parseDouble(priceText));
            ps.setInt(3, Integer.parseInt(authorIdText));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Book added successfully!");
            loadBooks();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    // 🔵 Update Book
    private void updateBook() {
        String bookIdText = bookIdField.getText();
        String title = titleField.getText();
        String priceText = priceField.getText();

        if (bookIdText.isEmpty() || title.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Book ID, Title, and Price are required!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement("UPDATE books SET title = ?, price = ? WHERE book_id = ?")) {
            ps.setString(1, title);
            ps.setDouble(2, Double.parseDouble(priceText));
            ps.setInt(3, Integer.parseInt(bookIdText));
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Book updated successfully!");
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(frame, "Book ID not found!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    // 🔴 Delete Book
    private void deleteBook() {
        String bookIdText = bookIdField.getText();
        if (bookIdText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Enter Book ID to delete!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE book_id = ?")) {
            ps.setInt(1, Integer.parseInt(bookIdText));
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Book deleted successfully!");
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(frame, "Book ID not found!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    // 🔍 Load Books
    private void loadBooks() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT b.book_id, b.title, b.price, a.author_name " +
                     "FROM books b JOIN authors a ON b.author_id = a.author_id")) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getDouble("price"),
                        rs.getString("author_name")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }
}
