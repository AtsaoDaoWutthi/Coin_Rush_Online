import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.util.Random;

// เหรียญ 1 เหรียญ (แบบเดียวกับ Ghost: move = สุ่มที่ใหม่, isHit = เช็คการชน)
public class Coin {

    int x, y;
    int size = 24;
    long hiddenUntil = 0;   // ฝั่งผู้เล่น: ซ่อนเหรียญที่เราเพิ่งเดินชน ระหว่างรอ Server ตอบ (เหรียญไม่ค้าง)
    Random random = new Random();

    // สุ่มตำแหน่งใหม่บนแผนที่
    public void move() {
        x = random.nextInt(GameServer.MAP_W - size);
        y = random.nextInt(GameServer.MAP_H - size);
    }

    public void draw(Graphics g, int offsetY, ImageObserver observer) {
        if (System.currentTimeMillis() < hiddenUntil) return;
        g.drawImage(UI.COIN, x, offsetY + y, size, size, observer);
    }

    // ผู้เล่นทับเหรียญหรือไม่ (สี่เหลี่ยม 2 อันซ้อนกัน)
    public boolean isHit(Player p) {
        return p.x < x + size && p.x + p.size > x
            && p.y < y + size && p.y + p.size > y;
    }
}
