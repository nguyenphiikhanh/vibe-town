package avatar.service;

import avatar.lib.KeyValue;
import avatar.model.ImageInfo;
import avatar.constants.Cmd;
import avatar.model.User;
import java.util.Vector;
import avatar.server.Avatar;
import java.io.IOException;
import java.io.DataOutputStream;
import avatar.network.Message;
import avatar.network.Session;
import java.util.List;
import avatar.manager.GameDataManager;
import org.apache.log4j.Logger;

public class FarmService extends Service {

    private static final Logger logger = Logger.getLogger(Service.class);

    public FarmService(Session cl) {
        super(cl);
    }

    public void setBigFarm(Message ms) throws IOException {
        ms = new Message(51);
        DataOutputStream ds = ms.writer();
        int[] images = {99, 206};
        ds.writeByte(images.length);
        for (int i = 0; i < images.length; ++i) {
            ds.writeShort(i);
            ds.writeShort(images[i]);
        }
        ds.writeInt(15378);
        ds.writeInt(62724);
        ds.flush();
        this.session.sendMessage(ms);
    }

    public void getBigFarm(Message ms) throws IOException {
        short imageID = ms.reader().readShort();
        String folder = this.session.getResourcesPath() + "bigFarm/";
        byte[] dat = Avatar.getFile(folder + imageID + ".png");
        if (dat == null) {
            return;
        }
        ms = new Message(54);
        DataOutputStream ds = ms.writer();
        ds.writeShort(imageID);
        ds.writeShort(dat.length);
        ds.writeShort(dat.length);
        for (int i = 0; i < dat.length; ++i) {
            ds.writeByte(dat[i]);
        }
        ds.flush();
        this.session.sendMessage(ms);
    }

    public void getImageData() {
        try {
            List<ImageInfo> imageInfos = GameDataManager.getInstance().getFarmImageData();
            Message ms = new Message(Cmd.GET_IMAGE_FARM);
            DataOutputStream ds = ms.writer();
            ds.writeShort(imageInfos.size());
            for (ImageInfo imageInfo : imageInfos) {
                ds.writeShort(imageInfo.getId());
                ds.writeShort(imageInfo.getBigImageID());
                ds.writeByte(imageInfo.getX());
                ds.writeByte(imageInfo.getY());
                ds.writeByte(imageInfo.getW());
                ds.writeByte(imageInfo.getH());
            }
            ds.flush();
            this.sendMessage(ms);
        } catch (IOException e) {
            logger.debug("getImageData: " + e.getMessage());
        }
    }

    public void getTreeInfo(Message ms) throws IOException {
        byte[] dat = Avatar.getFile("res/data/farm_info.dat");
        if (dat == null) {
            return;
        }
        ms = new Message(Cmd.GET_TREE_INFO);
        DataOutputStream ds = ms.writer();
        ds.write(dat);
        ds.flush();
        this.session.sendMessage(ms);
    }

    public void getInventory(Message ms) throws IOException {
        User us = session.user;
        Vector<KeyValue<Integer, Integer>> hatgiong = new Vector<>();
        hatgiong.add(new KeyValue(34, 10));
        Vector<KeyValue<Integer, Integer>> nongsan = new Vector<>();
        nongsan.add(new KeyValue(9, 23684));
        nongsan.add(new KeyValue(50, 4000));
        Vector<KeyValue<Integer, Integer>> phanbon = new Vector<>();
        phanbon.add(new KeyValue<Integer, Integer>(118, 70));
        phanbon.add(new KeyValue<Integer, Integer>(119, 78));
        Vector<KeyValue<Integer, Integer>> nongsandacbiet = new Vector<>();
        nongsandacbiet.add(new KeyValue(255, 20));
        nongsandacbiet.add(new KeyValue(215, 680));
        nongsandacbiet.add(new KeyValue(214, 4));
        ms = new Message(60);
        DataOutputStream ds = ms.writer();
        ds.writeByte(hatgiong.size());
        for (KeyValue<Integer, Integer> i : hatgiong) {
            ds.writeByte(i.getKey());
            ds.writeShort(i.getValue());
        }
        ds.writeByte(nongsan.size());
        for (KeyValue<Integer, Integer> i : nongsan) {
            ds.writeByte(i.getKey());
            ds.writeShort(i.getValue());
        }
        ds.writeInt(this.session.user.getXu());
        ds.writeByte(us.getLeverFarm());
        ds.writeByte(us.getLeverPercen());
        ds.writeByte(phanbon.size());
        for (KeyValue<Integer, Integer> i : phanbon) {
            ds.writeShort(i.getKey());
            ds.writeShort(i.getValue());
        }
        ds.writeByte(nongsandacbiet.size());
        for (KeyValue<Integer, Integer> i : nongsandacbiet) {
            ds.writeShort(i.getKey());
            ds.writeShort(i.getValue());
        }
        ds.writeByte(1);
        ds.writeInt(64000);
        ds.writeBoolean(true);
        ds.writeShort(us.getLeverFarm());
        ds.writeByte(us.getLeverPercen());
        ds.writeByte(nongsan.size());
        for (KeyValue<Integer, Integer> i : nongsan) {
            ds.writeShort(i.getKey());
            ds.writeInt(i.getValue());
        }
        ds.writeByte(nongsandacbiet.size());
        for (KeyValue<Integer, Integer> i : nongsandacbiet) {
            ds.writeShort(i.getKey());
            ds.writeInt(i.getValue());
        }
        ds.flush();
        this.session.sendMessage(ms);
    }

    private void writeInfoCell(DataOutputStream ds) throws IOException {
        ds.writeShort(2880);
        ds.writeByte(40);
        ds.writeByte(0);
        ds.writeBoolean(false);
        ds.writeBoolean(false);
        ds.writeBoolean(false);
    }

    private void writeInfoAnimal(DataOutputStream ds) throws IOException {
        ds.writeInt(2000);
        ds.writeByte(100);
        ds.writeByte(0);
        ds.writeByte(20);
        ds.writeBoolean(true);
        ds.writeBoolean(false);
        ds.writeBoolean(true);
    }

    public void joinFarm(Message ms) throws IOException {
        int userId = ms.reader().readInt();
        boolean exitsTree = true;
        ms = new Message(61);
        DataOutputStream ds = ms.writer();
        ds.writeInt(userId);
        ds.writeByte(48);
        for (int i = 0; i < 48; ++i) {
            if (exitsTree) {
                ds.writeByte(5);
                this.writeInfoCell(ds);
            } else {
                ds.writeByte(-1);
            }
        }
        ds.writeByte(10);
        for (int i = 0; i < 10; ++i) {
            ds.writeByte(50 + i % 7);
            this.writeInfoAnimal(ds);
        }
        ds.writeByte(10);
        ds.writeByte(8);
        ds.writeShort(5000);
        ds.writeShort(43);
        ds.writeShort(46);
        ds.writeShort(170);
        ds.writeShort(170);
        ds.writeShort(0);
        ds.writeShort(0);
        for (int i = 0; i < 48; ++i) {
            ds.writeByte(1);
        }
        ds.writeShort(1);
        ds.writeShort(5);
        ds.flush();
        this.session.sendMessage(ms);
    }

    public void getImgFarm(Message ms) throws IOException {
        short imageID = ms.reader().readShort();
        String folder = session.getResourcesPath() + "farm/";
        byte[] dat = Avatar.getFile(folder + imageID + ".png");
        if (dat == null) {
            return;
        }
        ms = new Message(Cmd.GET_IMG_FARM);
        DataOutputStream ds = ms.writer();
        ds.writeShort(imageID);
        ds.writeShort(dat.length);
        ds.write(dat);
        ds.flush();
        this.session.sendMessage(ms);
    }
}

