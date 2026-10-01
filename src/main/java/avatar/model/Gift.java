
package avatar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class Gift {

    private int id;
    private byte type;
    private int xu;
    private int xp;
    private int luong;
    private int expireDay;
}
