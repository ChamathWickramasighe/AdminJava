package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import java.sql.SQLException;
import db.DBConnection; // Import the DBConnection class

public class DoctorsTab extends JPanel {

    private JTable doctorsTable;

    public DoctorsTab() {
        // Set layout for Doctors tab
        setLayout(new BorderLayout());

        // Create column headers
        String[] columns = {"ID", "Name", "Email", "Specialty"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);  // Initialize table model

        // Initialize the JTable
        doctorsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(doctorsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load data from database
        loadDoctorsData(tableModel);
    }

    private void loadDoctorsData(DefaultTableModel tableModel) {
        try {
            // Get a connection to the database
            Connection connection = DBConnection.getConnection();
            
            // Create a statement
            Statement stmt = connection.createStatement();
            
            // Execute a query
            String query = "SELECT id, name, email, speciality FROM doctor";
            ResultSet rs = stmt.executeQuery(query);

            // Loop through the result set and add each row to the table model
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("speciality"));
                tableModel.addRow(row);
            }

            // Close resources
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading doctor data: " + e.getMessage());
        }
    }
}
