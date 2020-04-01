package best.aog.chat.client.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPConnection {

    private Socket socket;
    private Thread receiveThread;
    private TCPConnectionListener eventListener;
    private BufferedReader in;
    private PrintWriter out;

    public TCPConnection(TCPConnectionListener eventListener, String host, int port) throws IOException {
        this(eventListener, new Socket(host, port));
    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) {
        this.eventListener = eventListener;
        try {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            receiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        eventListener.onConnectionReady(TCPConnection.this);
                        while (!receiveThread.isInterrupted()) {
                            eventListener.onReceiveMessage(TCPConnection.this, in.readLine());
                        }
                    } catch (IOException e) {
                        eventListener.onException(TCPConnection.this, e);
                    } finally {
                        eventListener.onDisconnect(TCPConnection.this);
                    }
                }
            });
            receiveThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
} //???!!!