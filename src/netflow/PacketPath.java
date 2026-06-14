package netflow;

// DS #4 - Singly Linked List
// records every router a packet visits in order
// singly linked = each node only knows the next one, not previous
public class PacketPath {

    private static class Node {
        String routerId;
        Node   next;

        Node(String routerId) {
            this.routerId = routerId;
            this.next     = null;
        }
    }

    private Node head;
    private int  size;

    public void addRouter(String routerId) {
        Node newNode = new Node(routerId);
        if (head == null) {
            head = newNode;
        } else {
            // walk to end of list, then attach new node
            Node current = head;
            while (current.next != null) current = current.next;
            current.next = newNode;
        }
        size++;
    }

    // traverse from head to end, build path string
    public String getPath() {
        if (head == null) return "(empty)";
        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            sb.append(current.routerId);
            if (current.next != null) sb.append(" -> ");
            current = current.next;
        }
        return sb.toString();
    }

    public int getSize() { return size; }
}