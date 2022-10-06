import Task.EnumStatus;
import Task.EpicTask;
import Task.SimpleTask;
import Task.SubTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Test {
    private Manager manager;
    //Списки задач для отладки
    private Map<Integer, SimpleTask> simpleTasks = new HashMap<>();
    private Map<Integer, EpicTask> epicTasks = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();

    public Test(Manager manager) {
        this.manager = manager;
    }

    private Random random = new Random();

    int forI = 20;
    int randomBound;

    public void addNewTask() {
        update();

        String name;
        String description;
        Object task;

        System.out.println("Добавление задач");
        int taskCount = manager.getTasks().size();

        for (int i = taskCount; i <= forI + taskCount; i++) {
            name = "Name" + i;
            description = "Descr" + i;

            task = new SimpleTask(name, description);
            manager.addTask(task);
        }
        System.out.println(manager.getTasks());

        this.randomBound = forI + taskCount + 1;
    }

    public void moveSimpleToSimple() {
        int randomInt;

        System.out.println("Двигаем симплы на симплы");
        for (int i = 0; i <= 5; i++) {
            if (simpleTasks.size() == 0) {
                break;
            }
            randomInt = random.nextInt(randomBound);
            if (simpleTasks.containsKey(randomInt)) {
                SimpleTask simpleTaskFrom = (SimpleTask) manager.getTaskByID(randomInt);
                randomInt = random.nextInt(randomBound);
                if (simpleTasks.containsKey(randomInt) && randomInt != simpleTaskFrom.getId()) { //не перетаскиваем на себя
                    SimpleTask simpleTaskTo = (SimpleTask) manager.getTaskByID(randomInt);
                    System.out.println(simpleTaskFrom.getId() + " -> " + simpleTaskTo.getId());

                    System.out.println("До");
                    System.out.println(manager.getTaskByID(simpleTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(simpleTaskTo.getId()));

                    manager.moveTask(simpleTaskFrom, simpleTaskTo);

                    //После обновления данных получим наш таск
                    update();

                    System.out.println("После");
                    System.out.println(manager.getTaskByID(simpleTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(simpleTaskTo.getId()));
                } else {
                    i--;
                }
            } else {
                i--;
            }
        }
    }

    public void moveSimpleToEpic() {
        int randomInt;

        System.out.println("Двигаем симплы на эпики");
        for (int i = 0; i <= 5; i++) {
            randomInt = random.nextInt(randomBound);
            if (simpleTasks.size() == 0) {
                break;
            }
            if (simpleTasks.containsKey(randomInt)) {
                SimpleTask simpleTaskFrom = (SimpleTask) manager.getTaskByID(randomInt);
                randomInt = random.nextInt(randomBound);
                if (epicTasks.size() == 0) {
                    break;
                }
                if (epicTasks.containsKey(randomInt)) {
                    EpicTask epicTaskTo = (EpicTask) manager.getTaskByID(randomInt);
                    System.out.println(simpleTaskFrom.getId() + " -> " + epicTaskTo.getId());

                    System.out.println("До");
                    System.out.println(manager.getTaskByID(simpleTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(epicTaskTo.getId()));

                    manager.moveTask(simpleTaskFrom, epicTaskTo);

                    //После обновления данных получим наш таск
                    update();

                    System.out.println("После");
                    System.out.println(manager.getTaskByID(simpleTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(epicTaskTo.getId()));
                } else {
                    i--;
                }
            } else {
                i--;
            }
        }
    }

    public void moveSimpleToSub() {
        int randomInt;

        System.out.println("Двигаем симплы на сабы");
        for (int i = 0; i <= 5; i++) {
            randomInt = random.nextInt(randomBound);
            if (simpleTasks.size() == 0) {
                break;
            }
            if (simpleTasks.containsKey(randomInt)) {
                SimpleTask simpleTaskFrom = (SimpleTask) manager.getTaskByID(randomInt);
                randomInt = random.nextInt(randomBound);
                if (subTasks.size() == 0) {
                    break;
                }
                if (subTasks.containsKey(randomInt)) {
                    SubTask subTaskTo = (SubTask) manager.getTaskByID(randomInt);
                    System.out.println(simpleTaskFrom.getId() + " -> " + subTaskTo.getId());

                    int parentId = subTaskTo.getParentID();

                    System.out.println("До");
                    System.out.println(manager.getTaskByID(simpleTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(subTaskTo.getId()));
                    System.out.println(manager.getTaskByID(parentId));

                    manager.moveTask(simpleTaskFrom, subTaskTo);

                    //После обновления данных получим наш таск
                    update();

                    System.out.println("После");
                    System.out.println(manager.getTaskByID(simpleTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(subTaskTo.getId()));
                    System.out.println(manager.getTaskByID(parentId));
                } else {
                    i--;
                }
            } else {
                i--;
            }
        }
    }

    public void moveSubToSimple() {

        int randomInt;

        System.out.println("Двигаем сабы на симплы");
        for (int i = 0; i <= 5; i++) {
            randomInt = random.nextInt(randomBound);
            if (subTasks.size() == 0) {
                break;
            }
            if (subTasks.containsKey(randomInt)) {
                SubTask subTaskFrom = (SubTask) manager.getTaskByID(randomInt);
                randomInt = random.nextInt(randomBound);
                if (simpleTasks.size() == 0) {
                    break;
                }
                if (simpleTasks.containsKey(randomInt)) {
                    SimpleTask simpleTaskTo = (SimpleTask) manager.getTaskByID(randomInt);
                    System.out.println(subTaskFrom.getId() + " -> " + simpleTaskTo.getId());

                    int parentId = subTaskFrom.getParentID();

                    System.out.println("До");
                    System.out.println(manager.getTaskByID(subTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(simpleTaskTo.getId()));
                    System.out.println(manager.getTaskByID(parentId));

                    manager.moveTask(subTaskFrom, simpleTaskTo);

                    //После обновления данных получим наш таск
                    update();

                    System.out.println("После");
                    System.out.println(manager.getTaskByID(subTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(simpleTaskTo.getId()));
                    System.out.println(manager.getTaskByID(parentId));
                } else {
                    i--;
                }
            } else {
                i--;
            }
        }
    }

    public void moveSubToSub() {
        int randomInt;

        System.out.println("Двигаем сабы на сабы");
        for (int i = 0; i <= 5; i++) {
            randomInt = random.nextInt(randomBound);
            if (subTasks.size() == 0) {
                break;
            }
            if (subTasks.containsKey(randomInt)) {
                SubTask subTaskFrom = (SubTask) manager.getTaskByID(randomInt);
                randomInt = random.nextInt(randomBound);
                if (subTasks.size() == 0) {
                    break;
                }
                if (subTasks.containsKey(randomInt) && randomInt != subTaskFrom.getId()) { //не перетаскиваем на себя)
                    SubTask subTaskTo = (SubTask) manager.getTaskByID(randomInt);
                    System.out.println(subTaskFrom.getId() + " -> " + subTaskTo.getId());

                    int parentIdFrom = subTaskFrom.getParentID();
                    int parentIdTo = subTaskTo.getParentID();

                    System.out.println("До");
                    System.out.println(manager.getTaskByID(subTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(subTaskTo.getId()));
                    System.out.println(manager.getTaskByID(parentIdFrom));
                    System.out.println(manager.getTaskByID(parentIdTo));

                    manager.moveTask(subTaskFrom, subTaskTo);

                    //После обновления данных получим наш таск
                    update();

                    System.out.println("После");
                    System.out.println(manager.getTaskByID(subTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(subTaskTo.getId()));
                    System.out.println(manager.getTaskByID(parentIdFrom));
                    System.out.println(manager.getTaskByID(parentIdTo));
                } else {
                    i--;
                }
            } else {
                i--;
            }
        }
    }

    public void moveSubToEpic() {
        int randomInt;

        System.out.println("Двигаем сабы на эпик");
        for (int i = 0; i <= 5; i++) {
            randomInt = random.nextInt(randomBound);
            if (subTasks.size() == 0) {
                break;
            }
            if (subTasks.containsKey(randomInt)) {
                SubTask subTaskFrom = (SubTask) manager.getTaskByID(randomInt);
                randomInt = random.nextInt(randomBound);
                if (epicTasks.size() == 0) {
                    break;
                }
                if (epicTasks.containsKey(randomInt)) {
                    EpicTask epicTaskTo = (EpicTask) manager.getTaskByID(randomInt);
                    System.out.println(subTaskFrom.getId() + " -> " + epicTaskTo.getId());

                    int parentId = subTaskFrom.getParentID();

                    System.out.println("До");
                    System.out.println(manager.getTaskByID(subTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(epicTaskTo.getId()));
                    System.out.println(manager.getTaskByID(parentId));

                    manager.moveTask(subTaskFrom, epicTaskTo);

                    //После обновления данных получим наш таск
                    update();

                    System.out.println("После");
                    System.out.println(manager.getTaskByID(subTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(epicTaskTo.getId()));
                    System.out.println(manager.getTaskByID(parentId));
                } else {
                    i--;
                }
            } else {
                i--;
            }
        }
    }

    public void moveSubToNull() {

        int randomInt;

        System.out.println("Двигаем сабы на пустое место");
        for (int i = 0; i <= 5; i++) {
            randomInt = random.nextInt(randomBound);
            if (subTasks.size() == 0) {
                break;
            }
            if (subTasks.containsKey(randomInt)) {
                SubTask subTaskFrom = (SubTask) manager.getTaskByID(randomInt);

                System.out.println(subTaskFrom.getId() + " -> null");

                int parentId = subTaskFrom.getParentID();

                System.out.println("До");
                System.out.println(manager.getTaskByID(subTaskFrom.getId()));
                System.out.println(manager.getTaskByID(parentId));

                manager.moveTask(subTaskFrom);

                //После обновления данных получим наш таск
                update();

                System.out.println("После");
                System.out.println(manager.getTaskByID(subTaskFrom.getId()));
                System.out.println(manager.getTaskByID(parentId));
            } else {
                i--;
            }
        }
    }

    public void moveEpicToSimple() {
        int randomInt;

        System.out.println("Двигаем эпик на симпл");
        for (int i = 0; i <= 5; i++) {
            randomInt = random.nextInt(randomBound);
            if (epicTasks.size() == 0) {
                break;
            }
            if (epicTasks.containsKey(randomInt)) {
                EpicTask epicTaskFrom = (EpicTask) manager.getTaskByID(randomInt);
                randomInt = random.nextInt(randomBound);
                if (simpleTasks.size() == 0) {
                    break;
                }
                if (simpleTasks.containsKey(randomInt)) {
                    SimpleTask simpleTaskTo = (SimpleTask) manager.getTaskByID(randomInt);
                    System.out.println(epicTaskFrom.getId() + " -> " + simpleTaskTo.getId());

                    System.out.println("До");
                    System.out.println(manager.getTaskByID(epicTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(simpleTaskTo.getId()));

                    manager.moveTask(epicTaskFrom, simpleTaskTo);

                    //После обновления данных получим наш таск
                    update();

                    System.out.println("После");
                    System.out.println(manager.getTaskByID(epicTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(simpleTaskTo.getId()));
                } else {
                    i--;
                }
            } else {
                i--;
            }
        }
    }

    public void moveEpicToEpic() {
        int randomInt;

        System.out.println("Двигаем эпик на эпик'");
        for (int i = 0; i <= 5; i++) {
            randomInt = random.nextInt(randomBound);
            if (epicTasks.size() == 0) {
                break;
            }
            if (epicTasks.containsKey(randomInt)) {
                EpicTask epicTaskFrom = (EpicTask) manager.getTaskByID(randomInt);
                randomInt = random.nextInt(randomBound);
                if (epicTasks.size() == 0) {
                    break;
                }
                if (epicTasks.containsKey(randomInt) && randomInt != epicTaskFrom.getId()) { //не перетаскиваем на себя {
                    EpicTask epicTaskTo = (EpicTask) manager.getTaskByID(randomInt);
                    System.out.println(epicTaskFrom.getId() + " -> " + epicTaskTo.getId());

                    System.out.println("До");
                    System.out.println(manager.getTaskByID(epicTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(epicTaskTo.getId()));

                    manager.moveTask(epicTaskFrom, epicTaskTo);

                    //После обновления данных получим наш таск
                    update();

                    System.out.println("После");
                    System.out.println(manager.getTaskByID(epicTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(epicTaskTo.getId()));
                } else {
                    i--;
                }
            } else {
                i--;
            }
        }
    }

    public void moveEpicToSub() {
        int randomInt;

        System.out.println("Двигаем эпик на саб");
        for (int i = 0; i <= 5; i++) {
            randomInt = random.nextInt(randomBound);
            if (epicTasks.size() == 0) {
                break;
            }
            if (epicTasks.containsKey(randomInt)) {
                EpicTask epicTaskFrom = (EpicTask) manager.getTaskByID(randomInt);
                randomInt = random.nextInt(randomBound);
                if (subTasks.size() == 0) {
                    break;
                }
                if (subTasks.containsKey(randomInt)) {
                    SubTask subTaskTo = (SubTask) manager.getTaskByID(randomInt);
                    System.out.println(epicTaskFrom.getId() + " -> " + subTaskTo.getId());

                    System.out.println("До");
                    System.out.println(manager.getTaskByID(epicTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(subTaskTo.getId()));

                    manager.moveTask(epicTaskFrom, subTaskTo);

                    //После обновления данных получим наш таск
                    update();

                    System.out.println("После");
                    System.out.println(manager.getTaskByID(epicTaskFrom.getId()));
                    System.out.println(manager.getTaskByID(subTaskTo.getId()));
                } else {
                    i--;
                }
            } else {
                i--;
            }
        }
    }

    public void getAllTask() {
        System.out.println("Получение списка всех задач");
        System.out.println(manager.getTasks());
    }

    public void getAllSimpleTask() {
        System.out.println("Получение списка всех симплов");
        System.out.println(manager.getTasks(Manager.ClassName.SIMPLETASK.getName()));
    }

    public void getAllSubTask() {
        System.out.println("Получение списка всех сабов");
        System.out.println(manager.getTasks(Manager.ClassName.SUBTASK.getName()));
    }

    public void getAllEpicTask() {
        System.out.println("Получение списка всех эпиков");
        System.out.println(manager.getTasks(Manager.ClassName.EPICTASK.getName()));
    }

    public void getTaskById() {
        System.out.println("Получение по идентификатору");
        int randomInt;
        for (int i = 0; i <= 5; i++) {
            randomInt = random.nextInt(randomBound);
            System.out.println("ID = " + randomInt);
            System.out.println(manager.getTaskByID(randomInt));
        }
    }

    public void delById() {
        System.out.println("Удаление по идентификатору");
        int randomInt;
        for (int i = 0; i <= 3; i++) {
            randomInt = random.nextInt(randomBound);
            System.out.println("ID = " + randomInt);
            System.out.println(manager.getTaskByID(randomInt));
            manager.deleteByID(randomInt);
            //После обновления данных получим наш таск
            update();
        }
    }

    public void changeName() {
        System.out.println("Изменяем имя и описание");
        int randomInt;
        for (int i = 0; i <= 5; i++) {
            randomInt = random.nextInt(randomBound);
            Object task = manager.getTaskByID(randomInt);

            System.out.println("До");
            System.out.println(task);

            String taskName = task.getClass().getName();
            //симпл
            if (taskName.equals(Manager.ClassName.SIMPLETASK.getName())) {
                SimpleTask simpleTask = (SimpleTask) task;
                simpleTask.setName("New" + randomInt);
                simpleTask.setDescription("NewDescr" + randomInt);
                task = simpleTask;
                //саб
            } else if (taskName.equals(Manager.ClassName.SUBTASK.getName())) {
                SubTask subTask = (SubTask) task;
                subTask.setName("New" + randomInt);
                subTask.setDescription("NewDescr" + randomInt);
                task = subTask;
                //эпик
            } else if ((taskName.equals(Manager.ClassName.EPICTASK.getName()))) {
                EpicTask epicTask = (EpicTask) task;
                epicTask.setName("New" + randomInt);
                epicTask.setDescription("NewDescr" + randomInt);
                task = epicTask;
            }
            manager.updateTask(task);

            System.out.println("После");
            System.out.println(manager.getTaskByID(randomInt));
        }
    }

    public void getAllSubFromEpic() {
        System.out.println("Получение подзадач эпика");
        if (epicTasks.size() != 0) {
            int i = 0;
            for (Map.Entry<Integer, EpicTask> entry : epicTasks.entrySet()) {
                i++;
                if (i == 3) {
                    break;
                }
                EpicTask epicTask = entry.getValue();
                System.out.println("Эпик");
                System.out.println(epicTask);
                System.out.println("Сабы");

                ArrayList<Integer> subTaskIDs = epicTask.getSubTaskIDs();
                for (Integer id: subTaskIDs) {
                    System.out.println(manager.getTaskByID(id));
                }
            }
        }
    }

    public void updateStatus() {

        update();
        System.out.println("Обновление статусов");
        int i = 0;
        if (simpleTasks.size() != 0) {
            System.out.println("Симпл");
            for (Map.Entry<Integer, SimpleTask> entry : simpleTasks.entrySet()) {
                if (i == 2) {
                    break;
                }
                i++;
                System.out.println("До");
                SimpleTask simpleTask = entry.getValue();
                System.out.println(manager.getTaskByID(entry.getKey()));

                System.out.println(EnumStatus.Status.IN_PROGRESS);
                simpleTask.setStatus(EnumStatus.Status.IN_PROGRESS);
                manager.updateTask(simpleTask);
                System.out.println(manager.getTaskByID(entry.getKey()));

                System.out.println(EnumStatus.Status.DONE);
                simpleTask.setStatus(EnumStatus.Status.DONE);
                manager.updateTask(simpleTask);
                System.out.println(manager.getTaskByID(entry.getKey()));
                System.out.println("------");
            }
        }
        if ((subTasks.size() != 0)){
            System.out.println("Саб");
            for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
                if (i == 5) {
                    break;
                }
                i++;
                System.out.println("До");
                SubTask subTask = entry.getValue();
                System.out.println(manager.getTaskByID(entry.getKey()));
                System.out.println(manager.getTaskByID(subTask.getParentID()));

                System.out.println(EnumStatus.Status.IN_PROGRESS);
                subTask.setStatus(EnumStatus.Status.IN_PROGRESS);
                manager.updateTask(subTask);
                System.out.println(manager.getTaskByID(entry.getKey()));
                System.out.println(manager.getTaskByID(subTask.getParentID()));

                System.out.println(EnumStatus.Status.DONE);
                subTask.setStatus(EnumStatus.Status.DONE);
                manager.updateTask(subTask);
                System.out.println(manager.getTaskByID(entry.getKey()));
                System.out.println(manager.getTaskByID(subTask.getParentID()));
                System.out.println("------");
            }
        }
    }

    public void generateSubAndEpic() {
        int randomInt;

        for (int i = 0; i <= 10; i++) {
            if (simpleTasks.size() == 0) {
                break;
            }
            randomInt = random.nextInt(randomBound);
            if (simpleTasks.containsKey(randomInt)) {
                SimpleTask simpleTaskFrom = (SimpleTask) manager.getTaskByID(randomInt);
                randomInt = random.nextInt(randomBound);
                if (simpleTasks.containsKey(randomInt) && randomInt != simpleTaskFrom.getId()) { //не перетаскиваем на себя
                    SimpleTask simpleTaskTo = (SimpleTask) manager.getTaskByID(randomInt);

                    manager.moveTask(simpleTaskFrom, simpleTaskTo);
                    //После обновления данных получим наш таск
                    update();
                } else {
                    i--;
                }
            } else {
                i--;
            }
        }
    }

    private void update() {

        Map<Integer, Object> tasks = manager.getTasks();

        simpleTasks.clear();
        epicTasks.clear();
        subTasks.clear();

        for (Map.Entry<Integer, Object> task : tasks.entrySet()) {
            if (task.getValue().getClass().getName().equals(Manager.ClassName.SIMPLETASK.getName())) {
                updateSingleTask((SimpleTask) task.getValue());
            } else if (task.getValue().getClass().getName().equals(Manager.ClassName.EPICTASK.getName())) {
                updateSingleTask((EpicTask) task.getValue());
            } else if (task.getValue().getClass().getName().equals(Manager.ClassName.SUBTASK.getName())) {
                updateSingleTask((SubTask) task.getValue());
            }
        }
    }

    private void updateSingleTask(SimpleTask task) {
        //Обновление внутрених структур
        this.subTasks.remove(task.getId());
        this.epicTasks.remove(task.getId());
        this.simpleTasks.put(task.getId(), task);
    }

    private void updateSingleTask(EpicTask task) {
        //Обновление внутрених структур
        if (this.simpleTasks.containsKey(task.getId())) {
            this.simpleTasks.remove(task.getId());
        }
        if (this.subTasks.containsKey(task.getId())) {
            this.subTasks.remove(task.getId());
        }
        this.epicTasks.put(task.getId(), task);
    }

    private void updateSingleTask(SubTask task) {
        //Обновление внутрених структур
        if (this.simpleTasks.containsKey(task.getId())) {
            this.simpleTasks.remove(task.getId());
        }
        if (this.epicTasks.containsKey(task.getId())) {
            this.epicTasks.remove(task.getId());
        }
        this.subTasks.put(task.getId(), task);
    }
}
