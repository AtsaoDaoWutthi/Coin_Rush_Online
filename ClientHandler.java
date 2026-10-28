import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
        socket.setTcpNoDelay(true);
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
        } catch (RuntimeException e) {
            System.out.println("ข้อความผิดรูปแบบจาก P" + (id + 1));
        }
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
