
import Task.EnumStatus;
import Task.EpicTask;
import Task.SimpleTask;
import Task.SubTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Manager {
    private int Id;
    private ArrayList<Integer> freeId = new ArrayList<>();

    private Map<Integer, Object> tasks = new HashMap<>();


    public void addTask(Object task) {
        if (task != null) {
            int newID = getNewID();
            String taskName = task.getClass().getName();
            if (taskName.equals(ClassName.SIMPLETASK.getName())) {
                SimpleTask simpleTask = (SimpleTask) task;
                simpleTask.setID(newID);
                this.tasks.put(newID, simpleTask);
            }
            //Согласно ТЗ создавать можно любой таск. Оставил код
//            }else if (taskName.equals(ClassName.EPICTASK.getName())){
//                EpicTask epicTask = (EpicTask) task;
//                epicTask.setID(newID);
//                this.tasks.put(newID,epicTask);
//            }else if (taskName.equals(ClassName.SUBTASK.getName())){
//                if (parent != null){
//                    SubTask subTask = (SubTask) task;
//                    subTask.setID(newID);
//                    subTask.setParent(parent);
//                    parent.addSubTask(subTask);
//                }
//            }
        }
    }

    public Map<Integer, Object> getTasks() {
        return tasks;
    }

    public Map<Integer, Object> getTasks(String className) {
        Map<Integer, Object> simpleTasks = new HashMap<>();

        for (Map.Entry<Integer, Object> task : tasks.entrySet()) {
            if (task.getValue().getClass().getName().equals(className)) {
                simpleTasks.put(task.getKey(), task.getValue());
            }
        }
        return simpleTasks;
    }

    public void deleteTasks() {
        tasks.clear();
        this.Id = 0;
        freeId.clear();
    }

    private int getNewID() {
        int newID;
        if (freeId.size() == 0) {
            newID = this.Id;
            this.Id++;
        } else {
            newID = freeId.get(0);
            freeId.remove(0);
        }
        return newID;

    }

    public Object getTaskByID(int Id) {
        return tasks.getOrDefault(Id, null);
    }

    public void updateTask(Object task) {
        if (task != null) {
            String taskName = task.getClass().getName();
            //симпл
            if (taskName.equals(ClassName.SIMPLETASK.getName())) {
                updateSimpleTask((SimpleTask) task);
                //саб
            } else if (taskName.equals(ClassName.SUBTASK.getName())) {
                updateSubTask((SubTask) task);
                //эпик
            } else if ((taskName.equals(ClassName.EPICTASK.getName()))) {
                updateEpicTask((EpicTask) task);
            }
        }
    }

    public void deleteByID(int Id) {
        if (tasks.containsKey(Id)) {
            Object task = getTaskByID(Id);
            String taskName = task.getClass().getName();
            if (taskName.equals(ClassName.SIMPLETASK.getName())) {
                tasks.remove(Id);
                freeId.add(Id);
                Collections.sort(freeId);
            } else if (taskName.equals(ClassName.SUBTASK.getName())) {
                SubTask subTask = (SubTask) task;
                EpicTask parent = (EpicTask) getTaskByID(subTask.getParentID());

                tasks.remove(Id);
                freeId.add(Id);
                Collections.sort(freeId);

                parent.delSubTask(subTask);
                updateTask(parent);

            } else if ((taskName.equals(ClassName.EPICTASK.getName()))) {
                EpicTask epicTask = (EpicTask) task;
                ArrayList<Integer> subTaskIDs = epicTask.getSubTaskIDs();
                ArrayList<Integer> idList = new ArrayList<>();

                idList.addAll(subTaskIDs);

                tasks.remove(Id);
                freeId.add(Id);

                for (Integer ID : idList) {
                    SubTask subTask = (SubTask) getTaskByID(ID);
                    tasks.remove(subTask.getId());
                    freeId.add(subTask.getId());
                    epicTask.delSubTask(subTask);
                }

                Collections.sort(freeId);
            }

        }
    }

    private EnumStatus.Status getNewEpicStatus(EpicTask parent) {

        EnumStatus.Status status = EnumStatus.Status.NEW;

        ArrayList<Integer> subTaskIDs = parent.getSubTaskIDs();

        if (subTaskIDs.size() != 0) {

            int newCount = 0;
            int doneCount = 0;
            for (int subTaskID : subTaskIDs) {
                SubTask subTask = (SubTask) getTaskByID(subTaskID);
                if (subTask.getStatus().equals(EnumStatus.Status.NEW)) {
                    newCount++;
                } else if (subTask.getStatus().equals(EnumStatus.Status.DONE)) {
                    doneCount++;
                } else {
                  return  EnumStatus.Status.IN_PROGRESS;
                }
            }
            if (newCount == subTaskIDs.size()) {
                status = EnumStatus.Status.NEW;
            } else if (doneCount == subTaskIDs.size()) {
                status = EnumStatus.Status.DONE;
            }else {
                status = EnumStatus.Status.IN_PROGRESS;
            }
        }
        return status;
    }

    public void moveTask(SimpleTask taskFrom, SimpleTask taskTo) {
        if (taskFrom != null && taskTo != null) {
            SubTask subTask = convertToSubTask(taskFrom);
            EpicTask epicTask = convertToEpicTask(taskTo);
            linkSubAndEpic(subTask, epicTask);
        }
    }

    public void moveTask(SimpleTask taskFrom, EpicTask taskTo) {
        if (taskFrom != null && taskTo != null) {
            SubTask subTask = convertToSubTask(taskFrom);
            linkSubAndEpic(subTask, taskTo);
        }
    }

    public void moveTask(SimpleTask taskFrom, SubTask taskTo) {
        if (taskFrom != null && taskTo != null) {
            EpicTask parent = (EpicTask) getTaskByID(taskTo.getParentID());
            clearLinkSubAndEpic(taskTo, parent);
            SubTask subTask = convertToSubTask(taskFrom);
            EpicTask epicTask = convertToEpicTask(taskTo);
            linkSubAndEpic(subTask, epicTask);
        }
    }

    public void moveTask(SubTask taskFrom, SimpleTask taskTo) {
        if (taskFrom != null && taskTo != null) {
            EpicTask parent = (EpicTask) getTaskByID(taskFrom.getParentID());
            clearLinkSubAndEpic(taskFrom, parent);
            EpicTask epicTask = convertToEpicTask(taskTo);
            linkSubAndEpic(taskFrom, epicTask);
        }
    }

    public void moveTask(SubTask taskFrom, SubTask taskTo) {
        if (taskFrom != null && taskTo != null) {
            EpicTask parentFrom = (EpicTask) getTaskByID(taskFrom.getParentID());
            EpicTask parentTo = (EpicTask) getTaskByID(taskTo.getParentID());
            clearLinkSubAndEpic(taskFrom, parentFrom);
            clearLinkSubAndEpic(taskTo, parentTo);
            EpicTask epicTask = convertToEpicTask(taskTo);
            linkSubAndEpic(taskFrom, epicTask);
        }
    }

    public void moveTask(SubTask taskFrom, EpicTask taskTo) {
        if (taskFrom != null && taskTo != null) {
            EpicTask parent = (EpicTask) getTaskByID(taskFrom.getParentID());
            clearLinkSubAndEpic(taskFrom, parent);
            linkSubAndEpic(taskFrom, taskTo);
        }
    }

    public void moveTask(SubTask taskFrom) {
        if (taskFrom != null) {
            EpicTask parent = (EpicTask) getTaskByID(taskFrom.getParentID());
            clearLinkSubAndEpic(taskFrom, parent);
        }
    }

    public void moveTask(EpicTask taskFrom, SimpleTask taskTo) {
        if (taskFrom != null && taskTo != null) {
            EpicTask epicTo = convertToEpicTask(taskTo);
            ArrayList<Integer> subTaskIds = taskFrom.getSubTaskIDs();

            ArrayList<Integer> idList = new ArrayList<>();

            idList.addAll(subTaskIds);

            for (Integer iD : idList) {
                SubTask subTask = (SubTask) getTaskByID(iD);

                clearLinkSubAndEpic(subTask, taskFrom);
                linkSubAndEpic(subTask, epicTo);
            }
        }
    }

    public void moveTask(EpicTask taskFrom, EpicTask taskTo) {
        if (taskFrom != null && taskTo != null) {

            ArrayList<Integer> subTaskIds = taskFrom.getSubTaskIDs();

            ArrayList<Integer> idList = new ArrayList<>();

            idList.addAll(subTaskIds);

            for (Integer iD : idList) {
                SubTask subTask = (SubTask) getTaskByID(iD);

                clearLinkSubAndEpic(subTask, taskFrom);
                linkSubAndEpic(subTask, taskTo);
            }
        }
    }

    public void moveTask(EpicTask taskFrom, SubTask taskTo) {
        if (taskFrom != null && taskTo != null) {

            EpicTask parent = (EpicTask) getTaskByID(taskTo.getParentID());
            clearLinkSubAndEpic(taskTo, parent);
            EpicTask epicTaskTo = convertToEpicTask(taskTo);

            ArrayList<Integer> subTaskIds = taskFrom.getSubTaskIDs();

            ArrayList<Integer> idList = new ArrayList<>();

            idList.addAll(subTaskIds);

            for (Integer iD : idList) {
                SubTask subTask = (SubTask) getTaskByID(iD);
                clearLinkSubAndEpic(subTask, taskFrom);
                linkSubAndEpic(subTask, epicTaskTo);
            }
        }
    }

    private void linkSubAndEpic(SubTask subTask, EpicTask epicTask) {
        subTask.setParent(epicTask);
        epicTask.addSubTask(subTask);

        updateTask(subTask);
        updateTask(epicTask);
    }

    private void clearLinkSubAndEpic(SubTask subTask, EpicTask epicTask) {
        epicTask.delSubTask(subTask);
        //саб без родителя это симпл
        SimpleTask simpleTask = convertToSimpleTask(subTask);
        updateTask(simpleTask);
        //Эпик без сабов это симпл
        if (epicTask.getSubTaskIDs().size() == 0) {
            simpleTask = convertToSimpleTask(epicTask);
            updateTask(simpleTask);
        } else {
            updateTask(epicTask);
        }
    }

    private SubTask convertToSubTask(SimpleTask simpleTask) {
        SubTask subTask = new SubTask(simpleTask.getName(), simpleTask.getDescription());
        subTask.setID(simpleTask.getId());
        subTask.setStatus(simpleTask.getStatus());
        return subTask;
    }

    private EpicTask convertToEpicTask(SimpleTask simpleTask) {
        EpicTask epicTask = new EpicTask(simpleTask.getName(), simpleTask.getDescription());
        epicTask.setID(simpleTask.getId());
        epicTask.setStatus(simpleTask.getStatus());
        return epicTask;
    }

    private EpicTask convertToEpicTask(SubTask subTask) {
        EpicTask epicTask = new EpicTask(subTask.getName(), subTask.getDescription());
        epicTask.setID(subTask.getId());
        epicTask.setStatus(subTask.getStatus());
        return epicTask;
    }

    private SimpleTask convertToSimpleTask(SubTask subTask) {
        SimpleTask simpleTask = new SimpleTask(subTask.getName(), subTask.getDescription());
        simpleTask.setID(subTask.getId());
        simpleTask.setStatus(subTask.getStatus());
        return simpleTask;
    }

    private SimpleTask convertToSimpleTask(EpicTask epicTask) {
        SimpleTask simpleTask = new SimpleTask(epicTask.getName(), epicTask.getDescription());
        simpleTask.setID(epicTask.getId());
        simpleTask.setStatus(epicTask.getStatus());
        return simpleTask;
    }

    private void updateSimpleTask(SimpleTask simpleTask) {
        tasks.put(simpleTask.getId(), simpleTask);
    }

    private void updateSubTask(SubTask subTask) {
        Object taskOld = getTaskByID(subTask.getId());
        //Если предыдущая версия таска была не саб, то просто сохраним
        if (taskOld == null){
            tasks.put(subTask.getId(), subTask);
        }else{
            if (!taskOld.getClass().getName().equals(ClassName.SUBTASK.getName())) {
                tasks.put(subTask.getId(), subTask);
            } else {
                tasks.put(subTask.getId(), subTask);
                //Обновление эпика
                int parentID = subTask.getParentID();
                EpicTask parent = (EpicTask) getTaskByID(parentID);
                updateEpicTask(parent);
            }
        }
    }

    private void updateEpicTask(EpicTask epicTask) {
        EnumStatus.Status status = getNewEpicStatus(epicTask);
        epicTask.setStatus(status);
        tasks.put(epicTask.getId(), epicTask);
    }

    public enum ClassName {
        SIMPLETASK("Task.SimpleTask"),
        SUBTASK("Task.SubTask"),
        EPICTASK("Task.EpicTask");

        final private String name;

        ClassName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}


