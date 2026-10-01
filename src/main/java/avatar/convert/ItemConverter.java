
package avatar.convert;

import avatar.item.Item;


public class ItemConverter {
    
    private static final ItemConverter instance = new ItemConverter();
    
    public static ItemConverter getInstance() {
        return instance;
    }
    
    public Item newItem(Item oldItem) {
        return Item.builder()
                .id(oldItem.getId())
                .quantity(oldItem.getQuantity())
                .expired(oldItem.getExpired())
                .build();
    }
}
