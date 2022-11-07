package Manager;

import Task.Task;

class Node<E> {
    public E data;
    public Node<E> next;
    public Node<E> prev;

    public Node(Node<E> prev, E data, Node<E> next) {
        this.data = data;
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
            task = (Task) prev.data;
            idPrev = task.getId();
        }
        if (data != null) {
            task = (Task) data;
            idCurr = task.getId();
        }
        if (next != null) {
            task = (Task) next.data;
            idNext = task.getId();
        }
        return "Node{" +
                "prev=" + idPrev +
                ", curr=" + idCurr +
                ", next=" + idNext +
                '}';
    }
}