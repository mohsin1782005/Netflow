package netflow;

import java.util.HashMap;
import java.util.ArrayList;

// DS #3 - HashMap as routing table (O(1) lookup)
public class Network {

    public HashMap<String, RouterQueue> routers;
    public HashMap<String, String>      routingTable;
    public ArrayList<String>            routerIds;
    private EventLog                    eventLog;

    public Network(EventLog eventLog) {
        this.eventLog     = eventLog;
        this.routers      = new HashMap<>();
        this.routingTable = new HashMap<>();
        this.routerIds    = new ArrayList<>();
        setupRouters();
    }

    private void setupRouters() {
        String[] ids = {"Router-A", "Router-B", "Router-C", "Router-D", "Router-E"};
        for (String id : ids) {
            routers.put(id, new RouterQueue(id, 5));
            routerIds.add(id);
        }

        // routing rules - stored in HashMap for O(1) access
        routingTable.put("Router-A", "Router-B");
        routingTable.put("Router-B", "Router-D");
        routingTable.put("Router-C", "Router-E");
        routingTable.put("Router-D", "Router-E");
        routingTable.put("Router-E", "Router-A");

        eventLog.addEvent("Network ready: 5 routers set up");
    }

    // sends packet hop by hop using HashMap lookup + recursive backtracking
    public void sendPacket(Packet packet, String currentRouter,
                           ArrayList<String> visited, int depth) {

        // safety limit to stop infinite recursion
        if (depth > 10) {
            packet.status      = "DROPPED";
            packet.timeTakenMs = System.currentTimeMillis() - packet.startTimeMs;
            eventLog.addEvent("DROPPED [too many hops] " + packet.packetId);
            System.out.println("  DROPPED (too deep): " + packet.packetId);
            return;
        }

        // packet reached its destination
        if (currentRouter.equals(packet.destination)) {

            // DS #2 - Stack: push all routers visited, then pop in reverse (decapsulation)
            HeaderStack stack = new HeaderStack();
            for (String r : packet.pathRecord.getPath().split(" -> ")) stack.push(r);
            System.out.print("  Removing headers: ");
            while (!stack.isEmpty()) System.out.print(stack.pop() + " ");
            System.out.println();

            packet.status      = "DELIVERED";
            packet.timeTakenMs = System.currentTimeMillis() - packet.startTimeMs;
            eventLog.addEvent("DELIVERED " + packet.packetId
                    + " | routers passed: " + packet.routersPassed
                    + " | path: " + packet.pathRecord.getPath());
            System.out.println("  DELIVERED: " + packet.packetId
                    + " (" + packet.routersPassed + " routers, " + packet.timeTakenMs + "ms)");
            return;
        }

        // HashMap O(1) lookup - find which router to go to next
        String nextRouter = routingTable.get(currentRouter);

        if (nextRouter == null) {
            packet.status      = "DROPPED";
            packet.timeTakenMs = System.currentTimeMillis() - packet.startTimeMs;
            eventLog.addEvent("DROPPED [no path] " + packet.packetId);
            System.out.println("  DROPPED (no path): " + packet.packetId);
            return;
        }

        RouterQueue nextQueue = routers.get(nextRouter);

        if (nextQueue.isFull()) {
            // buffer full - try another router recursively
            System.out.println("  Buffer full at " + nextRouter + " - trying another...");
            eventLog.addEvent("OVERFLOW at " + nextRouter + " for " + packet.packetId);
            visited.add(currentRouter);

            for (String alt : routerIds) {
                if (!visited.contains(alt) && !alt.equals(packet.destination)
                        && !routers.get(alt).isFull()) {
                    packet.pathRecord.addRouter(alt);
                    packet.routersPassed++;
                    System.out.println("  Rerouting via " + alt);
                    sendPacket(packet, alt, visited, depth + 1); // recursive call
                    return;
                }
            }

            packet.status      = "DROPPED";
            packet.timeTakenMs = System.currentTimeMillis() - packet.startTimeMs;
            eventLog.addEvent("DROPPED [all full] " + packet.packetId);
            System.out.println("  DROPPED (all full): " + packet.packetId);

        } else {
            // normal flow - add to queue then move forward
            nextQueue.addToBuffer(packet);
            packet.pathRecord.addRouter(nextRouter);
            packet.routersPassed++;
            System.out.println("  " + packet.packetId + " -> " + nextRouter
                    + " [" + nextQueue.getCount() + "/" + nextQueue.getCapacity() + "]");
            nextQueue.removeFromBuffer();
            sendPacket(packet, nextRouter, visited, depth + 1); // recursive call
        }
    }

    // O(n) slow search - only used to compare speed with HashMap in report
    public String slowSearch(String routerId) {
        for (String key : routingTable.keySet()) {
            if (key.equals(routerId)) return routingTable.get(key);
        }
        return null;
    }
}