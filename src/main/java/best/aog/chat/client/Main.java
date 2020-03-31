package best.aog.chat.client;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import java.util.Collections;
import java.util.Map;

public class Main {
    //private Connection connection;

    public Main() {
        /*
        RegularMessageBody body = new RegularMessageBody();
        body.setUser(new User("aaa", "bbb"));
        User[] receivers = {new User("a1", "a1"), new User("a2", "a2")};
        body.setReceivers(receivers);
        body.setMessage("Hello");
        Message message = new Message(MessageType.REGULAR_MESSAGE, body);

        Gson gson = new Gson();
        String jsonmessage = gson.toJson(message);
        System.out.println(jsonmessage);

         */
        final Map<String, ?> config = Collections.emptyMap();
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonObject value = factory.createObjectBuilder()
                .add("firstName", "John")
                .add("lastName", "Smith")
                .add("age", 25)
                .add("address", factory.createObjectBuilder()
                        .add("streetAddress", "21 2nd Street")
                        .add("city", "New York")
                        .add("state", "NY")
                        .add("postalCode", "10021"))
                .add("phoneNumber", factory.createArrayBuilder()
                        .add(factory.createObjectBuilder()
                                .add("type", "home")
                                .add("number", "212 555-1234"))
                        .add(factory.createObjectBuilder()
                                .add("type", "fax")
                                .add("number", "646 555-4567")))
                .build();
        System.out.println(value);


        //connection = new Connection();
        //connection.connectToServer();

    }

    public static void main(String[] args) {
        Main main = new Main();
    }
}
/*

1) properties
2) logger to file (server + db)
3) history
4) gui
5) site

 */
/*
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

    public void startMainLoop () {

    }

    public synchronized void sendMessage(String message) {
        out.println(message);
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


public boolean register(User user) {
        boolean result = false;
        RegisterMessageBody registerMessageBody = new RegisterMessageBody(user);
        Message registerMessage = new Message(MessageType.REGISTER_MESSAGE, registerMessageBody);
        String messageToServerJson = gson.toJson(registerMessage);
        out.println(messageToServerJson);
        out.flush();
        /*
        try {
            result = processJsonMessage(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result) {
            eventListener.onAuthorisationGood(this);
        }


        return result;
        }

public boolean authorize(User user) {
        boolean result = false;
        AuthorizeMessageBody authorizeMessageBody = new AuthorizeMessageBody(user.getLogin(), user.getPassword());
        Message authorizeMessage = new Message(MessageType.AUTHORIZE_MESSAGE, authorizeMessageBody);
        String messageToServerJson = gson.toJson(authorizeMessage);
        out.println(messageToServerJson);
        out.flush();
        /*
        try {
            result = processJsonMessage(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result) {
            eventListener.onAuthorisationGood(this);
        }


        return result;
        }

private void startListeningThread() {
        new Thread(() -> {
        try {
        while (true) {
        //processJsonMessage(in.readLine());
        eventListener.onReceiveMessage(this, in.readLine());
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

 */
