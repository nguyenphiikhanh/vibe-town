
package avatar.lucky;

import avatar.convert.ItemConverter;
import avatar.db.DbManager;
import avatar.item.Item;
import avatar.lib.RandomCollection;
import avatar.model.Gift;
import avatar.model.User;
import avatar.server.Utils;
import avatar.service.AvatarService;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;


public class DialLucky {

    public static final byte ITEM = 1;
    public static final byte XU = 2;
    public static final byte XP = 3;
    public static final byte LUONG = 4;

    @Getter
    private byte type;
    private final RandomCollection<Byte> randomType = new RandomCollection<>();
    private final RandomCollection<Item> randomItem = new RandomCollection<>();

    public DialLucky(byte type) {
        this.type = type;
        randomType.add(70, ITEM);
        randomType.add(15, XU);
        randomType.add(14, XP);
        randomType.add(1, LUONG);
        load();
    }

    public void load() {
        try {
            String text = null;
            switch (this.type) {
                case DialLuckyManager.XU:
                    text = "SELECT * FROM `dial_lucky` WHERE `xu` = 1;";
                    break;

                case DialLuckyManager.LUONG:
                    text = "SELECT * FROM `dial_lucky` WHERE `luong` = 1;";
                    break;

                case DialLuckyManager.MIEN_PHI:
                    text = "SELECT * FROM `dial_lucky` WHERE `free` = 1;";
                    break;
            }
            PreparedStatement ps = DbManager.getInstance().getConnectionForGame().prepareStatement(text);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int itemID = rs.getInt("item_id");
                int ratio = rs.getInt("ratio");
                Item item = new Item(itemID);
                randomItem.add(ratio, item);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(DialLucky.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void show(User us) {
        AvatarService service = us.getAvatarService();
        NavigableMap<Double, Item> map = randomItem.getMap();
        List<Item> items = new ArrayList<>();
        map.entrySet().stream().forEach((t) -> {
            Item item = t.getValue();
            byte gender = item.getPart().getGender();
            if (!((gender == 2 || gender == 1) && (us.getGender() != gender))) {
                items.add(item);
            }
        });
        service.openUIShop(100, "Quay số", items);
    }

    public void doDial(User us, int itemID, int degree) {
        List<Gift> gifts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            byte type = randomType.next();
            Gift gift = new Gift();
            gift.setType((byte) type);
            if (type == ITEM) {
                Item item = randomItem.next();
                item = ItemConverter.getInstance().newItem(item);
                gift.setId(item.getId());
                if (item.getId() == itemID) {
                    gift.setExpireDay(-1);
                } else {
                    int time = Utils.getRandomInArray(new int[]{3, 7, 15, 30});
                    item.setExpired(System.currentTimeMillis() + (86400000L * time));
                    gift.setExpireDay(time);
                }
                us.addItemToChests(item);
            } else if (type == XU) {
                int xu = Utils.nextInt(1, 10) * 1000;
                gift.setXu(xu);
                us.updateXu(xu);
            } else if (type == XP) {
                int xp = Utils.nextInt(1, 10) * 10;
                gift.setXp(xp);
                us.addExp(xp);
            } else if (type == LUONG) {
                int luong = Utils.nextInt(1, 5);
                gift.setLuong(luong);
                us.updateLuongKhoa(luong);
            }
            gifts.add(gift);
        }
        us.getMapService().dialLucky(us, (short) degree, gifts);
    }

}
