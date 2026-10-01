package avatar.message;

import avatar.network.Message;
import avatar.network.Session;
import avatar.constants.Cmd;

public class AvatarMsgHandler extends MessageHandler {

    public AvatarMsgHandler(Session client) {
        super(client);
    }

    @Override
    public void onMessage(Message mss) {
        if (mss == null) {
            return;
        }
        if (this.client.user == null) {
            return;
        }
        try {
            switch (mss.getCommand()) {

                default:
                    System.out.println("AvatarMsgHandler: " + mss.getCommand());
                    super.onMessage(mss);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
