
import Task.EpicTask;
import Task.SimpleTask;
import Task.Status;
import Task.SubTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Manager {
    private int Id;
    private final Map<Integer, SimpleTask> simpleTasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, EpicTask> epicTasks = new HashMap<>();

    public void addTask(SimpleTask task) {
        if (task != null) {
            int newID = getNewID();
            task.setID(newID);
            updateSimpleTask(task);
        }
    }

    public void addTask(EpicTask task) {
        if (task != null) {
            int newID = getNewID();
            task.setID(newID);
            updateEpicTask(task);
        }
    }

    public void addTask(SubTask child, EpicTask parent) {
        if (child != null && parent != null) {
            int newID = getNewID();
            child.setID(newID);
            child.setParent(parent);
            parent.addSubTask(child);
            updateSubTask(child);
        }
    }

    public Map<Integer, Object> getTasks() {
        Map<Integer, Object> tasks = new HashMap<>();
        tasks.putAll(getSimpleTasks());
        tasks.putAll(getEpicTasks());
        tasks.putAll(getSubTasks());
        return tasks;
    }

    private Map<Integer, SimpleTask> getSimpleTasks() {
        return simpleTasks;
    }

    private Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    private Map<Integer, EpicTask> getEpicTasks() {
        return epicTasks;
    }

    public void clearTasks() {
        this.Id = 0;
        clearSimpleTasks();
        clearSubTasks();
        clearEpicTasks();
    }

    private void clearSimpleTasks() {
        this.simpleTasks.clear();
    }

    private void clearSubTasks() {
        this.subTasks.clear();
        for (Map.Entry<Integer, EpicTask> epicTaskEntry : this.epicTasks.entrySet()) {
            epicTaskEntry.getValue().clearSubTasks();
        }
    }

    private void clearEpicTasks() {
        this.epicTasks.clear();
        for (Map.Entry<Integer, SubTask> subTaskEntry : this.subTasks.entrySet()) {
            subTaskEntry.getValue().clearParent();
        }
    }

    private int getNewID() {
        int newID = this.Id;
        this.Id++;
        return newID;
    }

    public void deleteTaskByID(int Id) {
        deleteSimpleTaskByID(Id);
        deleteSubTaskByID(Id);
        deleteEpicTaskByID(Id);
    }

    private void deleteSimpleTaskByID(int Id) {
        this.simpleTasks.remove(Id);
    }

    private void deleteSubTaskByID(int Id) {
        if (this.subTasks.containsKey(Id)) {
            SubTask subTask = subTasks.get(Id);
            EpicTask parent = this.epicTasks.get(subTask.getParentID());
            if (parent != null) parent.delSubTask(subTask);
        }
        this.subTasks.remove(Id);
    }

    private void deleteEpicTaskByID(int Id) {
        if (this.epicTasks.containsKey(Id)) {
            Map<Integer,SubTask> subTasks = getSubTaskFromEpic(this.epicTasks.get(Id));
            for (Map.Entry<Integer,SubTask> subTask: subTasks.entrySet()) {
                subTask.getValue().clearParent();
            }
        }
        this.epicTasks.remove(Id);
    }

    public Object getTaskById(int Id) {

        Object task = getSimpleTaskById(Id);
        if (task != null) return task;
        task = getEpicTaskById(Id);
        if (task != null) return task;
        task = getSubTaskById(Id);
        return task;
    }

    private SimpleTask getSimpleTaskById(int Id) {
        return this.simpleTasks.getOrDefault(Id, null);
    }

    private EpicTask getEpicTaskById(int Id) {
        return this.epicTasks.getOrDefault(Id, null);
    }

    private SubTask getSubTaskById(int Id) {
        return this.subTasks.getOrDefault(Id, null);
    }

    public void updateTask(Object task) {
        if (task != null) {
            if (task instanceof EpicTask) {
                updateEpicTask((EpicTask) task);
            } else if (task instanceof SubTask) {
                updateSubTask((SubTask) task);
            } else if (task instanceof SimpleTask) {
                updateSimpleTask((SimpleTask) task);
            }
        }
    }

    private void updateSimpleTask(SimpleTask simpleTask) {
        this.simpleTasks.put(simpleTask.getId(), simpleTask);
    }

    private void updateSubTask(SubTask subTask) {
        this.subTasks.put(subTask.getId(), subTask);

        int parentID = subTask.getParentID();
        EpicTask parent = (EpicTask) getTaskById(parentID);
        if (parent != null) updateEpicTask(parent);
    }

    private void updateEpicTask(EpicTask epicTask) {
        Status status = getNewEpicStatus(epicTask);
        epicTask.setStatus(status);
        this.epicTasks.put(epicTask.getId(), epicTask);
    }

    private Status getNewEpicStatus(EpicTask parent) {

        Status status = Status.NEW;
        int newCount = 0;
        int doneCount = 0;

        Map<Integer,SubTask> subTasks = getSubTaskFromEpic(parent);
        if (subTasks.size() != 0) {
            for (Map.Entry<Integer, SubTask> subTask : subTasks.entrySet()) {
                if (subTask.getValue().getStatus().equals(Status.NEW)) {
                    newCount++;
                } else if (subTask.getValue().getStatus().equals(Status.DONE)) {
                    doneCount++;
                } else {
                    return Status.IN_PROGRESS;
                }
            }
            if (newCount == subTasks.size()) {
                return Status.NEW;
            } else if (doneCount == subTasks.size()) {
                status = Status.DONE;
            } else {
                status = Status.IN_PROGRESS;
            }
        }
        return status;
    }

    public Map<Integer, SubTask> getSubTaskFromEpic(EpicTask parent){
        Map<Integer, SubTask> subTasks = new HashMap<>();
        SubTask subTask;
        if (parent != null) {
            ArrayList<Integer> subTaskIDs = parent.getSubTaskIDs();
            if (subTaskIDs.size() != 0) {
                for (int subTaskID : subTaskIDs) {
                    subTask = getSubTaskById(subTaskID);
                    if (subTask != null){
                        subTasks.put(subTask.getId(), subTask);
                    }
                }
            }
        }
        return subTasks;
    }
}



