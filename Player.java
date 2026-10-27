import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.ImageObserver;

// ผู้เล่น 1 คน (คล้ายคลาส Ghost ใน Lab บทที่ 7: มีตำแหน่ง ขนาด และวาดตัวเองได้)
public class Player {

    int id;              // 0-3 (ใช้เลือกรูป/สี)
    int x, y;            // ตำแหน่งบนแผนที่ (ไม่รวมแถบคะแนนด้านบน)
    int size = 40;
    int score = 0;
    boolean joined = false;   // เข้าห้องแล้วหรือยัง
    boolean ready = false;    // กดพร้อมแล้วหรือยัง

    public Player(int id) {
        this.id = id;
    }

    public String getName() {
        return "P" + (id + 1);
    }

    // จุดเกิดของแต่ละคน (4 มุมแผนที่)
    public void moveToStart() {
        int[] startX = { 60, GameServer.MAP_W - 100, 60, GameServer.MAP_W - 100 };
        int[] startY = { 60, 60, GameServer.MAP_H - 100, GameServer.MAP_H - 100 };
        x = startX[id];
        y = startY[id];
    }

    // ไม่ให้เดินออกนอกแผนที่
    public void keepInMap() {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > GameServer.MAP_W - size) x = GameServer.MAP_W - size;
        if (y > GameServer.MAP_H - size) y = GameServer.MAP_H - size;
    }

    // วาดตัวละคร + ป้ายชื่อเหนือหัว (offsetY = ความสูงแถบคะแนน)
    public void draw(Graphics g, int offsetY, ImageObserver observer) {
        if (!joined) return;
        int drawY = offsetY + y;
        g.drawImage(UI.PLAYER_PICS[id], x, drawY, size, size, observer);
        g.setColor(UI.PLAYER_COLORS[id]);
        g.fillRect(x + 4, drawY - 20, 32, 18);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, 14));
        g.drawString(getName(), x + 10, drawY - 6);
    }
}
