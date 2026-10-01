
package avatar.play.offline;

import avatar.item.Item;
import avatar.model.Npc;
import java.util.ArrayList;


public class ObjPremium extends AbsMapOffline {

    public ObjPremium(int id) {
        super(id);
    }

    @Override
    public void init() {
        Npc thuongNhan = Npc.builder().id(1211)
                .name("thuong nhan")
                .x((short) 180)
                .y((short) 168)
                .wearing(new ArrayList<>()).build();
        thuongNhan.addItemToWearing(new Item(3079));
        thuongNhan.addItemToWearing(new Item(3078));
        thuongNhan.addItemToWearing(new Item(0));
        thuongNhan.addItemToWearing(new Item(4));
        thuongNhan.addItemToWearing(new Item(3077));
        thuongNhan.addChat("Chào mừng các bạn đến với shop premium");
        thuongNhan.addChat("Mời các bạn xem hàng");
        addNpc(thuongNhan);
        Npc shopDong = Npc.builder().id(1212)
                .name("shop dong")
                .x((short) 276)
                .y((short) 168)
                .wearing(new ArrayList<>()).build();
        shopDong.addItemToWearing(new Item(3079));
        shopDong.addItemToWearing(new Item(3078));
        shopDong.addItemToWearing(new Item(0));
        shopDong.addItemToWearing(new Item(4));
        shopDong.addItemToWearing(new Item(3077));
        shopDong.addChat("Shop xèng đê");
        shopDong.addChat("Xèng đê, xèng đê");
        addNpc(shopDong);
    }

}
