import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Server ของห้อง (ทำงานบนเครื่องคนที่กด "สร้างห้อง")
// เป็น Thread ที่คอย accept() รอคนเข้าห้องตลอดเวลา (ตามบทที่ 8: accept() รอจนมีคนต่อ จึงต้องแยก Thread)
// Server เป็นคนตัดสินทุกอย่าง: ใครเก็บเหรียญได้ คะแนน และเวลา แล้วส่งบอกทุกเครื่อง
public class GameServer extends Thread {

    public static final int PORT = 50101;
    public static final int MAP_W = 880;     // ขนาดแผนที่ (ใต้แถบคะแนน)
    public static final int MAP_H = 500;
    public static final int COIN_COUNT = 12;
    public static final int GAME_TIME = 60;  // วินาที

    String roomName;
    int maxPlayers;
    ServerSocket serverSocket;
    ClientHandler[] clients = new ClientHandler[4];
    Player[] players = new Player[4];
    Coin[] coins = new Coin[COIN_COUNT];
    boolean playing = false;
    boolean closed = false;

    public GameServer(String roomName, int maxPlayers) throws IOException {
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        serverSocket = new ServerSocket(PORT);   // เปิดพอร์ตไม่ได้ (เช่นเปิดซ้ำ) จะ throw ตรงนี้
        for (int i = 0; i < 4; i++) players[i] = new Player(i);
        for (int i = 0; i < COIN_COUNT; i++) coins[i] = new Coin();
    }

    // Thread รอรับคนเข้าห้อง
    public void run() {
        System.out.println("Server เปิดรอที่พอร์ต " + PORT + " ...");
        while (!closed) {
            try {
                Socket socket = serverSocket.accept();   // รอจนมีคนต่อเข้ามา
                addClient(socket);
            } catch (IOException e) {
                // serverSocket ถูกปิด (close) จะหลุดออกจาก accept() มาที่นี่
            }
        }
    }

    // ===== เมธอดด้านล่างเป็น synchronized: หลาย Thread เรียกพร้อมกันได้ แต่จะทำทีละคน ข้อมูลไม่ตีกัน =====

    synchronized void addClient(Socket socket) throws IOException {
        // หาที่ว่าง
        int id = -1;
        for (int i = 0; i < maxPlayers; i++) {
            if (clients[i] == null) { id = i; break; }
        }
        ClientHandler handler = new ClientHandler(this, socket, id);
        if (id == -1 || playing) {
            handler.send("FULL");   // ห้องเต็ม หรือเริ่มเล่นไปแล้ว
            socket.close();
            return;
        }
        clients[id] = handler;
        players[id].joined = true;
        players[id].ready = (id == 0);   // เจ้าของห้อง (คนแรก) พร้อมเสมอ
        players[id].score = 0;
        handler.send("WELCOME " + id);
        handler.send("ROOM " + maxPlayers + " " + roomName);
        handler.start();
        broadcastPlayers();
        System.out.println("ผู้เล่น P" + (id + 1) + " เข้าห้อง: " + socket.getInetAddress());
    }

    synchronized void removeClient(int id) {
        clients[id] = null;
        players[id].joined = false;
        players[id].ready = false;
        broadcastPlayers();
        System.out.println("ผู้เล่น P" + (id + 1) + " ออกจากห้อง");
    }

    // ข้อความที่ส่งมาจากผู้เล่นคนที่ id
    synchronized void handle(int id, String line) {
        String[] part = line.split(" ");
        if (part[0].equals("READY") && !playing) {
            if (id != 0) players[id].ready = !players[id].ready;
            broadcastPlayers();
        } else if (part[0].equals("START") && id == 0 && !playing && allReady()) {
            startGame();
        } else if (part[0].equals("MOVE") && playing) {
            Player p = players[id];
            p.x = Integer.parseInt(part[1]);
            p.y = Integer.parseInt(part[2]);
            p.keepInMap();
            broadcast("POS " + id + " " + p.x + " " + p.y);
            // เช็คว่าเก็บเหรียญได้ไหม
            for (int i = 0; i < COIN_COUNT; i++) {
                if (coins[i].isHit(p)) {
                    p.score++;
                    coins[i].move();   // เหรียญไปเกิดที่ใหม่
                    broadcast("SCORE " + id + " " + p.score);
                    broadcast("COIN " + i + " " + coins[i].x + " " + coins[i].y);
                }
            }
        }
    }

    boolean allReady() {
        for (int i = 0; i < 4; i++) {
            if (players[i].joined && !players[i].ready) return false;
        }
        return true;
    }

    void startGame() {
        playing = true;
        for (int i = 0; i < 4; i++) {
            players[i].score = 0;
            players[i].moveToStart();
            broadcast("POS " + i + " " + players[i].x + " " + players[i].y);
            broadcast("SCORE " + i + " 0");
        }
        for (int i = 0; i < COIN_COUNT; i++) {
            coins[i].move();
            broadcast("COIN " + i + " " + coins[i].x + " " + coins[i].y);
        }
        broadcast("TIME " + GAME_TIME);
        broadcast("START");
        new GameTimer(this).start();   // Thread นับเวลาถอยหลัง
    }

    synchronized void endGame() {
        playing = false;
        broadcast("OVER");
        // ทุกคนยังอยู่ในห้อง แต่ต้องกด "พร้อม" ใหม่ก่อนเริ่มรอบต่อไป
        for (int i = 1; i < 4; i++) players[i].ready = false;
        broadcastPlayers();
    }

    // ส่งสถานะในห้องรอ: เข้าแล้ว/พร้อม ของทั้ง 4 ช่อง (1 = ใช่, 0 = ไม่)
    void broadcastPlayers() {
        String msg = "PLAYERS";
        for (int i = 0; i < 4; i++) {
            msg += " " + (players[i].joined ? 1 : 0) + " " + (players[i].ready ? 1 : 0);
        }
        broadcast(msg);
    }

    // ส่งข้อความหาทุกคนในห้อง
    synchronized void broadcast(String msg) {
        for (int i = 0; i < 4; i++) {
            if (clients[i] != null) clients[i].send(msg);
        }
    }

    // ปิดห้อง
    public void close() {
        closed = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
        for (int i = 0; i < 4; i++) {
            if (clients[i] != null) clients[i].close();
        }
    }
}
