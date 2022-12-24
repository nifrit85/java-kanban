package task;

import constants.Constants;
import constants.Status;
import constants.TypeOfTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    ArrayList<Integer> subTaskIDs = new ArrayList<>();
    LocalDateTime endTime;

    public EpicTask(String name, String description, Status status) {
        super(name, description, status, null, null);
    }

    public void addSubTask(int subTaskId) {
        if (!this.subTaskIDs.contains(subTaskId)) {
            this.subTaskIDs.add(subTaskId);
        }
    }

    public void delSubTask(SubTask subTask) {
        if (subTask != null) {
            Integer subTaskId = subTask.getId();
            this.subTaskIDs.remove(subTaskId);
        }
    }

    public void clearSubTasks() {
        this.subTaskIDs.clear();
    }

    public List<Integer> getSubTaskIDs() {
        return subTaskIDs;
    }

    @Override
    public String toString() {
        String stringStartTime;
        if (startTime == null) stringStartTime = Constants.NOT_AVAILABLE;
        else stringStartTime = startTime.format(DateTimeFormatter.ofPattern(Constants.LOCAL_DATE_TIME_FORMAT));

        String stringDuration;
        if (duration == null) stringDuration = Constants.NOT_AVAILABLE;
        else stringDuration = duration.toMinutes() + " minutes";

        String stringEndTime;
        if (endTime == null) stringEndTime = Constants.NOT_AVAILABLE;
        else stringEndTime = endTime.format(DateTimeFormatter.ofPattern(Constants.LOCAL_DATE_TIME_FORMAT));


        return "Epic{" + "Id=" + id + ", name='" + name + "' " + ", description='" + description + "' " + ", status=" + status + ", startTime = " + stringStartTime + ", duration = " + stringDuration + ", endTime = " + stringEndTime + ", subTaskIDs=" + subTaskIDs.toString() + "}" + System.lineSeparator();
    }

    @Override
    public TypeOfTask getTaskType() {
        return TypeOfTask.EPIC;
    }

    @Override
    public String stringForFile() {
        return super.stringForFile() + System.lineSeparator();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
