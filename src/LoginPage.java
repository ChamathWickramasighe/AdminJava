import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginPage() {
        setTitle("Admin Panel - Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        // Username and password fields
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        
        // Login button
        loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginButtonListener());

        // Add components to the layout
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel());  // Empty cell for layout
        add(loginButton);

        setVisible(true);
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Check if credentials are correct
            if (username.equals("Admin") && password.equals("Admin@123")) {
                // Credentials are correct, open the main admin panel
                dispose();  // Close the login window
                new AdminPanelApp();  // Open the main admin panel
            } else {
                // Credentials are incorrect, show error message
                JOptionPane.showMessageDialog(null, "Incorrect Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
