package avatar.service;

import avatar.item.Item;
import avatar.item.PartManager;
import avatar.item.Part;
import avatar.model.Food;
import avatar.model.FoodManager;
import avatar.model.ImageInfo;
import avatar.model.MapItem;
import avatar.model.MapItemType;
import avatar.model.Position;
import avatar.server.Avatar;
import avatar.server.ServerManager;
import avatar.manager.GameDataManager;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import avatar.constants.Cmd;
import avatar.model.Command;
import avatar.model.Menu;
import avatar.model.User;
import avatar.network.Message;
import avatar.network.Session;
import avatar.play.Map;
import avatar.play.Zone;
import org.apache.log4j.Logger;

public class AvatarService extends Service {

    private static final Logger logger = Logger.getLogger(AvatarService.class);

    public AvatarService(Session cl) {
        super(cl);
    }

    public void openUIShop(int id, String name, List<Item> items) {
        try {
            System.out.println("openShop lent: " + items.size());
            Message ms = new Message(Cmd.OPEN_SHOP);
            DataOutputStream ds = ms.writer();
            ds.writeByte(id);
            ds.writeUTF(name);
            ds.writeShort(items.size());
            for (Item i : items) {
                ds.writeShort(i.getId());
            }
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("doRequestExpicePet ", ex);
        }
    }

    public void doRequestExpicePet(Message mss) {
        try {
            int userID = mss.reader().readInt();
            Message ms = new Message(Cmd.REQUEST_EXPICE_PET);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.writeByte(0);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("doRequestExpicePet ", ex);
        }
    }

    public void showUICreateChar(byte type) {
        try {
            Message ms = new Message(Cmd.CREATE_CHAR_INFO);
            DataOutputStream ds = ms.writer();
            ds.writeByte(type);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("showUICreateChar ", ex);
        }
    }

    public void viewChest(List<Item> chests) {
        try {
            Message ms = new Message(Cmd.CONTAINER);
            DataOutputStream ds = ms.writer();
            ds.writeShort(chests.size());
            for (Item item : chests) {
                ds.writeShort(item.getId());
                ds.writeByte(100 - item.reliability());
                ds.writeUTF(item.expiredString());
            }
            ds.flush();
            sendMessage(ms);
        } catch (IOException e) {
            logger.error("viewChest ", e);
        }
    }

    public void chatTo(String sender, String content) {
        try {
            Message ms = new Message(Cmd.CHAT_TO);
            DataOutputStream ds = ms.writer();
            ds.writeInt(1);
            ds.writeUTF(sender);
            ds.writeUTF(content);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("chatTo ", ex);
        }
    }

    public void onLoginSuccess() {
        try {
            User us = session.user;
            List<Item> wearing = us.getWearing();
            List<Command> listCmd = us.getListCmd();
            List<Command> listCmdRotate = us.getListCmdRotate();

            // Đảm bảo luôn có đủ 5 items để gửi cho client
            List<Item> wearingToSend = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                if (i < wearing.size() && wearing.get(i) != null) {
                    wearingToSend.add(wearing.get(i));
                } else {
                    Item emptyItem = new Item(0);
                    emptyItem.init(); // Gọi init để load Part
                    wearingToSend.add(emptyItem);
                }
            }

            Message ms5 = new Message(Cmd.LOGIN_SUCESS);
            DataOutputStream ds = ms5.writer();
            ds.writeInt(us.getId());
            ds.writeByte(wearingToSend.size());
            for (Item itm : wearingToSend) {
                ds.writeShort(itm.getId());
            }
            ds.writeByte(us.getGender());
            ds.writeByte(us.getLeverMain());
            ds.writeByte(us.getLeverMainPercen());
            ds.writeInt(us.getXu());
            ds.writeByte(us.getFriendly());
            ds.writeByte(us.getCrazy());
            ds.writeByte(us.getStylish());
            ds.writeByte(us.getHappy());
            ds.writeByte(100 - us.getHunger());
            ds.writeInt(us.getLuong());
            ds.writeByte(us.getStar());
            for (Item itm : wearingToSend) {
                ds.writeByte(itm.reliability());
                ds.writeUTF(itm.expiredString());
            }
            ds.writeShort(us.getIdImg());
            ds.writeByte(listCmd.size());
            for (Command cmd : listCmd) {
                ds.writeUTF(cmd.getName());
                ds.writeShort(cmd.getIcon());
            }
            ds.writeByte(listCmdRotate.size());
            for (Command cmd : listCmdRotate) {
                ds.writeShort(cmd.getAnthor());
                ds.writeUTF(cmd.getName());
                ds.writeShort(cmd.getIcon());
            }
            ds.writeBoolean(true);// isTour
            for (Command cmd : listCmdRotate) {
                ds.writeByte(cmd.getType());
            }
            ds.writeByte(1);
            ds.writeShort(us.getLeverMain());
            ds.writeShort(-1);
            ds.writeBoolean(session.isNewVersion());//new version
            if (session.isNewVersion()) {
                ds.writeInt(us.getXeng());
            }
            int m = 4;
            ds.writeByte((byte) m);
            short[] IDAction = {103, 102, 104, 107};
            String[] actionName = new String[]{"Tặng Hoa Violet", "Hôn", "Tặng cánh hoa",
                "Tặng Hoa Tuyết"};
            short[] IDIcon = {1124, 1188, 1187, 1173};
            int[] money = {20000, 2000, 10000, 5};
            byte[] typeMoney = {0, 0, 0, 1};
            for (int i2 = 0; i2 < m; ++i2) {
                ds.writeShort(IDAction[i2]);
                ds.writeUTF(actionName[i2]);
                ds.writeShort(IDIcon[i2]);
                ds.writeInt(money[i2]);
                ds.writeByte(typeMoney[i2]);
            }
            ds.writeInt(us.getLuong());
            ds.writeInt(us.getLuongKhoa());
            ds.writeByte(1);
            ds.writeUTF(us.getUsername());
            ds.flush();
            sendMessage(ms5);
        } catch (IOException ex) {
            logger.error("onLoginSuccess err", ex);
        }
    }

    public void getAvatarPart() {
        try {
            List<Part> parts = PartManager.getInstance().getAvatarPart();
            try {
                logger.info("[GET_AVATAR_PART] parts=" + (parts != null ? parts.size() : -1));
            } catch (Exception ignore) {
            }
            Message ms = new Message(Cmd.GET_AVATAR_PART);
            DataOutputStream ds = ms.writer();
            ds.writeShort(parts.size());
            for (Part part : parts) {
                ds.writeShort(part.getId());
                ds.writeInt(part.getCoin());
                ds.writeShort(part.getGold());
                short type = part.getType();
                ds.writeShort(type);
                switch (type) {
                    case -2:
                        ds.writeUTF(part.getName());
                        ds.writeByte(part.getSell());
                        ds.writeShort(part.getIcon());
                        break;

                    case -1:
                        ds.writeUTF(part.getName());
                        ds.writeByte(part.getSell());
                        ds.writeByte(part.getZOrder());
                        ds.writeByte(part.getGender());
                        ds.writeByte(part.getLevel());
                        ds.writeShort(part.getIcon());
                        short[] imgID = part.getImgID();
                        byte[] dx = part.getDx();
                        byte[] dy = part.getDy();
                        for (int i = 0; i < 15; i++) {
                            ds.writeShort(imgID != null && i < imgID.length ? imgID[i] : -1);
                            ds.writeByte(dx != null && i < dx.length ? dx[i] : 0);
                            ds.writeByte(dy != null && i < dy.length ? dy[i] : 0);
                        }
                        break;

                    default:
                        ds.writeShort(part.getIcon());
                        break;
                }
            }
            ds.flush();
            sendMessage(ms);
        } catch (Exception e) {
            logger.error("getAvatarPart() ", e);
        }
    }

    public void requestPartDynaMic(Message ms) {
        try {
            short itemID = ms.reader().readShort();
            Part part = PartManager.getInstance().findPartByID(itemID);
            if (part == null) {
                logger.warn("REQUEST_DYNAMIC_PART missing part id=" + itemID);
                return;
            }
            ms = new Message(Cmd.REQUEST_DYNAMIC_PART);
            DataOutputStream ds = ms.writer();
            ds.writeShort(part.getId());
            ds.writeInt(part.getCoin());
            ds.writeShort(part.getGold());
            short type = part.getType();
            ds.writeShort(type);
            switch (type) {
                case -2:
                    ds.writeUTF(part.getName());
                    ds.writeByte(part.getSell());
                    ds.writeShort(part.getIcon());
                    break;

                case -1:
                    ds.writeUTF(part.getName());
                    ds.writeByte(part.getSell());
                    ds.writeByte(part.getZOrder());
                    ds.writeByte(part.getGender());
                    ds.writeByte(part.getLevel());
                    ds.writeShort(part.getIcon());
                    short[] imgID2 = part.getImgID();
                    byte[] dx2 = part.getDx();
                    byte[] dy2 = part.getDy();
                    for (int i = 0; i < 15; i++) {
                        ds.writeShort(imgID2 != null && i < imgID2.length ? imgID2[i] : -1);
                        ds.writeByte(dx2 != null && i < dx2.length ? dx2[i] : 0);
                        ds.writeByte(dy2 != null && i < dy2.length ? dy2[i] : 0);
                    }
                    break;

                default:
                    ds.writeShort(part.getIcon());
                    break;
            }
            ds.flush();
            this.sendMessage(ms);
        } catch (IOException ex) {
            logger.error("requestPartDynaMic() ", ex);
        }
    }

    public void enter(Zone z) {
        try {
            List<User> players = z.getPlayers();
            Map map = z.getMap();
            try {
                int selfId = (session != null && session.user != null) ? session.user.getId() : -1;
                int wearingCount = (session != null && session.user != null && session.user.getWearing() != null)
                        ? session.user.getWearing().size() : -1;
                logger.info("[ENTER_MAP] user=" + selfId
                        + " map=" + (map != null ? map.getId() : -1)
                        + " zone=" + z.getId()
                        + " players=" + (players != null ? players.size() : -1)
                        + " wearing=" + wearingCount);
                if (session != null && session.user != null && session.user.getWearing() != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("[ENTER_MAP] wearingIds=");
                    for (Item it : session.user.getWearing()) {
                        if (it != null) {
                            sb.append(it.getId()).append(",");
                        }
                    }
                    logger.info(sb.toString());

                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("[ENTER_MAP] wearingPartNull=");
                    for (Item it : session.user.getWearing()) {
                        if (it == null) {
                            continue;
                        }
                        it.init();
                        sb2.append(it.getId()).append(":").append(it.getPart() == null ? "null" : "ok").append(",");
                    }
                    logger.info(sb2.toString());
                }
            } catch (Exception ignore) {
            }
            Message ms = new Message(Cmd.AVATAR_JOIN_PARK);
            DataOutputStream ds = ms.writer();
            ds.writeByte(map.getId());
            ds.writeByte(z.getId());
            ds.writeShort(-1);
            ds.writeShort(-1);
            int numUser = players.size();
            ds.writeByte((byte) numUser);
            for (User pl : players) {
                ds.writeInt(pl.getId());
                ds.writeUTF(pl.getUsername());
                List<Item> plWearing = pl.getWearing();
                if (plWearing == null) {
                    plWearing = new ArrayList<>();
                }
                // Đảm bảo luôn có đủ 5 items
                List<Item> wearingToSend = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    if (i < plWearing.size() && plWearing.get(i) != null) {
                        wearingToSend.add(plWearing.get(i));
                    } else {
                        Item emptyItem = new Item(0);
                        emptyItem.init();
                        wearingToSend.add(emptyItem);
                    }
                }
                ds.writeByte(wearingToSend.size());
                for (Item item : wearingToSend) {
                    ds.writeShort(item.getId());
                }
                ds.writeShort(pl.getX());
                ds.writeShort(pl.getY());
                ds.writeByte(pl.getRole());//0 la npc
            }
            for (User pl : players) {
                ds.writeByte(pl.getDirect());
            }
            for (int i = 0; i < numUser; ++i) {
                ds.writeByte(101);
            }
            for (int i = 0; i < numUser; ++i) {
                ds.writeShort(-1);
            }
            ds.writeByte(0);
            ds.writeByte(0);
            List<MapItem> mapItems = map.getMapItems();
            if (mapItems == null) {
                mapItems = new java.util.ArrayList<>();
            }
            List<MapItemType> mapItemTypes = map.getMapItemTypes();
            if (mapItemTypes == null) {
                mapItemTypes = new java.util.ArrayList<>();
            }
            logger.info("[ENTER_MAP] mapItems=" + mapItems.size() + " mapItemTypes(raw)=" + mapItemTypes.size());
            ds.writeShort(mapItems.size());
            java.util.ArrayList<MapItemType> nonNullItemTypes = new java.util.ArrayList<>();
            for (MapItemType t : mapItemTypes) {
                if (t != null) {
                    nonNullItemTypes.add(t);
                }
            }
            logger.info("[ENTER_MAP] mapItemTypes(nonNull)=" + nonNullItemTypes.size());
            ds.writeByte(nonNullItemTypes.size());
            for (MapItemType mapItemType : nonNullItemTypes) {
                ds.writeByte(mapItemType.getId());
                ds.writeShort(mapItemType.getImgID());
                ds.writeByte(mapItemType.getIconID());
                ds.writeShort(mapItemType.getDx());
                ds.writeShort(mapItemType.getDy());
                List<Position> positions = mapItemType.getListNotTrans();
                if (positions == null) {
                    positions = new java.util.ArrayList<>();
                }
                ds.writeByte(positions.size());
                for (Position position : positions) {
                    ds.writeByte(position.getX());
                    ds.writeByte(position.getY());
                }
            }
            ds.writeByte(mapItems.size());
            for (MapItem mapItem : mapItems) {
                ds.writeByte(mapItem.getType());
                ds.writeByte(mapItem.getTypeID());
                ds.writeByte(mapItem.getX());
                ds.writeByte(mapItem.getY());
            }
            for (int i = 0; i < numUser; ++i) {
                ds.writeShort(-1);
            }
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("enter() ", ex);
        }
    }

    public void getImageData() {
        try {
            List<ImageInfo> imageInfos = GameDataManager.getInstance().getItemImageData();
            Message ms = new Message(Cmd.GET_IMAGE);
            DataOutputStream ds = ms.writer();
            ds.writeShort(imageInfos.size());
            for (ImageInfo imageInfo : imageInfos) {
                ds.writeShort(imageInfo.getId());
                ds.writeShort(imageInfo.getBigImageID());
                ds.writeByte(imageInfo.getX());
                ds.writeByte(imageInfo.getY());
                ds.writeByte(imageInfo.getW());
                ds.writeByte(imageInfo.getH());
            }
            ds.flush();
            this.sendMessage(ms);
        } catch (IOException e) {
            logger.error("getImageData() ", e);
        }
    }

    public void getMapItemType() {
        try {
            Message ms = new Message(Cmd.MAP_ITEM_TYPE);
            DataOutputStream ds = ms.writer();
            ds.write(GameDataManager.getInstance().getMapItemTypesData());
            ds.flush();
            this.sendMessage(ms);
        } catch (IOException e) {
            logger.error("getMapItemType() ", e);
        }
    }

    public void getTileMap() {
        try {
            byte[] dat = Avatar.getFile(session.getResourcesPath() + "house/tile.png");
            if (dat == null) {
                return;
            }
            Message ms = new Message(Cmd.GET_TILE_MAP);
            DataOutputStream ds = ms.writer();
            ds.writeShort(21);
            ds.writeInt(dat.length);
            ds.write(dat);
            ds.flush();
            this.sendMessage(ms);
        } catch (IOException e) {
            logger.error("getTileMap() ", e);
        }
    }

    public void getMapItem() {
        try {
            Message ms = new Message(Cmd.MAP_ITEM);
            DataOutputStream ds = ms.writer();
            ds.write(GameDataManager.getInstance().getMapItemsData());
            ds.flush();
            this.sendMessage(ms);
        } catch (IOException e) {
            logger.error("getMapItem() ", e);
        }
    }

    public void getBigImage(Message ms) {
        try {
            short id = ms.reader().readShort();
            String folder = session.getResourcesPath() + "big/";
            String path = folder + id + ".png";
            byte[] dat = Avatar.getFile(path);
            if (dat == null && session.isResourceHD()) {
                // Nếu client chọn HD nhưng server không có file HD thì fallback sang medium
                String fallback = avatar.server.ServerManager.resMediumPath + "big/" + id + ".png";
                dat = Avatar.getFile(fallback);
            }
            if (dat == null) {
                logger.warn("GET_BIG missing id=" + id + " path=" + path + " user.dir=" + System.getProperty("user.dir"));
                return;
            }
            ms = new Message(Cmd.GET_BIG);
            DataOutputStream ds = ms.writer();
            ds.writeShort(id);
            ds.writeShort(dat.length);
            ds.writeShort(dat.length);
            ds.write(dat);
            if (id > 20) {
                ds.writeShort(2);
            } else if (id > 10) {
                ds.writeShort(1);
            }
            ds.flush();
            this.sendMessage(ms);
        } catch (IOException e) {
            logger.error("getBigImage() ", e);
        }
    }

    public void getBigData() {
        try {
            Message ms = new Message(Cmd.SET_BIG);
            DataOutputStream ds = ms.writer();
            File file = new File(session.getResourcesPath() + "big/");
            File[] listFiles = file.listFiles();
            if (listFiles == null) {
                logger.warn("SET_BIG missing folder=" + file.getPath() + " user.dir=" + System.getProperty("user.dir"));
                listFiles = new File[0];
            }
            ds.writeByte(listFiles.length);
            for (File f : listFiles) {
                String name = f.getName().split("\\.")[0];
                int id = Integer.parseInt(name);
                int size = (int) f.length();
                ds.writeShort(id);
                ds.writeShort(size);
            }
            ds.writeShort(ServerManager.bigImgVersion);
            ds.writeShort(ServerManager.partVersion);
            ds.writeShort(ServerManager.bigItemImgVersion);
            ds.writeShort(ServerManager.itemTypeVersion);
            ds.writeShort(ServerManager.itemVersion);
            ds.writeByte(0);
            ds.writeInt(ServerManager.objectVersion);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("getBigData() ", ex);
        }
    }

    public void updateMoney(int type) {
        try {
            Message ms = new Message(Cmd.UPDATE_MONEY);
            DataOutputStream ds = ms.writer();
            ds.writeInt(session.user.xeng);
            ds.writeByte((byte) type);
            ds.writeInt(session.user.xu);
            ds.writeInt(session.user.luong);
            ds.writeInt(session.user.luongKhoa);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("updateMoney ", ex);
        }
    }

    public void openMenuOption(int userID, int menuID, String... menus) {
        try {
            Message ms = new Message(Cmd.MENU_OPTION);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.writeByte(menuID);
            ds.writeByte(menus.length);
            for (int i = 0; i < menus.length; ++i) {
                ds.writeUTF(menus[i]);
            }
            ds.flush();
            sendMessage(ms);
        } catch (IOException e) {
            logger.error("openMenuOption ", e);
        }
    }

    public void requestYourInfo(User us) {
        try {
            Message ms = new Message(Cmd.REQUEST_YOUR_INFO);
            DataOutputStream ds = ms.writer();
            ds.writeInt(us.getId());
            ds.writeByte(us.getLeverMain());
            ds.writeByte(us.getLeverMainPercen());
            ds.writeByte(us.getFriendly());
            ds.writeByte(us.getCrazy());
            ds.writeByte(us.getStylish());
            ds.writeByte(us.getHappy());
            ds.writeByte(100 - us.getHunger());
            ds.writeInt(-1);//keets hon
            ds.writeShort(us.getLeverMain());
            ds.flush();
            sendMessage(ms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openUIMenu(int userID, int menuID, List<Menu> menus, String npcName, String npcChat) {
        try {
            Message ms = new Message(Cmd.MENU_OPTION);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.writeByte(menuID);
            ds.writeByte(menus.size());
            for (Menu m : menus) {
                ds.writeUTF(m.getName());
            }
            for (Menu m : menus) {
                ds.writeShort(m.getId());
            }
            if (npcName != null) {
                ds.writeUTF(npcName);
                ds.writeUTF(npcChat);
                for (Menu m : menus) {
                    ds.writeBoolean(m.isMenu());
                }
            }
            ds.flush();
            sendMessage(ms);
        } catch (IOException e) {
            logger.error("openMenuOption ", e);
        }
    }

    public void getFoodData() {
        try {
            Message ms = new Message(Cmd.GET_ITEM_INFO);
            DataOutputStream ds = ms.writer();
            List<Food> foods = FoodManager.getInstance().getFoods();
            ds.writeShort(foods.size());
            for (Food food : foods) {
                ds.writeShort(food.getId());
                ds.writeUTF(food.getName());
                ds.writeUTF(food.getDescription());
                ds.writeInt(food.getPrice());
                ds.writeByte(food.getShop());
                ds.writeShort(food.getIcon());
            }
            ds.flush();
            this.sendMessage(ms);
        } catch (IOException e) {
            logger.error("getFoodData ", e);
        }
    }

    public void customTab(String title, String content) {
        try {
            Message ms = new Message(Cmd.CUSTOM_TAB);
            DataOutputStream ds = ms.writer();
            ds.writeByte(0);
            ds.writeUTF(title);
            ds.writeUTF(content);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("customTab ", ex);
        }
    }
}
