package avatar.network;

public interface IMessageHandler {

    void onMessage(Message p0);

    void onConnectionFail();

    void onDisconnected();

    void onConnectOK();
}
