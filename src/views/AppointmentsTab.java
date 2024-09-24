package views;

import db.DBConnection;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AppointmentsTab extends JPanel {

    private JTable appointmentsTable;
    private DefaultTableModel tableModel;

    public AppointmentsTab() {
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"Patient ID", "Doctor ID", "Appointment Date", "Status", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0);
        appointmentsTable = new JTable(tableModel);

        // Custom renderer and editor for the "Action" column to display the button
        appointmentsTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        appointmentsTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        // Fetch appointments data
        fetchAppointmentsData();

        // Add table to JScrollPane and then to panel
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void fetchAppointmentsData() {
        // Assuming you have a method to get a connection to your database
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT patient_id, doctor_id, date, status, symptoms, description FROM appointment")) {

            while (resultSet.next()) {
                String patientId = resultSet.getString("patient_id");
                String doctorId = resultSet.getString("doctor_id");
                String appointmentDate = resultSet.getString("date");
                String status = resultSet.getString("status");

                // Add data to the table model, the "View" button is created in the renderer/editor
                tableModel.addRow(new Object[]{patientId, doctorId, appointmentDate, status, "View"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving appointments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAppointmentDetails(String patientId, String doctorId, String appointmentDate, String status, String symptoms, String description) {
        // Create a modal dialog to show appointment details
        JDialog dialog = new JDialog();
        dialog.setTitle("Appointment Details");
        dialog.setModal(true);
        dialog.setSize(300, 300);
        dialog.setLayout(new GridLayout(0, 1));

        dialog.add(new JLabel("Patient ID: " + patientId));
        dialog.add(new JLabel("Doctor ID: " + doctorId));
        dialog.add(new JLabel("Appointment Date: " + appointmentDate));
        dialog.add(new JLabel("Status: " + status));
        dialog.add(new JLabel("Symptoms: " + symptoms));
        dialog.add(new JLabel("Description: " + description));

        // Add approve and cancel buttons
        JRadioButton approveButton = new JRadioButton("Approve");
        JRadioButton cancelButton = new JRadioButton("Cancel");
        ButtonGroup group = new ButtonGroup();
        group.add(approveButton);
        group.add(cancelButton);
        dialog.add(approveButton);
        dialog.add(cancelButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            // Update status in the database according to selected option
            String newStatus = approveButton.isSelected() ? "approve" : "cancel";
            updateAppointmentStatus(patientId, newStatus);
            dialog.dispose();
        });
        dialog.add(updateButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton);

        dialog.setVisible(true);
    }

    private void updateAppointmentStatus(String patientId, String newStatus) {
        // Update the status in the database
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE appointment SET status = ? WHERE patient_id = ?")) {

            preparedStatement.setString(1, newStatus);
            preparedStatement.setString(2, patientId);
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Status updated to: " + newStatus);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom renderer class to display buttons in the "Action" column
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "View" : value.toString());
            return this;
        }
    }

    // Custom editor class to handle button clicks in the "Action" column
    class ButtonEditor extends DefaultCellEditor {
        private String label;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "View" : value.toString();
            JButton button = new JButton(label);
            button.addActionListener(e -> {
                String patientId = table.getValueAt(row, 0).toString();
                String doctorId = table.getValueAt(row, 1).toString();
                String appointmentDate = table.getValueAt(row, 2).toString();
                String status = table.getValueAt(row, 3).toString();
                String symptoms = ""; // Fetch the symptoms
                String description = ""; // Fetch the description
                showAppointmentDetails(patientId, doctorId, appointmentDate, status, symptoms, description);
                fireEditingStopped();
            });
            return button;
        }
    }
}
