package avatar.server;

import avatar.constants.NpcName;
import avatar.item.Item;
import avatar.item.Part;
import avatar.item.PartManager;
import avatar.model.User;
import avatar.lucky.DialLucky;
import avatar.lucky.DialLuckyManager;
import avatar.model.Menu;
import avatar.model.Npc;
import java.io.IOException;

import avatar.play.NpcManager;
import avatar.play.Zone;
import java.util.ArrayList;
import java.util.List;

public class NpcHandler {

    public static void quaySo(User us, byte type) {
        DialLucky dl = DialLuckyManager.getInstance().find(type);
        if (dl != null) {
            if (dl.getType() == DialLuckyManager.MIEN_PHI) {
                Item itm = us.findItemInChests(593);
                if (itm == null || itm.getQuantity() <= 0) {
                    us.getAvatarService().serverDialog("Bạn không có Vé quay số miễn phí!");
                    return;
                }
            }
            if (dl.getType() == DialLuckyManager.XU) {
                if (us.getXu() < 15000) {
                    us.getAvatarService().serverDialog("Bạn không đủ xu!");
                    return;
                }
            }
            if (dl.getType() == DialLuckyManager.LUONG) {
                if (us.getLuong() < 5) {
                    us.getAvatarService().serverDialog("Bạn không đủ lượng!");
                    return;
                }
            }
        }
        us.setDialLucky(dl);
        dl.show(us);
    }

    public static void handlerCommunicate(int npcId, User us) throws IOException {
        Zone z = us.getZone();
        if (z == null) {
            return;
        }
        
        int mapId = z.getMap().getId();
        int zoneId = z.getId();
        
        // Tìm NPC bằng NpcManager thay vì tìm trong Zone
        Npc npc =
                NpcManager.getInstance().find(mapId, zoneId, npcId);
        if (npc == null) {
            System.out.println("[handlerCommunicate] NPC not found: npcId=" + npcId + " map=" + mapId + " zone=" + zoneId);
            return;
        }

        int npcIdCase = npcId - Npc.ID_ADD;

        switch (npcIdCase) {
            case NpcName.QUAY_SO: {
                List<Menu> list = new ArrayList<>();
                Menu quaySo = new Menu("Quay Số");
                quaySo.addMenu(new Menu("5 lượng", () -> {
                    quaySo(us, DialLuckyManager.LUONG);
                }));
                quaySo.addMenu(new Menu("15.000 xu", () -> {
                    quaySo(us, DialLuckyManager.XU);
                }));
                quaySo.addMenu(new Menu("Quay số miễn phí", () -> {
                    quaySo(us, DialLuckyManager.MIEN_PHI);
                }));
                quaySo.addMenu(new Menu("Thoát"));
                quaySo.setNpcName("quay số");
                quaySo.setNpcChat("Vòng quay may mắn nhận những vật phẩm quí hiếm đây! Mại dô!");
                list.add(quaySo);
                list.add(new Menu("Xem Hướng dẫn", () -> {
                    us.getAvatarService().customTab("Hướng dẫn", "Để tham gia quay số bạn phải có ít nhất 5 lượng hoặc 25 ngàn xu trong tài khoản và 3 ô trống trong rương\n Bạn sẽ nhận được danh sách những món đồ đặc biệt mà bạn muốn quay. Những món đồ đặc biệt này bạn sẽ không thể tìm thấy trong bất cứ shop nào của thành phố.\n Sau khi chọn được món đồ muốn quay bạn sẽ bắt đầu chỉnh vòng quay để quay\n Khi quay bạn giữ phím 5 để chỉnh lực quay sau đó thả ra để bắt đầu quay\n Khi quay bạn sẽ có cơ hội trúng từ 1 đến 3 món quà\n Quà của bạn nhận được có thể là vật phẩm bất kì, xu, hoặc điểm kinh nghiệm\n Bạn có thể quay được những bộ đồ bán bằng lượng như đồ hiệp sĩ, pháp sư...\n Tuy nhiên vật phẩm bạn quay được sẽ có hạn sử dụng trong một số ngày nhất định.\n Nếu bạn quay được đúng món đồ mà bạn đã chọn thì bạn sẽ được sở hữu món đồ đó vĩnh viễn.\n Hãy thử vận may để sở hữa các món đồ cực khủng nào !!!");
                }));
                list.add(new Menu("Thoát"));
                us.setMenus(list);
                us.getAvatarService().openUIMenu(npcId, 0, list, "quay số", "Vòng quay may mắn nhận những vật phẩm quí hiếm đây! Mại dô!");
                break;
            }

            case NpcName.LAI_BUON: {
                List<Menu> list = new ArrayList<>();
                Menu baoDanh = new Menu("Báo danh hàng ngày");
                baoDanh.addMenu(new Menu("Báo danh hàng ngày", () -> {
                    Item item = new Item(593, -1, 1);
                    us.addItemToChests(item);
                    us.addExp(5);
                    us.getService().serverMessage("Bạn nhận được 1 điểm chuyên cần + 1 thẻ quay số miễn phí");
                }));
                baoDanh.addMenu(new Menu("Thông tin chuyên cần", () -> {

                }));
                baoDanh.addMenu(new Menu("Đổi quà", () -> {

                }));
                baoDanh.addMenu(new Menu("Hướng dẫn", () -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Đăng nhập mỗi ngày để nhận quà:").append("\n");
                    sb.append("Báo danh mỗi ngày để nhận 1 bịch phân bón giảm 15 phút").append("\n");
                    sb.append("Báo danh mỗi 3 ngày để nhận 1 bịch phân bón giảm 30 phút").append("\n");
                    sb.append("Báo danh mỗi 6 ngày để nhận 1 bịch phân bón giảm 60 phút").append("\n");
                    sb.append("Bên cạnh đó  báo danh mỗi ngày nhận dduocww 5 điểm chuyên cần và 1 thẻ quay số miễn phí").append("\n");
                    sb.append("Dùng điểm chuyên cần để nhận đucợ những món quà có giá trị trong tương lai").append("\n");
                    us.getAvatarService().customTab("Báo danh hàng ngày", sb.toString());
                }));
                list.add(baoDanh);
                us.setMenus(list);
                us.getAvatarService().openUIMenu(npcId, 0, list, "quay số", "Vòng quay may mắn nhận những vật phẩm quí hiếm đây! Mại dô!");
            }
            break;

            case NpcName.VE_SO: {
                List<Menu> list = new ArrayList<>();
                Menu veSo = new Menu("Mua Vé Số");
                veSo.addMenu(new Menu("Mua vé số", () -> {
                    List<Part> parts = PartManager.getInstance().getPartsBySell((byte) 9);
                    List<Item> items = new ArrayList<>();
                    for (Part p : parts) {
                        items.add(new Item(p.getId()));
                    }
                    us.getAvatarService().openUIShop(9, "Vé Số", items);
                }));
                veSo.addMenu(new Menu("Hướng dẫn", () -> {
                    us.getAvatarService().customTab("Hướng dẫn mua vé số", "Mua vé số để tham gia dự đoán kết quả xổ số.\nChọn con số may mắn của bạn và chờ đợi kết quả!");
                }));
                veSo.addMenu(new Menu("Thoát"));
                veSo.setNpcName("Vé Số");
                veSo.setNpcChat("Chào bạn! Tôi có những tấm vé số may mắn đây. Mua ngay để thử vận may nào!");
                list.add(veSo);
                us.setMenus(list);
                us.getAvatarService().openUIMenu(npcId, 0, list, "Vé Số", "Chào bạn! Tôi có những tấm vé số may mắn đây. Mua ngay để thử vận may nào!");
            }
            break;
        }
    }

    public static void handlerAction(User us, int npcId, byte menuId, byte select) throws IOException {
        Zone z = us.getZone();
        if (z != null) {
            User u = z.find(npcId);
            if (u == null) {
                return;
            }
        } else {
            return;
        }
        int npcIdCase = npcId - 2000000000;
        List<Menu> menus = us.getMenus();
        if (menus != null && select < menus.size()) {
            Menu menu = menus.get(select);
            if (menu.isMenu()) {
                us.setMenus(menu.getMenus());
                us.getAvatarService().openUIMenu(npcId, menuId + 1, menu.getMenus(), menu.getNpcName(), menu.getNpcChat());
            } else if (menu.getRunnable() != null) {
                menu.run();
            } else {
                switch (menu.getId()) {

                }
            }
            return;
        }
    }
}
