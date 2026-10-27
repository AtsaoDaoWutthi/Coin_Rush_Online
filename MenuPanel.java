import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

// หน้า 1: หน้าหลัก (Main Menu)
public class MenuPanel extends JPanel implements ActionListener {

    MainFrame frame;   // เก็บหน้าต่างไว้ เพื่อสั่ง frame.showPage(...) ตอนกดปุ่ม

    JButton startButton = UI.button("เริ่มเกม", UI.GOLD, Color.BLACK);
    JButton exitButton  = UI.button("ออกจากเกม", UI.CRIMSON, Color.WHITE);

    public MenuPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(null);   // วางเองด้วย setBounds

        // ชื่อเกม
        JLabel title = new JLabel("Coin Collector Online", JLabel.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 52));
        title.setForeground(UI.GOLD);
        title.setBounds(0, 70, MainFrame.WIDTH, 70);
        add(title);

        // ปุ่ม 2 ปุ่ม เรียงลงมา (x, y, กว้าง, สูง)
        startButton.setBounds(300, 190, 300, 55);
        exitButton.setBounds(300, 265, 300, 50);

        JButton[] buttons = { startButton, exitButton };
        for (int i = 0; i < buttons.length; i++) {
            add(buttons[i]);
            buttons[i].addActionListener(this);
        }
    }

    // รูปพื้นหลัง
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(UI.MENU_BG, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            frame.showPage(new PlayModePanel(frame));     // เลือกว่าจะสร้างหรือเข้าร่วมห้อง
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }
}
