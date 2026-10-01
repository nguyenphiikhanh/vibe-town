package avatar.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Avatar {

    private static Thread T;

    public static void main(String[] args) {
        Avatar.start();
    }

    public static void start() {
        T = new Thread(() -> {
            System.out.println("     _                      _                      ____                                      \n    / \\    __   __   __ _  | |_    __ _   _ __    / ___|    ___   _ __  __   __   ___   _ __ \n   / _ \\   \\ \\ / /  / _` | | __|  / _` | | '__|   \\___ \\   / _ \\ | '__| \\ \\ / /  / _ \\ | '__|\n  / ___ \\   \\ V /  | (_| | | |_  | (_| | | |       ___) | |  __/ | |     \\ V /  |  __/ | |   \n /_/   \\_\\   \\_/    \\__,_|  \\__|  \\__,_| |_|      |____/   \\___| |_|      \\_/    \\___| |_|   \n                                                                                             ");
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

                @Override
                public void run() {
                    System.out.println("Shutdown Server!");
                    ServerManager.stop();
                }
            }));
            ServerManager.init();
            ServerManager.start();
        });
        T.start();
    }

    public static byte[] getFile(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            byte[] ab = new byte[fis.available()];
            fis.read(ab, 0, ab.length);
            fis.close();
            return ab;
        } catch (IOException e) {
            return null;
        }
    }

    public static int getFileSize(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            int size = fis.available();
            fis.close();
            return size;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void saveFile(String url, byte[] ab) {
        try {
            File f = new File(url);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(url);
            fos.write(ab);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
