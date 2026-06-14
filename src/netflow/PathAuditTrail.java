package netflow;

// Singly Linked List - tracks every router a packet passes through
public class PathAuditTrail {

    private static class Node {
        String routerId;
        Node next;

        Node(String routerId) {
            this.routerId = routerId;
            this.next = null;
        }
    }

    private Node head;
    private int size;

    public PathAuditTrail() {
        head = null;
        size = 0;
    }

    // append a router ID to the end of the trail
    public void addHop(String routerId) {
        Node newNode = new Node(routerId);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    // returns path as a readable string e.g. "Router-A -> Router-C -> Router-E"
    public String getTrail() {
        if (head == null) return "(no trail)";
        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            sb.append(current.routerId);
            if (current.next != null) sb.append(" -> ");
            current = current.next;
        }
        return sb.toString();
    }

    public int getSize() {
        return size;
    }
}