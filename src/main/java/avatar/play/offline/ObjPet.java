
package avatar.play.offline;

import avatar.item.Item;
import avatar.model.Npc;
import java.util.ArrayList;


public class ObjPet extends AbsMapOffline {

    public ObjPet(int id) {
        super(id);
    }

    @Override
    public void init() {
        Npc pet = Npc.builder().id(1213)
                .name("thu nuoi")
                .x((short) 175)
                .y((short) 168)
                .wearing(new ArrayList<>()).build();
        pet.addItemToWearing(new Item(3079));
        pet.addItemToWearing(new Item(3078));
        pet.addItemToWearing(new Item(0));
        pet.addItemToWearing(new Item(4));
        pet.addItemToWearing(new Item(3077));
        pet.addChat("Chào mừng bạn đến với shop thú cưng");
        pet.addChat("Các con thú thật dễ tương phải không nào");
        pet.addChat("Mời bạn lại xem");
        addNpc(pet);
    }

}
