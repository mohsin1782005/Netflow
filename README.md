# NetFlow — Network Packet Simulator

NetFlow is a high-performance, console-based Java application that simulates real-time data packet routing across a network of virtual routers. Built entirely from scratch without external libraries or Java's built-in collections framework, this project demonstrates the practical application of core Data Structures and Algorithms (DSA) in solving fundamental networking challenges like buffer management, packet encapsulation, routing efficiency, and path tracking.

---

## 🚀 Key Features
* **Custom Data Structures:** Implements all 5 foundational data structures completely from scratch.
* **Autonomous Packet Generation:** Simulates realistic network traffic via a background Java Thread.
* **Smart Rerouting:** Employs recursive backtracking to dynamically reroute packets when primary router buffers are full.
* **Bidirectional Event Logging:** Captures all simulation activities in a custom Doubly Linked List for chronological or reverse inspection.
* **Performance Benchmarking:** Includes a terminal report measuring $O(1)$ HashMap routing tables against $O(n)$ linear searches using `System.nanoTime()`.
* **CSV Reporting:** Automatically exports detailed packet-level performance logs to a `simulation_log.csv` file upon completion.

---

## 🛠️ Data Structures Architecture

| Data Structure | Implementation Class | Role in NetFlow Simulation |
| :--- | :--- | :--- |
| **Queue** | `RouterQueue.java` | Acts as the router buffer. Implemented via a circular array with index wrapping (`%`) to manage packet waiting lines (Max capacity: 5). |
| **Stack** | `HeaderStack.java` | Models OSI layer packet encapsulation. Router IDs are pushed (`++top`) during transit and popped (`top--`) in LIFO order upon delivery. |
| **HashMap** | `Network.java` | Functions as the routing table for instant $O(1)$ next-hop lookups mapped to destination keys. |
| **Singly Linked List** | `PacketPath.java` | Bound to individual packets to record their dynamic traversal path node-by-node (`Router-A -> Router-B`). |
| **Doubly Linked List** | `EventLog.java` | Maintains the global simulation diary. Supports bidirectional traversal (`next`/`prev`) to view logs forward or backward. |

---

## 🧠 Algorithms Implemented

### 1. Recursive Routing (`sendPacket`)
Moves data packets hop-by-hop toward their destinations. Each recursive call represents a single network hop. It includes a depth guard ($depth > 10$) to handle base cases safely and avoid infinite routing loops.

### 2. Backtracking (Alternate Path Selection)
Triggered instantly when a preferred router's buffer (`RouterQueue`) is full. The algorithm tracks visited routers and backtracks through a looping mechanism to locate an available alternative path rather than dropping the packet prematurely.

---

## 💻 How to Setup and Run

### Prerequisites
* Java JDK 11 or above (JDK 23 recommended)
* IntelliJ IDEA (Community or Ultimate Edition)

### Step-by-Step Installation
1. Clone this repository to your local machine:
   ```bash
   git clone [https://github.com/mohsin1782005/Netflow.git)
