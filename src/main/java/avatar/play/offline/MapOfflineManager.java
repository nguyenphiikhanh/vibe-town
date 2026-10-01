
package avatar.play.offline;

import java.util.ArrayList;
import java.util.List;


public class MapOfflineManager {

    private static final MapOfflineManager instance = new MapOfflineManager();

    public static MapOfflineManager getInstance() {
        return instance;
    }

    private final List<AbsMapOffline> maps = new ArrayList<>();

    public MapOfflineManager() {
        add(new ObjPremium(3));
        add(new ObjPet(4));
    }

    public void add(AbsMapOffline mapOffline) {
        synchronized (maps) {
            maps.add(mapOffline);
        }
    }

    public void remove(AbsMapOffline mapOffline) {
        synchronized (maps) {
            maps.remove(mapOffline);
        }
    }

    public AbsMapOffline find(int id) {
        synchronized (maps) {
            for (AbsMapOffline mapOffline : maps) {
                if (mapOffline.getId() == id) {
                    return mapOffline;
                }
            }
            return null;
        }
    }
}
