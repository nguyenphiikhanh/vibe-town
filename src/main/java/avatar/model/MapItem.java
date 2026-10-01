package avatar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class MapItem {

    private short id;
    private short typeID;
    private byte type;
    private byte x;
    private byte y;
}
