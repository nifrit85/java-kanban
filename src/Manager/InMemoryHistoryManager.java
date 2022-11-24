package Manager;

import Task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> historyLink = new HashMap<>();

    private Node head;
    private Node tail;

    static class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task task, Node next) {
            this.data = task;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public String toString() {
            Integer idPrev = null;
            Integer idCurr = null;
            Integer idNext = null;
            Task task;
            if (prev != null) {
                task = prev.data;
                idPrev = task.getId();
            }
            if (data != null) {
                task = data;
                idCurr = task.getId();
            }
            if (next != null) {
                task = next.data;
                idNext = task.getId();
            }
            return "Node{" +
                    "prev=" + idPrev +
                    ", curr=" + idCurr +
                    ", next=" + idNext +
                    '}';
        }
    }

        public void linkLast(Task task) {
            if (task != null) {
                final Node oldTail = tail;
                final Node newNode = new Node(tail, task, null);
                tail = newNode;
                if (oldTail == null) {
                    head = newNode;
                } else {
                    oldTail.next = newNode;
                }

                historyLink.put(task.getId(), newNode);
            }
        }

        public void removeNode(Node node) {
            if (node == head) {
                head = node.next;
                if (head == null) {
                    tail = null;
                }
                return;
            }
            if (node == tail) {
                tail = node.prev;
                tail.next = null;
                return;
            }
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        public List<Task> getTasks() {
            ArrayList<Task> history = new ArrayList<>();
            Node next = head;
            if (head != null) {
                history.add(head.data);
                while (next != tail) {
                    next = next.next;
                    history.add(next.data);
                }
            }
            return history;
        }
//    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        Node node = historyLink.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void clearAll() {
        historyLink.clear();
        head = null;
        tail = null;
    }
}