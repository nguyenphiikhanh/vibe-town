package avatar.message;

import java.io.IOException;
import avatar.network.Message;
import avatar.network.Session;
import avatar.service.FarmService;
import avatar.constants.Cmd;

public class FarmMsgHandler extends MessageHandler {

    private FarmService service;

    public FarmMsgHandler(Session client) {
        super(client);
        this.service = new FarmService(client);
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
            System.out.println("FarmMsgHandler: " + mss.getCommand());
            switch (mss.getCommand()) {
                case Cmd.SET_BIG_FARM: {
                    this.service.setBigFarm(mss);
                    break;
                }
                case Cmd.GET_BIG_FARM: {
                    this.service.getBigFarm(mss);
                    break;
                }
                case Cmd.GET_IMAGE_FARM: {
                    this.service.getImageData();
                    break;
                }
                case Cmd.GET_TREE_INFO: {
                    this.service.getTreeInfo(mss);
                    break;
                }
                case Cmd.INVENTORY: {
                    this.service.getInventory(mss);
                    break;
                }
                case Cmd.JOIN: {
                    this.service.joinFarm(mss);
                    break;
                }
                case Cmd.GET_IMG_FARM: {
                    this.service.getImgFarm(mss);
                    break;
                }

                default:
                    super.onMessage(mss);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
