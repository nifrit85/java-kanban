package constants;

public class Constants {
    public static final String LOCAL_DATE_TIME_FORMAT = "dd.MM.yyyy, HH:mm:ss";
    public static final String INTERSECTION_MESSAGE = "Новая задача пересекается по времени выполнения с задачей номер ";
    public static final String NOT_AVAILABLE = "NaN";
    public static final String FILE_WRITE_ERROR = "Не удалось записать данные в файл";
    public static final String HTTP_TASKS = "tasks";
    public static final String HTTP_EPIC = "epic";
    public static final String HTTP_SUB = "subtask";
    public static final String HTTP_HISTORY = "history";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String GOOD_PATTERN = "^/tasks/?(task/?|epic/?|subtask/?)*";
    public static final String PATH_SIMPLE = "/tasks/task";
    public static final String PATH_SUB = "/tasks/subtask";
    public static final String PATH_EPIC = "/tasks/epic";
    public static final String PATH_HISTORY = "/tasks/history";
    public static final String PATH_TASKS = "/tasks";
    public static final String GOOD_PATTERN_FOR_ID = "^/tasks/(task|epic|subtask)/$";
    public static final String ID_PATTERN = "id=";
    public static final String HISTORY_PATTERN = "^/tasks/history/?";
    public static final String TASKS_PATTERN = "^/tasks/?";


}
