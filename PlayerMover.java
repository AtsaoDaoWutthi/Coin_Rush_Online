public class PlayerMover extends Thread {

    GamePanel panel;
    GameClient client;
    double speed = 250;

    public PlayerMover(GamePanel panel, GameClient client) {
        this.panel = panel;
        this.client = client;
    }

    public void run() {
        Player me = client.players[client.myId];
        double myX = me.x, myY = me.y;
        int sentX = me.x, sentY = me.y;
        long last = System.nanoTime();

        while (client.playing && !client.finished) {
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
            if (me.x != (int) Math.round(myX)) myX = me.x;
            if (me.y != (int) Math.round(myY)) myY = me.y;

            if (me.x != sentX || me.y != sentY) {
                client.send("MOVE " + me.x + " " + me.y);
                sentX = me.x;
                sentY = me.y;
            }

            for (int i = 0; i < client.coins.length; i++) {
                if (client.coins[i].isHit(me)) {
                    client.coins[i].hiddenUntil = System.currentTimeMillis() + 500;
                }
            }

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
