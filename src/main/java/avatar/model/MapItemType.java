package avatar.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MapItemType {

    private short id;

    private short imgID;

    private short iconID;

    private short priceLuong;

    private short dx;

    private short dy;

    private String name;

    private String des;

    private int priceXu;

    private byte buy;

    private byte dir;

    private List<Position> listNotTrans;
}
