import java.io.File;
import java.io.IOException;


public class FileManager {

    public static boolean fileExist(String pathToFile) throws IOException {

        File file = new File(pathToFile);
        if (!file.exists()) {
            if (file.getParent() != null) {
                File directory = new File(file.getParent());
                if (!directory.mkdirs()) {
                    return false;
                }
            }
            return file.createNewFile();
        } else {
            return true;
        }
    }
}
