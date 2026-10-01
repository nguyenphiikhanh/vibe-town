package avatar.server;

import avatar.db.DbManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Random;

public class Utils {

    private static final Random rand;
    private static final SimpleDateFormat dateFormat;
    private static final short[] sinData;
    private static final short[] cosData;
    private static final int[] tanData;
    private static Pattern userNamePattern;
    private static final char[] SOURCE_CHARACTERS;
    private static final char[] DESTINATION_CHARACTERS;

    public static final int sin(int arg) {
        if ((arg = toArg0_360(arg)) >= 0 && arg < 90) {
            return Utils.sinData[arg];
        }
        if (arg >= 90 && arg < 180) {
            return Utils.sinData[180 - arg];
        }
        if (arg >= 180 && arg < 270) {
            return -Utils.sinData[arg - 180];
        }
        return -Utils.sinData[360 - arg];
    }

    public static final int cos(int arg) {
        if ((arg = toArg0_360(arg)) >= 0 && arg < 90) {
            return Utils.cosData[arg];
        }
        if (arg >= 90 && arg < 180) {
            return -Utils.cosData[180 - arg];
        }
        if (arg >= 180 && arg < 270) {
            return -Utils.cosData[arg - 180];
        }
        return Utils.cosData[360 - arg];
    }

    public static final int getArg(int cos, int sin) {
        if (cos == 0) {
            return (sin == 0) ? 0 : ((sin < 0) ? 270 : 90);
        }
        int arg = Math.abs((sin << 10) / cos);
        while (true) {
            for (int i = 0; i <= 90; ++i) {
                if (Utils.tanData[i] >= arg) {
                    arg = i;
                    if (sin >= 0 && cos < 0) {
                        arg = 180 - arg;
                    }
                    if (sin < 0 && cos < 0) {
                        arg += 180;
                    }
                    if (sin < 0 && cos >= 0) {
                        arg = 360 - arg;
                    }
                    return arg;
                }
            }
            arg = 0;
            continue;
        }
    }

    public static final int toArg0_360(int arg) {
        if (arg >= 360) {
            arg -= 360;
        }
        if (arg < 0) {
            arg += 360;
        }
        return arg;
    }

    public static int getSqrt(int num) {
        if (num <= 0) {
            return 0;
        }
        int newS = (num + 1) / 2;
        int oddS;
        do {
            oddS = newS;
            newS = newS / 2 + num / (newS * 2);
        } while (Math.abs(oddS - newS) > 1);
        return newS;
    }

    public static int nextInt(int from, int to) {
        return from + Utils.rand.nextInt(to - from);
    }

    public static int nextInt(int max) {
        return Utils.rand.nextInt(max);
    }

    public static int nextInt(int[] percen) {
        int next = nextInt(1000);
        int i;
        for (i = 0; i < percen.length; ++i) {
            if (next < percen[i]) {
                return i;
            }
            next -= percen[i];
        }
        return i;
    }

    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int getRandomInArray(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static Date getDate(String dateString) {
        try {
            return Utils.dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setTimeout(Runnable runnable, long delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static String toDateString(Date date) {
        return Utils.dateFormat.format(date);
    }

    public static void addNumDay(Date dat, int nDays) {
        dat.setTime(dat.getTime() + nDays * 86400000L);
    }

    public static int getNumDay(Date from, Date to) {
        return (int) ((to.getTime() - from.getTime()) / 1000L / 86400L);
    }

    public static boolean isNewDay(Date from, Date to) {
        int dateFrom = from.getDate();
        int dateTo = to.getDate();
        return dateFrom != dateTo;
    }

    public static void addNumHour(Date dat, int nHours) {
        dat.setTime(dat.getTime() + nHours * 3600000L);
    }

    public static int getNumHour(Date from, Date to) {
        return (int) ((to.getTime() - from.getTime()) / 1000L / 3600L);
    }

    public static String getStringNumber(int num) {
        if (num >= 1000000000) {
            return num / 1000000000 + "t\u1ef7";
        }
        if (num >= 1000000) {
            return num / 1000000 + "tr";
        }
        if (num >= 10000) {
            return num / 1000 + "k";
        }
        return String.valueOf(num);
    }

    public static short getShort(byte[] ab, int off) {
        return (short) ((ab[off] & 0xFF) << 8 | (ab[off + 1] & 0xFF));
    }

    public static boolean inRegion(int x, int y, int x0, int y0, int w, int h) {
        return x >= x0 && x < x0 + w && y >= y0 && y < y0 + h;
    }

    public static boolean intersecRegions(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return x1 + w1 >= x2 && x1 <= x2 + w2 && y1 + h1 >= y2 && y1 <= y2 + h2;
    }

    public static boolean isNotAlpha(int rgb) {
        return rgb >> 24 != 0;
    }

    public static int getTeamPoint(int TongDD, int nteam) {
        if (nteam == 1) {
            return 0;
        }
        return (TongDD - 100) / 100 + (TongDD - 100) * nteam / 1000;
    }

    public static String tinhRank(int top, boolean profile) {
        if (top > 0) {
            if (top <= 20) {
                return "Th\u00e1ch \u0111\u1ea5u";
            }
            if (top <= 40) {
                return "Cao Th\u1ee7";
            }
            if (top <= 60) {
                return "Kim C\u01b0\u01a1ng" + tinhRankphu(top, 140);
            }
            if (top <= 80) {
                return "B\u1ea1ch Kim" + tinhRankphu(top, 140);
            }
            if (top <= 100) {
                return "V\u00e0ng" + tinhRankphu(top, 140);
            }
            if (top <= 120) {
                return "B\u1ea1c" + tinhRankphu(top, 140);
            }
            if (top <= 140) {
                return "\u0110\u1ed3ng" + tinhRankphu(top, 140);
            }
        }
        if (profile) {
            return "Ch\u01b0a c\u00f3 h\u1ea1ng";
        }
        return null;
    }

    public static int tinhRankIcon(int top) {
        if (top > 0) {
            if (top <= 20) {
                return 1006;
            }
            if (top <= 40) {
                return 1005;
            }
            if (top <= 60) {
                return 1004;
            }
            if (top <= 80) {
                return 1003;
            }
            if (top <= 100) {
                return 1002;
            }
            if (top <= 120) {
                return 1001;
            }
            if (top <= 140) {
                return 1000;
            }
        }
        return 0;
    }

    public static String tinhRankphu(int top, int i) {
        String capRankS = null;
        int capRank = (top - i) / 4;
        if (capRank == 1) {
            capRankS = "I";
        } else if (capRank == 2) {
            capRankS = "II";
        } else if (capRank == 3) {
            capRankS = "III";
        } else if (capRank == 4) {
            capRankS = "IV";
        } else if (capRank == 5) {
            capRankS = "V";
        }
        return capRankS;
    }

    public static String md5(String str) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            BigInteger bi = new BigInteger(1, md.digest());
            result = String.format("%32s", bi.toString(16)).replace(' ', '0');
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean validateUserName(String userName) {
        Matcher mtch = Utils.userNamePattern.matcher(userName);
        return mtch.matches();
    }

    public static char removeAccent(char ch) {
        int index = Arrays.binarySearch(Utils.SOURCE_CHARACTERS, ch);
        if (index >= 0) {
            ch = Utils.DESTINATION_CHARACTERS[index];
        }
        return ch;
    }

    public static String removeAccent(String str) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < sb.length(); ++i) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        DbManager.getInstance().start();
        //decodeItemFile();
    }

    private static void decodeFoodFile() {
        try {
            byte[] dat = Avatar.getFile("res/data/food.dat");
            ByteArrayInputStream is = new ByteArrayInputStream(dat);
            DataInputStream dis = new DataInputStream(is);
            short numItem = dis.readShort();
            System.out.println("Num item " + numItem);
            for (short n = 0; n < numItem; ++n) {
                short itemID = dis.readShort();
                String name = dis.readUTF();
                String desc = dis.readUTF();
                int price = dis.readInt();
                byte shop = dis.readByte();
                short imgId = dis.readShort();
                DbManager.getInstance().insertResultAsObj(
                        "INSERT INTO `foods` (`id`, `name`, `description`, `price`, `shop`, `img`) VALUES (?, ?, ?, ?, ?, ?);",
                        itemID, name, desc, price, shop, imgId);
            }
        } catch (IOException ex) {
        }
    }

    public static void decodeItemFile() {
        try {
            byte[] dat = Avatar.getFile("res/data/item.dat");
            ByteArrayInputStream is = new ByteArrayInputStream(dat);
            DataInputStream dis = new DataInputStream(is);
            short numItem = dis.readShort();
            System.out.println("Num item " + numItem);
            for (short n = 0; n < numItem; ++n) {
                short itemID = dis.readShort();
                short bigID = dis.readShort();
                short x0 = (short) dis.readUnsignedByte();
                short y0 = (short) dis.readUnsignedByte();
                short w = dis.readByte();
                short h = dis.readByte();
                DbManager.getInstance().insertResultAsObj(
                        "INSERT INTO `item_image_data`(`id`, `image_id`, `x`, `y`, `w`, `h`) VALUES (?,?,?,?,?,?)",
                        itemID,
                        bigID, x0, y0, w, h);
            }
        } catch (IOException ex) {
        }
    }

    public static void decodeMapItemFile() {
        try {
            byte[] dat = Avatar.getFile("res/data/map_item.dat");
            ByteArrayInputStream is = new ByteArrayInputStream(dat);
            DataInputStream dis = new DataInputStream(is);
            short numItem = dis.readShort();
            System.out.println("Num item " + numItem);
            for (short n = 0; n < numItem; ++n) {
                short id = dis.readShort();
                short typeID = dis.readShort();
                short type = dis.readByte();
                short x = dis.readByte();
                short y = dis.readByte();
                DbManager.getInstance().insertResultAsObj(
                        "INSERT INTO `map_item`(`id`, `type_id`, `type`, `x`, `y`) VALUES (?,?,?,?,?)",
                        id, typeID, type, x, y);
            }
        } catch (IOException ex) {
        }
    }

    public static void decodeMapItemTypeFile() {
        try {
            byte[] dat = Avatar.getFile("res/data/map_item_type.dat");
            ByteArrayInputStream is = new ByteArrayInputStream(dat);
            DataInputStream dis = new DataInputStream(is);
            short numItem = dis.readShort();
            System.out.println("Num item " + numItem);
            for (short n = 0; n < numItem; ++n) {
                short id = dis.readShort();
                String name = dis.readUTF();
                String des = dis.readUTF();
                short imgID = dis.readShort();
                short iconID = dis.readShort();
                byte dx = dis.readByte();
                byte dy = dis.readByte();
                short price_coin = dis.readShort();
                short price_gold = dis.readShort();
                byte buy = dis.readByte();
                byte pn = dis.readByte();
                JSONArray position = new JSONArray();
                for (int i = 0; i < pn; i++) {
                    JSONObject obj = new JSONObject();
                    byte x = dis.readByte();
                    byte y = dis.readByte();
                    obj.put("x", x);
                    obj.put("y", y);
                    position.add(obj);
                }
                DbManager.getInstance().insertResultAsObj(
                        "INSERT INTO `map_item_type`(`id`, `name`, `description`, `image`, `icon`, `price_coin`, `price_gold`, `buy`, `dx`, `dy`, `position`) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                        id, name, des, imgID, iconID, price_coin, price_gold, buy, dx, dy, position.toJSONString());
            }
        } catch (IOException ex) {
        }
    }

    public static void decodeItemDataFile(byte[] dat, boolean isSimple) {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(dat);
            DataInputStream dis = new DataInputStream(is);
            short numItem;
            if (!isSimple) {
                numItem = dis.readShort();
            } else {
                numItem = 1;
            }
            System.out.println("S\u1ed1 l\u01b0\u1ee3ng item: " + numItem);
            for (int i = 0; i < numItem; ++i) {
                String itemName = "";
                short itemID = dis.readShort();
                int itemXu = dis.readInt();
                short itemLuong = dis.readShort();
                System.out.println("Item: " + itemID);
                short itemType;
                if ((itemType = dis.readShort()) == -2) {
                    itemName = dis.readUTF();
                    byte sell = dis.readByte();
                    short idIcon = dis.readShort();
                    String INSERT_ITEM = "INSERT INTO `items` (`id`, `coin`, `gold`, `type`, `icon`, `name`, `sell`) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps = DbManager.getInstance().getConnectionForGame()
                            .prepareStatement(INSERT_ITEM);
                    ps.setShort(1, itemID);
                    ps.setInt(2, itemXu);
                    ps.setShort(3, itemLuong);
                    ps.setShort(4, itemType);
                    ps.setShort(5, idIcon);
                    ps.setString(6, itemName);
                    ps.setByte(7, sell);
                    ps.executeUpdate();
                } else if (itemType == -1) {
                    itemName = dis.readUTF();
                    byte sell = dis.readByte();
                    byte zOrder = dis.readByte();
                    byte gender = dis.readByte();
                    byte lvRequire = dis.readByte();
                    short idIcon2 = dis.readShort();
                    JSONArray animation = new JSONArray();
                    for (int j = 0; j < 15; ++j) {
                        JSONObject animation_i = new JSONObject();
                        short imgID = dis.readShort();
                        byte dx = dis.readByte();
                        byte dy = dis.readByte();
                        animation_i.put("img", imgID);
                        animation_i.put("dx", dx);
                        animation_i.put("dy", dy);
                        animation.add(animation_i);
                    }
                    String INSERT_ITEM2 = "INSERT INTO `items` (`id`, `coin`, `gold`, `type`, `icon`, `name`, `sell`, `zorder`, `gender`, `level`, `animation`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps2 = DbManager.getInstance().getConnectionForGame()
                            .prepareStatement(INSERT_ITEM2);
                    ps2.setShort(1, itemID);
                    ps2.setInt(2, itemXu);
                    ps2.setShort(3, itemLuong);
                    ps2.setShort(4, itemType);
                    ps2.setShort(5, idIcon2);
                    ps2.setString(6, itemName);
                    ps2.setByte(7, sell);
                    ps2.setByte(8, zOrder);
                    ps2.setByte(9, gender);
                    ps2.setByte(10, lvRequire);
                    ps2.setString(11, animation.toJSONString());
                    ps2.execute();
                } else {
                    short color = dis.readShort();
                    String INSERT_ITEM3 = "INSERT INTO `items` (`id`, `coin`, `gold`, `type`, `icon`) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement ps3 = DbManager.getInstance().getConnectionForGame()
                            .prepareStatement(INSERT_ITEM3);
                    ps3.setShort(1, itemID);
                    ps3.setInt(2, itemXu);
                    ps3.setShort(3, itemLuong);
                    ps3.setShort(4, itemType);
                    ps3.setShort(5, color);
                    ps3.execute();
                }
            }

        } catch (SQLException sqlEx) {
            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause());
            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
        } catch (Exception ex) {
        }
    }

    static {
        Utils.userNamePattern = Pattern.compile("^[a-z0-9]{5,16}$");
        rand = new Random();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sinData = new short[]{0, 18, 36, 54, 71, 89, 107, 125, 143, 160, 178, 195, 213, 230, 248, 265, 282, 299, 316,
            333, 350, 367, 384, 400, 416, 433, 449, 465, 481, 496, 512, 527, 543, 558, 573, 587, 602, 616, 630, 644,
            658, 672, 685, 698, 711, 724, 737, 749, 761, 773, 784, 796, 807, 818, 828, 839, 849, 859, 868, 878, 887,
            896, 904, 912, 920, 928, 935, 943, 949, 956, 962, 968, 974, 979, 984, 989, 994, 998, 1002, 1005, 1008,
            1011, 1014, 1016, 1018, 1020, 1022, 1023, 1023, 1024, 1024};
        cosData = new short[91];
        tanData = new int[91];
        for (int i = 0; i <= 90; ++i) {
            Utils.cosData[i] = Utils.sinData[90 - i];
            if (Utils.cosData[i] == 0) {
                Utils.tanData[i] = Integer.MAX_VALUE;
            } else {
                Utils.tanData[i] = (Utils.sinData[i] << 10) / Utils.cosData[i];
            }
        }
        SOURCE_CHARACTERS = new char[]{'\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c8', '\u00c9', '\u00ca', '\u00cc',
            '\u00cd', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d9', '\u00da', '\u00dd', '\u00e0', '\u00e1',
            '\u00e2', '\u00e3', '\u00e8', '\u00e9', '\u00ea', '\u00ec', '\u00ed', '\u00f2', '\u00f3', '\u00f4',
            '\u00f5', '\u00f9', '\u00fa', '\u00fd', '\u0102', '\u0103', '\u0110', '\u0111', '\u0128', '\u0129',
            '\u0168', '\u0169', '\u01a0', '\u01a1', '\u01af', '\u01b0', '\u1ea0', '\u1ea1', '\u1ea2', '\u1ea3',
            '\u1ea4', '\u1ea5', '\u1ea6', '\u1ea7', '\u1ea8', '\u1ea9', '\u1eaa', '\u1eab', '\u1eac', '\u1ead',
            '\u1eae', '\u1eaf', '\u1eb0', '\u1eb1', '\u1eb2', '\u1eb3', '\u1eb4', '\u1eb5', '\u1eb6', '\u1eb7',
            '\u1eb8', '\u1eb9', '\u1eba', '\u1ebb', '\u1ebc', '\u1ebd', '\u1ebe', '\u1ebf', '\u1ec0', '\u1ec1',
            '\u1ec2', '\u1ec3', '\u1ec4', '\u1ec5', '\u1ec6', '\u1ec7', '\u1ec8', '\u1ec9', '\u1eca', '\u1ecb',
            '\u1ecc', '\u1ecd', '\u1ece', '\u1ecf', '\u1ed0', '\u1ed1', '\u1ed2', '\u1ed3', '\u1ed4', '\u1ed5',
            '\u1ed6', '\u1ed7', '\u1ed8', '\u1ed9', '\u1eda', '\u1edb', '\u1edc', '\u1edd', '\u1ede', '\u1edf',
            '\u1ee0', '\u1ee1', '\u1ee2', '\u1ee3', '\u1ee4', '\u1ee5', '\u1ee6', '\u1ee7', '\u1ee8', '\u1ee9',
            '\u1eea', '\u1eeb', '\u1eec', '\u1eed', '\u1eee', '\u1eef', '\u1ef0', '\u1ef1'};
        DESTINATION_CHARACTERS = new char[]{'A', 'A', 'A', 'A', 'E', 'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U',
            'Y', 'a', 'a', 'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u', 'y', 'A', 'a', 'D', 'd',
            'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
            'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e',
            'E', 'e', 'E', 'e', 'E', 'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
            'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
            'U', 'u', 'U', 'u', 'U', 'u'};
    }
}
