

import views.DashboardTab;
import views.DoctorsTab;
import views.AppointmentsTab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import views.AppointmentsTab;
import views.AppointmentsTab;
import views.DashboardTab;
import views.DashboardTab;
import views.DoctorsTab;
import views.DoctorsTab;
import views.DrugsTab;
import views.PrescriptionsTab;

public class AdminPanelApp extends JFrame {

    private JPanel sideMenuPanel;
    private JPanel bodyPanel;
    private CardLayout cardLayout;  // To switch between different tab content

    public AdminPanelApp() {
        // Set up the main JFrame
        setTitle("Admin Panel");
        setSize(800, 600); // Set your preferred size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Use BorderLayout for main layout

        // Initialize panels
        sideMenuPanel = new JPanel();
        sideMenuPanel.setLayout(new GridLayout(5, 1));  // 5 rows for 5 menu items

        bodyPanel = new JPanel();
        cardLayout = new CardLayout(); // To swap content in body panel
        bodyPanel.setLayout(cardLayout);

        // Create and add menu buttons
        String[] menuItems = {"Dashboard", "Doctors", "Appointments", "Prescriptions", "Drugs"};
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.addActionListener(new MenuButtonListener());
            sideMenuPanel.add(button);
        }

        // Add side menu to the left and body panel to the center
        add(sideMenuPanel, BorderLayout.WEST);
        add(bodyPanel, BorderLayout.CENTER);

        // Add instances of the separate classes for each tab
        bodyPanel.add(new DashboardTab(), "Dashboard");
        bodyPanel.add(new DoctorsTab(), "Doctors");
        bodyPanel.add(new AppointmentsTab(), "Appointments");
        bodyPanel.add(new PrescriptionsTab(), "Prescriptions");
        bodyPanel.add(new DrugsTab(), "Drugs");

        // Placeholder panels for other tabs (we'll add these in future steps)
//        JPanel appointmentsPanel = new JPanel();
//        appointmentsPanel.add(new JLabel("Appointments"));

//        JPanel prescriptionsPanel = new JPanel();
//        prescriptionsPanel.add(new JLabel("Prescriptions"));
//
//        JPanel drugsPanel = new JPanel();
//        drugsPanel.add(new JLabel("Drugs"));

        // Add placeholders to the CardLayout
        //bodyPanel.add(appointmentsPanel, "Appointments");
        //bodyPanel.add(prescriptionsPanel, "Prescriptions");
        //bodyPanel.add(drugsPanel, "Drugs");

        // Show the JFrame
        setVisible(true);
    }

    // Listener class to switch between panels based on menu button clicks
    private class MenuButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            cardLayout.show(bodyPanel, command);
        }
    }

    public static void main(String[] args) {
        new AdminPanelApp();
    }
}
