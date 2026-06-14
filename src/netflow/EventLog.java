package netflow;

// Doubly Linked List - global log of simulation events, supports forward and backward reading
public class EventLog {

    private static class LogNode {
        String message;
        LogNode next;
        LogNode prev;

        LogNode(String message) {
            this.message = message;
            this.next = null;
            this.prev = null;
        }
    }

    private LogNode head;
    private LogNode tail;
    private int count;

    public EventLog() {
        head = null;
        tail = null;
        count = 0;
    }

    public void addEvent(String message) {
        LogNode newNode = new LogNode(message);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
        count++;
    }

    // oldest to newest
    public void printForward() {
        System.out.println("\n--- Event Log (Oldest to Newest) ---");
        LogNode current = head;
        int i = 1;
        while (current != null) {
            System.out.println("[" + i + "] " + current.message);
            current = current.next;
            i++;
        }
    }

    // newest to oldest
    public void printBackward() {
        System.out.println("\n--- Event Log (Newest to Oldest) ---");
        LogNode current = tail;
        int i = count;
        while (current != null) {
            System.out.println("[" + i + "] " + current.message);
            current = current.prev;
            i--;
        }
    }

    public int getCount() { return count; }
}