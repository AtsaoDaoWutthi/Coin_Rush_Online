// Thread ขยับตัวละครของเรา + วาดจอใหม่ประมาณ 60 ครั้งต่อวินาที (แบบ RainLetter บทที่ 6: while + sleep)
// ทำงานเฉพาะระหว่างเล่น 1 รอบ (หมดเวลาแล้ว Thread นี้จบเอง รอบใหม่สร้างใหม่)
public class PlayerMover extends Thread {

    GamePanel panel;
    GameClient client;
    double speed = 250;   // พิกเซลต่อวินาที

    public PlayerMover(GamePanel panel, GameClient client) {
        this.panel = panel;
        this.client = client;
    }

    public void run() {
        Player me = client.players[client.myId];
        double myX = me.x, myY = me.y;      // เก็บเป็นทศนิยม เดินได้ละเอียด
        int sentX = me.x, sentY = me.y;     // ตำแหน่งล่าสุดที่ส่งให้ Server
        long last = System.nanoTime();

        while (client.playing && !client.finished) {
            // ระยะเดิน = ความเร็ว x เวลาที่ผ่านไปจริง
            // (sleep บน Windows นอนจริงไม่เท่ากันทุกรอบ ถ้าเดินรอบละ 5 px คงที่ จะเร็วช้าไม่สม่ำเสมอ)
            long now = System.nanoTime();
            double step = speed * (now - last) / 1000000000.0;
            last = now;

            if (panel.up)    myY -= step;
            if (panel.down)  myY += step;
            if (panel.left)  myX -= step;
            if (panel.right) myX += step;
            me.x = (int) Math.round(myX);
            me.y = (int) Math.round(myY);
            me.keepInMap();
            if (me.x != (int) Math.round(myX)) myX = me.x;   // ชนขอบแผนที่
            if (me.y != (int) Math.round(myY)) myY = me.y;

            // ขยับจริงค่อยส่งบอก Server (Server จะเช็คเหรียญให้)
            if (me.x != sentX || me.y != sentY) {
                client.send("MOVE " + me.x + " " + me.y);
                sentX = me.x;
                sentY = me.y;
            }

            // เหรียญที่เราทับอยู่ ซ่อนไว้ก่อนเลยไม่ต้องรอ Server (ถ้า Server ไม่นับ อีก 0.5 วิ เหรียญจะโผล่กลับมา)
            for (int i = 0; i < client.coins.length; i++) {
                if (client.coins[i].isHit(me)) {
                    client.coins[i].hiddenUntil = System.currentTimeMillis() + 500;
                }
            }

            // ผู้เล่นคนอื่น: ค่อยๆ เลื่อนไปหาตำแหน่งที่ Server บอก
            for (int i = 0; i < client.players.length; i++) {
                if (i != client.myId) client.players[i].smoothMove();
            }

            panel.repaint();
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
            }
        }
    }
}
