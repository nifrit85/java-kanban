package Manager;

import java.io.IOException;
import java.util.Scanner;

public class Managers {
    public static TaskManager getManager(TypeOfManager type){
        if (type == TypeOfManager.FILE){
            try {
                return new FileBackedTasksManager(aksUserPath());
            }catch (IOException e){
                System.err.println("Проблемы с файлом. Программа работает в памяти");
            }
        }
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TypeOfManager askUserTypeOfManager(){
        System.out.println("Выберите режим работы:");
        System.out.println("1 - Работа с файлом");
        System.out.println("2 - Работа в памяти");
        Scanner scanner = new Scanner(System.in);
        String typeOfManager = scanner.next();
        switch (typeOfManager){
            case "1":
                return TypeOfManager.FILE;
            default:
                return  TypeOfManager.MEMORY;
        }
    }

    private static String aksUserPath(){
        System.out.println("Укажите путь к файлу");
        System.out.println("В формате : каталог/файл");
        Scanner scanner = new Scanner(System.in);
        return scanner.next();
    }
}
