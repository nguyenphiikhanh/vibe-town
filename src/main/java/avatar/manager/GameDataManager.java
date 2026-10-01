package avatar.manager;

import avatar.db.DbManager;
import avatar.model.ImageInfo;
import avatar.model.MapItem;
import avatar.model.MapItemType;
import avatar.model.Position;
import avatar.server.Avatar;
import avatar.utils.DataParser;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


public class GameDataManager {

    private static final Logger logger = Logger.getLogger(GameDataManager.class);
    private static final GameDataManager instance = new GameDataManager();

    public static GameDataManager getInstance() {
        return instance;
    }

    private final List<ImageInfo> itemImageData = new ArrayList<>();
    private final List<ImageInfo> farmImageData = new ArrayList<>();
    private final Map<Byte, List<MapItem>> mapItems = new HashMap<>();
    private final Map<Byte, List<MapItemType>> mapItemTypes = new HashMap<>();

    private byte[] mapItemsData;
    private byte[] mapItemTypesData;

    public void load() {
        mapItemsData = Avatar.getFile("res/data/map_item.dat");
        mapItemTypesData = Avatar.getFile("res/data/map_item_type.dat");
        if (mapItemsData == null || mapItemTypesData == null) {
            throw new RuntimeException("Error loading map item data");
        }

        loadItemImageData();
        loadFarmImageData();

       
        boolean okItems = loadMapItemsFromDat(mapItemsData);
        boolean okTypes = loadMapItemTypesFromDat(mapItemTypesData);
        if (!okItems) {
            logger.warn("map_item.dat format not supported for map_id; fallback to DB loading.");
            loadMapItemsFromDb();
        }
        if (!okTypes) {
            logger.warn("map_item_type.dat format not supported for map_id; fallback to DB loading.");
            loadMapItemTypesFromDb();
        }
    }

    public List<ImageInfo> getItemImageData() {
        return itemImageData;
    }

    public List<ImageInfo> getFarmImageData() {
        return farmImageData;
    }

    public byte[] getMapItemsData() {
        return mapItemsData;
    }

    public byte[] getMapItemTypesData() {
        return mapItemTypesData;
    }

    public List<MapItem> getMapItems(byte mapId) {
        return mapItems.get(mapId);
    }

    public List<MapItemType> getMapItemTypes(byte mapId) {
        return mapItemTypes.get(mapId);
    }

    public MapItemType findMapItemType(byte mapId, int typeId) {
        List<MapItemType> list = mapItemTypes.get(mapId);
        if (list == null) {
            return null;
        }
        for (MapItemType t : list) {
            if (t != null && t.getId() == (short) typeId) {
                return t;
            }
        }
        return null;
    }

    private void loadItemImageData() {
        itemImageData.clear();
        try {
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame()
                    .prepareStatement("SELECT * FROM `item_image_data`;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int bigImageID = rs.getInt("image_id");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int w = rs.getInt("w");
                int h = rs.getInt("h");
                itemImageData.add(ImageInfo.builder().id(id).bigImageID(bigImageID).x(x).y(y).w(w).h(h).build());
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.error("loadItemImageData", e);
        }
    }

    private void loadFarmImageData() {
        farmImageData.clear();
        try {
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame()
                    .prepareStatement("SELECT * FROM `farm_image_data`;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int bigImageID = rs.getInt("image_id");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int w = rs.getInt("w");
                int h = rs.getInt("h");
                farmImageData.add(ImageInfo.builder().id(id).bigImageID(bigImageID).x(x).y(y).w(w).h(h).build());
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.error("loadFarmImageData", e);
        }
    }

    
    private boolean loadMapItemsFromDat(byte[] dat) {
        mapItems.clear();
        if (dat.length <= 2) {
            return false;
        }
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(dat))) {
            short count = dis.readShort();
            if (count <= 0) {
                return true;
            }

            int remaining = dat.length - 2;
            int perNew = 1 + 2 + 1 + 2 + 1 + 1; // 8
            if (remaining >= (count * perNew)) {
                for (int i = 0; i < count; i++) {
                    byte mapId = dis.readByte();
                    short id = dis.readShort();
                    byte type = dis.readByte();
                    short typeId = dis.readShort();
                    byte x = dis.readByte();
                    byte y = dis.readByte();
                    mapItems.computeIfAbsent(mapId, k -> new ArrayList<>())
                            .add(MapItem.builder().id(id).type(type).typeID(typeId).x(x).y(y).build());
                }
                return true;
            }

            return false;
        } catch (IOException e) {
            logger.error("loadMapItemsFromDat", e);
            return false;
        }
    }

    
    private boolean loadMapItemTypesFromDat(byte[] dat) {
        
        if (dat.length <= 2) {
            return false;
        }
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(dat))) {
            short count = dis.readShort();
            if (count <= 0) {
                mapItemTypes.clear();
                return true;
            }

            dis.mark(dat.length);
            boolean hasMapId;
            try {
                dis.readByte(); // mapId
                dis.readShort(); // typeId
                dis.readUTF();
                dis.readUTF();
                hasMapId = true;
            } catch (Exception ex) {
                hasMapId = false;
            }
            dis.reset();
            if (!hasMapId) {
                return false;
            }

            mapItemTypes.clear();
            for (int i = 0; i < count; i++) {
                byte mapId = dis.readByte();
                short id = dis.readShort();
                String name = dis.readUTF();
                String des = dis.readUTF();
                short imgID = dis.readShort();
                short iconID = dis.readShort();
                byte dx = dis.readByte();
                byte dy = dis.readByte();
                short priceCoin = dis.readShort();
                short priceGold = dis.readShort();
                byte buy = dis.readByte();
                byte pn = dis.readByte();
                List<Position> positions = new ArrayList<>();
                for (int p = 0; p < pn; p++) {
                    byte x = dis.readByte();
                    byte y = dis.readByte();
                    positions.add(new Position(x, y));
                }

                MapItemType itemType = MapItemType.builder()
                        .id(id)
                        .name(name)
                        .des(des)
                        .imgID(imgID)
                        .iconID(iconID)
                        .dx((short) dx)
                        .dy((short) dy)
                        .priceXu(priceCoin)
                        .priceLuong(priceGold)
                        .buy(buy)
                        .listNotTrans(positions)
                        .build();

                mapItemTypes.computeIfAbsent(mapId, k -> new ArrayList<>()).add(itemType);
            }

            return true;
        } catch (IOException e) {
            logger.error("loadMapItemTypesFromDat", e);
            return false;
        }
    }

    private void loadMapItemsFromDb() {
        mapItems.clear();
        try {
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame()
                    .prepareStatement("SELECT * FROM `map_item`;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                byte mapId = rs.getByte("map_id");
                short id = rs.getShort("id");
                byte type = rs.getByte("type");
                short typeId = rs.getShort("type_id");
                byte x = rs.getByte("x");
                byte y = rs.getByte("y");
                mapItems.computeIfAbsent(mapId, k -> new ArrayList<>())
                        .add(MapItem.builder().id(id).type(type).typeID(typeId).x(x).y(y).build());
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.error("loadMapItemsFromDb", e);
        }
    }

    private void loadMapItemTypesFromDb() {
        mapItemTypes.clear();
        try {
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame()
                    .prepareStatement("SELECT * FROM `map_item_type`;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                byte mapId = rs.getByte("map_id");
              
                short id;
                try {
                    id = rs.getShort("type_id");
                } catch (SQLException ignore) {
                    id = rs.getShort("id");
                }
                String name = rs.getString("name");
                String description = rs.getString("description");
                short imageID = rs.getShort("image");
                short iconID = rs.getShort("icon");
                int priceCoin = rs.getInt("price_coin");
                short priceGold = rs.getShort("price_gold");
                byte buy = rs.getByte("buy");
                short dx = rs.getShort("dx");
                short dy = rs.getShort("dy");

                List<Position> positions;
                try {
                    positions = DataParser.parsePositions(rs.getString("position"));
                } catch (Exception ex) {
                    positions = new ArrayList<>();
                }

                MapItemType itemType = MapItemType.builder()
                        .id(id)
                        .name(name)
                        .des(description)
                        .imgID(imageID)
                        .iconID(iconID)
                        .priceXu(priceCoin)
                        .priceLuong(priceGold)
                        .buy(buy)
                        .dx(dx)
                        .dy(dy)
                        .listNotTrans(positions)
                        .build();

                mapItemTypes.computeIfAbsent(mapId, k -> new ArrayList<>()).add(itemType);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.error("loadMapItemTypesFromDb", e);
        }
    }
}

