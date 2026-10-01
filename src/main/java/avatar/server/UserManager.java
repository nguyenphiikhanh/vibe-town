
package avatar.server;

import avatar.model.User;
import java.util.LinkedList;
import java.util.List;


public class UserManager {

    private static final UserManager instance = new UserManager();

    public static UserManager getInstance() {
        return instance;
    }
    
    private final List<User> users = new LinkedList<>();
    
    public void add(User us) {
        synchronized(users) {
            users.add(us);
        }
    }
    
    public void remove(User us) {
        synchronized(users) {
            users.remove(us);
        }
    }
    
    public User find(int id) {
        synchronized(users) {
            for (User us : users) {
                if (us.getId() == id) {
                    return us;
                }
            }
        }
        return null;
    }
}
