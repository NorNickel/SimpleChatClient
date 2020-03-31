package best.aog.chat.client.view;

import best.aog.chat.client.controller.TCPConnection;
import best.aog.chat.client.controller.TCPConnectionListener;
import best.aog.chat.client.model.Config;
import best.aog.chat.client.model.User;
import best.aog.chat.client.model.messages.Message;
import best.aog.chat.client.model.messages.MessageResultType;
import best.aog.chat.client.model.messages.MessageType;
import best.aog.chat.client.model.messages.Messages;
import best.aog.chat.client.model.messages.client.RegularMessageBody;
import best.aog.chat.client.model.messages.server.ResultMessageBody;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements TCPConnectionListener {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private User user = null;
    private final static Gson gson = new Gson();

    private final JTextArea areaChat = new JTextArea();
    private final JTextField fieldInput = new JTextField();

    private JPanel panelAuth = new JPanel();
    private JTextField fieldLogin = new JTextField(10);
    private JPasswordField fieldPassword = new JPasswordField(10);
    private JButton buttonAuthorize = new JButton("Войти");


    JLabel infoLabel = new JLabel("Введите логин и пароль");
    JLabel buttonRegister = new JLabel("<html><u>Зарегистрироваться</u></html>"); // button as JLabel

    private TCPConnection connection;
    private boolean isAuthorized = false;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        areaChat.setEditable(false);
        areaChat.setLineWrap(true); // автоматический перенос слов
        add(areaChat, BorderLayout.CENTER);

        add(fieldInput, BorderLayout.SOUTH);
        fieldInput.addActionListener(new SendActionListener());

        panelAuth.add(infoLabel);
        panelAuth.add(fieldLogin);
        panelAuth.add(fieldPassword);
        buttonAuthorize.addActionListener(new ButtonAuthorizeListener());
        panelAuth.add(buttonAuthorize);
        panelAuth.add(buttonRegister);
        buttonRegister.addMouseListener(new ButtonRegisterListener());


        add(panelAuth, BorderLayout.NORTH);

        setVisible(true);

        try {
            connection = new TCPConnection(this, Config.IP, Config.PORT);
        } catch (IOException e) {
            printString("Невозможно подключиться к серверу");
        }
    }

    @Override
    public void onConnectionReady(TCPConnection connection) {
        printString("Соединение с сервером установлено");
    }

    @Override
    public void onAuthorizationAccepted(TCPConnection connection) {
        printString("Авторизация прошла успешно");
    }

    @Override
    public void onDisconnect(TCPConnection connection) {
        printString("Disconnected");
    }

    @Override
    public void onReceiveMessage(TCPConnection connection, String jsonMessage) {
        Message message = Messages.parseMessage(jsonMessage);
        if (message.getMessageType() == MessageType.RESULT_MESSAGE
                || message.getMessageType() == MessageType.AUTHORIZE_MESSAGE) {
            ResultMessageBody resBody = (ResultMessageBody)message.getMessageBody();
            if (resBody.getResult() == MessageResultType.ACCEPT) {
                printString("Регистрация/авторизация успешна");
            } else {
                printString("Регистрация/авторизация не успешна: " + resBody.getMessage());
            }
        } else if (message.getMessageType() == MessageType.REGULAR_MESSAGE) {
            RegularMessageBody regBody = (RegularMessageBody)message.getMessageBody();
            printString(regBody.getUser().getLogin() + ": " + regBody.getMessage());
        }
    }

    @Override
    public void onException(TCPConnection connection, Exception e) {

    }

    class SendActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = fieldInput.getText();
            if (text.equals("")) return;
            fieldInput.setText("");
            connection.sendMessage(Messages.createRegularMessage(user, text, null));
        }
    }

    class RegisterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            user = new User(fieldLogin.getText(), String.valueOf(fieldPassword.getPassword()));
            connection.sendMessage(Messages.createRegisterMessage(user));
        }
    }

    class ButtonRegisterListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            printString("mouse clicked");
        }

        @Override
        public void mousePressed(MouseEvent e) {
            printString("mouse pressed");
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            printString("mouse released");
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            printString("mouse entered");
        }

        @Override
        public void mouseExited(MouseEvent e) {
            printString("mouse exited");
        }
    }

    class ButtonAuthorizeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            user = new User(fieldLogin.getText(), String.valueOf(fieldPassword.getPassword()));
            connection.sendMessage(Messages.createRegisterMessage(user));
        }
    }

    private void printString(String str) {
        areaChat.append(str + "\n");
        areaChat.setCaretPosition(areaChat.getDocument().getLength());
    }
}
