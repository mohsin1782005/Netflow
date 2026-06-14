package netflow;

import java.util.HashMap;
import java.util.ArrayList;

public class Network {

    public HashMap<String, RouterBuffer> routers;
    public HashMap<String, String> routingTable;
    public ArrayList<String> routerIds;
    private EventLog eventLog;
    public int linearSearchCount = 0;

    public Network(EventLog eventLog) {
        this.eventLog = eventLog;
        this.routers = new HashMap<>();
        this.routingTable = new HashMap<>();
        this.routerIds = new ArrayList<>();
        buildNetwork();
    }

    private void buildNetwork() {
        String[] ids = {"Router-A", "Router-B", "Router-C", "Router-D", "Router-E"};
        for (String id : ids) {
            routers.put(id, new RouterBuffer(id, 5));
            routerIds.add(id);
        }

        // HashMap routing table: destination -> next hop
        routingTable.put("Router-A", "Router-B");
        routingTable.put("Router-B", "Router-D");
        routingTable.put("Router-C", "Router-E");
        routingTable.put("Router-D", "Router-E");
        routingTable.put("Router-E", "Router-A");

        eventLog.addEvent("Network initialized with 5 routers");
    }

    // routes a packet hop by hop; uses recursive backtracking if a buffer is full
    public void routePacket(Packet packet, String currentRouterId,
                            ArrayList<String> visited, int depth) {

        if (depth > 10) {
            packet.status = "DROPPED";
            packet.travelTimeMs = System.currentTimeMillis() - packet.createdAtMs;
            eventLog.addEvent("DROPPED (depth limit): " + packet.packetId);
            System.out.println("  DROPPED (depth limit): " + packet.packetId);
            return;
        }

        if (currentRouterId.equals(packet.destination)) {
            // simulate header decapsulation using a stack
            PacketStack stack = new PacketStack();
            String[] trail = packet.auditTrail.getTrail().split(" -> ");
            for (String hop : trail) stack.push("HDR:" + hop);
            System.out.print("  Decapsulating: ");
            while (!stack.isEmpty()) System.out.print(stack.pop() + " ");
            System.out.println();

            packet.status = "DELIVERED";
            packet.travelTimeMs = System.currentTimeMillis() - packet.createdAtMs;
            eventLog.addEvent("DELIVERED " + packet.packetId
                    + " in " + packet.travelTimeMs + "ms | Hops: " + packet.hopCount
                    + " | Trail: " + packet.auditTrail.getTrail());
            System.out.println("  Delivered: " + packet.packetId
                    + " (" + packet.hopCount + " hops, " + packet.travelTimeMs + "ms)");
            return;
        }

        // HashMap lookup O(1)
        String nextHop = routingTable.get(currentRouterId);

        if (nextHop == null) {
            packet.status = "DROPPED";
            packet.travelTimeMs = System.currentTimeMillis() - packet.createdAtMs;
            eventLog.addEvent("DROPPED (no route): " + packet.packetId);
            System.out.println("  DROPPED (no route): " + packet.packetId);
            return;
        }

        RouterBuffer nextBuffer = routers.get(nextHop);

        if (nextBuffer.isFull()) {
            // buffer full - try another router recursively
            System.out.println("  Buffer full at " + nextHop + " - backtracking for " + packet.packetId);
            eventLog.addEvent("OVERFLOW at " + nextHop + " for " + packet.packetId);
            visited.add(currentRouterId);

            for (String altRouter : routerIds) {
                if (!visited.contains(altRouter)
                        && !altRouter.equals(packet.destination)
                        && !routers.get(altRouter).isFull()) {
                    packet.auditTrail.addHop(altRouter);
                    packet.hopCount++;
                    System.out.println("  Rerouting via " + altRouter);
                    routePacket(packet, altRouter, visited, depth + 1);
                    return;
                }
            }

            // no alternative found
            packet.status = "DROPPED";
            packet.travelTimeMs = System.currentTimeMillis() - packet.createdAtMs;
            eventLog.addEvent("DROPPED (all buffers full): " + packet.packetId);
            System.out.println("  DROPPED (all full): " + packet.packetId);

        } else {
            nextBuffer.enqueue(packet);
            packet.auditTrail.addHop(nextHop);
            packet.hopCount++;
            System.out.println("  " + packet.packetId + " -> " + nextHop
                    + " [" + nextBuffer.getCount() + "/" + nextBuffer.getCapacity() + "]");
            nextBuffer.dequeue();
            routePacket(packet, nextHop, visited, depth + 1);
        }
    }

    // linear search through routing table - used in report to compare with HashMap
    public String linearSearchRoute(String currentRouterId) {
        linearSearchCount++;
        for (String key : routingTable.keySet()) {
            if (key.equals(currentRouterId)) {
                return routingTable.get(key);
            }
        }
        return null;
    }
}