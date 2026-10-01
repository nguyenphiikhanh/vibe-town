package avatar.network;

public interface ISession {

    boolean isConnected();

    void setHandler(IMessageHandler p0);

    void sendMessage(Message p0);

    void close();
}
