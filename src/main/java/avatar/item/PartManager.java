package avatar.item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import avatar.db.DbManager;
import lombok.Getter;

public class PartManager {

    private static final PartManager instance = new PartManager();

    public static PartManager getInstance() {
        return instance;
    }

    @Getter
    private final List<Part> parts = new ArrayList<>();

    public void load() {
        try {
            parts.clear();
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame()
                    .prepareStatement("SELECT * FROM `items`;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int coin = rs.getInt("coin");
                int gold = rs.getInt("gold");
                short type = rs.getShort("type");
                String name = rs.getString("name");
                short icon = rs.getShort("icon");
                int expiredDay = rs.getInt("expired_day");
                byte level = rs.getByte("level");
                byte sell = rs.getByte("sell");
                byte zOrder = rs.getByte("zorder");
                byte gender = 0;
                try {
                    gender = rs.getByte("gender");
                } catch (SQLException ignore) {
                }
                short[] imgID = new short[15];
                byte[] dx = new byte[15];
                byte[] dy = new byte[15];
                JSONArray animation = (JSONArray) JSONValue.parse(rs.getString("animation"));
                if (animation != null) {
                    int size = Math.min(15, animation.size());
                    for (int i = 0; i < size; i++) {
                        JSONObject obj = (JSONObject) animation.get(i);
                        if (obj == null) {
                            continue;
                        }
                        Object img = obj.get("img");
                        Object odx = obj.get("dx");
                        Object ody = obj.get("dy");
                        if (img != null) {
                            imgID[i] = ((Long) img).shortValue();
                        }
                        if (odx != null) {
                            dx[i] = ((Long) odx).byteValue();
                        }
                        if (ody != null) {
                            dy[i] = ((Long) ody).byteValue();
                        }
                    }
                }
                parts.add(Part.builder().id(id)
                        .coin(coin)
                        .gold(gold)
                        .type(type)
                        .name(name)
                        .icon(icon)
                        .expiredDay(expiredDay)
                        .level(level)
                        .sell(sell)
                        .zOrder(zOrder)
                        .gender(gender)
                        .imgID(imgID)
                        .dx(dx)
                        .dy(dy)
                        .build());
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Part> getAvatarPart() {
        
        return parts.stream().filter((t) -> t != null && t.getId() < 2000).collect(Collectors.toList());
    }

    public Part findPartByID(int id) {
        for (Part part : parts) {
            if (part.getId() == id) {
                return part;
            }
        }
        return null;
    }

    public List<Part> getPartsBySell(byte sell) {
        return parts.stream().filter((t) -> t != null && t.getSell() == sell).collect(Collectors.toList());
    }

}
