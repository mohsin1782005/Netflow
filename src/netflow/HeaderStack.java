package netflow;

// DS #2 - Stack (LIFO)
// headers are pushed at source router, popped in reverse at destination
public class HeaderStack {

    private String[] stack;
    private int      top;
    private static final int MAX = 20;

    public HeaderStack() {
        stack = new String[MAX];
        top   = -1;
    }

    // push: add to top
    public void push(String value) {
        if (top < MAX - 1) stack[++top] = value;
    }

    // pop: remove from top (reverse order - LIFO)
    public String pop() {
        if (isEmpty()) return null;
        return stack[top--];
    }

    public boolean isEmpty() { return top == -1; }
}