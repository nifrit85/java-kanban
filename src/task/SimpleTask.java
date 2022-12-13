package task;

import constant.Status;
import constant.TypeOfTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleTask extends Task {
    public SimpleTask(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
    }

    @Override
    public String toString() {

        return "Simple{" + "Id= " + id + ", name= '" + name + "' " + ", description= '" + description + "' " + ", status= " + status + "}" + ", startTime = " + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")) + ", duration = " + duration.toMinutes() + " minutes" + ", endTime = " + getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")) + System.lineSeparator();
    }

    @Override
    public TypeOfTask getTaskType() {
        return TypeOfTask.SIMPLE;
    }

    @Override
    public String stringForFile() {
        return super.stringForFile() + System.lineSeparator();
    }
}
