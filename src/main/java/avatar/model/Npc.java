package avatar.model;

import avatar.item.Item;
import avatar.network.Message;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class Npc extends User {

    public static final int ID_ADD = 2000000000;

    @Getter
    private List<String> textChats;

    @Builder
    public Npc(int id, String name, short x, short y, ArrayList<Item> wearing) {
        setId(id + ID_ADD);
        setUsername(name);
        setRole((byte) 0);
        setX(x);
        setY(y);
        setWearing(wearing);
        textChats = new ArrayList<>();
    }

    public void addChat(String chat) {
        textChats.add(chat);
    }

    @Override
    public void sendMessage(Message ms) {

    }
}
