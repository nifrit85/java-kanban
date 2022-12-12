package managers;

import exceptions.IntersectionsException;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InMemoryTaskManager implements TaskManager {

    int id;
    HistoryManager historyManager = Managers.getDefaultHistory();

    Logger log = Logger.getAnonymousLogger();

    public void addTask(Task task, EpicTask parent) {
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
        historyManager.remove(id);
        prioritizedTasks.removeIf(task -> task.getId() == id);

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
    public void updateTask(Task task) {
        if (task != null) {
            if (task instanceof EpicTask) {
                updateEpicTask((EpicTask) task);
            } else if (task instanceof SubTask) {
                try {
                    updateSubTask((SubTask) task);
                } catch (IntersectionsException e) {
                    EpicTask parent = (EpicTask) getTaskByIdInternalUse(((SubTask) task).getParentID());
                    if (parent != null) {
                        parent.delSubTask((SubTask) task);
                    }
                    log.log(Level.WARNING, e.getMessage());
                    return;
                }
            } else if (task instanceof SimpleTask) {
                try {
                    updateSimpleTask((SimpleTask) task);
                } catch (IntersectionsException e) {
                    log.log(Level.WARNING, e.getMessage());
                    return;
                }
            }
            prioritizedTasks.removeIf(t -> t.getId() == task.getId());
            prioritizedTasks.add(task);
        }
    }

    @Override
    public Map<Integer, SubTask> getSubTaskFromEpic(EpicTask parent) {
        Map<Integer, SubTask> subTasks = new HashMap<>();
        SubTask subTask;
        if (parent != null) {
            List<Integer> subTaskIDs = parent.getSubTaskIDs();
            if (!subTaskIDs.isEmpty()) {
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
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
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
        TaskManager.simpleTasks.clear();
    }

    private void clearSubTasks() {
        TaskManager.subTasks.clear();
        for (Map.Entry<Integer, EpicTask> epicTaskEntry : TaskManager.epicTasks.entrySet()) {
            epicTaskEntry.getValue().clearSubTasks();
        }
    }

    private void clearEpicTasks() {
        TaskManager.epicTasks.clear();
    }

    protected int getId() {
        return this.id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    private void deleteSimpleTaskByID(int id) {
        TaskManager.simpleTasks.remove(id);
    }

    private void deleteSubTaskByID(int id) {
        if (TaskManager.subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            EpicTask parent = TaskManager.epicTasks.get(subTask.getParentID());
            if (parent != null) {
                parent.delSubTask(subTask);
                calculateNewEndTimeForEpicTask(parent);
            }
        }
        TaskManager.subTasks.remove(id);
    }

    private void deleteEpicTaskByID(int id) {
        if (TaskManager.epicTasks.containsKey(id)) {
            Map<Integer, SubTask> subTasks = getSubTaskFromEpic(TaskManager.epicTasks.get(id));
            for (Map.Entry<Integer, SubTask> subTask : subTasks.entrySet()) {
                subTask.getValue().clearParent();
                TaskManager.subTasks.remove(subTask.getValue().getId());
                prioritizedTasks.removeIf(task -> task.getId() == subTask.getValue().getId());
                historyManager.remove(subTask.getValue().getId());
            }
        }
        TaskManager.epicTasks.remove(id);
    }

    private void updateSimpleTask(SimpleTask simpleTask) throws IntersectionsException {
        checkIntersections(simpleTask);
        TaskManager.simpleTasks.put(simpleTask.getId(), simpleTask);
    }

    private void updateSubTask(SubTask subTask) throws IntersectionsException {
        checkIntersections(subTask);
        TaskManager.subTasks.put(subTask.getId(), subTask);
        int parentID = subTask.getParentID();
        EpicTask parent = (EpicTask) getTaskByIdInternalUse(parentID);
        if (parent != null) updateEpicTask(parent);


    }

    private void updateEpicTask(EpicTask epicTask) {
        Status status = getNewEpicStatus(epicTask);
        epicTask.setStatus(status);
        calculateNewEndTimeForEpicTask(epicTask);
        TaskManager.epicTasks.put(epicTask.getId(), epicTask);
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

    private SimpleTask getSimpleTaskById(int id) {
        return TaskManager.simpleTasks.getOrDefault(id, null);
    }

    private EpicTask getEpicTaskById(int id) {
        return TaskManager.epicTasks.getOrDefault(id, null);
    }

    private SubTask getSubTaskById(int id) {
        return TaskManager.subTasks.getOrDefault(id, null);
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
}



