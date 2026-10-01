
package avatar.item;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Item {
    
    private int id;
    private long expired;
    private int quantity;
    private Part part;
    
    public Item(int id) {
        this.id = id;
        init();
    }
    
    @Builder
    public Item(int id, long expired, int quantity) {
        this.id = id;
        this.expired = expired;
        this.quantity = quantity;
        init();
    }
    
    public boolean isForever() {
        return this.expired == -1;
    }
    
    public int getDay() {
        return (int) ((this.expired - System.currentTimeMillis()) / 1000 / 60 / 60 / 24) + 1;
    }
    
    public synchronized int increase(int quantity) {
        this.quantity += quantity;
        return this.quantity;
    }
    
    public synchronized int reduce(int quantity) {
        return increase(-quantity);
    }
    
    public String expiredString() {
        if (isForever()) {
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return "Ngày hết hạn: " + dateFormat.format(new Date(this.expired));
    }
    
    public byte reliability() {
        int reliability = getDay() * 100 / 30;
        if (isForever()) {
            reliability = 100;
        } else if (reliability > 100) {
            reliability = 100;
        } else if (reliability < 0) {
            reliability = 0;
        }
        return (byte) reliability;
    }
    
    public void init() {
        this.part = PartManager.getInstance().findPartByID(id);
    }
}
