package netflow;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class NetFlowSimulator {

    static final int TOTAL_PACKETS   = 15;
    static final int DELAY_MS        = 400;

    static EventLog  eventLog;
    static Network   network;
    static int       delivered    = 0;
    static int       dropped      = 0;
    static ArrayList<Packet> allPackets = new ArrayList<>();
    static PrintWriter csvWriter;
    static Random  random        = new Random();
    static int     packetCounter = 1;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=".repeat(50));
        System.out.println("  NetFlow - Network Packet Simulator");
        System.out.println("  DSA Project | Muhammad Mohsin | L1F24BSSE0041");
        System.out.println("=".repeat(50));

        eventLog = new EventLog();
        network  = new Network(eventLog);
        openCsvFile();

        System.out.println("\nSimulation starting...\n");
        System.out.println("-".repeat(50));

        // Thread auto-generates packets without user input
        Thread packetGenerator = new Thread(() -> {
            for (int i = 0; i < TOTAL_PACKETS; i++) {

                String source, destination;
                do {
                    source      = network.routerIds.get(random.nextInt(5));
                    destination = network.routerIds.get(random.nextInt(5));
                } while (source.equals(destination));

                int sizeKB = random.nextInt(10) + 1;
                Packet pkt = new Packet("P-" + packetCounter++, source, destination, sizeKB);
                allPackets.add(pkt);

                System.out.println("\nPacket " + pkt.packetId
                        + " | " + pkt.source + " -> " + pkt.destination
                        + " | " + pkt.sizeKB + "KB");

                pkt.pathRecord.addRouter(pkt.source);
                network.sendPacket(pkt, pkt.source, new ArrayList<>(), 0);

                if (pkt.status.equals("DELIVERED")) delivered++;
                else dropped++;

                writeToCsv(pkt);

                try { Thread.sleep(DELAY_MS); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        });

        packetGenerator.start();
        packetGenerator.join();

        if (csvWriter != null) csvWriter.close();

        System.out.println("\n" + "-".repeat(50));
        System.out.println("Simulation done.\n");

        // show event log forward then backward (Doubly Linked List feature)
        eventLog.printForward();
        System.out.println("\n-- Same log read BACKWARD (Doubly Linked List) --");
        eventLog.printBackward();

        printReport();
    }

    static void openCsvFile() {
        try {
            csvWriter = new PrintWriter(new FileWriter("simulation_log.csv"));
            csvWriter.println("packet_id,source,destination,size_kb,routers_passed,time_ms,status,path");
        } catch (IOException e) {
            System.out.println("CSV error: " + e.getMessage());
        }
    }

    static void writeToCsv(Packet pkt) {
        if (csvWriter == null) return;
        csvWriter.println(pkt.packetId + "," + pkt.source + "," + pkt.destination + ","
                + pkt.sizeKB + "," + pkt.routersPassed + "," + pkt.timeTakenMs + ","
                + pkt.status + ",\"" + pkt.pathRecord.getPath() + "\"");
        csvWriter.flush();
    }

    static void printReport() {

        double deliveryRate = delivered * 100.0 / TOTAL_PACKETS;

        int  totalRouters = 0;
        long totalTime    = 0;
        for (Packet p : allPackets) {
            totalRouters += p.routersPassed;
            totalTime    += p.timeTakenMs;
        }

        // HashMap O(1) vs Linear Search O(n) benchmark
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) network.routingTable.get("Router-C");
        long hashMapTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) network.slowSearch("Router-C");
        long linearTime = System.nanoTime() - start;

        System.out.println("\n--- SIMULATION REPORT ---");
        System.out.printf("Total Packets  : %d%n", TOTAL_PACKETS);
        System.out.printf("Delivered      : %d%n", delivered);
        System.out.printf("Dropped        : %d%n", dropped);
        System.out.printf("Delivery Rate  : %.1f%%%n", deliveryRate);
        System.out.println("-------------------------");
        System.out.printf("Avg Routers    : %.2f%n", (double) totalRouters / TOTAL_PACKETS);
        System.out.printf("Avg Time       : %.2f ms%n", (double) totalTime / TOTAL_PACKETS);
        System.out.printf("Events Logged  : %d%n", eventLog.getCount());
        System.out.println("-------------------------");
        System.out.println("HashMap vs Linear Search (1000 runs):");
        System.out.printf("  HashMap : %d ns%n", hashMapTime);
        System.out.printf("  Linear  : %d ns%n", linearTime);
        System.out.printf("  Speedup : %.1fx faster%n",
                linearTime > 0 ? (double) linearTime / hashMapTime : 1.0);
        System.out.println("-------------------------");
        System.out.println("Data Structures:");
        System.out.println("  #1 Queue       -> RouterQueue");
        System.out.println("  #2 Stack       -> HeaderStack");
        System.out.println("  #3 HashMap     -> Routing Table");
        System.out.println("  #4 Singly LL   -> PacketPath");
        System.out.println("  #5 Doubly LL   -> EventLog");
        System.out.println("CSV saved: simulation_log.csv");
    }
}