import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentManagementSystem {
    private JFrame frame;
    private JTextField nameField, ageField, gradeField, idField, searchField;
    private JTextArea displayArea;
    private Connection conn;

    public StudentManagementSystem() {
        initializeDB();
        initializeUI();
    }

    private void initializeDB() {
        try {
            Class.forName("org.sqlite.JDBC"); // Load SQLite Driver
            conn = DriverManager.getConnection("jdbc:sqlite:students.db");
            Statement stmt = conn.createStatement();
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, age INTEGER, grade TEXT)");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "SQLite JDBC Driver Not Found!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        frame = new JFrame("Student Management System");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(240, 248, 255)); // Light blue background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(5);
        nameField = new JTextField(10);
        ageField = new JTextField(5);
        gradeField = new JTextField(5);
        searchField = new JTextField(10);
        displayArea = new JTextArea(15, 50);
        displayArea.setFont(new Font("Arial", Font.PLAIN, 14));
        displayArea.setEditable(false);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton viewButton = new JButton("View All");
        JButton searchButton = new JButton("Search");

        addButton.setBackground(Color.GREEN);
        updateButton.setBackground(Color.ORANGE);
        deleteButton.setBackground(Color.RED);
        viewButton.setBackground(Color.BLUE);
        searchButton.setBackground(Color.CYAN);

        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        viewButton.addActionListener(e -> viewStudents());
        searchButton.addActionListener(e -> searchStudent());

        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        frame.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        frame.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        frame.add(ageField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(new JLabel("Grade:"), gbc);
        gbc.gridx = 1;
        frame.add(gradeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(new JLabel("Search by Name:"), gbc);
        gbc.gridx = 1;
        frame.add(searchField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        frame.add(addButton, gbc);
        gbc.gridx = 1;
        frame.add(updateButton, gbc);

        gbc.gridx = 2;
        frame.add(deleteButton, gbc);
        gbc.gridx = 3;
        frame.add(viewButton, gbc);

        gbc.gridx = 4;
        frame.add(searchButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 5;
        frame.add(new JScrollPane(displayArea), gbc);

        frame.setVisible(true);
    }

    private void addStudent() {
        String name = nameField.getText();
        String grade = gradeField.getText();
        int age;

        try {
            age = Integer.parseInt(ageField.getText());
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO students (name, age, grade) VALUES (?, ?, ?)");
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, grade);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Student Added Successfully!");
            viewStudents();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid age!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateStudent() {
        int id;
        try {
            id = Integer.parseInt(idField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid student ID!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String grade = gradeField.getText();

        try {
            PreparedStatement pstmt = conn
                    .prepareStatement("UPDATE students SET name = ?, age = ?, grade = ? WHERE id = ?");
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, grade);
            pstmt.setInt(4, id);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(frame, "Student Updated Successfully!");
                viewStudents();
            } else {
                JOptionPane.showMessageDialog(frame, "Student ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteStudent() {
        int id;
        try {
            id = Integer.parseInt(idField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid student ID!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this student?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM students WHERE id = ?");
            pstmt.setInt(1, id);
            int deletedRows = pstmt.executeUpdate();
            if (deletedRows > 0) {
                JOptionPane.showMessageDialog(frame, "Student Deleted Successfully!");
                viewStudents();
            } else {
                JOptionPane.showMessageDialog(frame, "Student ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewStudents() {
        displayArea.setText("");
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");
            while (rs.next()) {
                displayArea.append(rs.getInt("id") + ". " + rs.getString("name") + " - " + rs.getInt("age")
                        + " years - Grade: " + rs.getString("grade") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchStudent() {
        String searchName = searchField.getText();
        displayArea.setText("");
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM students WHERE name LIKE ?");
            pstmt.setString(1, "%" + searchName + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                displayArea.append(rs.getInt("id") + ". " + rs.getString("name") + " - " + rs.getInt("age")
                        + " years - Grade: " + rs.getString("grade") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new StudentManagementSystem();
    }
}
