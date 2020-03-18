package best.aog.chat.client;

import java.io.*;
import java.net.Socket;

public class Main {
    public final static String IP = "localhost";
    public final static int PORT = 8290;

    public static void main(String[] args) {
        System.out.println("Hi, best.aog.chat.client.Client!");
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        try {
            Socket socket = new Socket(IP, PORT);
            System.out.print("Введите логин: ");
            String login = console.readLine();
            System.out.print("Введите пароль: ");
            String password = console.readLine();
            Client client = new Client(login, password);
            System.out.println(client.toJSON());

            new Thread(() -> {
                try {
                    ObjectOutputStream outputStream =
                            new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(client);
                    outputStream.flush();
                    PrintWriter writer = new PrintWriter(socket.getOutputStream());
                    while (true) {
                        String message = console.readLine();
                    //writer.write(client.toJSON());
                        writer.write(message);
                        writer.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}