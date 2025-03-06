import javax.swing.*;
import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
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
            Class.forName("org.sqlite.JDBC"); // Ensure the SQLite driver is loaded
            conn = DriverManager.getConnection("jdbc:sqlite:students.db");
            Statement stmt = conn.createStatement();
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, age INTEGER, grade TEXT)");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver Not Found!");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        frame = new JFrame("Student Management System");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        idField = new JTextField(5);
        nameField = new JTextField(10);
        ageField = new JTextField(5);
        gradeField = new JTextField(5);
        searchField = new JTextField(10);
        JButton addButton = new JButton("Add Student");
        JButton updateButton = new JButton("Update Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton viewButton = new JButton("View Students");
        JButton searchButton = new JButton("Search Student");
        displayArea = new JTextArea(15, 50);

        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        viewButton.addActionListener(e -> viewStudents());
        searchButton.addActionListener(e -> searchStudent());

        frame.add(new JLabel("ID:"));
        frame.add(idField);
        frame.add(new JLabel("Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Age:"));
        frame.add(ageField);
        frame.add(new JLabel("Grade:"));
        frame.add(gradeField);
        frame.add(new JLabel("Search by Name:"));
        frame.add(searchField);
        frame.add(searchButton);
        frame.add(addButton);
        frame.add(updateButton);
        frame.add(deleteButton);
        frame.add(viewButton);
        frame.add(new JScrollPane(displayArea));

        frame.setVisible(true);
    }

    private void addStudent() {
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String grade = gradeField.getText();
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO students (name, age, grade) VALUES (?, ?, ?)");
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, grade);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Student Added Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateStudent() {
        int id = Integer.parseInt(idField.getText());
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
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Student Updated Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteStudent() {
        int id = Integer.parseInt(idField.getText());
        try {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM students WHERE id = ?");
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Student Deleted Successfully!");
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
