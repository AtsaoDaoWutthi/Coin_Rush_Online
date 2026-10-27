import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

// ฝั่งผู้เล่น (ทุกเครื่องมี รวมถึงเจ้าของห้องด้วย ซึ่งต่อเข้า 127.0.0.1 ของตัวเอง)
// เป็น Thread คอยรับข้อความจาก Server ตลอดเวลา (เหมือน receiver ใน ChatClient บทที่ 8)
// และเก็บข้อมูลเกมล่าสุดไว้ให้หน้าต่างๆ เอาไปวาด
public class GameClient extends Thread {

    MainFrame frame;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    GameServer server;          // มีค่าเฉพาะเครื่องเจ้าของห้อง (ไว้ปิดห้องตอนออก)

    // ===== ข้อมูลเกมที่ได้จาก Server =====
    int myId = -1;
    String roomName = "";
    int maxPlayers = 4;
    Player[] players = new Player[4];
    Coin[] coins = new Coin[GameServer.COIN_COUNT];
    int timeLeft = GameServer.GAME_TIME;
    boolean playing = false;
    boolean finished = false;   // จบเกม/ออกจากห้องแล้ว
    WaitingPanel waitingPage;   // หน้าห้องรอที่เปิดอยู่ (ไว้สั่งอัปเดตรายชื่อ)

    public GameClient(MainFrame frame, String ip) throws IOException {
        this.frame = frame;
        socket = new Socket(ip, GameServer.PORT);   // ต่อไปหา Server (ต่อไม่ได้จะ throw)
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        for (int i = 0; i < 4; i++) players[i] = new Player(i);
        for (int i = 0; i < coins.length; i++) coins[i] = new Coin();
    }

    public void send(String msg) {
        out.println(msg);
    }

    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                handle(line);
            }
        } catch (IOException e) {
            // เราปิด socket เอง หรือสายหลุด
        }
        // Server ปิด / สายหลุด ระหว่างอยู่ในห้องหรือในเกม -> กลับหน้าหลัก
        if (!finished) {
            finished = true;
            backToMenu("หลุดการเชื่อมต่อจากห้อง");
        }
    }

    void handle(String line) {
        String[] part = line.split(" ");
        String cmd = part[0];

        if (cmd.equals("WELCOME")) {
            myId = Integer.parseInt(part[1]);
        } else if (cmd.equals("ROOM")) {
            maxPlayers = Integer.parseInt(part[1]);
            roomName = line.substring(line.indexOf(' ', 5) + 1);   // ชื่อห้องอาจมีเว้นวรรค
            refreshWaiting();
        } else if (cmd.equals("PLAYERS")) {
            for (int i = 0; i < 4; i++) {
                players[i].joined = part[1 + i * 2].equals("1");
                players[i].ready  = part[2 + i * 2].equals("1");
            }
            refreshWaiting();
        } else if (cmd.equals("POS")) {
            int i = Integer.parseInt(part[1]);
            // ตัวเราเองขยับในเครื่องเราอยู่แล้ว ระหว่างเล่นไม่ต้องรับตำแหน่งตัวเองกลับมา
            if (i != myId || !playing) {
                players[i].x = Integer.parseInt(part[2]);
                players[i].y = Integer.parseInt(part[3]);
            }
        } else if (cmd.equals("COIN")) {
            int i = Integer.parseInt(part[1]);
            coins[i].x = Integer.parseInt(part[2]);
            coins[i].y = Integer.parseInt(part[3]);
        } else if (cmd.equals("SCORE")) {
            players[Integer.parseInt(part[1])].score = Integer.parseInt(part[2]);
        } else if (cmd.equals("TIME")) {
            timeLeft = Integer.parseInt(part[1]);
        } else if (cmd.equals("START")) {
            playing = true;
            waitingPage = null;
            // เปลี่ยนหน้าจอต้องทำใน Thread ของ Swing จึงใช้ invokeLater
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    frame.showPage(new GamePanel(frame, GameClient.this));
                }
            });
        } else if (cmd.equals("OVER")) {
            playing = false;
            finished = true;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    frame.showPage(new GameOverPanel(frame, GameClient.this));
                }
            });
        } else if (cmd.equals("FULL")) {
            finished = true;
            backToMenu("ห้องเต็ม หรือเริ่มเกมไปแล้ว");
        }
    }

    void refreshWaiting() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (waitingPage != null) waitingPage.refresh();
            }
        });
    }

    void backToMenu(final String message) {
        close();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(frame, message);
                frame.showPage(new MenuPanel(frame));
            }
        });
    }

    // ออกจากห้อง (ถ้าเป็นเจ้าของห้อง ปิด Server ด้วย)
    public void close() {
        finished = true;
        try {
            socket.close();
        } catch (IOException e) {
        }
        if (server != null) server.close();
    }
}
