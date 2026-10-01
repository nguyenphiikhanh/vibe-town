
package avatar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@AllArgsConstructor
@Builder
@Getter
public class Food {

    private int id;
    private String name;
    private String description;
    private int shop;
    private int icon;
    private int price;
    private int percentHelth;
}
