package netflow;

// Queue (FIFO) - each router has one of these as its packet buffer
public class RouterBuffer {

    public String routerId;
    private Packet[] buffer;
    private int capacity;
    private int front;
    private int rear;
    private int count;
    public int peakLoad;

    public RouterBuffer(String routerId, int capacity) {
        this.routerId = routerId;
        this.capacity = capacity;
        this.buffer = new Packet[capacity];
        this.front = 0;
        this.rear = 0;
        this.count = 0;
        this.peakLoad = 0;
    }

    // add packet to back of queue
    public boolean enqueue(Packet packet) {
        if (isFull()) return false;
        buffer[rear] = packet;
        rear = (rear + 1) % capacity;
        count++;
        if (count > peakLoad) peakLoad = count;
        return true;
    }

    // remove and return packet from front
    public Packet dequeue() {
        if (isEmpty()) return null;
        Packet p = buffer[front];
        buffer[front] = null;
        front = (front + 1) % capacity;
        count--;
        return p;
    }

    public Packet peek() {
        if (isEmpty()) return null;
        return buffer[front];
    }

    public boolean isFull()      { return count == capacity; }
    public boolean isEmpty()     { return count == 0; }
    public int getCount()        { return count; }
    public int getCapacity()     { return capacity; }
}