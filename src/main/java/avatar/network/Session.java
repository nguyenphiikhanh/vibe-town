package avatar.network;

import avatar.constants.Cmd;
import avatar.db.DbManager;
import avatar.item.Item;
import avatar.item.PartManager;
import avatar.item.Part;
import avatar.lucky.DialLucky;
import avatar.lucky.DialLuckyManager;
import java.util.ArrayDeque;
import java.util.Deque;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;
import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import avatar.message.HomeMsgHandler;
import avatar.message.FarmMsgHandler;
import avatar.message.ParkMsgHandler;
import avatar.message.AvatarMsgHandler;
import java.io.IOException;
import avatar.message.MessageHandler;
import avatar.model.CreateChar;
import avatar.model.Food;
import avatar.model.FoodManager;
import avatar.model.Npc;
import avatar.play.HouseItem;
import avatar.play.Zone;
import avatar.server.MenuFunction;
import avatar.server.NpcHandler;
import avatar.server.Avatar;
import avatar.server.ServerManager;
import avatar.model.User;
import avatar.play.offline.AbsMapOffline;
import avatar.play.offline.MapOfflineManager;
import avatar.server.UserManager;
import avatar.server.Utils;
import avatar.service.AvatarService;
import avatar.service.FarmService;
import avatar.service.HomeService;
import avatar.service.ParkService;
import avatar.service.Service;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.Socket;
import java.util.List;

public class Session implements ISession {

    private static final byte[] key = (System.currentTimeMillis() + "_kitakeyos").getBytes();
    public Socket sc;
    public DataInputStream dis;
    public DataOutputStream dos;
    public int id;
    public String ip;
    public User user;
    public MenuFunction menuf;
    private IMessageHandler messageHandler;
    public boolean connected;
    public boolean login;
    private byte curR;
    private byte curW;
    private final Sender sender;
    private Thread collectorThread;
    protected Thread sendThread;
    public final Object obj;
    protected String platform;
    protected String versionARM;
    private byte resourceType;
    @Getter
    private AvatarService avatarService;
    @Getter
    private FarmService farmService;
    @Getter
    private HomeService homeService;
    @Getter
    private ParkService parkService;
    @Getter
    private Service service;

    public Session(Socket sc, int id) throws IOException {
        this.obj = new Object();
        this.resourceType = 0;
        this.sc = sc;
        this.id = id;
        this.ip = ((InetSocketAddress) this.sc.getRemoteSocketAddress()).getAddress().toString().replace("/", "");
        this.dis = new DataInputStream(sc.getInputStream());
        this.dos = new DataOutputStream(sc.getOutputStream());
        this.setHandler(new MessageHandler(this));
        this.sendThread = new Thread(this.sender = new Sender());
        (this.collectorThread = new Thread(new MessageCollector())).start();
        this.avatarService = new AvatarService(this);
        this.farmService = new FarmService(this);
        this.homeService = new HomeService(this);
        this.parkService = new ParkService(this);
        this.service = new Service(this);
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public void setHandler(IMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void sendMessage(Message message) {
        this.sender.AddMessage(message);
    }

    protected synchronized void doSendMessage(Message m) {
        byte[] data = m.getData();
        try {
            if (this.connected) {
                byte b = this.writeKey(m.getCommand());
                this.dos.writeByte(b);
            } else {
                this.dos.writeByte(m.getCommand());
            }
            if (data != null) {
                int size = data.length;
                if (m.getCommand() == 90) {
                    this.dos.writeInt(size);
                    this.dos.write(data);
                } else {
                    if (this.connected) {
                        int byte1 = this.writeKey((byte) (size >> 8));
                        this.dos.writeByte(byte1);
                        int byte2 = this.writeKey((byte) (size & 0xFF));
                        this.dos.writeByte(byte2);
                    } else {
                        this.dos.writeShort(size);
                    }
                    if (this.connected) {
                        for (int i = 0; i < data.length; ++i) {
                            data[i] = this.writeKey(data[i]);
                        }
                    }
                    this.dos.write(data);
                }
            } else {
                this.dos.writeShort(0);
            }
            this.dos.flush();
            m.cleanup();
        } catch (IOException ex) {
        }
    }

    private byte readKey(byte b) {
        byte[] key = Session.key;
        byte curR = this.curR;
        this.curR = (byte) (curR + 1);
        byte i = (byte) ((key[curR] & 0xFF) ^ (b & 0xFF));
        if (this.curR >= Session.key.length) {
            this.curR %= (byte) Session.key.length;
        }
        return i;
    }

    private byte writeKey(byte b) {
        byte[] key = Session.key;
        byte curW = this.curW;
        this.curW = (byte) (curW + 1);
        byte i = (byte) ((key[curW] & 0xFF) ^ (b & 0xFF));
        if (this.curW >= Session.key.length) {
            this.curW %= (byte) Session.key.length;
        }
        return i;
    }

    @Override
    public void close() {
        try {
            if (this.user != null) {
                this.user.close();
                UserManager.getInstance().remove(user);
            }
            ServerManager.disconnect(this);
            this.cleanNetwork();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cleanNetwork() {
        this.curR = 0;
        this.curW = 0;
        try {
            this.connected = false;
            this.login = false;
            this.dis.close();
        } catch (Exception ex) {
            try {
                this.dos.close();
            } catch (Exception ex2) {
                try {
                    this.sc.close();
                } catch (Exception ex3) {
                } finally {
                    this.sendThread = null;
                    this.collectorThread = null;
                    System.gc();
                }
            } finally {
                try {
                    this.sc.close();
                } catch (Exception ex4) {
                    this.sendThread = null;
                    this.collectorThread = null;
                    System.gc();
                } finally {
                    this.sendThread = null;
                    this.collectorThread = null;
                    System.gc();
                }
            }
        } finally {
            try {
                this.dos.close();
            } catch (Exception ex5) {
                try {
                    this.sc.close();
                } catch (Exception ex6) {
                    this.sendThread = null;
                    this.collectorThread = null;
                    System.gc();
                } finally {
                    this.sendThread = null;
                    this.collectorThread = null;
                    System.gc();
                }
            } finally {
                try {
                    this.sc.close();
                } catch (Exception ex7) {
                    this.sendThread = null;
                    this.collectorThread = null;
                    System.gc();
                } finally {
                    this.sendThread = null;
                    this.collectorThread = null;
                    System.gc();
                }
            }
        }
    }

    @Override
    public String toString() {
        if (this.user != null) {
            return this.user.toString();
        }
        return "Client " + this.id;
    }

    public boolean isResourceHD() {
        return this.resourceType == 1;
    }

    public String getResourcesPath() {
        return isResourceHD() ? ServerManager.resHDPath : ServerManager.resMediumPath;
    }

    public void handshakeMessage() throws IOException {
        Message ms = new Message(-27);
        DataOutputStream ds = ms.writer();
        ds.writeByte(Session.key.length);
        ds.writeByte(Session.key[0]);
        for (int i = 1; i < Session.key.length; ++i) {
            ds.writeByte(Session.key[i] ^ Session.key[i - 1]);
        }
        ds.flush();
        this.doSendMessage(ms);
        this.connected = true;
        this.sendThread.start();
    }

    public void getHandler(Message ms) throws IOException {
        byte index = ms.reader().readByte();
        System.out.println("getHandler: " + index);
        if (index == 8) {
            Zone zone = user.getZone();
            zone.leave(user);
        }
        ms = new Message(Cmd.GET_HANDLER);
        DataOutputStream ds2 = ms.writer();
        ds2.writeByte(index);
        ds2.flush();
        this.sendMessage(ms);
        switch (index) {
            case 8: {
                setHandler(new AvatarMsgHandler(this));
                break;
            }
            case 9: {
                setHandler(new ParkMsgHandler(this));
                break;
            }
            case 10: {
                setHandler(new FarmMsgHandler(this));
                break;
            }
            case 11: {
                this.setHandler(new HomeMsgHandler(this));
                break;
            }
            default: {
                setHandler(new MessageHandler(this));
                break;
            }
        }
    }

    public void doGetImgIcon(Message ms) throws IOException {
        short imageID = ms.reader().readShort();
        String folder = this.getResourcesPath() + "object/";
        String path = folder + imageID + ".png";
        byte[] dat = Avatar.getFile(path);
        if (dat == null && isResourceHD()) {
            String fallback = ServerManager.resMediumPath + "object/" + imageID + ".png";
            dat = Avatar.getFile(fallback);
        }
        if (dat == null) {
            return;
        }
        ms = new Message(Cmd.GET_IMG_ICON);
        DataOutputStream ds = ms.writer();
        ds.writeShort(imageID);
        ds.writeShort(dat.length);
        ds.write(dat);
        ds.flush();
        this.sendMessage(ms);

    }

    public void requestImagePart(Message ms) throws IOException {
        short imageID = ms.reader().readShort();
        String folder = getResourcesPath() + "item/";
        String path = folder + imageID + ".png";
        byte[] dat = Avatar.getFile(path);
        if (dat == null && isResourceHD()) {
            String fallback = ServerManager.resMediumPath + "item/" + imageID + ".png";
            dat = Avatar.getFile(fallback);
        }
        if (dat == null) {
            return;
        }
        ms = new Message(Cmd.REQUEST_IMAGE_PART);
        DataOutputStream ds = ms.writer();
        ds.writeShort(imageID);
        ds.writeShort(dat.length);
        ds.write(dat);
        ds.flush();
        this.sendMessage(ms);
    }

    public void doRequestExpicePet(Message ms) throws IOException {
        int userID = ms.reader().readInt();
        ms = new Message(-70);
        DataOutputStream ds = ms.writer();
        ds.writeInt(userID);
        ds.writeByte(0);
        ds.flush();
        this.sendMessage(ms);
    }

    public void clientInfo(Message ms) throws IOException {
        byte provider = ms.reader().readByte();
        int memory = ms.reader().readInt();
        String platform = ms.reader().readUTF();
        this.platform = platform;
        int rmsSize = ms.reader().readInt();
        int width = ms.reader().readInt();
        int height = ms.reader().readInt();
        boolean aaaaa = ms.reader().readBoolean();
        byte resource = ms.reader().readByte();
        this.resourceType = resource;
        String version = ms.reader().readUTF();
        if (ms.reader().available() > 0) {
            ms.reader().readUTF();
            ms.reader().readUTF();
            ms.reader().readUTF();
        }
    }

    public void agentInfo(Message ms) throws IOException {
        String agent = ms.reader().readUTF();
        System.out.println("agentInfo: " + agent);
    }

    public void doRequestService(Message ms) throws IOException {
        byte id = ms.reader().readByte();
        String msg = ms.reader().readUTF();
        switch (id) {
            case 6: {
                ms = new Message(-10);
                DataOutputStream ds = ms.writer();
                ds.writeUTF(String.format("Bạn đang đăng nhập vào thành phố %s. Dân số %d  người.", ServerManager.cityName, ServerManager.clients.size()));
                ds.flush();
                this.sendMessage(ms);
                break;
            }
            case 3: {
                ms = new Message(-10);
                DataOutputStream ds = ms.writer();
                ds.writeUTF("Chưa có game khác bạn ơiiiii !");
                ds.flush();
                this.sendMessage(ms);
                break;
            }
        }
    }

    public void doLogin(Message ms) throws IOException {
        if (this.login) {
            return;
        }
        String username = ms.reader().readUTF().trim();
        String password = ms.reader().readUTF().trim();
        String version = ms.reader().readUTF().trim();
        this.versionARM = version;
        User us = new User();
        us.setUsername(username);
        us.setPassword(password);
        us.setSession(this);
        boolean result = us.login();
        if (result) {
            this.login = true;
            this.user = us;
            enter();
        } else {
            this.login = false;
        }
    }

    private void enter() {
        if (user.loadData()) {
            DbManager.getInstance().update("UPDATE `players` SET `is_online` = ?, `client_id` = ? WHERE `user_id` = ? LIMIT 1;", 1, this.id, user.getId());
            user.initAvatar();
            this.menuf = new MenuFunction(user);
            UserManager.getInstance().add(user);
            getAvatarService().onLoginSuccess();
        } else {
            DbManager.getInstance().insertResultAsObj("INSERT INTO `players`(`user_id`, `level_main`, `exp_main`, `exp_farm`, `friendly`, `crazy`, `stylish`, `happy`, `hunger`, `chest_slot`, `chest_home_slot`, `chests`, `wearing`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", user.getId(), 1, 0, 0, 100, 0, 0, 100, 0, 10, 10, "[]", "[]");
            enter();
        }
    }

    public boolean isNewVersion() {
        return true;
    }

    public void regMessage(Message ms) throws IOException {
        String username = ms.reader().readUTF().trim();
        String password = ms.reader().readUTF().trim();
    }

    public void createCharacter(Message ms) throws IOException {
        byte gender = ms.reader().readByte();
        byte numItem = ms.reader().readByte();
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < numItem; ++i) {
            short itemID = ms.reader().readShort();
            items.add(new Item(itemID, -1, 1));
        }
        boolean isError = false;
        if (gender != 1 && gender != 2) {
            isError = true;
        }
        isError = !CreateChar.getInstance().check(gender, items);
        if (isError) {
            ms = new Message(-35);
            DataOutputStream ds = ms.writer();
            ds.writeBoolean(false);
            ds.flush();
            this.sendMessage(ms);
            return;
        }
        Item item = new Item(593, -1, 999);
        user.addItemToChests(item);
        user.setGender(gender);
        user.setWearing(items);
        ms = new Message(-35);
        DataOutputStream ds = ms.writer();
        ds.writeBoolean(true);
        ds.flush();
        this.sendMessage(ms);
    }

    public void doiKhuVuc(Message ms) throws IOException {
        if (this.messageHandler instanceof FarmMsgHandler) {
            return;
        }
        byte numKhuVuc = 30;
        byte map = ms.reader().readByte();
        ms = new Message(60);
        DataOutputStream ds = ms.writer();
        ds.writeByte(numKhuVuc);
        for (int i = 0; i < numKhuVuc; ++i) {
            ds.writeByte(2);
        }
        ds.flush();
        this.sendMessage(ms);
    }

    public void doJoinHouse4(Message ms) throws IOException {
        System.out.println("-104:  " + ms.reader().readInt());
    }

    public void buyItemShop(Message ms) {
        try {
            short partID = ms.reader().readShort();
            byte type = ms.reader().readByte();
            if (type < 1 || type > 2) {
                this.user.getService().serverMessage("Có lỗi xảy ra, vui lòng liên hệ admin. Mã lỗi: buyItemShopWrongType");
                return;
            }
            Part part = PartManager.getInstance().findPartByID(partID);
            if (part != null) {
                int priceXu = part.getCoin();
                int priceLuong = part.getGold();
                int price = 0;
                if ((priceXu == -1 && priceLuong == -1) || (type == 1 && priceXu == -1)
                        || (type == 2 && priceLuong == -1)) {
                    return;
                }
                if (priceXu > 0) {
                    price = priceXu;
                    if (user.getXu() < price) {
                        this.user.getService().serverMessage("Bạn không đủ xu!");
                        return;
                    }
                    this.user.updateXu(-price);
                } else {
                    price = priceLuong;
                    if (user.getLuong() < price) {
                        this.user.getService().serverMessage("Bạn không đủ lượng!");
                        return;
                    }
                    this.user.updateLuong(-price);
                }
                long expired = System.currentTimeMillis() + ((long) part.getExpiredDay() * 86400000L);
                if (part.getExpiredDay() == 0) {
                    expired = -1;
                }
                Item item = Item.builder()
                        .id(part.getId())
                        .expired(expired)
                        .build();
                System.out.println("expired: " + expired);
                user.addItemToChests(item);
                ms = new Message(-24);
                DataOutputStream ds = ms.writer();
                ds.writeShort(partID);
                if (partID != -1) {
                    ds.writeInt(price);
                    ds.writeByte(1);
                }
                ds.writeUTF("Bạn đã mua vật phẩm thành công.");
                ds.writeInt(user.getXu());
                ds.writeInt(user.getLuong());
                ds.writeInt(user.getLuongKhoa());
                ds.flush();
                this.sendMessage(ms);
            } else {
                this.avatarService.serverMessage("Vật phẩm không tồn tại !!!");
            }
        } catch (Exception e) {
            System.out.println("[ERROR-DB]" + e.getMessage());
        }
    }

    public void doJoinOfflineMap(Message ms) throws IOException {
        byte map = ms.reader().readByte();
        AbsMapOffline mapOffline = MapOfflineManager.getInstance().find(map);
        List<Npc> npcs = new ArrayList<>();
        if (mapOffline != null) {
            npcs = mapOffline.getNpcs();
        } else {
            System.out.println("Map offline join: " + map);
        }
        ms = new Message(Cmd.JOIN_OFFLINE_MAP);
        DataOutputStream ds = ms.writer();
        ds.writeByte(map);
        ds.writeByte(npcs.size());
        for (Npc npc : npcs) {
            ds.writeInt(npc.getId());
            ds.writeUTF(npc.getUsername());
            List<Item> wearing = npc.getWearing();
            // Đảm bảo luôn có đủ 5 items
            List<Item> wearingToSend = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                if (i < wearing.size() && wearing.get(i) != null) {
                    wearingToSend.add(wearing.get(i));
                } else {
                    wearingToSend.add(new Item(0));
                }
            }
            ds.writeByte(wearingToSend.size());
            for (Item item : wearingToSend) {
                ds.writeShort(item.getId());
            }
            ds.writeShort(npc.getX());
            ds.writeShort(npc.getY());
            ds.writeByte(npc.getStar());
            ds.writeByte(0);
            ds.writeShort(npc.getIdImg());
            List<String> chats = npc.getTextChats();
            ds.writeByte(chats.size());
            for (String text : chats) {
                ds.writeUTF(text);
            }
        }
        ds.writeShort(0);
        ds.flush();
        this.sendMessage(ms);
    }

    public void doRequestCityMap(Message ms) throws IOException {
        if (ms.reader().available() > 0) {
            byte idMini = ms.reader().readByte();
            System.out.println("RequestCityMap: " + idMini);
        }
        ms = new Message(-63);
        DataOutputStream ds = ms.writer();
        ds.writeByte(-1);
        ds.flush();
        this.sendMessage(ms);
        user.getAvatarService().openMenuOption(5, 0, "Đảo Hawaii", "Ai Cập", "Vương Quốc Bóng Đêm");
    }

    public void doCommunicate(Message ms) throws IOException {
        int userId = ms.reader().readInt();
        if (userId >= 2000000000) {
            NpcHandler.handlerCommunicate(userId, this.user);
            return;
        } else {
            System.out.println("userId = " + userId);
        }

    }

    public void doDialLucky(Message ms) throws IOException {
        short partId = ms.reader().readShort();
        short degree = ms.reader().readShort();
        DialLucky dl = user.getDialLucky();
        if (dl != null) {
            if (dl.getType() == DialLuckyManager.MIEN_PHI) {
                Item itm = user.findItemInChests(593);
                if (itm == null || itm.getQuantity() <= 0) {
                    return;
                }
                user.removeItem(593, 1);
            }
            if (dl.getType() == DialLuckyManager.XU) {
                if (user.getXu() < 15000) {
                    return;
                }
                user.updateXu(-15000);
            }
            if (dl.getType() == DialLuckyManager.LUONG) {
                if (user.getLuong() < 5) {
                    return;
                }
                user.updateLuong(-5);
            }
            dl.doDial(user, partId, degree);
        }
    }

    public void requestTileMap(Message ms) throws IOException {
        byte idTileImg = ms.reader().readByte();
        System.out.println("map = " + idTileImg);
        byte[] dat = Avatar.getFile(getResourcesPath() + "tilemap/");
        if (dat == null) {
            return;
        }
        ms = new Message(Cmd.REQUEST_TILE_MAP);
        DataOutputStream ds = ms.writer();
        ds.writeByte(idTileImg);
        ds.write(dat);
        ds.flush();
        this.sendMessage(ms);
    }

    public void doParkBuyItem(Message ms) throws IOException {
        short id = ms.reader().readShort();
        Food food = FoodManager.getInstance().findFoodByFoodID(id);
        if (food != null) {
            int shop = food.getShop();
            int price = food.getPrice();
            if (price > user.xu) {
                this.user.getService().serverDialog("Bạn không đủ xu!");
                return;
            }
            String name = food.getName();
            this.user.updateXu(-price);
            if (shop == 4) {
                int health = 100 - this.user.getHunger() + food.getPercentHelth();
                health = ((health > 100) ? 100 : health);
                this.user.updateHunger(100 - health);
                this.user.getAvatarService()
                        .serverDialog(String.format("Bạn đã ăn một %s sức khoẻ bạn hiện tại là %d", name, health));
            } else if (shop == 5) {
                this.user.getService().serverDialog("Bạn đã cho thú nuôi ăn thành công");
            }
        }
    }

    public void requestFriendList(Message ms) throws IOException {
        ms = new Message(-81);
        DataOutputStream ds = ms.writer();
        ds.flush();
    }

    public void joinHouse(Message ms) throws IOException {
        int userId = ms.reader().readInt();
        Vector<HouseItem> hItems = new Vector<>();
        try {
            String GET_HOUSE_DATA = "SELECT * FROM `house_buy` WHERE `user_id` = ? LIMIT 1";
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame().prepareStatement(GET_HOUSE_DATA);
            ps.setInt(1, userId);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                JSONArray ja_map = (JSONArray) JSONValue.parse(res.getString("map_data"));
                byte[] map_data = new byte[ja_map.size()];
                for (int i = 0; i < ja_map.size(); ++i) {
                    map_data[i] = ((Long) ja_map.get(i)).byteValue();
                }
                ps.close();
                res.close();
                String GET_ITEMS_IN_CHEST = "SELECT * FROM `house_player_item` WHERE `user_id` = ?";
                ps = DbManager.getInstance().getConnectionForGame().prepareStatement(GET_ITEMS_IN_CHEST);
                ps.setInt(1, userId);
                res = ps.executeQuery();
                if (res != null) {
                    while (res.next()) {
                        HouseItem hItem = new HouseItem();
                        hItem.itemId = res.getShort("house_item_id");
                        hItem.x = res.getByte("x");
                        hItem.y = res.getByte("y");
                        hItem.rotate = res.getByte("rotate");
                        hItems.add(hItem);
                    }
                }
                ps.close();
                res.close();
                this.user.getZone().leave(user);
                ms = new Message(-65);
                DataOutputStream ds = ms.writer();
                ds.writeByte(3);
                ds.writeInt(this.user.getId());
                ds.writeShort(map_data.length);
                for (int j = 0; j < map_data.length; ++j) {
                    ds.write(map_data[j]);
                }
                ds.writeByte(28);
                ds.writeShort(hItems.size());
                for (HouseItem hItem2 : hItems) {
                    ds.writeShort(hItem2.itemId);
                    ds.writeByte(hItem2.x);
                    ds.writeByte(hItem2.y);
                    ds.writeByte(hItem2.rotate);
                }
                ds.flush();
                this.sendMessage(ms);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeMessage() {
        if (this.isConnected()) {
            if (this.messageHandler != null) {
                this.messageHandler.onDisconnected();
            }
            this.close();
        }
    }

    public void changePassword(Message ms) throws IOException {
        String passOld = ms.reader().readUTF();
        String passNew = ms.reader().readUTF();
        try {
            String ACCOUNT_LOGIN = "SELECT * FROM `users` WHERE `id` = ? AND `password` = ? LIMIT 1";
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame().prepareStatement(ACCOUNT_LOGIN);
            ps.setInt(1, this.user.getId());
            ps.setString(2, Utils.md5(passOld));
            ResultSet red = ps.executeQuery();
            if (red.next()) {
                String ACCOUNT_UPDATE_PASSWORD = "UPDATE `users` SET `password` = ? WHERE `id` = ?";
                PreparedStatement changePass = DbManager.getInstance().getConnectionForGame()
                        .prepareStatement(ACCOUNT_UPDATE_PASSWORD);
                changePass.setString(1, Utils.md5(passNew));
                changePass.setInt(2, this.user.getId());
                int result = changePass.executeUpdate();
                if (result > 0) {
                    ms = new Message(-62);
                    DataOutputStream ds = ms.writer();
                    ds.writeUTF(passNew);
                    ds.flush();
                    this.sendMessage(ms);
                    this.user.getService().serverDialog("\u0110\u1ed5i m\u1eadt kh\u1ea9u th\u00e0nh c\u00f4ng.");
                } else {
                    this.user.getAvatarService()
                            .serverDialog("C\u00f3 l\u1ed7i x\u1ea3y ra, vui l\u00f2ng th\u1eed l\u1ea1i sau.");
                }
                changePass.close();
            } else {
                this.user.getService().serverDialog("M\u1eadt kh\u1ea9u c\u0169 kh\u00f4ng \u0111\u00fang.");
            }
            red.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private class Sender implements Runnable {

        private Deque<Message> sendingMessage;

        public Sender() {
            this.sendingMessage = new ArrayDeque<Message>();
        }

        public void AddMessage(Message message) {
            this.sendingMessage.add(message);
        }

        @Override
        public void run() {
            while (isConnected()) {
                while (!this.sendingMessage.isEmpty()) {
                    Message message = this.sendingMessage.poll();
                    doSendMessage(message);
                }
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    class MessageCollector implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    Message message = this.readMessage();
                    if (message == null) {
                        break;
                    }
                    messageHandler.onMessage(message);
                    message.cleanup();
                }
            } catch (Exception ex) {
            }
            if (isConnected()) {
                if (messageHandler != null) {
                    messageHandler.onDisconnected();
                }
                close();
            }
        }

        private Message readMessage() {
            try {
                byte cmd = dis.readByte();
                if (connected) {
                    cmd = readKey(cmd);
                }
                int size;
                if (connected) {
                    byte b1 = dis.readByte();
                    byte b2 = dis.readByte();
                    size = ((readKey(b1) & 0xFF) << 8 | (readKey(b2) & 0xFF));
                } else {
                    size = dis.readUnsignedShort();
                }
                byte[] data = new byte[size];
                for (int len = 0, byteRead = 0; len != -1 && byteRead < size; byteRead += len) {
                    len = dis.read(data, byteRead, size - byteRead);
                    if (len > 0) {
                    }
                }
                if (connected) {
                    for (int i = 0; i < data.length; ++i) {
                        data[i] = readKey(data[i]);
                    }
                }
                Message msg = new Message(cmd, data);
                return msg;
            } catch (Exception e) {
            }
            return null;
        }
    }
}

