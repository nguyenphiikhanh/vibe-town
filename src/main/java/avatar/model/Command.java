
package avatar.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Command {

    private int icon;
    private String name;
    private short anthor;
    private byte type;

    public Command(String name, int icon) {
        this.icon = icon;
        this.name = name;
    }

    public Command(short anthor, String name, int icon, byte type) {
        this(name, icon);
        this.anthor = anthor;
        this.type = type;
    }
}
