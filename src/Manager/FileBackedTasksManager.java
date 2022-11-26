package Manager;

import Task.*;

import java.io.*;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    String pathToFile;

    public FileBackedTasksManager(String pathToFile) {
        this.pathToFile = pathToFile;
        try {
            readFile(pathToFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void deleteTaskByID(int Id) {
        super.deleteTaskByID(Id);
        save();
    }

    @Override
    public Task getTaskById(int Id) {
        Task task = super.getTaskById(Id);
        save();
        return task;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Map<Integer, SubTask> getSubTaskFromEpic(EpicTask parent) {
        Map<Integer, SubTask> subTasks = super.getSubTaskFromEpic(parent);
        save();
        return subTasks;
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public void addTask(Task task, EpicTask parent) {
        super.addTask(task, parent);
        save();
    }

    private void readFile(String pathToFile) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(pathToFile))) {
            List<String> content = new ArrayList<>();
            while (fileReader.ready()) {
                content.add(fileReader.readLine());
            }
            if (content.size() != 0) {
                createDataFromFile(content);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private void save() {

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
            System.err.println("Не удалось записать данные в файл");
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

        List<List<String>> splitedContent = splitContentForTaskAndHistory(content);
        if (splitedContent.size() > 1) {
            List<String[]> parsedLinesOfTasks = parseTaskFromContent(splitedContent.get(0));
            Map<Integer, ArrayList<Integer>> parentAndChilds = getParentAndChilds(parsedLinesOfTasks);
            addTasksFromFile(parsedLinesOfTasks, parentAndChilds);
        }
        if (splitedContent.size() == 2) {
            List<String> historyList = parseHistoryFromContent(splitedContent.get(1).get(0));
            addHistoryFromFile(historyList);
        }
    }

    private Map<Integer, ArrayList<Integer>> getParentAndChilds(List<String[]> content) {
        Map<Integer, ArrayList<Integer>> parentAndChilds = new HashMap<>();

        for (String[] line : content) {
            if (line.length == 6) {
                ArrayList<Integer> childs = new ArrayList<>();
                if (parentAndChilds.containsKey(Integer.parseInt(line[5]))) {
                    childs = parentAndChilds.get(Integer.parseInt(line[5]));
                }
                childs.add(Integer.parseInt(line[0]));
                parentAndChilds.put(Integer.parseInt(line[5]), childs);
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

        boolean is_history_found = false;
        for (String line : content) {
            if (line.isEmpty()) {
                is_history_found = true;
                continue;
            }
            if (!is_history_found) {
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
            switch (TypeOfTask.valueOf(lineOfTask[1])) {
                case SIMPLE:
                    SimpleTask simpleTask = new SimpleTask(lineOfTask[2], lineOfTask[4]);
                    simpleTask.setID(Integer.parseInt(lineOfTask[0]));
                    simpleTask.setStatus(Status.valueOf(lineOfTask[3]));
                    simpleTasks.put(Integer.parseInt(lineOfTask[0]), simpleTask);
                    break;

                case EPIC:
                    EpicTask epicTask = new EpicTask(lineOfTask[2], lineOfTask[4]);
                    epicTask.setID(Integer.parseInt(lineOfTask[0]));
                    epicTask.setStatus(Status.valueOf(lineOfTask[3]));

                    ArrayList<Integer> childList = parentAndChilds.get(Integer.parseInt(lineOfTask[0]));
                    for (Integer childId : childList) {
                        epicTask.addSubTask(childId);
                    }
                    epicTasks.put(Integer.parseInt(lineOfTask[0]), epicTask);
                    break;

                case SUB:
                    SubTask subTask = new SubTask(lineOfTask[2], lineOfTask[4]);
                    subTask.setID(Integer.parseInt(lineOfTask[0]));
                    subTask.setStatus(Status.valueOf(lineOfTask[3]));
                    subTask.setParent(Integer.parseInt(lineOfTask[5]));
                    subTasks.put(Integer.parseInt(lineOfTask[0]), subTask);
            }
        }
    }

    private void addHistoryFromFile(List<String> historyList) {
        for (String historyId : historyList) {
            historyManager.add(getTaskByIdInternalUse(Integer.parseInt(historyId)));
        }
    }
}
