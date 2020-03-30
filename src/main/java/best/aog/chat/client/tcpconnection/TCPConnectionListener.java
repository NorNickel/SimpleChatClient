package best.aog.chat.client.tcpconnection;

public interface TCPConnectionListener {
    void onConnectionReady(TCPConnection connection);
    void onDisconnect(TCPConnection connection);
    void onReceiveMessage(TCPConnection connection, String message);
    void onException(TCPConnection connection, Exception e);
}
