package avatar.service;

import avatar.network.Session;
import org.apache.log4j.Logger;

public class ParkService extends Service {
    
    private static final Logger logger = Logger.getLogger(AvatarService.class);

    public ParkService(Session cl) {
        super(cl);
    }
}
