package task;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class Task {

    private static final String NOT_AVAILABLE = "NaN";
    int id;
    String name;
    String description;
    Status status;
    LocalDateTime startTime;
    Duration duration;


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    protected Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeOfTask getTaskType() {
        return null;
    }

    public String stringForFile() {
        String stringStartTime;
        if (startTime == null) stringStartTime = NOT_AVAILABLE;
        else stringStartTime = getStartTime().toString();

        String stringDuration;
        if (duration == null) stringDuration = NOT_AVAILABLE;
        else stringDuration = getDuration().toString();

        return id + "," + getTaskType().toString() + "," + getName() + "," + getStatus().toString() + "," + getDescription() + "," + stringStartTime + "," + stringDuration;

    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == LocalDateTime.MAX)
            startTime = LocalDateTime.now(); //Если указали только продолжительность, отсчёт начнём с текущей даты
        return startTime.plusSeconds(duration.toSeconds());
    }

}
