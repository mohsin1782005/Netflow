package netflow;

// DS #5 - Doubly Linked List
// saves every event; can be read forward or backward
public class EventLog {

    private static class EventNode {
        String    message;
        EventNode next; // newer event
        EventNode prev; // older event

        EventNode(String message) {
            this.message = message;
        }
    }

    private EventNode head; // oldest entry
    private EventNode tail; // newest entry
    private int count;

    public void addEvent(String message) {
        EventNode node = new EventNode(message);
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            // link new node to tail, then move tail forward
            node.prev  = tail;
            tail.next  = node;
            tail       = node;
        }
        count++;
    }

    // traverse using .next pointer (oldest to newest)
    public void printForward() {
        System.out.println("\n-- EVENT LOG (Oldest to Newest) --");
        EventNode current = head;
        int i = 1;
        while (current != null) {
            System.out.println("[" + i++ + "] " + current.message);
            current = current.next;
        }
        System.out.println("----------------------------------");
    }

    // traverse using .prev pointer (newest to oldest) - only possible in doubly LL
    public void printBackward() {
        System.out.println("\n-- EVENT LOG (Newest to Oldest) --");
        EventNode current = tail;
        int i = count;
        while (current != null) {
            System.out.println("[" + i-- + "] " + current.message);
            current = current.prev;
        }
        System.out.println("----------------------------------");
    }

    public int getCount() { return count; }
}