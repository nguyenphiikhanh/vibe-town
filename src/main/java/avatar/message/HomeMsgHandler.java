package avatar.message;

import java.io.IOException;
import avatar.network.Message;
import avatar.network.Session;
import avatar.service.HomeService;
import avatar.constants.Cmd;

public class HomeMsgHandler extends MessageHandler {

    private HomeService service;

    public HomeMsgHandler(Session client) {
        super(client);
        this.service = new HomeService(client);
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
                case Cmd.BUY_ITEM_HOUSE: {
                    this.service.buyItemHouse(mss);
                    break;
                }
                case Cmd.SORT_ITEM_HOUSE: {
                    this.service.sortItemHouse(mss);
                    break;
                }
                case Cmd.GET_TYPE_HOUSE: {
                    this.service.getTypeHouse(mss);
                    break;
                }
                case Cmd.DEL_ITEM_HOUSE: {
                    this.service.delItemHouse(mss);
                    break;
                }
                case Cmd.CREATE_HOME: {
                    this.service.createHome(mss);
                    break;
                }
                case Cmd.GET_IMG_OBJ_INFO: {
                    this.service.getImgObjInfo(mss);
                    break;
                }

                default:
                    System.out.println("HomeMsgHandler: " + mss.getCommand());
                    super.onMessage(mss);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
