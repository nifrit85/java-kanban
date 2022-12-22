package managers;

import constant.Status;
import constant.TypeOfTask;
import exceptions.IntersectionsException;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import task.EpicTask;
import task.SimpleTask;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InMemoryTaskManager implements TaskManager {

    Map<Integer, SimpleTask> simpleTasks = new HashMap<>();
    Map<Integer, SubTask> subTasks = new HashMap<>();
    Map<Integer, EpicTask> epicTasks = new HashMap<>();
    Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    int id;
    HistoryManager historyManager = Managers.getDefaultHistory();

    Logger log = Logger.getAnonymousLogger();

    public void addTask(Task task, EpicTask parent) throws IntersectionsException {
        if (task != null) {
            int currentId = getId();
            setId(getNewID(currentId));
            task.setID(getNewID(currentId));

            if (parent != null) {
                SubTask child = (SubTask) task;
                child.setParent(parent.getId());
                parent.addSubTask(child.getId());
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
        this.id = 0;
        clearSimpleTasks();
        clearSubTasks();
        clearEpicTasks();
        historyManager.clearAll();
        prioritizedTasks.clear();
    }

    public int getNewID(int id) {
        id++;
        return id;
    }

    @Override
    public void deleteTaskByID(int id) {
        deleteSimpleTaskByID(id);
        deleteSubTaskByID(id);
        deleteEpicTaskByID(id);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = getSimpleTaskById(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        }
        task = getEpicTaskById(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        }
        task = getSubTaskById(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public void updateTask(Task task) throws IntersectionsException {
        if (task != null) {
            switch (task.getTaskType()) {
                case EPIC:
                    updateEpicTask((EpicTask) task);
                    break;
                case SUB:
                    try {
                        updateSubTask((SubTask) task);
                    } catch (IntersectionsException e) {
                        EpicTask parent = (EpicTask) getTaskByIdInternalUse(((SubTask) task).getParentID());
                        if (parent != null) {
                            parent.delSubTask((SubTask) task);
                        }
                        throw new IntersectionsException(task.getId());
                    }
                    break;
                case SIMPLE:
                        updateSimpleTask((SimpleTask) task);

            }
            prioritizedTasks.removeIf(t -> t.getId() == task.getId());
            prioritizedTasks.add(task);

        }
    }

    @Override
    public Map<Integer, SubTask> getSubTaskFromEpic(EpicTask parent) {
        Map<Integer, SubTask> subTasksFromEpic = new HashMap<>();
        SubTask subTask;
        if (parent != null) {
            List<Integer> subTaskIDs = parent.getSubTaskIDs();
            if (!subTaskIDs.isEmpty()) {
                for (int subTaskID : subTaskIDs) {
                    subTask = getSubTaskById(subTaskID);
                    if (subTask != null) {
                        subTasksFromEpic.put(subTask.getId(), subTask);
                    }
                }
            }
        }
        return subTasksFromEpic;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public Map<Integer, SimpleTask> getSimpleTasks() {
        return simpleTasks;
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public Map<Integer, EpicTask> getEpicTasks() {
        return epicTasks;
    }

    public void clearSimpleTasks() {
        simpleTasks.clear();
    }

    public void clearSubTasks() {
        subTasks.clear();
        for (Map.Entry<Integer, EpicTask> epicTaskEntry : epicTasks.entrySet()) {
            epicTaskEntry.getValue().clearSubTasks();
        }
    }

    public void clearEpicTasks() {
        epicTasks.clear();
    }

    protected int getId() {
        return this.id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public void deleteSimpleTaskByID(int id) {
        simpleTasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }

    public void deleteSubTaskByID(int id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            EpicTask parent = epicTasks.get(subTask.getParentID());
            if (parent != null) {
                parent.delSubTask(subTask);
                calculateNewEndTimeForEpicTask(parent);
            }
        }
        subTasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }

    public void deleteEpicTaskByID(int id) {
        if (epicTasks.containsKey(id)) {
            Map<Integer, SubTask> subTasksFromEpic = getSubTaskFromEpic(epicTasks.get(id));
            for (Map.Entry<Integer, SubTask> subTask : subTasksFromEpic.entrySet()) {
                subTask.getValue().clearParent();
                subTasks.remove(subTask.getValue().getId());
                prioritizedTasks.removeIf(task -> task.getId() == subTask.getValue().getId());
                historyManager.remove(subTask.getValue().getId());
            }
        }
        epicTasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }

    private void updateSimpleTask(SimpleTask simpleTask) throws IntersectionsException {
        checkIntersections(simpleTask);
        simpleTasks.put(simpleTask.getId(), simpleTask);
    }

    private void updateSubTask(SubTask subTask) throws IntersectionsException {
        checkIntersections(subTask);
        subTasks.put(subTask.getId(), subTask);
        int parentID = subTask.getParentID();
        EpicTask parent = (EpicTask) getTaskByIdInternalUse(parentID);
        if (parent != null) updateEpicTask(parent);


    }

    private void updateEpicTask(EpicTask epicTask) {
        Status status = getNewEpicStatus(epicTask);
        epicTask.setStatus(status);
        calculateNewEndTimeForEpicTask(epicTask);
        epicTasks.put(epicTask.getId(), epicTask);
    }

    private Status getNewEpicStatus(EpicTask parent) {

        Status status = Status.NEW;
        int newCount = 0;
        int doneCount = 0;

        Map<Integer, SubTask> subTasksFromEpic = getSubTaskFromEpic(parent);
        if (subTasksFromEpic.size() != 0) {
            for (Map.Entry<Integer, SubTask> subTask : subTasksFromEpic.entrySet()) {
                if (subTask.getValue().getStatus().equals(Status.NEW)) {
                    newCount++;
                } else if (subTask.getValue().getStatus().equals(Status.DONE)) {
                    doneCount++;
                } else {
                    return Status.IN_PROGRESS;
                }
            }
            if (newCount == subTasksFromEpic.size()) {
                return Status.NEW;
            } else if (doneCount == subTasksFromEpic.size()) {
                status = Status.DONE;
            } else {
                status = Status.IN_PROGRESS;
            }
        }
        return status;
    }

    private SimpleTask getSimpleTaskById(int id) {
        return simpleTasks.getOrDefault(id, null);
    }

    private EpicTask getEpicTaskById(int id) {
        return epicTasks.getOrDefault(id, null);
    }

    private SubTask getSubTaskById(int id) {
        return subTasks.getOrDefault(id, null);
    }

    protected Task getTaskByIdInternalUse(int id) {
        Task task = getSimpleTaskById(id);
        if (task != null) {
            return task;
        }
        task = getEpicTaskById(id);
        if (task != null) {
            return task;
        }
        task = getSubTaskById(id);
        return task;
    }

    private void calculateNewEndTimeForEpicTask(EpicTask parent) {
        boolean isStartTimeFound = false;
        boolean isEndTimeFound = false;
        LocalDateTime startTimeOfEpic = LocalDateTime.MAX;
        LocalDateTime endTimeOfEpic = LocalDateTime.MIN;
        Duration durationOfEpic = Duration.ZERO;
        List<Integer> subTaskIDs = parent.getSubTaskIDs();
        for (int subTaskId : subTaskIDs) {
            SubTask subTask = (SubTask) getTaskByIdInternalUse(subTaskId);
            if (subTask != null) {
                if (startTimeOfEpic == null || startTimeOfEpic.isAfter(subTask.getStartTime())) {
                    startTimeOfEpic = subTask.getStartTime();
                    isStartTimeFound = true;
                }
                if (endTimeOfEpic == null || endTimeOfEpic.isBefore(subTask.getEndTime())) {
                    endTimeOfEpic = subTask.getEndTime();
                    isEndTimeFound = true;
                }
                durationOfEpic = durationOfEpic.plus(subTask.getDuration());
            }
        }
        if (isStartTimeFound) parent.setStartTime(startTimeOfEpic);
        if (isEndTimeFound) parent.setEndTime(endTimeOfEpic);
        parent.setDuration(durationOfEpic);
        prioritizedTasks.removeIf(task -> task.getId() == parent.getId());
        prioritizedTasks.add(parent);
    }


    private void checkIntersections(Task task) throws IntersectionsException {
        for (Task prioritizedTask : prioritizedTasks) {
            if (task.getId() == prioritizedTask.getId()) return;
            if (prioritizedTask.getTaskType() == TypeOfTask.EPIC) continue;
            if (task.getEndTime() != null && task.getStartTime() != null && prioritizedTask.getEndTime() != null && prioritizedTask.getStartTime() != null
                    //Покрывает полностью
                    && ((task.getStartTime().isBefore(prioritizedTask.getStartTime()) && task.getEndTime().isAfter(prioritizedTask.getEndTime()))
                    // Конец во время выполнения другой
                    || (task.getEndTime().isAfter(prioritizedTask.getStartTime()) && task.getEndTime().isBefore(prioritizedTask.getEndTime()))
                    // Начало во время выполнения другой
                    || (task.getStartTime().isAfter(prioritizedTask.getStartTime()) && task.getStartTime().isBefore(prioritizedTask.getEndTime()))
                    // Полностью совпадает начало и конец
                    || (task.getStartTime().isEqual(prioritizedTask.getStartTime()) && task.getEndTime().isEqual(prioritizedTask.getEndTime()))))

                throw new IntersectionsException(prioritizedTask.getId());
        }
    }

    @Override
    public void clearHistory() {
        historyManager.clearAll();
    }
}



