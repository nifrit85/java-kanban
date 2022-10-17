package Manager;

import Task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class InMemoryTaskManager implements TaskManager {
    int Id;

    HistoryManager historyManager = Managers.getDefaultHistory();

    public void addTask(Task task, EpicTask parent){
        if (task != null) {
            int currentId = getId();
            int newID = getNewID(currentId);
            setId(newID);
            task.setID(newID);
            if (parent != null){
                SubTask child = (SubTask) task;
                child.setParent(parent);
                parent.addSubTask(child);
            }
            updateTask(task);
        }
    }

    @Override
    public Map<Integer, Task> getTasks() {
        Map<Integer, Task> tasks = new HashMap<>();
        tasks.putAll(getSimpleTasks());
        tasks.putAll(getEpicTasks());
        tasks.putAll(getSubTasks());
        return tasks;
    }

    @Override
    public void clearTasks() {
        this.Id = 0;
        clearSimpleTasks();
        clearSubTasks();
        clearEpicTasks();
    }

    public int getNewID(int id) {
        id++;
        return id;
    }

    @Override
    public void deleteTaskByID(int Id) {
        deleteSimpleTaskByID(Id);
        deleteSubTaskByID(Id);
        deleteEpicTaskByID(Id);
    }

    @Override
    public Task getTaskById(int Id) {
        Task task = getSimpleTaskById(Id);
        if (task != null){
            historyManager.add(task);
            return task;
        }
        task = getEpicTaskById(Id);
        if (task != null){
            historyManager.add(task);
            return task;
        }
        task = getSubTaskById(Id);
        historyManager.add(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
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

    @Override
    public Map<Integer, SubTask> getSubTaskFromEpic(EpicTask parent){
        Map<Integer, SubTask> subTasks = new HashMap<>();
        SubTask subTask;
        if (parent != null) {
            ArrayList<Integer> subTaskIDs = parent.getSubTaskIDs();
            if (subTaskIDs.size() != 0) {
                for (int subTaskID : subTaskIDs) {
                    subTask = getSubTaskById(subTaskID);
                    if (subTask != null) {
                        subTasks.put(subTask.getId(), subTask);
                    }
                }
            }
        }
        return subTasks;
    }

    @Override
    public Queue<Task> getHistory(){
        return historyManager.getHistory();
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

    private int getId() {
        return Id;
    }

    private void setId(int id) {
        Id = id;
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
            Map<Integer, SubTask> subTasks = getSubTaskFromEpic(this.epicTasks.get(Id));
            for (Map.Entry<Integer, SubTask> subTask : subTasks.entrySet()) {
                subTask.getValue().clearParent();
                this.subTasks.remove(subTask.getValue().getId());
            }
        }
        this.epicTasks.remove(Id);
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

        Map<Integer, SubTask> subTasks = getSubTaskFromEpic(parent);
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

    private SimpleTask getSimpleTaskById(int Id) {
        return this.simpleTasks.getOrDefault(Id, null);
    }

    private EpicTask getEpicTaskById(int Id) {
        return this.epicTasks.getOrDefault(Id, null);
    }

    private SubTask getSubTaskById(int Id) {
        return this.subTasks.getOrDefault(Id, null);
    }

}



