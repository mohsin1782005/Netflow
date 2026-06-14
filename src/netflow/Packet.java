package netflow;

public class Packet {

    public String     packetId;
    public String     source;
    public String     destination;
    public int        sizeKB;
    public long       startTimeMs;    // when packet was created
    public int        routersPassed;  // how many routers it went through
    public String     status;         // DELIVERED or DROPPED
    public long       timeTakenMs;    // total travel time
    public PacketPath pathRecord;     // DS #4 - singly linked list of routers visited

    public Packet(String packetId, String source, String destination, int sizeKB) {
        this.packetId      = packetId;
        this.source        = source;
        this.destination   = destination;
        this.sizeKB        = sizeKB;
        this.startTimeMs   = System.currentTimeMillis();
        this.routersPassed = 0;
        this.status        = "IN_TRANSIT";
        this.timeTakenMs   = 0;
        this.pathRecord    = new PacketPath();
    }

    @Override
    public String toString() {
        return "[" + packetId + " | " + source + " -> " + destination
                + " | " + sizeKB + "KB | " + status + "]";
    }
}