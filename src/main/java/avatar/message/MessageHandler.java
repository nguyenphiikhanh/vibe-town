package avatar.message;

import java.io.IOException;
import avatar.server.ServerManager;
import avatar.network.Message;
import avatar.network.Session;
import avatar.constants.Cmd;
import avatar.network.IMessageHandler;

public class MessageHandler implements IMessageHandler {

    protected final Session client;

    public MessageHandler(Session client) {
        this.client = client;
    }

    @Override
    public void onMessage(Message mss) {
        if (mss != null) {
            try {
                switch (mss.getCommand()) {
                    case Cmd.JOIN_HOUSE_4: {
                        if (this.client.user != null) {
                            this.client.doJoinHouse4(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.JOIN_OFFLINE_MAP: {
                        if (this.client.user != null) {
                            this.client.doJoinOfflineMap(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.REQUEST_IMAGE_PART: {
                        if (this.client.user != null) {
                            this.client.requestImagePart(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.REQUEST_DYNAMIC_PART: {
                        if (this.client.user != null) {
                            this.client.getAvatarService().requestPartDynaMic(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.REQUEST_TILE_MAP: {
                        if (this.client.user != null) {
                            this.client.requestTileMap(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.REQUEST_CITY_MAP: {
                        if (this.client.user != null) {
                            this.client.doRequestCityMap(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.GET_IMG_ICON: {
                        if (this.client.user != null) {
                            this.client.doGetImgIcon(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.SET_AGENT: {
                        if (this.client.user != null) {
                            this.client.agentInfo(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.GET_TILE_MAP: {
                        if (this.client.user != null) {
                            this.client.getAvatarService().getTileMap();
                            break;
                        }
                        break;
                    }
                    case Cmd.REQUEST_EXPICE_PET: {
                        if (this.client.user != null) {
                            this.client.doRequestExpicePet(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.JOIN_HOUSE: {
                        if (this.client.user != null) {
                            this.client.joinHouse(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.DIAL_LUCKY: {
                        if (this.client.user != null) {
                            this.client.doDialLucky(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.CHANGE_PASS: {
                        if (this.client.user != null) {
                            this.client.changePassword(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.COMMUNICATE: {
                        if (this.client.user != null) {
                            this.client.doCommunicate(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.TEXT_BOX: {
                        if (this.client.user != null) {
                            this.client.menuf.handlerTextBox(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.MENU_OPTION: {
                        if (this.client.user != null) {
                            this.client.menuf.handlerMenuOption(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.REQUEST_SERVICE: {
                        this.client.doRequestService(mss);
                        break;
                    }
                    case Cmd.UPDATE_CONTAINER: {
                        if (this.client.user != null) {
                            this.client.doRequestService(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.NUMBER_SUPPORT: {
                        System.out.println("-52:  " + mss.reader().readInt());
                        break;
                    }
                    case Cmd.USING_PART: {
                        if (this.client.user != null) {
                            this.client.user.usingItem(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.CONTAINER: {
                        if (this.client.user != null) {
                            this.client.user.viewChest(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.MAP_ITEM: {
                        if (this.client.user != null) {
                            this.client.getAvatarService().getMapItem();
                            break;
                        }
                        break;
                    }
                    case Cmd.MAP_ITEM_TYPE: {
                        if (this.client.user != null) {
                            this.client.getAvatarService().getMapItemType();
                            break;
                        }
                        break;
                    }
                    case Cmd.PARK_BUY_ITEM: {
                        if (this.client.user != null) {
                            this.client.doParkBuyItem(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.GET_ITEM_INFO: {
                        if (this.client.user != null) {
                            this.client.getAvatarService().getFoodData();
                            break;
                        }
                        break;
                    }
                    case Cmd.REMOVE_ITEM: {
                        if (this.client.user != null) {
                            this.client.user.doRemoveItem(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.CRE_CHARACTER: {
                        if (this.client.user != null) {
                            this.client.createCharacter(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.AVATAR_BUY_ITEM: {
                        if (this.client.user != null) {
                            this.client.buyItemShop(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.REQUEST_YOUR_INFO: {
                        if (this.client.user != null) {
                            this.client.user.requestYourInfo(mss);
                            break;
                        }
                        break;
                    }
                    case -27: {
                        this.client.handshakeMessage();
                        break;
                    }
                    case Cmd.REQUEST_FRIENDLIST: {
                        this.client.requestFriendList(mss);
                        break;
                    }
                    case Cmd.SET_PROVIDER: {
                        this.client.clientInfo(mss);
                        break;
                    }
                    case Cmd.GET_AVATAR_PART: {
                        this.client.getAvatarService().getAvatarPart();
                        break;
                    }
                    case Cmd.GET_IMAGE: {
                        this.client.getAvatarService().getImageData();
                        break;
                    }
                    case Cmd.GET_BIG: {
                        this.client.getAvatarService().getBigImage(mss);
                        break;
                    }
                    case Cmd.SET_BIG: {
                        this.client.getAvatarService().getBigData();
                        break;
                    }
                    case Cmd.LOGIN: {
                        if (this.client.user == null) {
                            this.client.doLogin(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.GET_HANDLER: {
                        if (this.client.user != null) {
                            this.client.getHandler(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.AVATAR_JOIN_PARK: {
                        if (this.client.user != null) {
                            ServerManager.joinAreaMessage(this.client.user, mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.MOVE_PARK: {
                        if (this.client.user != null) {
                            this.client.user.move(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.CHAT_PARK: {
                        if (this.client.user != null) {
                            this.client.user.chat(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.AVATAR_FEEL: {
                        if (this.client.user != null) {
                            this.client.user.doAvatarFeel(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.ACTION: {
                        if (this.client.user != null) {
                            this.client.user.doAction(mss);
                            break;
                        }
                        break;
                    }
                    case Cmd.PARK_BOARD_LIST: {
                        this.client.doiKhuVuc(mss);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionFail() {
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onConnectOK() {
    }
}
