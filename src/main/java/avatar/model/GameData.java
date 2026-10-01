package avatar.model;

import avatar.lucky.DialLucky;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import avatar.utils.DataParser;

import avatar.db.DbManager;
import avatar.lucky.DialLuckyManager;
import lombok.Getter;

@Getter
public class GameData {
    private static final GameData instance = new GameData();

    public static final GameData getInstance() {
        return instance;
    }

    private List<ImageInfo> itemImageDatas = new ArrayList<>();
    private List<ImageInfo> farmImageDatas = new ArrayList<>();
    private List<MapItem> mapItems = new ArrayList<>();
    private List<MapItemType> mapItemTypes = new ArrayList<>();
    private byte[] mapItemsData;
    private byte[] mapItemTypesData;

    public void load() {
        mapItemsData = avatar.server.Avatar.getFile("res/data/map_item.dat");
        mapItemTypesData = avatar.server.Avatar.getFile("res/data/map_item_type.dat");
        if (mapItemsData == null || mapItemTypesData == null) {
            throw new RuntimeException("Error loading map item data");
        }
        loadItemImageData();
        loadFarmImageData();
        loadMapItem();
        loadMapItemType();
    }

    public void loadItemImageData() {
        try {
            itemImageDatas.clear();
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
                itemImageDatas.add(ImageInfo.builder().id(id).bigImageID(bigImageID).x(x).y(y).w(w).h(h).build());
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void loadFarmImageData() {
        try {
            farmImageDatas.clear();
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame().prepareStatement("SELECT * FROM `farm_image_data`;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int bigImageID = rs.getInt("image_id");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int w = rs.getInt("w");
                int h = rs.getInt("h");
                farmImageDatas.add(
                        ImageInfo.builder().id(id).bigImageID(bigImageID).x(x).y(y).w(w).h(h).build());
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void loadMapItem() {
        try {
            mapItems.clear();
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame()
                    .prepareStatement("SELECT * FROM `map_item`;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int typeID = rs.getInt("type_id");
                int type = rs.getInt("type");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                mapItems.add(MapItem.builder()
                        .id((short) id)
                        .typeID((short) typeID)
                        .type((byte) type)
                        .x((byte) x)
                        .y((byte) y)
                        .build());
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void loadMapItemType() {
        try {
            mapItemTypes.clear();
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame()
                    .prepareStatement("SELECT * FROM `map_item_type`;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                int imageID = rs.getInt("image");
                int iconID = rs.getInt("icon");
                int priceCoin = rs.getInt("price_coin");
                int priceGold = rs.getInt("price_gold");
                int buy = rs.getInt("buy");
                int dx = rs.getInt("dx");
                int dy = rs.getInt("dy");
                List<Position> positions;
                try {
                    positions = DataParser.parsePositions(rs.getString("position"));
                } catch (Exception ex) {
                    positions = new ArrayList<>();
                }
                mapItemTypes.add(MapItemType.builder()
                        .id((short) id)
                        .name(name)
                        .des(description)
                        .imgID((short) imageID)
                        .iconID((short) iconID)
                        .priceXu(priceCoin)
                        .priceLuong((short) priceGold)
                        .buy((byte) buy)
                        .dx((short) dx)
                        .dy((short) dy)
                        .listNotTrans(positions)
                        .build());
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public MapItemType findMapItemType(int idType) {
        for (MapItemType mapItemType : mapItemTypes) {
            if (mapItemType.getId() == idType) {
                return mapItemType;
            }
        }
        return null;
    }
}
