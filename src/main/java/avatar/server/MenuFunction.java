package avatar.server;

import avatar.model.User;
import java.io.DataOutputStream;
import java.io.IOException;
import avatar.network.Message;

public class MenuFunction {

    private User us;

    public MenuFunction(User user) {
        this.us = user;
    }

    public void comingSoon() {
        try {
            Message ms = new Message(-10);
            DataOutputStream ds = ms.writer();
            ds.writeUTF("Chức năng đang được xây dựng, vui lòng quay lại sau !!!");
            ds.flush();
            this.us.sendMessage(ms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void textBox(int userId, int menuId, String message, int type) {
        try {
            Message ms = new Message(-60);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userId);
            ds.writeByte(menuId);
            ds.writeUTF(message);
            ds.writeByte(type);
            ds.flush();
            this.us.sendMessage(ms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handlerTextBox(Message ms) throws IOException {
        int userId = ms.reader().readInt();
        byte menuId = ms.reader().readByte();
        String text = ms.reader().readUTF();
    }

    public void handlerMenuOption(Message ms) throws IOException {
        int userId = ms.reader().readInt();
        byte menuId = ms.reader().readByte();
        byte select = ms.reader().readByte();
        System.out.println("userId = " + userId + ", menuId = " + menuId + ", select = " + select);
        if (userId >= 2000000000) {
            NpcHandler.handlerAction(this.us, userId, menuId, select);
            return;
        }
        
    }

    private void sendCityMap() throws IOException {
        String folder = "res/map/";
        byte[] data = Avatar.getFile(String.valueOf(folder) + "cityMap.dat");
        byte[] image = Avatar.getFile(String.valueOf(folder) + "cityMap.png");
        byte[] map27 = Avatar.getFile(String.valueOf(folder) + "27.dat");
        byte[] map_bg = Avatar.getFile(String.valueOf(folder) + "bg/27.png");
        Message ms = new Message(-92);
        DataOutputStream ds = ms.writer();
        ds.writeByte(1);
        ds.writeInt(image.length);
        ds.write(image);
        ds.writeInt(data.length);
        ds.writeByte(34);
        ds.write(data);
        short[] idImg = new short[]{821, 827, 850};
        String[] doorName = new String[]{"Sân Bay", "Bãi Biển", "Trung Tâm Giải Trí"};
        byte[] x = new byte[]{23, 9, 21};
        byte[] y = new byte[]{7, 13, 18};
        ds.writeByte(idImg.length);
        int i = 0;
        while (i < idImg.length) {
            ds.writeByte(i);
            ds.writeShort(idImg[i]);
            ds.writeUTF(doorName[i]);
            ds.writeByte(x[i]);
            ds.writeByte(y[i]);
            ++i;
        }
        ds.flush();
        this.us.sendMessage(ms);
        // ms = new Message(-93);
        // ds = ms.writer();
        // ds.writeByte(27);
        // ds.writeByte(1);
        // ds.writeShort(306);
        // ds.writeByte(34);
        // ds.writeShort(map27.length);
        // ds.write(map27);
        // short[] arr = new short[] { 828, -1, 835, 853, 852, 836, 832, 832, 851, 833,
        // 837, 842, 843, -1, -1 };
        // ds.writeByte(arr.length);
        // int j = 0;
        // while (j < arr.length) {
        // ds.writeShort(arr[j]);
        // ++j;
        // }
        // ds.writeShort(map_bg.length);
        // ds.write(map_bg);
        // try {
        // int mapId = 27;
        // String GET_MAP_ITEM_TYPE = "SELECT * FROM `map_item_type` WHERE `map_id` =
        // ?";
        // PreparedStatement ps =
        // DbManager.getInstance().getConnectionForGame().prepareStatement(GET_MAP_ITEM_TYPE,
        // 1005, 1007);
        // ps.setInt(1, mapId);
        // ResultSet res = ps.executeQuery();
        // if (res != null) {
        // ds.writeShort(204);
        // res.last();
        // int rows = res.getRow();
        // ds.writeByte(rows);
        // res.beforeFirst();
        // while (res.next()) {
        // ds.writeByte(res.getByte("id_type"));
        // ds.writeShort(res.getShort("id_img"));
        // ds.writeByte(res.getByte("icon_id"));
        // ds.writeShort(res.getShort("dx"));
        // ds.writeShort(res.getShort("dy"));
        // JSONArray av_position = (JSONArray)
        // JSONValue.parse(res.getString("av_position"));
        // ds.writeByte(av_position.size());
        // int k = 0;
        // while (k < av_position.size()) {
        // JSONObject av_position_element = (JSONObject) av_position.get(k);
        // ds.writeByte(((Long) av_position_element.get("x")).shortValue());
        // ds.writeByte(((Long) av_position_element.get("y")).shortValue());
        // ++k;
        // }
        // }
        // byte[] mapItemA = new byte[] { 26, 27, 28, 29, 31, 64 };
        // byte[] byArray = new byte[6];
        // byArray[5] = 4;
        // byte[] mapItemB = byArray;
        // byte[] mapItemC = new byte[] { 2, 7, 27, 20, 33, 15 };
        // byte[] byArray2 = new byte[6];
        // byArray2[0] = 1;
        // byArray2[1] = 1;
        // byArray2[2] = 1;
        // byArray2[3] = 1;
        // byArray2[4] = 1;
        // byte[] mapItemD = byArray2;
        // ds.writeByte(mapItemA.length);
        // int l = 0;
        // while (l < mapItemA.length) {
        // ds.writeByte(mapItemA[l]);
        // ds.writeByte(mapItemB[l]);
        // ds.writeByte(mapItemC[l]);
        // ds.writeByte(mapItemD[l]);
        // ++l;
        // }
        // } else {
        // ds.writeShort(0);
        // }
        // } catch (SQLException e) {
        // e.printStackTrace();
        // }
        // ds.flush();
        // this.us.sendMessage(ms);
    }
}
