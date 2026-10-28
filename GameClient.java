import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GameClient extends Thread {

    MainFrame frame;
    Socket socket;
    String ip;
    BufferedReader in;
    PrintWriter out;
    GameServer server;

    int myId = -1;
    String roomName = "";
    int maxPlayers = 4;
    Player[] players = new Player[4];
    Coin[] coins = new Coin[GameServer.COIN_COUNT];
    int timeLeft = GameServer.GAME_TIME;
    volatile boolean playing = false;
    volatile boolean finished = false;
    WaitingPanel waitingPage;

    public GameClient(MainFrame frame, String ip) throws IOException {
        this.frame = frame;
        this.ip = ip;
        socket = new Socket(ip, GameServer.PORT);
        socket.setTcpNoDelay(true);
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
        }
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
            roomName = line.substring(line.indexOf(' ', 5) + 1);
            refreshWaiting();
        } else if (cmd.equals("PLAYERS")) {
            for (int i = 0; i < 4; i++) {
                players[i].joined = part[1 + i * 2].equals("1");
                players[i].ready  = part[2 + i * 2].equals("1");
            }
            refreshWaiting();
        } else if (cmd.equals("POS")) {
            int i = Integer.parseInt(part[1]);
            int px = Integer.parseInt(part[2]);
            int py = Integer.parseInt(part[3]);
            if (i != myId || !playing) {
                players[i].targetX = px;
                players[i].targetY = py;
                if (!playing) {
                    players[i].x = px;
                    players[i].y = py;
                }
            }
        } else if (cmd.equals("COIN")) {
            int i = Integer.parseInt(part[1]);
            coins[i].x = Integer.parseInt(part[2]);
            coins[i].y = Integer.parseInt(part[3]);
            coins[i].hiddenUntil = 0;
        } else if (cmd.equals("SCORE")) {
            players[Integer.parseInt(part[1])].score = Integer.parseInt(part[2]);
        } else if (cmd.equals("TIME")) {
            timeLeft = Integer.parseInt(part[1]);
        } else if (cmd.equals("START")) {
            playing = true;
            waitingPage = null;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    frame.showPage(new GamePanel(frame, GameClient.this));
                }
            });
        } else if (cmd.equals("OVER")) {
            playing = false;
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

    public void close() {
        finished = true;
        try {
            socket.close();
        } catch (IOException e) {
        }
        if (server != null) server.close();
    }
}
