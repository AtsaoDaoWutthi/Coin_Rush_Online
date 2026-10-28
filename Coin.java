import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.util.Random;

public class Coin {

    int x, y;
    int size = 24;
    long hiddenUntil = 0;
    Random random = new Random();

    public void move() {
        x = random.nextInt(GameServer.MAP_W - size);
        y = random.nextInt(GameServer.MAP_H - size);
    }

    public void draw(Graphics g, int offsetY, ImageObserver observer) {
        if (System.currentTimeMillis() < hiddenUntil) return;
        g.drawImage(UI.COIN, x, offsetY + y, size, size, observer);
    }

    public boolean isHit(Player p) {
        return p.x < x + size && p.x + p.size > x
            && p.y < y + size && p.y + p.size > y;
    }
}
