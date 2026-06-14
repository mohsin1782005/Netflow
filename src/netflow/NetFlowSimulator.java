package netflow;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class NetFlowSimulator {

    static final int TOTAL_PACKETS = 15;
    static final int PACKET_DELAY_MS = 400;

    static EventLog eventLog;
    static Network network;
    static int delivered = 0;
    static int dropped = 0;
    static ArrayList<Packet> allPackets = new ArrayList<>();
    static PrintWriter csvWriter;
    static Random random = new Random();
    static int packetCounter = 1;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("NetFlow - Network Packet Simulator");
        System.out.println("Muhammad Mohsin | L1F24BSSE0041");
        System.out.println("----------------------------------");

        eventLog = new EventLog();
        network = new Network(eventLog);
        openCsvFile();

        System.out.println("\nSimulation started:\n");

        // background thread automatically generates packets - no user input needed
        Thread packetGenerator = new Thread(() -> {
            for (int i = 0; i < TOTAL_PACKETS; i++) {
                String source, destination;
                do {
                    source = network.routerIds.get(random.nextInt(5));
                    destination = network.routerIds.get(random.nextInt(5));
                } while (source.equals(destination));

                int sizeKB = random.nextInt(10) + 1;
                Packet pkt = new Packet("P-" + packetCounter++, source, destination, sizeKB);
                allPackets.add(pkt);

                System.out.println("\nPacket " + pkt.packetId
                        + " | " + pkt.source + " -> " + pkt.destination
                        + " | " + pkt.sizeKB + "KB");

                pkt.auditTrail.addHop(pkt.source);
                network.routePacket(pkt, pkt.source, new ArrayList<>(), 0);

                if (pkt.status.equals("DELIVERED")) delivered++;
                else dropped++;

                writeToCsv(pkt);

                try { Thread.sleep(PACKET_DELAY_MS); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        });

        packetGenerator.start();
        packetGenerator.join();

        if (csvWriter != null) csvWriter.close();

        System.out.println("\nSimulation complete.\n");

        eventLog.printForward();
        System.out.println("\n(Reading log backward - Doubly Linked List traversal)");
        eventLog.printBackward();

        bubbleSort(allPackets);
        printSortedPackets();
        printAnalysisReport();
    }

    static void openCsvFile() {
        try {
            csvWriter = new PrintWriter(new FileWriter("simulation_log.csv"));
            csvWriter.println("packet_id,source,destination,size_kb,hops,travel_time_ms,status,path");
        } catch (IOException e) {
            System.out.println("Could not open CSV: " + e.getMessage());
        }
    }

    static void writeToCsv(Packet pkt) {
        if (csvWriter == null) return;
        csvWriter.println(pkt.packetId + "," + pkt.source + "," + pkt.destination + ","
                + pkt.sizeKB + "," + pkt.hopCount + "," + pkt.travelTimeMs + ","
                + pkt.status + ",\"" + pkt.auditTrail.getTrail() + "\"");
        csvWriter.flush();
    }

    static void printAnalysisReport() {
        double deliveryRate = (TOTAL_PACKETS > 0) ? (delivered * 100.0 / TOTAL_PACKETS) : 0;

        int totalHops = 0;
        long totalTime = 0;
        for (Packet p : allPackets) {
            totalHops += p.hopCount;
            totalTime += p.travelTimeMs;
        }
        double avgHops = (double) totalHops / TOTAL_PACKETS;
        double avgTime = (double) totalTime / TOTAL_PACKETS;

        // find the most congested router
        String bottleneck = "None";
        int peakLoad = 0;
        for (String id : network.routerIds) {
            RouterBuffer buf = network.routers.get(id);
            if (buf.peakLoad > peakLoad) {
                peakLoad = buf.peakLoad;
                bottleneck = id;
            }
        }

        // benchmark HashMap vs linear search
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) network.routingTable.get("Router-C");
        long hashMapNs = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) network.linearSearchRoute("Router-C");
        long linearNs = System.nanoTime() - start;

        System.out.println("\n--- Simulation Report ---");
        System.out.println("Total Packets : " + TOTAL_PACKETS);
        System.out.println("Delivered     : " + delivered);
        System.out.println("Dropped       : " + dropped);
        System.out.printf("Delivery Rate : %.1f%%%n", deliveryRate);
        System.out.printf("Avg Hops      : %.2f%n", avgHops);
        System.out.printf("Avg Time      : %.2f ms%n", avgTime);
        System.out.println("Events Logged : " + eventLog.getCount());
        System.out.println("Bottleneck    : " + bottleneck + " (peak: " + peakLoad + ")");
        System.out.println("\n--- HashMap vs Linear Search (1000 lookups each) ---");
        System.out.println("HashMap : " + hashMapNs + " ns");
        System.out.println("Linear  : " + linearNs + " ns");
        System.out.printf("HashMap is %.1fx faster%n", (linearNs > 0 ? (double) linearNs / hashMapNs : 1.0));
        System.out.println("\nData Structures Used:");
        System.out.println("  1. Queue      - RouterBuffer");
        System.out.println("  2. Stack      - PacketStack");
        System.out.println("  3. HashMap    - Routing Table");
        System.out.println("  4. Singly LL  - PathAuditTrail");
        System.out.println("  5. Doubly LL  - EventLog");
        System.out.println("\nLog saved to: simulation_log.csv");
    }

    // bubble sort - sorts packets by hop count (low to high)
    static void bubbleSort(ArrayList<Packet> packets) {
        int n = packets.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (packets.get(j).hopCount > packets.get(j + 1).hopCount) {
                    Packet temp = packets.get(j);
                    packets.set(j, packets.get(j + 1));
                    packets.set(j + 1, temp);
                }
            }
        }
    }

    static void printSortedPackets() {
        System.out.println("\n--- Packets Sorted by Hop Count (Bubble Sort) ---");
        for (Packet p : allPackets) {
            System.out.println(p.packetId + " | hops: " + p.hopCount
                    + " | status: " + p.status
                    + " | path: " + p.auditTrail.getTrail());
        }
    }
}