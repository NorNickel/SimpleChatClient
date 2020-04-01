package best.aog.chat.client.view;

import best.aog.chat.client.controller.TCPConnection;
import best.aog.chat.client.controller.TCPConnectionListener;
import best.aog.chat.client.model.Config;
import best.aog.chat.client.model.User;
import best.aog.chat.client.model.messages.Message;
import best.aog.chat.client.model.messages.MessageResultType;
import best.aog.chat.client.model.messages.MessageType;
import best.aog.chat.client.model.messages.Messages;
import best.aog.chat.client.model.messages.client.PrivateMessageBody;
import best.aog.chat.client.model.messages.client.RegularMessageBody;
import best.aog.chat.client.model.messages.server.AddUserMessageBody;
import best.aog.chat.client.model.messages.server.AllUsersMessageBody;
import best.aog.chat.client.model.messages.server.RemoveUserMessageBody;
import best.aog.chat.client.model.messages.server.ResultMessageBody;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private final JTextField fieldInput = new JTextField(50);

    private JPanel panelAuth = new JPanel();
    private JTextField fieldLogin = new JTextField(10);
    private JPasswordField fieldPassword = new JPasswordField(10);
    private JLabel infoLabel = new JLabel("Введите логин и пароль");
    private JButton buttonAuthorize = new JButton("Войти");
    private JButton buttonRegister = new JButton("Зарегистрироваться");

    private List<User> connectedUsers = new ArrayList<User>();
    private DefaultListModel connectedUsersModel = new DefaultListModel();
    private JList listConnectedUsers = new JList(connectedUsersModel);

    private JPanel panelSend = new JPanel();
    private DefaultComboBoxModel connectedUserCombo = new DefaultComboBoxModel();
    private JComboBox comboAllUsers = new JComboBox(connectedUserCombo);

    private TCPConnection connection;
    private boolean isAuthorized = false;

    private ClientWindow() {
        setTitle("Simple Chat by AOG (ver. 1.0)");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);

        areaChat.setEditable(false);
        areaChat.setLineWrap(true); // автоматический перенос слов
        getContentPane().add(new JScrollPane(areaChat), BorderLayout.CENTER);

        //getContentPane().add(fieldInput, BorderLayout.SOUTH);
        //fieldInput.addActionListener(new SendActionListener());

        panelAuth.add(infoLabel);
        panelAuth.add(fieldLogin);
        panelAuth.add(fieldPassword);
        buttonAuthorize.addActionListener(new ButtonAuthorizeListener());
        panelAuth.add(buttonAuthorize);
        buttonRegister.addActionListener(new ButtonRegisterListener());
        panelAuth.add(buttonRegister);

        getContentPane().add(panelAuth, BorderLayout.NORTH);

        getContentPane().add(new JScrollPane(listConnectedUsers), BorderLayout.EAST);

        connectedUserCombo.addElement("Отправить всем:");
        panelSend.add(comboAllUsers, BorderLayout.WEST);
        fieldInput.addActionListener(new SendActionListener());
        panelSend.add(fieldInput, BorderLayout.EAST);

        getContentPane().add(panelSend, BorderLayout.SOUTH);

        System.out.println(comboAllUsers.getSelectedItem());

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
                user = new User(fieldLogin.getText(), String.valueOf(fieldPassword.getPassword()));
            } else {
                printString("Регистрация/авторизация не успешна: " + resBody.getMessage());
            }
        } else if (message.getMessageType() == MessageType.REGULAR_MESSAGE) {
            RegularMessageBody regBody = (RegularMessageBody)message.getMessageBody();
            String loginFrom = regBody.getUser().getLogin();
            String from = (loginFrom.equals(user.getLogin()) ? "YOU" : loginFrom);
            printString(from + ": " + regBody.getMessage());
        } else if (message.getMessageType() == MessageType.ALL_USERS) {
            AllUsersMessageBody body = (AllUsersMessageBody)message.getMessageBody();
            addConnectedUsers(body.getLogin());
        }else if (message.getMessageType() == MessageType.USER_ADDED) {
            AddUserMessageBody body = (AddUserMessageBody)message.getMessageBody();
            addConnectedUser(body.getUserName());
        }else if (message.getMessageType() == MessageType.USER_REMOVED) {
            RemoveUserMessageBody body = (RemoveUserMessageBody)message.getMessageBody();
            removeConnectedUser(body.getUserName());
        } else if (message.getMessageType() == MessageType.PRIVATE) {
            PrivateMessageBody body = (PrivateMessageBody)message.getMessageBody();
            String loginFrom = body.getUserName();
            String from = (loginFrom.equals(user.getLogin()) ? "YOU: [PRIVATE to " + body.getReceiverUserName() + "]" : loginFrom + " [PRIVATE]");
            printString(from + ": " + body.getMessage());
        }

    }

    public void addConnectedUser(String userName) {
        connectedUsersModel.addElement(userName);
        if (userName != user.getLogin()) {
            connectedUserCombo.addElement(userName);
        }
    }

    public void addConnectedUsers(String[] userNames) {
        connectedUsersModel.addAll(Arrays.asList(userNames));
        connectedUserCombo.addAll(Arrays.asList(userNames));
    }

    public void removeConnectedUser(String userName) {
        connectedUsersModel.removeElement(userName);
        if (connectedUserCombo.getSelectedItem().equals(userName)) {
            connectedUserCombo.setSelectedItem("Отправить всем:");
        }
        connectedUserCombo.removeElement(userName);
        printString("Нас покинул " + userName);
    }


    @Override
    public void onException(TCPConnection connection, Exception e) {
        printString("Ошибка: " + e);
    }

    class SendActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = fieldInput.getText();
            if (text.equals("")) return;
            fieldInput.setText("");

            if (comboAllUsers.getSelectedItem().equals("Отправить всем:")) {
                connection.sendMessage(Messages.createRegularMessage(user, text));
            } else {
                connection.sendMessage(
                        Messages.createPrivateMessage(user.getLogin(), text, (String) comboAllUsers.getSelectedItem()));
            }
        }
    }

    class ButtonRegisterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            user = new User(fieldLogin.getText(), String.valueOf(fieldPassword.getPassword()));
            connection.sendMessage(Messages.createRegisterMessage(user));
        }
    }

    class ButtonAuthorizeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String authorizeMessage = Messages.createAuthorizeMessage(
                    fieldLogin.getText(), String.valueOf(fieldPassword.getPassword()));
            connection.sendMessage(authorizeMessage);
        }
    }

    private void printString(String str) {
        areaChat.append(str + "\n");
        areaChat.setCaretPosition(areaChat.getDocument().getLength());
    }
}
