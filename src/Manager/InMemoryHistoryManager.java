package Manager;

import Task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> historyList = new CustomLinkedList<>();
    private final HashMap<Integer, Node> historyLink = new HashMap<>();

    public class CustomLinkedList<T> {
        public Node<T> head;
        public Node<T> tail;
        private int size = 0;

        public void linkLast(T element) {
            if (element != null) {
                final Node<T> oldTail = tail;
                final Node<T> newNode = new Node<>(tail, element, null);
                tail = newNode;
                if (oldTail == null) {
                    head = newNode;
                } else {
                    oldTail.next = newNode;
                }
                size++;

                Task task = (Task) element;
                historyLink.put(task.getId(), newNode);
            }
        }

        public void removeNode(Node node) {
            if (node == head) {
                head = node.next;
                if (head == null) {
                    tail = null;
                }
                size--;
                return;
            }
            if (node == tail) {
                tail = node.prev;
                tail.next = null;
                size--;
                return;
            }
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }

        public List<Task> getTasks() {
            ArrayList<Task> history = new ArrayList<>();
            Node next = historyList.head;
            history.add(historyList.head.data);
            while (next != historyList.tail) {
                next = next.next;
                history.add((Task) next.data);
            }
            return history;
        }
    }


    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            historyList.linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        Node node = historyLink.get(id);
        if (node != null) {
            historyList.removeNode(node);
            historyLink.remove(id, node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    @Override
    public void clearAll() {
        historyLink.clear();
        historyList.head = null;
        historyList.tail = null;
        historyList.size = 0;
    }
}