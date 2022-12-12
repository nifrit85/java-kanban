package exceptions;

public class IntersectionsException extends Exception {
    public IntersectionsException(int id) {
        super("Новая задача пересекается по времени выполнения с задачей номер " + id);
    }
}
