package netflow;

public class Packet {

    public String packetId;
    public String source;
    public String destination;
    public int sizeKB;
    public long createdAtMs;
    public int hopCount;
    public String status;
    public long travelTimeMs;
    public PathAuditTrail auditTrail;

    public Packet(String packetId, String source, String destination, int sizeKB) {
        this.packetId = packetId;
        this.source = source;
        this.destination = destination;
        this.sizeKB = sizeKB;
        this.createdAtMs = System.currentTimeMillis();
        this.hopCount = 0;
        this.status = "IN_TRANSIT";
        this.travelTimeMs = 0;
        this.auditTrail = new PathAuditTrail();
    }

    @Override
    public String toString() {
        return "[" + packetId + " | " + source + " -> " + destination
                + " | " + sizeKB + "KB | " + status + "]";
    }
}