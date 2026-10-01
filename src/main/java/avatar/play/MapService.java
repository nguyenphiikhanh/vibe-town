
package avatar.play;

import avatar.item.Item;
import avatar.constants.Cmd;
import avatar.lucky.DialLucky;
import avatar.model.Gift;
import avatar.network.Message;
import avatar.network.Session;
import avatar.model.User;
import avatar.service.Service;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import org.apache.log4j.Logger;


public class MapService extends Service {

    private static final Logger logger = Logger.getLogger(MapService.class);

    @Setter
    private Zone zone;

    public MapService(Session cl) {
        super(cl);
    }

    public void leavePark(int userID) {
        try {
            Message ms = new Message(Cmd.PLAYER_LEAVE_PARK);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("leavePark()", ex);
        }
    }

    public void move(User us) {
        try {
            Message ms = new Message(Cmd.MOVE_PARK);
            DataOutputStream ds = ms.writer();
            ds.writeInt(us.getId());
            ds.writeShort(us.getX());
            ds.writeShort(us.getY());
            ds.writeByte(us.getDirect());
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("move()", ex);
        }
    }

    public void chat(User user, String text) {
        try {
            Message ms = new Message(zone.getMap().getId() == 22 ? Cmd.CHAT_FARM : Cmd.CHAT_PARK);
            DataOutputStream ds = ms.writer();
            ds.writeInt(user.getId());
            ds.writeUTF(text);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("chat()", ex);
        }
    }

    public void dialLucky(User user, short degree, List<Gift> gifts) {
        try {
            Message ms = new Message(Cmd.DIAL_LUCKY);
            DataOutputStream ds = ms.writer();
            ds.writeInt(user.getId());
            ds.writeShort(degree);
            ds.writeByte(gifts.size());
            for (Gift gift : gifts) {
                ds.writeByte(gift.getType());
                switch (gift.getType()) {
                    case DialLucky.ITEM:
                        ds.writeShort(gift.getId());
                        ds.writeByte(gift.getExpireDay());
                        break;

                    case DialLucky.XU:
                        ds.writeInt(gift.getXu());
                        break;

                    case DialLucky.XP:
                        ds.writeInt(gift.getXp());
                        break;

                    case DialLucky.LUONG:
                        ds.writeInt(gift.getLuong());
                        break;
                }
            }
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("dialLucky", ex);
        }
    }

    public void doAction(int userID, int idTo, short action) {
        try {
            Message ms = new Message(59);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.writeInt(idTo);
            ds.writeShort(action);
            if (action == -1) {
                ds.writeUTF("Có thằng nào vừa làm cái gì đó, thông báo admin biết nhen !");
            } else {
                ds.writeShort(10);
            }
            sendMessage(ms);
        } catch (IOException e) {
            logger.error("doAction()", e);
        }
    }

    public void doAvatarFeel(int userID, byte idFeel) {
        try {
            Message ms = new Message(Cmd.AVATAR_FEEL);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.writeByte(idFeel);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("doAvatarFeel()", ex);
        }
    }

    public void addPlayer(User us) {
        try {
            Message ms = new Message(51);
            DataOutputStream ds = ms.writer();
            ds.writeInt(us.getId());
            ds.writeUTF(us.getUsername());
            List<Item> wearingList = us.getWearing();
            if (wearingList == null) {
                wearingList = new ArrayList<>();
            }
            // Đảm bảo luôn có đủ 5 items
            List<Item> wearingToSend = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                if (i < wearingList.size() && wearingList.get(i) != null) {
                    wearingToSend.add(wearingList.get(i));
                } else {
                    Item emptyItem = new Item(0);
                    emptyItem.init();
                    wearingToSend.add(emptyItem);
                }
            }
            ds.writeByte((byte) wearingToSend.size());
            for (Item item : wearingToSend) {
                ds.writeShort(item.getId());
            }
            ds.writeShort(us.getX());
            ds.writeShort(us.getY());
            ds.writeByte(us.getRole());
            ds.writeByte(-1);
            ds.writeShort(-1);
            ds.writeShort(-1);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("addPlayer()", ex);
        }
    }

    public void usingPart(int userID, short itemID) {
        try {
            Message ms = new Message(Cmd.USING_PART);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.writeShort(itemID);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("usingPart()", ex);
        }
    }

    public void sendMessage(Message ms) {
        List<User> players = zone.getPlayers();
        synchronized (players) {
            for (User us : players) {
                us.sendMessage(ms);
            }
        }
    }
}
