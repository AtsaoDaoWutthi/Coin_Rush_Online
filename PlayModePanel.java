import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

// หน้าเลือกโหมด (กด "เริ่มเกม" ในหน้าหลักแล้วมาหน้านี้): สร้างห้อง หรือ เข้าร่วมห้อง
public class PlayModePanel extends JPanel implements ActionListener {

    MainFrame frame;

    JButton backButton   = UI.button("<", UI.WOOD_DARK, Color.WHITE);
    JButton createButton = UI.button("สร้างห้อง (Host)", UI.GOLD, Color.BLACK);
    JButton joinButton   = UI.button("เข้าร่วมห้อง (Join)", UI.CRIMSON, Color.WHITE);

    public PlayModePanel(MainFrame frame) {
        this.frame = frame;
        setLayout(null);
        setBackground(UI.WOOD);

        backButton.setBounds(30, 25, 50, 40);
        add(backButton);
        backButton.addActionListener(this);

        JLabel header = UI.label("เลือกโหมดการเล่น", UI.HEADER);
        header.setHorizontalAlignment(JLabel.CENTER);
        header.setBounds(0, 140, MainFrame.WIDTH, 40);
        add(header);

        createButton.setBounds(300, 220, 300, 60);
        add(createButton);
        createButton.addActionListener(this);

        joinButton.setBounds(300, 300, 300, 60);
        add(joinButton);
        joinButton.addActionListener(this);
    }

    // พื้นหลังกำแพงหิน
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(UI.STONE_BG, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            frame.showPage(new MenuPanel(frame));
        } else if (e.getSource() == createButton) {
            frame.showPage(new CreateRoomPanel(frame));
        } else if (e.getSource() == joinButton) {
            frame.showPage(new JoinRoomPanel(frame));
        }
    }
}
