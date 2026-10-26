import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

// หน้า 4: ระหว่างเล่น (Gameplay)
// ตอนนี้เป็น "หน้าตา" อย่างเดียว ยังไม่ขยับ ยังไม่เก็บเหรียญ
public class GamePanel extends JPanel implements ActionListener {

    MainFrame frame;

    // ===== ข้อมูลตัวอย่าง =====
    String[] names = { "P1", "P2", "P3", "P4" };
    int[] scores   = { 5, 3, 7, 2 };
    int[] playerX  = { 120, 330, 450, 620 };
    int[] playerY  = { 150, 330, 230, 400 };
    int[] coinX    = { 60, 200, 260, 380, 520, 560, 640, 820, 840, 300, 150, 470 };
    int[] coinY    = { 330, 120, 420, 90, 340, 160, 300, 120, 420, 230, 450, 470 };
    int timeLeft   = 42;

    int barHeight = 50;   // ความสูงแถบคะแนนด้านบน

    // ปุ่มชั่วคราว ไว้ทดสอบไปหน้า Game Over (ลบออกเมื่อมีระบบจับเวลาจริง)
    JButton endButton = UI.button("จบเกม (ทดสอบ)", Color.DARK_GRAY, Color.WHITE);

    public GamePanel(MainFrame frame) {
        this.frame = frame;
        setLayout(null);
        endButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        endButton.setBounds(560, 10, 130, 30);
        add(endButton);
        endButton.addActionListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1) แผนที่ (ใต้แถบคะแนน)
        g.drawImage(UI.MAP, 0, barHeight, getWidth(), getHeight() - barHeight, this);

        // 2) เหรียญ
        for (int i = 0; i < coinX.length; i++) {
            g.drawImage(UI.COIN, coinX[i], barHeight + coinY[i], 24, 24, this);
        }

        // 3) ผู้เล่น + ป้ายชื่อเหนือหัว
        g.setFont(new Font("Tahoma", Font.BOLD, 14));
        for (int i = 0; i < names.length; i++) {
            int x = playerX[i];
            int y = barHeight + playerY[i];
            g.drawImage(UI.PLAYER_PICS[i], x, y, 40, 40, this);
            g.setColor(UI.PLAYER_COLORS[i]);
            g.fillRect(x + 4, y - 20, 32, 18);
            g.setColor(Color.WHITE);
            g.drawString(names[i], x + 10, y - 6);
        }

        // 4) แถบคะแนนด้านบน (วาดท้ายสุดให้อยู่บนสุด)
        g.setColor(UI.WOOD_DARK);
        g.fillRect(0, 0, getWidth(), barHeight);
        g.setFont(new Font("Tahoma", Font.BOLD, 20));
        for (int i = 0; i < names.length; i++) {
            int x = 15 + i * 130;
            g.setColor(UI.PLAYER_COLORS[i]);
            g.fillRect(x, 10, 40, 30);
            g.setColor(Color.WHITE);
            g.drawString(names[i], x + 6, 32);
            g.drawImage(UI.COIN, x + 50, 13, 24, 24, this);
            g.drawString("" + scores[i], x + 82, 32);
        }

        // เวลา มุมขวาบน
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.PLAIN, 14));
        g.drawString("เวลา", getWidth() - 110, 22);
        g.setFont(new Font("Tahoma", Font.BOLD, 26));
        g.drawString("" + timeLeft, getWidth() - 70, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == endButton) {
            frame.showPage(new GameOverPanel(frame));
        }
    }
}
