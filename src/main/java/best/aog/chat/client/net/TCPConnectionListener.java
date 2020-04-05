package best.aog.chat.client.net;

public interface TCPConnectionListener {
    void onConnectionReady(TCPConnection connection);
    void onAuthorizationAccepted(TCPConnection connection);
    void onDisconnect(TCPConnection connection);
    void onReceiveMessage(TCPConnection connection, String message);
    void onException(TCPConnection connection, Exception e);
}
