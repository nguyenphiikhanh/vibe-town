package avatar.play;

import avatar.manager.GameDataManager;
import avatar.model.MapItem;
import avatar.model.MapItemType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Map {
    
    private byte id;
    private byte type;
    private String name;
    private List<Zone> zones;
    private List<MapItem> mapItems;
    private List<MapItemType> mapItemTypes;
    
    public Map(int id, int type, int maxEntrys) {
        this.id = (byte) id;
        this.type = (byte) type;
        this.zones = new ArrayList<>();
        this.mapItems = new ArrayList<>();
        this.mapItemTypes = new ArrayList<>();
        load();
        for (int i = 0; i < maxEntrys; ++i) {
            this.zones.add(new Zone(this, (byte) i));
        }
    }
    
    public void load() {
        mapItems = GameDataManager.getInstance().getMapItems(id);
        if (mapItems == null) {
            mapItems = new ArrayList<>();
        }
        List<MapItemType> rawTypes = GameDataManager.getInstance().getMapItemTypes(id);
        if (rawTypes == null) {
            rawTypes = new ArrayList<>();
        }

        // 只发送本地图实际用到的 type（按 mapItems.typeID 过滤 + 去重 + 去 null），避免数量过大导致客户端解析失败。
        HashSet<Short> neededTypeIds = new HashSet<>();
        for (MapItem it : mapItems) {
            if (it != null) {
                neededTypeIds.add(it.getTypeID());
            }
        }
        HashSet<Short> seen = new HashSet<>();
        mapItemTypes = new ArrayList<>();
        for (MapItemType t : rawTypes) {
            if (t == null) {
                continue;
            }
            short tid = t.getId();
            if (!neededTypeIds.contains(tid)) {
                continue;
            }
            if (seen.add(tid)) {
                mapItemTypes.add(t);
            }
        }
    }
    
    public void update() {
        
    }
}
