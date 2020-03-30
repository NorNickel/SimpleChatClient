package best.aog.chat.client.controller;

public interface TCPConnectionListener {
    void onConnectionReady(TCPConnection connection);
    void onDisconnect(TCPConnection connection);
    void onReceiveMessage(TCPConnection connection, String message);
    void onException(TCPConnection connection, Exception e);
}
