package best.aog.chat.client.gui;

import best.aog.chat.client.messages.*;
import best.aog.chat.client.model.Config;
import best.aog.chat.client.model.User;
import best.aog.chat.client.net.TCPConnection;
import best.aog.chat.client.net.TCPConnectionListener;

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

    private static final String SEND_TO_ALL_WORDS = "Отправить всем:";
    private String userName = "";

    private JPanel panelChat;
    private final JTextArea areaChat = new JTextArea(20, 40);
    private DefaultListModel connectedUsersModel = new DefaultListModel();
    private JList listConnectedUsers = new JList(connectedUsersModel);

    private JPanel panelAuth;
    private final JTextField fieldInput = new JTextField(20);
    private JPasswordField fieldPassword = new JPasswordField(10);
    private JLabel labelLogin = new JLabel("Логин:");
    private JLabel labelPassword = new JLabel("Пароль:");
    private JButton buttonAuthorize = new JButton("Войти");
    private JButton buttonRegister = new JButton("Зарегистрироваться");
    private JLabel labelAuthInfo;

    private JPanel panelSend;
    private DefaultComboBoxModel comboSendTo = new DefaultComboBoxModel();
    private JComboBox comboAllUsers = new JComboBox(comboSendTo);
    private JTextField fieldLogin = new JTextField(10);
    private JButton buttonSend = new JButton("Отправить");

    private JPanel panelAfterAuth;
    private JLabel labelConnectedAs = new JLabel();
    private JButton buttonDisconnect = new JButton("Выйти из чата");

    private TCPConnection connection;

    private static final Dimension AUTH_WINDOW_SIZE = new Dimension(350, 200);

    private ClientWindow() {
        setTitle("Chat by AOG");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        panelAuth = createAuthPane();
        panelSend = createSendPanel();
        panelChat = createChatPanel();
        panelAfterAuth = createAfterAuthPanel();

        connectToServer();
        showAuthWindow(false);
        setVisible(true);
    }

    private void connectToServer() {
        try {
            connection = new TCPConnection(this, Config.IP, Config.PORT);
        } catch (IOException e) {
            String errorText = "Невозможно подключиться к серверу";
            System.out.println(errorText);
            JOptionPane.showMessageDialog(this, errorText, "Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private JPanel createAuthPane() {
        JPanel panelAuth = new JPanel();
        panelAuth.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        gbc.fill = GridBagConstraints.WEST;
        panelAuth.add(labelLogin, gbc);
        gbc.gridy++;
        panelAuth.add(labelPassword, gbc);

        gbc.fill = GridBagConstraints.CENTER;
        gbc.gridx++;
        gbc.gridy = 0;
        panelAuth.add(fieldLogin, gbc);
        gbc.gridy++;
        panelAuth.add(fieldPassword, gbc);

        gbc.fill = GridBagConstraints.WEST;
        gbc.gridy = 1;
        gbc.gridx++;
        buttonAuthorize.addActionListener(new ButtonAuthorizeListener());
        panelAuth.add(buttonAuthorize, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        labelAuthInfo = new JLabel("Войдите или зарегистрируйтесь");
        panelAuth.add(labelAuthInfo, gbc);

        gbc.gridy++;

        buttonRegister.addActionListener(new ButtonRegisterListener());
        panelAuth.add(buttonRegister, gbc);

        return panelAuth;
    }

    private JPanel createSendPanel() {
        JPanel panelSend = new JPanel();
        panelSend.setLayout(new GridBagLayout());

        comboSendTo.addElement(SEND_TO_ALL_WORDS);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelSend.add(comboAllUsers, gbc);

        gbc.gridx++;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        SendActionListener sendActionListener = new SendActionListener();
        fieldInput.addActionListener(sendActionListener);
        panelSend.add(fieldInput, gbc);

        gbc.gridx++;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.PAGE_END;

        buttonSend.addActionListener(sendActionListener);
        panelSend.add(buttonSend, gbc);

        return panelSend;
    }

    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        areaChat.setLineWrap(true); // автоматический перенос слов
        chatPanel.add(new JScrollPane(areaChat), gbc);

        gbc.gridx += 20;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;

        gbc.gridy++;
        listConnectedUsers.setPreferredSize(new Dimension(150, 0));
        JScrollPane scrollPane = new JScrollPane(listConnectedUsers);
        scrollPane.setMinimumSize(new Dimension(20, 50));
        chatPanel.add(scrollPane, gbc);

        return chatPanel;
    }

    private JPanel createAfterAuthPanel() {
        panelAfterAuth = new JPanel();

        panelAfterAuth.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;

        gbc.anchor = GridBagConstraints.WEST;
        panelAfterAuth.add(labelConnectedAs, gbc);

        buttonDisconnect.addActionListener(new ButtonDisconnectListener());
        //gbc.gridx++;
        //gbc.anchor = GridBagConstraints.EAST;
        //panelAfterAuth.add(buttonDisconnect, gbc);

        return panelAfterAuth;
    }

    private void showAuthWindow(boolean isFromChatWindow) {
        if (isFromChatWindow) {
            getContentPane().remove(panelAfterAuth);
            getContentPane().remove(panelChat);
            getContentPane().remove(panelSend);
        }
        getContentPane().add(panelAuth, BorderLayout.CENTER);
        setMinimumSize(AUTH_WINDOW_SIZE);
        setSize(AUTH_WINDOW_SIZE);
        repaint();
        setResizable(false);
    }

    private void showChatWindow() {
        getContentPane().remove(panelAuth);

        labelConnectedAs.setText("Вы зашли в чат как " + connection.getUserName());
        getContentPane().add(panelAfterAuth, BorderLayout.NORTH);
        getContentPane().add(panelChat, BorderLayout.CENTER);
        getContentPane().add(panelSend, BorderLayout.SOUTH);
        pack();
        setResizable(true);
        setMinimumSize(getSize());
        getContentPane().repaint();
    }

    @Override
    public void onConnectionReady(TCPConnection connection) {
        System.out.println("Соединение с сервером установлено");
    }

    @Override
    public void onAuthorizationAccepted(TCPConnection connection) {
        connection.setUserName(fieldLogin.getText());
        showChatWindow();
        printToChatArea("Вы успешно авторизовались! Приятного общения!");
    }

    @Override
    public void onDisconnect(TCPConnection connection) {
        try {
            connection.close();
            connectToServer();
            showAuthWindow(true);
        } catch (IOException e) {
            printToAuthLabel("Ошибка: " + e);
        }
    }

    @Override
    public void onReceiveMessage(TCPConnection connection, String jsonMessage) {
        Message message = Messages.parseMessage(jsonMessage);
        MessageType messageType = message.getType();

        if (messageType == MessageType.RESULT) {
            ResultMessageBody resBody = (ResultMessageBody)message.getBody();
            if (resBody.getResult() == MessageResultType.ACCEPT) {
                onAuthorizationAccepted(connection);
            } else {
                printToAuthLabel(resBody.getMessage());
            }

        }  else if (messageType == MessageType.ALL_USERS) {
            AllUsersMessageBody body = (AllUsersMessageBody)message.getBody();
            addConnectedUsers(body.getUserName());

        }else if (messageType == MessageType.USER_ADDED) {
            AddUserMessageBody body = (AddUserMessageBody)message.getBody();
            addConnectedUser(body.getUserName());

        }else if (messageType == MessageType.USER_REMOVED) {
            RemoveUserMessageBody body = (RemoveUserMessageBody)message.getBody();
            removeConnectedUser(body.getUserName());

        } else if (messageType == MessageType.REGULAR) {
            RegularMessageBody regBody = (RegularMessageBody)message.getBody();
            String loginFrom = regBody.getUserName();
            String from = (loginFrom.equals(this.userName) ?
                    "Вы" : loginFrom);
            printToChatArea(from + ": " + regBody.getMessage());

        } else if (messageType == MessageType.PRIVATE) {
            PrivateMessageBody body = (PrivateMessageBody)message.getBody();
            String loginFrom = body.getUserName();
            String from = (loginFrom.equals(this.userName) ?
                    "Вы: [лично для " + body.getReceiverUserName() + "]:" :
                    "[лично от " + loginFrom + "]:");
            printToChatArea(from + ": " + body.getMessage());
        }
    }

    public void addConnectedUser(String userName) {
        connectedUsersModel.addElement(userName);
        if (!userName.equals(this.userName)) {
            comboSendTo.addElement(userName);
        }
        printToChatArea("К нам присодиняется " + userName + " )))");
    }

    public void addConnectedUsers(String[] userNames) {
        List<String> userNamesList = new ArrayList<>(Arrays.asList(userNames));
        connectedUsersModel.addAll(userNamesList);
        userNamesList.remove(userName);
        comboSendTo.addAll(userNamesList);
    }

    public void removeConnectedUser(String userName) {
        connectedUsersModel.removeElement(userName);
        if (comboSendTo.getSelectedItem().equals(userName)) {
            comboSendTo.setSelectedItem(SEND_TO_ALL_WORDS);
        }
        comboSendTo.removeElement(userName);
        printToChatArea("Нас покидает " + userName + " (((");
    }

    @Override
    public void onException(TCPConnection connection, Exception e) {
        printToAuthLabel("Сервак упал, сушите весла!");
    }

    class SendActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = fieldInput.getText();
            if (text.equals("")) return;
            fieldInput.setText("");

            if (comboAllUsers.getSelectedItem().equals(SEND_TO_ALL_WORDS)) {
                connection.sendMessage(new Message(MessageType.REGULAR, new RegularMessageBody(userName, text)));
            } else {
                connection.sendMessage(
                        new Message(MessageType.PRIVATE,
                                new PrivateMessageBody(userName, text, (String)comboAllUsers.getSelectedItem())));
            }
        }
    }

    class ButtonRegisterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!checkInput()) return;
            userName = fieldLogin.getText();
            String password = String.valueOf(fieldPassword.getPassword());
            connection.sendMessage(
                    new Message(MessageType.REGISTER,
                            new RegisterMessageBody((new User(userName, password)))));
        }
    }

    class ButtonAuthorizeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!checkInput()) return;
            userName = fieldLogin.getText();
            String password = String.valueOf(fieldPassword.getPassword());
            connection.sendMessage(new Message(MessageType.AUTHORIZE, new AuthorizeMessageBody(userName, password)));
        }
    }

    class ButtonDisconnectListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            connection.disconnect();
        }
    }

    private boolean checkInput() {
        if (fieldLogin.getText().length() == 0
                || fieldPassword.getPassword().length == 0) {

            labelAuthInfo.setText("<html><font color='red'>Логин и пароль не могут быть пустыми</font></html>");
            return false;
        }
        return true;
    }

    private void printToChatArea(String text) {
        areaChat.append(text + "\n");
        areaChat.setCaretPosition(areaChat.getDocument().getLength());
    }

    private void printToAuthLabel(String text) {
        labelAuthInfo.setText("<html><font color='red'>" + text + "</font></html>");
    }

}
