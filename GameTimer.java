public class GameTimer extends Thread {

    GameServer server;

    public GameTimer(GameServer server) {
        this.server = server;
    }

    public void run() {
        for (int t = GameServer.GAME_TIME; t >= 0; t--) {
            if (server.closed) return;
            server.broadcast("TIME " + t);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        server.endGame();
    }
}
