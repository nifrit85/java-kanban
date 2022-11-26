import Manager.Managers;
import Manager.TaskManager;
import Manager.TypeOfManager;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        TypeOfManager typeOfManager = UserInterface.askUserTypeOfManager();
        String pathToFile = null;

        if (typeOfManager == TypeOfManager.FILE) {
            try {
                pathToFile = UserInterface.aksUserPath();
                if (!FileManager.fileExist(pathToFile)) {
                    System.err.println("Ошибка с файлом");
                    return;
                }
            } catch (IOException e) {
                e.getMessage();
            }

        }
        TaskManager manager = Managers.getManager(typeOfManager, pathToFile);
        Test test = new Test(manager);
        test.runThirdTest(pathToFile);
    }
}
