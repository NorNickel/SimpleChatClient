package best.aog.chat.client.tcpconnection;

import best.aog.chat.client.model.Config;
import best.aog.chat.client.model.User;
import best.aog.chat.client.model.messages.Message;
import best.aog.chat.client.model.messages.MessageResultType;
import best.aog.chat.client.model.messages.MessageType;
import best.aog.chat.client.model.messages.client.AuthorizeMessageBody;
import best.aog.chat.client.model.messages.client.RegisterMessageBody;
import best.aog.chat.client.model.messages.client.RegularMessageBody;
import best.aog.chat.client.model.messages.server.ResultMessageBody;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class TCPConnection {

    private User user;
    private Socket socket;
    private Thread receiveThread;
    private final TCPConnectionListener eventListener;
    private BufferedReader in;
    private PrintWriter out;

    public TCPConnection(TCPConnectionListener eventListener, String host, int port) throws IOException {
        this(eventListener, new Socket(host, port));
    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                eventListener.onConnectionReady(TCPConnection.this);
                while (!receiveThread.isInterrupted()) {
                    String message = null;
                    try {
                        message = in.readLine();
                        eventListener.onReceiveMessage(TCPConnection.this, message);
                    } catch (IOException e) {

                    } finally {

                    }
                }
            }
        });
        receiveThread.start();
    }

    public synchronized void sendMessage(String message) {
        out.println(message);
        out.flush();
    }

    public synchronized void disconnect() {
        receiveThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    public void connectToServer() {
        try {
            System.out.println("Start!");
            socket = new Socket(Config.IP, Config.PORT);
            System.out.println("Подключены к серверу");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Введите логин:");
            String login = console.readLine();
            System.out.println("Введите пароль:");
            String password = console.readLine();

            User newUser = new User(login, password);
            if (register(newUser)) {
                user = newUser; // 1 Регистрация
            } else if (authorize(newUser)) {
                user = newUser; // 2 Авторизация
            }
        } catch (ConnectException e) {
            System.out.println("Невозможно подключиться к серверу");
        } catch (IOException e) {
            System.out.println("Сервак упал, сушите весла");
            //???!!! e.printStackTrace();
        }
    }

    private boolean register(User newUser) {
        boolean result = false;
        RegisterMessageBody registerMessageBody = new RegisterMessageBody(newUser);
        Message registerMessage = new Message(MessageType.REGISTER_MESSAGE, registerMessageBody);
        String messageToServerJson = gson.toJson(registerMessage);
        out.println(messageToServerJson);
        out.flush();
        try {
            result = processJsonMessage(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean authorize(User user) {
        boolean result = false;
        AuthorizeMessageBody authorizeMessageBody = new AuthorizeMessageBody(user.getLogin(), user.getPassword());
        Message authorizeMessage = new Message(MessageType.AUTHORIZE_MESSAGE, authorizeMessageBody);
        String messageToServerJson = gson.toJson(authorizeMessage);
        out.println(messageToServerJson);
        out.flush();
        try {
            result = processJsonMessage(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void startListeningThread() {
        new Thread(() -> {
            try {
                while (true) {
                    processJsonMessage(in.readLine());
                }
            } catch (IOException e) {
                System.out.println("Сервак упал, сушите весла");
                //e.printStackTrace();
            }
        }).start();
    }

    private void startWriterThread() {
        new Thread(() -> {
            try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
                while (true) {
                    String messageFromConsole = console.readLine();
/*
                    RegularMessageBody body = new RegularMessageBody();
                    body.setUser(new User("aaa", "bbb"));
                    User[] receivers = {new User("a1", "a1"), new User("a2", "a2")};
                    body.setReceivers(receivers);
                    body.setMessage("Hello");
                    Message message = new Message(MessageType.REGULAR_MESSAGE, body);
                    String messageToServerJson = gson.toJson(message);
                    */
                    RegularMessageBody regBody = new RegularMessageBody(this.user, messageFromConsole, null);
                    Message message = new Message(MessageType.REGULAR_MESSAGE, regBody);
                    String messageToServerJson = gson.toJson(message);

                    out.println(messageToServerJson);
                    out.flush();
                }
            } catch (IOException e) {
                System.out.println("Сервак упал, сушите весла");
                //e.printStackTrace();
            }
        }).start();
    }

    private boolean processJsonMessage(String messageFromServerJson) {
        boolean result = false;
        JsonObject jsonObject = JsonParser.parseString(messageFromServerJson).getAsJsonObject();
        MessageType messageType = gson.fromJson(jsonObject.get("messageType"), MessageType.class);
        if (messageType == MessageType.RESULT_MESSAGE || messageType == MessageType.AUTHORIZE_MESSAGE ) {
            ResultMessageBody resBody = gson.fromJson(jsonObject.get("messageBody"), ResultMessageBody.class);
            if (resBody.getResult() == MessageResultType.ACCEPT) {
                System.out.println("Регистрация/авторизация успешна");
                startWriterThread();
                startListeningThread();
                result = true;
            } else {
                System.out.println("Регистрация/авторизация не успешна: " + resBody.getMessage());
                result = false;
            }
        } else if (messageType == MessageType.REGULAR_MESSAGE) {
            RegularMessageBody regBody = gson.fromJson(jsonObject.get("messageBody"), RegularMessageBody.class);
            System.out.println(regBody.getUser().getLogin() + ": " + regBody.getMessage());
        }
        return result;
    }
}
