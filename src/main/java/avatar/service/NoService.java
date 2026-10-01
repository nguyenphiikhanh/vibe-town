
package avatar.service;

import avatar.model.User;
import avatar.network.Message;
import avatar.network.Session;
import avatar.play.MapService;


public class NoService extends MapService {

    private static final NoService instance = new NoService(null);

    public static NoService getInstance() {
        return instance;
    }

    public NoService(Session cl) {
        super(cl);
    }

    @Override
    public void sendMessage(Message ms) {

    }

    @Override
    public void chat(User user, String text) {

    }

    @Override
    public void move(User us) {

    }

}
