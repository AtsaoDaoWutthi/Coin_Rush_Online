// Thread ขยับตัวละครของเรา + วาดจอใหม่ทุก 20 ms (แบบ RainLetter บทที่ 6: while + sleep)
public class PlayerMover extends Thread {

    GamePanel panel;
    GameClient client;
    int speed = 5;   // พิกเซลต่อรอบ

    public PlayerMover(GamePanel panel, GameClient client) {
        this.panel = panel;
        this.client = client;
    }

    public void run() {
        while (!client.finished) {
            Player me = client.players[client.myId];
            int oldX = me.x, oldY = me.y;

            if (panel.up)    me.y -= speed;
            if (panel.down)  me.y += speed;
            if (panel.left)  me.x -= speed;
            if (panel.right) me.x += speed;
            me.keepInMap();

            // ขยับจริงค่อยส่งบอก Server (Server จะเช็คเหรียญให้)
            if (me.x != oldX || me.y != oldY) {
                client.send("MOVE " + me.x + " " + me.y);
            }

            panel.repaint();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
            }
        }
    }
}
