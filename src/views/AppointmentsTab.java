package views;

import db.DBConnection;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.EventObject;

public class AppointmentsTab extends JPanel {

    private JTable appointmentsTable;
    private DefaultTableModel tableModel;

    public AppointmentsTab() {
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"Appointment ID", "Patient ID", "Doctor ID", "Appointment Date", "Status", "Approve", "Reject"};
        tableModel = new DefaultTableModel(columnNames, 0);
        appointmentsTable = new JTable(tableModel);

        // Fetch appointments data
        fetchAppointmentsData();

        // Add table to JScrollPane and then to panel
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Set custom renderer and editor for the Approve and Reject columns
        appointmentsTable.getColumn("Approve").setCellRenderer(new ButtonRenderer());
        appointmentsTable.getColumn("Approve").setCellEditor(new ButtonEditor(new JCheckBox(), "approve"));
        appointmentsTable.getColumn("Reject").setCellRenderer(new ButtonRenderer());
        appointmentsTable.getColumn("Reject").setCellEditor(new ButtonEditor(new JCheckBox(), "reject"));
    }

    private void fetchAppointmentsData() {
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, patient_id, doctor_id, date, status FROM appointment")) {

            while (resultSet.next()) {
                String appointmentId = resultSet.getString("id");
                String patientId = resultSet.getString("patient_id");
                String doctorId = resultSet.getString("doctor_id");
                String appointmentDate = resultSet.getString("date");
                String status = resultSet.getString("status");

                // Add data to the table model
                tableModel.addRow(new Object[]{appointmentId, patientId, doctorId, appointmentDate, status, "Approve", "Reject"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving appointments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAppointmentStatus(String patientId, String newStatus) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE appointment SET status = ? WHERE id = ?")) {

            ps.setString(1, newStatus);
            ps.setString(2, patientId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Appointment status updated to: " + newStatus);
                refreshTable();  // Refresh the table after updating the status
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update status for patient ID: " + patientId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Refreshes the table to display updated status
    private void refreshTable() {
        tableModel.setRowCount(0);  // Clear the existing data
        fetchAppointmentsData();    // Fetch updated data
    }

    // Custom renderer for the Approve and Reject buttons
    private class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Custom editor for the Approve and Reject buttons
    private class ButtonEditor extends DefaultCellEditor {
        private String label;
        private String status;
        private String patientId;

        public ButtonEditor(JCheckBox checkBox, String status) {
            super(checkBox);
            this.status = status;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            patientId = table.getValueAt(row, 0).toString();  // Get the patient ID from the row
            JButton button = new JButton(label);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateAppointmentStatus(patientId, status);  // Update the status based on the button clicked
                    fireEditingStopped();  // To stop editing the cell after the button click
                }
            });
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            fireEditingStopped();
            return super.stopCellEditing();
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }
    }
}
