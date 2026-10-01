
package avatar.play;


public class HouseItem {

    public short itemId;
    public short x;
    public short y;
    public byte rotate;

    public HouseItem() {
    }

    public HouseItem(int itemId, int x, int y, int rotate) {
        this.itemId = (short) itemId;
        this.x = (short) (x / 24);
        this.y = (short) (y / 24);
        this.rotate = (byte) rotate;
    }
}
