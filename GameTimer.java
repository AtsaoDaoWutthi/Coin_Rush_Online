// Thread นับเวลาถอยหลังของ Server (ไม่ใช้ Timer ตามที่อาจารย์กำหนด แบบเดียวกับ StopWatch บทที่ 6)
public class GameTimer extends Thread {

    GameServer server;

    public GameTimer(GameServer server) {
        this.server = server;
    }

    public void run() {
        for (int t = GameServer.GAME_TIME; t >= 0; t--) {
            if (server.closed) return;
            server.broadcast("TIME " + t);   // บอกเวลาที่เหลือให้ทุกเครื่อง
            try {
                Thread.sleep(1000);          // รอ 1 วินาที
            } catch (InterruptedException e) {
            }
        }
        server.endGame();                    // หมดเวลา
    }
}
