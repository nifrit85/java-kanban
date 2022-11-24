import Manager.Managers;
import Manager.TaskManager;
import Manager.TypeOfManager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getManager(Managers.askUserTypeOfManager());
        Test test = new Test(manager);
        test.runThirdTest();
    }


}
