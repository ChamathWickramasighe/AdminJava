package views;

import javax.swing.*;
import java.awt.*;

public class DashboardTab extends JPanel {

    public DashboardTab() {
        // Set the layout and components for the Dashboard tab
        setLayout(new BorderLayout());

        // Simple message for now
        JLabel welcomeLabel = new JLabel("Welcome to the Dashboard", SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.CENTER);

        // Add a button that links to the client-side web app (assuming URL is available)
        JButton goToClientButton = new JButton("Go to Client-Side Web App");
        goToClientButton.addActionListener(e -> {
            // Here you could add functionality to open a web browser to the client-side app
            System.out.println("Redirect to Client-Side Web App");
        });
        add(goToClientButton, BorderLayout.SOUTH);
    }
}
