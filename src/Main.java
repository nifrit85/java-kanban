

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();


        //Отладка
        Test test = new Test(manager);
        //Создаём симплы
        test.addNewTask();
        //Двигаем симпылы на симплы
        test.moveSimpleToSimple();
        //Двигаем симплы на эпики
        test.moveSimpleToEpic();
        //Двигаем симплы на сабы
        test.moveSimpleToSub();
        //Двигаем сабы на симплы
        test.addNewTask();
        test.moveSubToSimple();
        //Двигаем сабы на сабы
        test.moveSubToSub();
        //Двигаем сабы на эпик
        test.moveSubToEpic();
        //Двигаем сабы на пустое место
        test.moveSubToNull();
        //Двигаем эпик на симпл
        //К этому моменту не остаётся эпиков и сабов обычно
        test.generateSubAndEpic();
        test.moveEpicToSimple();
        //Двигаем эпик на эпик
        test.moveEpicToEpic();
        //Двигаем эпик на саб
        test.moveEpicToSub();
        //Получение списка симплов
        test.getAllSimpleTask();
        //Получение списка эпиков
        test.getAllEpicTask();
        //Получение списка сабов
        test.getAllSubTask();
        //Получение по идентификатору
        test.getTaskById();
        //Изменим названия
        test.changeName();
        //удаление по идентификатору
        test.delById();
        //Получение списка всех задач
        test.getAllTask();
        //Получение списка всех задач эпика
        test.getAllSubFromEpic();
        //Обновление статусов
        test.updateStatus();
        manager.deleteTasks();







    }

}
