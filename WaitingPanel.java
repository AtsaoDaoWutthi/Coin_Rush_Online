import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

// หน้า 3: ห้องรอเริ่มเกม (Waiting Room)
public class WaitingPanel extends JPanel implements ActionListener {

    MainFrame frame;

    // ===== ข้อมูลตัวอย่าง =====
    String[] playerNames = { "Player1 (คุณ)", "Player2", "Player3", "Player4" };
    boolean[] ready      = { true, false, true, false };
    boolean[] joined     = { true, true, true, false };   // Player4 ยังเข้าไม่ครบ

    JButton startButton = UI.button("เริ่มเกม", UI.CRIMSON, Color.WHITE);

    public WaitingPanel(MainFrame frame, String roomName) {
        this.frame = frame;
        setLayout(null);
        setBackground(UI.WOOD);

        // นับคนที่เข้าห้องแล้ว
        int count = 0;
        for (int i = 0; i < joined.length; i++) {
            if (joined[i]) count++;
        }

        JLabel header = UI.label("ห้อง : " + roomName, UI.HEADER);
        header.setBounds(150, 30, 450, 40);
        add(header);

        JLabel countLabel = UI.label("ผู้เล่น " + count + "/" + playerNames.length, UI.BOLD);
        countLabel.setBounds(620, 30, 150, 40);
        add(countLabel);

        // รายชื่อผู้เล่น 4 แถว
        for (int i = 0; i < playerNames.length; i++) {
            JPanel row = new JPanel(null);
            row.setBackground(UI.WOOD_DARK);
            row.setBounds(150, 100 + i * 75, 600, 60);

            // รูปตัวละครย่อ 40x40
            Image small = UI.PLAYER_PICS[i].getScaledInstance(40, 40, Image.SCALE_DEFAULT);
            JLabel pic = new JLabel(new ImageIcon(small));
            pic.setBounds(15, 10, 40, 40);
            row.add(pic);

            JLabel name = UI.label(playerNames[i], UI.NORMAL);
            name.setBounds(75, 13, 300, 34);
            row.add(name);

            // สถานะ: ยังไม่เข้า / พร้อม / ยังไม่พร้อม
            JLabel status;
            if (!joined[i]) {
                status = UI.label("กำลังเข้าร่วม...", UI.NORMAL);
                status.setForeground(Color.GRAY);
            } else if (ready[i]) {
                status = UI.label("พร้อม", UI.BOLD);
                status.setForeground(UI.GREEN);
            } else {
                status = UI.label("ยังไม่พร้อม", UI.NORMAL);
                status.setForeground(Color.ORANGE);
            }
            status.setBounds(440, 13, 150, 34);
            row.add(status);

            add(row);
        }

        startButton.setBounds(330, 420, 240, 50);
        add(startButton);
        startButton.addActionListener(this);
    }

    // พื้นหลังกำแพงหิน
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(UI.STONE_BG, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            frame.showPage(new GamePanel(frame));
        }
    }
}
