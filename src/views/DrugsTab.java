package views;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DrugsTab extends JPanel {

    private JTable drugsTable;
    private DefaultTableModel tableModel;
    private JTextField drugIdField, drugNameField, drugDescriptionField, drugImgPathField, drugPriceField;

    public DrugsTab() {
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"Drug ID", "Name", "Description", "Image Path", "Price", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0);
        drugsTable = new JTable(tableModel);

        // Fetch drugs data
        fetchDrugsData();

        // Add table to JScrollPane and then to panel
        JScrollPane scrollPane = new JScrollPane(drugsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add "Add New Drug" button
        JButton addDrugButton = new JButton("Add New Drug");
        addDrugButton.addActionListener(e -> openAddDrugDialog());
        add(addDrugButton, BorderLayout.SOUTH);
    }

    private void fetchDrugsData() {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM drug")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String drugId = rs.getString("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String imgPath = rs.getString("img_path");
                String price = rs.getString("amount");

                JButton actionButton = new JButton("Edit/Delete");
                actionButton.addActionListener(e -> openEditDeleteDialog(drugId, name, description, imgPath, price));

                tableModel.addRow(new Object[]{drugId, name, description, imgPath, price, actionButton});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openAddDrugDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Drug");
        dialog.setSize(300, 400);
        dialog.setLayout(new GridLayout(0, 2));

        // Fields for new drug details
        dialog.add(new JLabel("Drug ID:"));
        drugIdField = new JTextField();
        dialog.add(drugIdField);

        dialog.add(new JLabel("Name:"));
        drugNameField = new JTextField();
        dialog.add(drugNameField);

        dialog.add(new JLabel("Description:"));
        drugDescriptionField = new JTextField();
        dialog.add(drugDescriptionField);

        dialog.add(new JLabel("Image Path:"));
        drugImgPathField = new JTextField();
        dialog.add(drugImgPathField);

        dialog.add(new JLabel("Price:"));
        drugPriceField = new JTextField();
        dialog.add(drugPriceField);

        // Add buttons
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addNewDrug(dialog));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(addButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    private void addNewDrug(JDialog dialog) {
        String drugId = drugIdField.getText();
        String name = drugNameField.getText();
        String description = drugDescriptionField.getText();
        String imgPath = drugImgPathField.getText();
        String price = drugPriceField.getText();

        // Insert into the database
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO drug (id, name, description, img_path, amount) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, drugId);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.setString(4, imgPath);
            ps.setString(5, price);
            ps.executeUpdate();

            // Refresh table
            tableModel.addRow(new Object[]{drugId, name, description, imgPath, price, new JButton("Edit/Delete")});
            dialog.dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding drug: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditDeleteDialog(String drugId, String name, String description, String imgPath, String price) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Edit/Delete Drug");
        dialog.setSize(300, 400);
        dialog.setLayout(new GridLayout(0, 2));

        // Fields to edit
        dialog.add(new JLabel("Name:"));
        drugNameField = new JTextField(name);
        dialog.add(drugNameField);

        dialog.add(new JLabel("Description:"));
        drugDescriptionField = new JTextField(description);
        dialog.add(drugDescriptionField);

        dialog.add(new JLabel("Image Path:"));
        drugImgPathField = new JTextField(imgPath);
        dialog.add(drugImgPathField);

        dialog.add(new JLabel("Price:"));
        drugPriceField = new JTextField(price);
        dialog.add(drugPriceField);

        // Add buttons
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateDrug(drugId, dialog));
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteDrug(drugId, dialog));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(updateButton);
        dialog.add(deleteButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    private void updateDrug(String drugId, JDialog dialog) {
        String name = drugNameField.getText();
        String description = drugDescriptionField.getText();
        String imgPath = drugImgPathField.getText();
        String price = drugPriceField.getText();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE drug SET name = ?, description = ?, img_path = ?, amount = ? WHERE id = ?")) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, imgPath);
            ps.setString(4, price);
            ps.setString(5, drugId);
            ps.executeUpdate();

            // Refresh table
            int rowIndex = getTableRowIndex(drugId);
            if (rowIndex != -1) {
                tableModel.setValueAt(name, rowIndex, 1);
                tableModel.setValueAt(description, rowIndex, 2);
                tableModel.setValueAt(imgPath, rowIndex, 3);
                tableModel.setValueAt(price, rowIndex, 4);
            }
            dialog.dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating drug: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDrug(String drugId, JDialog dialog) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM drug WHERE id = ?")) {
            ps.setString(1, drugId);
            ps.executeUpdate();

            // Remove row from the table
            int rowIndex = getTableRowIndex(drugId);
            if (rowIndex != -1) {
                tableModel.removeRow(rowIndex);
            }
            dialog.dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting drug: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getTableRowIndex(String drugId) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(drugId)) {
                return i;
            }
        }
        return -1;
    }
}
