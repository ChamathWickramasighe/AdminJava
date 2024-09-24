package views;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PrescriptionsTab extends JPanel {

    private JTable prescriptionsTable;
    private DefaultTableModel tableModel;

    public PrescriptionsTab() {
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"Issued Date", "Doctor ID", "Patient ID", "Appointment Number", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        prescriptionsTable = new JTable(tableModel);

        // Fetch prescription data
        fetchPrescriptionData();

        // Add table to JScrollPane and then to panel
        JScrollPane scrollPane = new JScrollPane(prescriptionsTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void fetchPrescriptionData() {
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT date, doctor_id, patient_id, appo_id , status FROM prescription")) {

            while (resultSet.next()) {
                String issuedDate = resultSet.getString("date");
                String doctorId = resultSet.getString("doctor_id");
                String patientId = resultSet.getString("patient_id");
                String appointmentNumber = resultSet.getString("appo_id");
                String status = resultSet.getString("status");

                // Add data to the table model
                tableModel.addRow(new Object[]{issuedDate, doctorId, patientId, appointmentNumber, status});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving prescriptions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
