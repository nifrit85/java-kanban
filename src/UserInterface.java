import Manager.TypeOfManager;

import java.util.Scanner;

public class UserInterface {

    public static TypeOfManager askUserTypeOfManager() {
        System.out.println("Выберите режим работы:");
        System.out.println("1 - Работа с файлом");
        System.out.println("2 - Работа в памяти");
        Scanner scanner = new Scanner(System.in);
        String typeOfManager = scanner.next();
        switch (typeOfManager) {
            case "1":
                return TypeOfManager.FILE;
            default:
                return TypeOfManager.MEMORY;
        }
    }

    public static String aksUserPath() {
        System.out.println("Укажите путь к файлу");
        System.out.println("В формате : каталог/файл");
        Scanner scanner = new Scanner(System.in);
        return scanner.next();
    }

}
