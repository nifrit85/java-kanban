package utilities;

import java.io.File;
import java.io.IOException;


public class FileManager {

    public static boolean fileExist(String pathToFile) {

        File file = new File(pathToFile);
        if (!file.exists()) {
            if (file.getParent() != null) {
                File directory = new File(file.getParent());
                if (!directory.mkdirs()) {
                    return false;
                }
            }
            try {
                return file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        } else {
            return true;
        }
    }
}
