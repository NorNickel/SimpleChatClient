package best.aog.chat.client.view;

import best.aog.chat.client.controller.TCPConnection;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class MainFrame extends JFrame {

    //JPanel chatPanel = new ChatPanel();
    //JPanel authorizationPanel = new AuthorisationPanel();
    private boolean isAuthorized;
    private JLabel infoLabel;
    private JTextField loginTextField;
    private JPasswordField passwordField;
    private JButton submitButton;
    private TCPConnection TCPConnection;

    public MainFrame() {
        setTitle("Simple JAVA Chat by AOG (version 1.0)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        infoLabel = new JLabel("Input your login and password");
        add(infoLabel, BorderLayout.NORTH);

        loginTextField = new JTextField(10);
        add(loginTextField, BorderLayout.EAST);

        passwordField = new JPasswordField(10);
        add(passwordField, BorderLayout.WEST);

        submitButton = new JButton("Submit");
        add(submitButton, BorderLayout.SOUTH);

        messages = new JTextArea(30, 50);
        add(new JScrollPane(messages));

        sendButton = new JButton("Send");
        add(sendButton, BorderLayout.SOUTH);
        sendButton.addActionListener(e ->
        {
            sendButton.setEnabled(true);
            messages.append("sendButton clicked");
        });

        add(authorizationPanel, BorderLayout.NORTH);
        add(chatPanel, BorderLayout.SOUTH);
        setVisible(true);

        TCPConnection = new TCPConnection();
        TCPConnection.connectToServer();

    }

}
