import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

// หน้า 5: จบเกม (Game Over) ตารางอันดับ
public class GameOverPanel extends JPanel implements ActionListener {

    MainFrame frame;
    GameClient client;

    // ===== ผลจริงจาก client (เรียงจากคะแนนมากไปน้อย ใน constructor) =====
    String[] names;
    int[] playerNo;     // ใช้เลือกรูป/สีของผู้เล่น (index 0-3)
    int[] scores;

    JButton backButton = UI.button("กลับไปหน้าหลัก", UI.CRIMSON, Color.WHITE);

    public GameOverPanel(MainFrame frame, GameClient client) {
        this.frame = frame;
        this.client = client;
        setLayout(null);
        sortResult();

        JLabel title = new JLabel("จบเกม!", JLabel.CENTER);
        title.setFont(UI.TITLE);
        title.setForeground(UI.GOLD);
        title.setBounds(0, 40, MainFrame.WIDTH, 60);
        add(title);

        // หัวตาราง
        JPanel head = new JPanel(null);
        head.setBackground(UI.WOOD);
        head.setBounds(200, 120, 500, 40);
        addCell(head, "อันดับ", 20);
        addCell(head, "ผู้เล่น", 200);
        addCell(head, "คะแนน", 390);
        add(head);

        // แถวผู้เล่น
        for (int i = 0; i < names.length; i++) {
            JPanel row = new JPanel(null);
            row.setBackground(UI.WOOD_DARK);
            row.setBounds(200, 165 + i * 60, 500, 55);

            // อันดับ 1 สีทอง
            JLabel rank = UI.label("" + (i + 1), UI.HEADER);
            if (i == 0) rank.setForeground(UI.GOLD);
            rank.setBounds(35, 10, 50, 35);
            row.add(rank);

            Image small = UI.PLAYER_PICS[playerNo[i]].getScaledInstance(36, 36, Image.SCALE_DEFAULT);
            JLabel pic = new JLabel(new ImageIcon(small));
            pic.setBounds(170, 9, 36, 36);
            row.add(pic);

            JLabel name = UI.label(names[i], UI.BOLD);
            name.setBounds(220, 10, 100, 35);
            row.add(name);

            JLabel score = UI.label("" + scores[i], UI.HEADER);
            score.setBounds(410, 10, 80, 35);
            row.add(score);

            add(row);
        }

        // ผู้ชนะ
        JLabel winner = new JLabel("ผู้ชนะ: " + names[0] + " (" + scores[0] + " คะแนน)", JLabel.CENTER);
        winner.setFont(UI.BOLD);
        winner.setForeground(Color.WHITE);
        winner.setBounds(0, 415, MainFrame.WIDTH, 30);
        add(winner);

        backButton.setBounds(330, 460, 240, 50);
        add(backButton);
        backButton.addActionListener(this);
    }

    // เอาเฉพาะคนที่อยู่ในห้อง แล้วเรียงคะแนนมากไปน้อย (Selection sort)
    private void sortResult() {
        int n = 0;
        for (int i = 0; i < 4; i++) {
            if (client.players[i].joined) n++;
        }
        names = new String[n];
        playerNo = new int[n];
        scores = new int[n];
        int k = 0;
        for (int i = 0; i < 4; i++) {
            Player p = client.players[i];
            if (p.joined) {
                names[k] = p.getName();
                playerNo[k] = i;
                scores[k] = p.score;
                k++;
            }
        }
        for (int i = 0; i < n - 1; i++) {
            int best = i;
            for (int j = i + 1; j < n; j++) {
                if (scores[j] > scores[best]) best = j;
            }
            String tn = names[i];  names[i] = names[best];  names[best] = tn;
            int tp = playerNo[i];  playerNo[i] = playerNo[best];  playerNo[best] = tp;
            int ts = scores[i];    scores[i] = scores[best];  scores[best] = ts;
        }
    }

    // ตัวช่วยใส่ข้อความหัวตาราง
    private void addCell(JPanel row, String text, int x) {
        JLabel l = UI.label(text, new Font("Tahoma", Font.BOLD, 16));
        l.setBounds(x, 5, 120, 30);
        row.add(l);
    }

    // พื้นหลัง = แผนที่จางๆ
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(UI.MAP, 0, 0, getWidth(), getHeight(), this);
        g.setColor(new Color(0, 0, 0, 150));   // สีดำโปร่งใส ทับให้มืดลง
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            client.close();   // ออกจากห้อง (เจ้าของห้องจะปิด Server ด้วย)
            frame.showPage(new MenuPanel(frame));
        }
    }
}
