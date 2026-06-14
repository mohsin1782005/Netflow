package netflow;

// DS #1 - Queue (FIFO) using circular array
// waiting line at each router - first in, first out
public class RouterQueue {

    public  String   routerId;
    private Packet[] buffer;
    private int      capacity;
    private int      front;    // next to be removed
    private int      rear;     // where next packet goes
    private int      count;

    public RouterQueue(String routerId, int capacity) {
        this.routerId = routerId;
        this.capacity = capacity;
        this.buffer   = new Packet[capacity];
        this.front    = 0;
        this.rear     = 0;
        this.count    = 0;
    }

    // add packet to rear of queue (circular using modulo)
    public boolean addToBuffer(Packet packet) {
        if (isFull()) return false;
        buffer[rear] = packet;
        rear = (rear + 1) % capacity;
        count++;
        return true;
    }

    // remove packet from front of queue (circular using modulo)
    public Packet removeFromBuffer() {
        if (isEmpty()) return null;
        Packet p = buffer[front];
        buffer[front] = null;
        front = (front + 1) % capacity;
        count--;
        return p;
    }

    public boolean isFull()      { return count == capacity; }
    public boolean isEmpty()     { return count == 0; }
    public int     getCount()    { return count; }
    public int     getCapacity() { return capacity; }
}