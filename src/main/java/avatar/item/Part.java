package avatar.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class Part {
    
    private int id;
    private String name;
    private int coin;
    private int gold;
    private short icon;
    private short type;
    private byte zOrder;
    private byte sell;
    private byte level;
    private byte gender;
    private int expiredDay;
    private short[] imgID;
    private byte[] dx;
    private byte[] dy;
}
