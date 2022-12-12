import managers.Managers;
import managers.TaskManager;
import managers.TypeOfManager;
import utilities.FileManager;
import utilities.UserInterface;

import java.io.IOException;

public class Main {
    /*Привет, несколько особенностей с которыми я не разобрался:
    1) В public class Managers мне выдаётся предупреждение "Add a private constructor to hide the implicit public one." Я не понял, что от меня хотят
    2) Имеет ли смысл заменять System.out на Logger?
    3) Не придумал как проверять updateTask
    4) В FileBackedTasksManagerTest приходится очищать manager.clearTasks(). Не смотря на что я создаю новый экземпляр FileBackedTasksManager
     для чтения новых файлов, данные он тянет из старого менеджера.Если написать manager = null не помогает
     */


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
                System.err.println(e.getMessage());
                return;
            }

        }
        TaskManager manager = Managers.getManager(typeOfManager, pathToFile);
        Test test = new Test(manager);
        test.run(pathToFile);
    }
}
