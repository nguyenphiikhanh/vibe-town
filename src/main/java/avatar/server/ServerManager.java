package avatar.server;

import avatar.model.User;
import avatar.db.DbManager;
import avatar.item.Item;
import avatar.item.PartManager;
import avatar.model.FoodManager;
import avatar.manager.GameDataManager;
import avatar.network.Session;
import avatar.play.Zone;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONValue;
import org.json.simple.JSONArray;
import avatar.model.Npc;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;

import avatar.network.Message;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import avatar.play.Map;
import avatar.play.MapManager;
import avatar.play.NpcManager;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ServerManager {

    public static String cityName;
    public static String hashSettings;
    public static boolean active;
    protected static short port;
    public static String notify;
    public static int bigImgVersion;
    public static int partVersion;
    public static int bigItemImgVersion;
    public static int itemTypeVersion;
    public static int itemVersion;
    public static int objectVersion;
    public static String resHDPath;
    public static String resMediumPath;
    protected static int numClients;
    public static ArrayList<Session> clients;
    protected static ServerSocket server;
    protected static boolean start;
    protected static int id;
    private static boolean debug;

    private static void loadConfigFile() {
        try {
            FileInputStream input = new FileInputStream(new File("config.properties"));
            Properties props = new Properties();
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            ServerManager.port = Short.parseShort(props.getProperty("server.port"));
            ServerManager.cityName = props.getProperty("game.city.name");
            ServerManager.active = Boolean.parseBoolean(props.getProperty("server.active"));
            ServerManager.debug = Boolean.parseBoolean(props.getProperty("server.debug"));
            if (props.containsKey("game.notify")) {
                ServerManager.notify = props.getProperty("game.notify");
            }
            ServerManager.bigImgVersion = Short.parseShort(props.getProperty("game.big.image.version"));
            ServerManager.partVersion = Short.parseShort(props.getProperty("game.part.version"));
            ServerManager.bigItemImgVersion = Short.parseShort(props.getProperty("game.big.item.image.version"));
            ServerManager.itemTypeVersion = Short.parseShort(props.getProperty("game.itemtype.version"));
            ServerManager.itemVersion = Integer.parseInt(props.getProperty("game.item.version"));
            ServerManager.objectVersion = Integer.parseInt(props.getProperty("game.object.version"));
            ServerManager.resHDPath = props.getProperty("game.resources.hd.path");
            ServerManager.resMediumPath = props.getProperty("game.resources.medium.path");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void loadSettings() {
        System.out.println("Load settings in database");
        try {
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame()
                    .prepareStatement("SELECT * FROM `settings`;");
            ResultSet res = ps.executeQuery();
            HashMap<String, String> settings = new HashMap<String, String>();
            while (res.next()) {
                String name = res.getString("name");
                String value = res.getString("value");
                settings.put(name, value);
            }
            res.close();
            ps.close();
            if (settings.containsKey("hash_settings")) {
                ServerManager.hashSettings = settings.get("hash_settings");
            }
            if (settings.containsKey("bao_tri")) {
                ServerManager.active = Boolean.parseBoolean(settings.get("bao_tri"));
            }
            if (settings.containsKey("thong_bao")) {
                ServerManager.notify = settings.get("thong_bao");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    protected static void init() {
        ServerManager.start = false;
        DbManager.getInstance().start();
        loadConfigFile();
        loadSettings();
        GameDataManager.getInstance().load();
        PartManager.getInstance().load();
        FoodManager.getInstance().load();
        int numMap = 60;
        for (int i = 0; i < numMap; ++i) {
            MapManager.getInstance().add(new Map(i, 0, 30));
        }
        System.out.println("Load NPC data start ...");
        try {
            int numNPC = 0;
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame()
                    .prepareStatement("SELECT * FROM `npc`;");
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                int botID = res.getInt("id");
                String botName = res.getString("name");
                byte map = res.getByte("map");
                short X = res.getShort("x");
                short Y = res.getShort("y");
                ArrayList<Item> items = new ArrayList<>();
                JSONArray listItem = (JSONArray) JSONValue.parse(res.getString("items"));
                for (int j = 0; j < listItem.size(); ++j) {
                    Item item = new Item(((Long) listItem.get(j)).intValue());
                    items.add(item);
                }
                Map m = MapManager.getInstance().find(map);
                if (m != null) {

                    List<Zone> zones = m.getZones();
                    for (Zone z : zones) {
                        Npc npc = Npc.builder()
                                .id(botID)
                                .name(botName)
                                .x(X)
                                .y(Y)
                                .wearing(items)
                                .build();
                        NpcManager.getInstance().add(npc);
                        z.enter(npc, X, Y);
                    }
                }
                System.out.println("  + NPC " + Utils.removeAccent(botName));
                ++numNPC;
            }
            res.close();
            ps.close();
            System.out.println("Load success " + numNPC + " NPC !");
            System.out.println("Reset player online ...");
            DbManager.getInstance().update("UPDATE `players` SET `is_online` = 0, `client_id` = -1");
            System.out.println("Reset player online successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    protected static void start() {
        System.out.println("Start socket port = " + ServerManager.port);
        try {
            ServerManager.clients = new ArrayList<Session>();
            ServerManager.server = new ServerSocket(ServerManager.port);
            ServerManager.id = 0;
            ServerManager.numClients = 0;
            ServerManager.start = true;
            System.out.println("Start server Success !");
            while (ServerManager.start) {
                try {
                    Socket client = ServerManager.server.accept();
                    Session cl = new Session(client, ++ServerManager.id);
                    ServerManager.clients.add(cl);
                    ++ServerManager.numClients;
                    log("Accept socket " + cl + " done!");
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void stop() {
        if (ServerManager.start) {
            close();
            ServerManager.start = false;
            System.gc();
        }
    }

    protected static void close() {
        try {
            ServerManager.server.close();
            ServerManager.server = null;
            while (ServerManager.clients.size() > 0) {
                Session c = ServerManager.clients.get(0);
                c.close();
                --ServerManager.numClients;
            }
            ServerManager.clients = null;
            DbManager.getInstance().shutdown();
            System.gc();
            System.out.println("End socket");
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public static void log(String s) {
        if (ServerManager.debug) {
            System.out.println(s);
        }
    }

    public static void disconnect(Session cl) {
        synchronized (ServerManager.clients) {
            ServerManager.clients.remove(cl);
            --ServerManager.numClients;
            System.out.println("Disconnect client: " + cl);
        }
    }

    public static void joinAreaMessage(User us, Message ms) throws IOException {
        byte map = ms.reader().readByte();
        byte area = ms.reader().readByte();
        short x = ms.reader().readShort();
        short y = ms.reader().readShort();
        System.out.println("[JOIN_MAP] user=" + (us != null ? us.getId() : -1)
                + " map=" + map + " area=" + area + " x=" + x + " y=" + y);
        if (area < 0) {
            area = joinAreaAutoNumber(map);
        }
        // 一些客户端会发送 x/y = -1 作为“自动出生点”的信号；这里做兜底避免客户端进图后立即被弹回。
        if (x < 0 || y < 0) {
            x = 0;
            y = 0;
        }
        Map m = MapManager.getInstance().find(map);
        if (m != null) {
            List<Zone> zones = m.getZones();
            if (zones == null || zones.isEmpty()) {
                System.out.println("[JOIN_MAP] map=" + map + " has no zones");
                return;
            }
            int idx = area;
            if (idx < 0) {
                idx = 0;
            } else if (idx >= zones.size()) {
                idx = zones.size() - 1;
            }
            Zone zone = zones.get(idx);
            System.out.println("[JOIN_MAP] resolved area=" + idx + " players=" + zone.getPlayers().size());
            zone.enter(us, x, y);
        } else {
            System.out.println("[JOIN_MAP] map not found: " + map);
        }
    }

    private static byte joinAreaAutoNumber(byte map) {
        Map m = MapManager.getInstance().find(map);
        List<Zone> zones = m.getZones();
        int i = 0;
        for (Zone z : zones) {
            if (z.getPlayers().size() <= 15) {
                return (byte) i;
            }
            i++;
        }
        return 0;
    }
}
