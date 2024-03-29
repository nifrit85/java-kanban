package managers;

import constants.Constants;
import constants.Status;
import constants.TypeOfTask;
import exceptions.IntersectionsException;
import managers.interfaces.TaskManager;
import task.EpicTask;
import task.SimpleTask;
import task.SubTask;
import task.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private String pathToFile;

    public FileBackedTasksManager(String pathToFile) {
        this.pathToFile = pathToFile;
        try {
            readFile();
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage());
        }
    }

    public FileBackedTasksManager() {
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void updateTask(Task task) throws IntersectionsException {
        super.updateTask(task);
        save();
    }

    @Override
    public Map<Integer, SubTask> getSubTaskFromEpic(EpicTask parent) {
        Map<Integer, SubTask> subTasksFromEpic = super.getSubTaskFromEpic(parent);
        save();
        return subTasksFromEpic;
    }

    @Override
    public void addTask(Task task, EpicTask parent) throws IntersectionsException {
        super.addTask(task, parent);
        save();
    }

    protected void readFile() throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(pathToFile))) {
            List<String> content = new ArrayList<>();
            while (fileReader.ready()) {
                content.add(fileReader.readLine());
            }
            if (!content.isEmpty()) {
                createDataFromFile(content);
            }
        }
    }

    protected void save() {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathToFile))) {

            for (Map.Entry<Integer, SimpleTask> line : simpleTasks.entrySet()) {
                bufferedWriter.write(line.getValue().stringForFile());
            }
            for (Map.Entry<Integer, EpicTask> line : epicTasks.entrySet()) {
                bufferedWriter.write(line.getValue().stringForFile());
            }
            for (Map.Entry<Integer, SubTask> line : subTasks.entrySet()) {
                bufferedWriter.write(line.getValue().stringForFile());
            }
            bufferedWriter.write(System.lineSeparator());

            saveHistoryToFile(bufferedWriter);

        } catch (IOException e) {
            log.log(Level.WARNING, Constants.FILE_WRITE_ERROR);
        }
    }

    private void saveHistoryToFile(BufferedWriter bufferedWriter) throws IOException {

        Iterator<Task> iterator = getHistory().iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next().getId();
            if (!iterator.hasNext()) {
                bufferedWriter.write(id.toString());
            } else {
                bufferedWriter.write(id + ",");
            }
        }
    }

    private void createDataFromFile(List<String> content) {

        List<List<String>> splittedContent = splitContentForTaskAndHistory(content);
        if (splittedContent.size() > 1) {
            List<String[]> parsedLinesOfTasks = parseTaskFromContent(splittedContent.get(0));
            Map<Integer, ArrayList<Integer>> parentAndChilds = getParentAndChilds(parsedLinesOfTasks);
            addTasksFromFile(parsedLinesOfTasks, parentAndChilds);
        }

        if (splittedContent.size() == 2 && !splittedContent.get(1).isEmpty()) {
            List<String> historyList = parseHistoryFromContent(splittedContent.get(1).get(0));
            addHistoryFromFile(historyList);
        }

    }

    private Map<Integer, ArrayList<Integer>> getParentAndChilds(List<String[]> content) {
        Map<Integer, ArrayList<Integer>> parentAndChilds = new HashMap<>();

        for (String[] line : content) {
            if (line.length == 8) {
                ArrayList<Integer> childs = new ArrayList<>();
                if (parentAndChilds.containsKey(Integer.parseInt(line[7]))) {
                    childs = parentAndChilds.get(Integer.parseInt(line[7]));
                }
                childs.add(Integer.parseInt(line[0]));
                parentAndChilds.put(Integer.parseInt(line[7]), childs);
            }
        }
        return parentAndChilds;
    }

    private List<String[]> parseTaskFromContent(List<String> content) {
        List<String[]> parsedLines = new ArrayList<>();
        for (String line : content) {
            parsedLines.add(line.split(","));
        }
        return parsedLines;
    }

    private List<String> parseHistoryFromContent(String content) {
        return Arrays.asList(content.split(","));
    }

    private List<List<String>> splitContentForTaskAndHistory(List<String> content) {
        List<List<String>> splittedContent = new ArrayList<>();
        List<String> tasksList = new ArrayList<>();
        List<String> historyList = new ArrayList<>();

        boolean isHistoryFound = false;
        for (String line : content) {
            if (line.isEmpty()) {
                isHistoryFound = true;
                continue;
            }
            if (!isHistoryFound) {
                tasksList.add(line);
            } else {
                historyList.add(line);
            }
        }
        splittedContent.add(tasksList);
        splittedContent.add(historyList);
        return splittedContent;
    }

    private void addTasksFromFile(List<String[]> parsedLinesOfTasks, Map<Integer, ArrayList<Integer>> parentAndChilds) {
        int maxId = 0;
        for (String[] lineOfTask : parsedLinesOfTasks) {
            int id = Integer.parseInt(lineOfTask[0]);
            if (maxId < id) {
                maxId = id;
                setId(id);
            }
            TypeOfTask typeOfTask = TypeOfTask.valueOf(lineOfTask[1]);
            String name = lineOfTask[2];
            Status status = Status.valueOf(lineOfTask[3]);
            String description = lineOfTask[4];
            LocalDateTime startTime = null;
            if (!lineOfTask[5].equals(Constants.NOT_AVAILABLE)) startTime = LocalDateTime.parse(lineOfTask[5]);

            Duration duration = Duration.parse(lineOfTask[6]);

            switch (typeOfTask) {
                case SIMPLE:
                    SimpleTask simpleTask = new SimpleTask(name, description, status, startTime, duration);
                    simpleTask.setID(id);
                    addSimpleTaskFromFile(simpleTask);
                    break;
                case EPIC:
                    EpicTask epicTask = new EpicTask(name, description, status);
                    epicTask.setID(id);
                    epicTask.setStartTime(startTime);
                    epicTask.setDuration(duration);
                    ArrayList<Integer> childList = parentAndChilds.get(id);
                    addEpicTaskFromFile(epicTask, childList);
                    break;

                case SUB:
                    int parentId = Integer.parseInt(lineOfTask[7]);
                    SubTask subTask = new SubTask(name, description, status, startTime, duration);
                    subTask.setID(id);
                    subTask.setParent(parentId);
                    addSubTaskFromFile(subTask);
            }
        }
    }

    private void addHistoryFromFile(List<String> historyList) {
        for (String historyId : historyList) {
            historyManager.add(getTaskByIdInternalUse(Integer.parseInt(historyId)));
        }
    }

    @Override
    public void clearSimpleTasks() {
        super.clearSimpleTasks();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    @Override
    public void clearEpicTasks() {
        super.clearEpicTasks();
        save();
    }

    @Override
    public void deleteSimpleTaskByID(int id) {
        super.deleteSimpleTaskByID(id);
        save();
    }

    @Override
    public void deleteSubTaskByID(int id) {
        super.deleteSubTaskByID(id);
        save();
    }

    @Override
    public void deleteEpicTaskByID(int id) {
        super.deleteEpicTaskByID(id);
        save();
    }

    private void addSimpleTaskFromFile(SimpleTask simpleTask) {
        try {
            updateTask(simpleTask);
        } catch (IntersectionsException e) {
            log.log(Level.WARNING, e.getMessage());
        }
    }

    private void addEpicTaskFromFile(EpicTask epicTask, ArrayList<Integer> childList) {
        if (childList != null) {
            for (Integer childId : childList) {
                epicTask.addSubTask(childId);
            }
        }
        try {
            updateTask(epicTask);
        } catch (IntersectionsException e) {
            log.log(Level.WARNING, e.getMessage());
        }
    }

    private void addSubTaskFromFile(SubTask subTask) {
        try {
            updateTask(subTask);
        } catch (IntersectionsException e) {
            log.log(Level.WARNING, e.getMessage());
        }
    }
}
