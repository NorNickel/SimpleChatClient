package best.aog.chat.client.view;

import javax.swing.*;
import java.awt.*;

class AuthorisationPanel extends JPanel {
    private boolean isAuthorized;

    private JLabel infoLabel;
    private JTextField loginTextField;
    private JPasswordField passwordField;
    private JButton submitButton;

    AuthorisationPanel() {
        infoLabel = new JLabel("Input your login and password");
        add(infoLabel, BorderLayout.NORTH);

        loginTextField = new JTextField(10);
        add(loginTextField, BorderLayout.EAST);

        passwordField = new JPasswordField(10);
        add(passwordField, BorderLayout.WEST);

        submitButton = new JButton("Submit");
        add(submitButton, BorderLayout.SOUTH);
    }

}
