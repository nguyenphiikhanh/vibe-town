
package avatar.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Menu {

    private int id;
    private String name;
    private List<Menu> menus;
    private Runnable runnable;
    private String npcName;
    private String npcChat;

    public Menu(String name) {
        this.name = name;
        init();
    }

    public Menu(int id, String name) {
        this.id = id;
        this.name = name;
        init();
    }

    public Menu(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
        init();
    }

    public Menu(int id, String name, Runnable runnable) {
        this.id = id;
        this.name = name;
        this.runnable = runnable;
        init();
    }

    private void init() {
        this.menus = new ArrayList<>();
    }

    public void addMenu(Menu menu) {
        this.menus.add(menu);
    }

    public boolean isMenu() {
        return this.menus.size() > 0;
    }

    public void run() {
        if (runnable != null) {
            runnable.run();
        }
    }
}
