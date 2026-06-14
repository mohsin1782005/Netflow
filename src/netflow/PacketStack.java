package netflow;

// Stack (LIFO) - used to simulate packet header encapsulation/decapsulation
public class PacketStack {

    private String[] stack;
    private int top;
    private static final int MAX_HEADERS = 20;

    public PacketStack() {
        stack = new String[MAX_HEADERS];
        top = -1;
    }

    public void push(String header) {
        if (top < MAX_HEADERS - 1) {
            stack[++top] = header;
        }
    }

    public String pop() {
        if (isEmpty()) return null;
        return stack[top--];
    }

    public String peek() {
        if (isEmpty()) return null;
        return stack[top];
    }

    public boolean isEmpty() { return top == -1; }
    public int size()        { return top + 1; }

    public String getAllHeaders() {
        if (isEmpty()) return "(no headers)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= top; i++) {
            sb.append(stack[i]);
            if (i < top) sb.append(" | ");
        }
        return sb.toString();
    }
}