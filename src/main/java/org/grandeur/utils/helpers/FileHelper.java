package org.grandeur.utils.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * Created by harfiyan on 4/5/2017.
 */
public final class FileHelper {

    public static boolean IsExistsOrCreated(File f) {
        boolean isExistOrCreated = false;
        if (!f.exists()) {
            try {
                isExistOrCreated = f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ((f.exists()) && (!f.isDirectory())) {
            isExistOrCreated = true;
        }

        return isExistOrCreated;
    }

    public static String ReadAsString(String filepath) {

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filepath));
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static class LastModifiedComparator implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            if (o1.lastModified() > o2.lastModified()) {
                return -1;
            }
            if (o1.lastModified() < o2.lastModified()) {
                return 1;
            }
            return 0;
        }
    }
}
