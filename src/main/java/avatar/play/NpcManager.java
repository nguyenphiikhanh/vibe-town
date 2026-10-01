
package avatar.play;

import avatar.model.Npc;
import java.util.ArrayList;


public class NpcManager {

    private static final NpcManager instance = new NpcManager();

    public static NpcManager getInstance() {
        return instance;
    }

    private final ArrayList<Npc> npcs = new ArrayList<>();

    public void add(Npc npc) {
        synchronized (npcs) {
            npcs.add(npc);
        }
    }

    public void remove(Npc npc) {
        synchronized (npcs) {
            npcs.remove(npc);
        }
    }

    public Npc find(int map, int zone, int id) {
        synchronized (npcs) {
            for (Npc npc : npcs) {
                if (npc.getId() == id) {
                    Zone z = npc.getZone();
                    if (z != null && z.getId() == zone && z.getMap().getId() == map) {
                        return npc;
                    }
                }
            }
        }
        return null;
    }
}
