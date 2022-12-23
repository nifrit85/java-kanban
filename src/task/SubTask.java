package task;

import constant.Status;
import constant.TypeOfTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubTask extends Task {
    private Integer parentID;

    public SubTask(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
    }

    public void setParent(Integer parentId) {
        this.parentID = parentId;
    }

    public Integer getParentID() {
        return parentID;
    }

    public void clearParent() {
        this.parentID = null;
    }

    @Override
    public String toString() {
        return "Sub{" + "Id=" + id + ", name='" + name + "' " + ", description='" + description + "' " + ", status=" + status + ", startTime = " + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")) + ", duration = " + duration.toMinutes() + " minutes" + ", endTime = " + getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")) + ", Parent=" + parentID + "}" + System.lineSeparator();
    }

    @Override
    public TypeOfTask getTaskType() {
        return TypeOfTask.SUB;
    }

    @Override
    public String stringForFile() {
        return super.stringForFile() + "," + getParentID() + System.lineSeparator();
    }

}
