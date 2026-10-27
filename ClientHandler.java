import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// อยู่ฝั่ง Server: 1 ผู้เล่น = 1 ClientHandler = 1 Thread
// คอยรับข้อความจากผู้เล่นคนนั้นตลอดเวลา (เหมือน Thread "คนเฝ้าประตู" ใน ChatServer)
public class ClientHandler extends Thread {

    GameServer server;
    Socket socket;
    int id;
    BufferedReader in;
    PrintWriter out;

    public ClientHandler(GameServer server, Socket socket, int id) throws IOException {
        this.server = server;
        this.socket = socket;
        this.id = id;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                server.handle(id, line);
            }
        } catch (IOException e) {
            // สายหลุด
        } catch (RuntimeException e) {
            System.out.println("ข้อความผิดรูปแบบจาก P" + (id + 1));
        }
        // readLine() ได้ null หรือหลุด = ผู้เล่นออกจากห้อง
        server.removeClient(id);
        close();
    }

    public void send(String msg) {
        out.println(msg);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
