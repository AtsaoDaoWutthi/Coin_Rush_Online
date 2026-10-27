import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

// หน้า 4: ระหว่างเล่น (Gameplay)
// กดลูกศร / WASD เดิน -> PlayerMover (Thread) ขยับตัวเรา ส่งตำแหน่งให้ Server แล้ววาดจอใหม่
// ข้อมูลคนอื่น เหรียญ คะแนน เวลา มาจาก Server ผ่าน GameClient
public class GamePanel extends JPanel implements KeyListener {

    MainFrame frame;
    GameClient client;

    int barHeight = 50;   // ความสูงแถบคะแนนด้านบน

    // ปุ่มที่กดค้างอยู่ (PlayerMover อ่านค่าพวกนี้)
    boolean up, down, left, right;

    public GamePanel(MainFrame frame, GameClient client) {
        this.frame = frame;
        this.client = client;
        setLayout(null);
        setFocusable(true);       // ให้รับคีย์บอร์ดได้
        addKeyListener(this);
        new PlayerMover(this, client).start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1) แผนที่ (ใต้แถบคะแนน)
        g.drawImage(UI.MAP, 0, barHeight, getWidth(), getHeight() - barHeight, this);

        // 2) เหรียญ
        for (int i = 0; i < client.coins.length; i++) {
            client.coins[i].draw(g, barHeight, this);
        }

        // 3) ผู้เล่น + ป้ายชื่อเหนือหัว
        for (int i = 0; i < client.players.length; i++) {
            client.players[i].draw(g, barHeight, this);
        }

        // 4) แถบคะแนนด้านบน (วาดท้ายสุดให้อยู่บนสุด)
        g.setColor(UI.WOOD_DARK);
        g.fillRect(0, 0, getWidth(), barHeight);
        g.setFont(new Font("Tahoma", Font.BOLD, 20));
        for (int i = 0; i < client.players.length; i++) {
            Player p = client.players[i];
            if (!p.joined) continue;
            int x = 15 + i * 130;
            g.setColor(UI.PLAYER_COLORS[i]);
            g.fillRect(x, 10, 40, 30);
            g.setColor(Color.WHITE);
            g.drawString(p.getName(), x + 6, 32);
            g.drawImage(UI.COIN, x + 50, 13, 24, 24, this);
            g.drawString("" + p.score, x + 82, 32);
        }

        // เวลา มุมขวาบน
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.PLAIN, 14));
        g.drawString("เวลา", getWidth() - 110, 22);
        g.setFont(new Font("Tahoma", Font.BOLD, 26));
        g.drawString("" + client.timeLeft, getWidth() - 70, 40);
    }

    // ===== คีย์บอร์ด: กด = true, ปล่อย = false =====
    @Override
    public void keyPressed(KeyEvent e) {
        setKey(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        setKey(e.getKeyCode(), false);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void setKey(int code, boolean pressed) {
        if (code == KeyEvent.VK_UP    || code == KeyEvent.VK_W) up = pressed;
        if (code == KeyEvent.VK_DOWN  || code == KeyEvent.VK_S) down = pressed;
        if (code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) left = pressed;
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) right = pressed;
    }
}
