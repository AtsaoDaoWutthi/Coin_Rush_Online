import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// หน้า 2: สร้าง / เข้าห้อง (Lobby)
public class LobbyPanel extends JPanel implements ActionListener {

    MainFrame frame;

    // ===== ข้อมูลตัวอย่าง (ภายหลังเพื่อนจะได้มาจาก Server) =====
    String[] roomNames  = { "ห้อง1", "ห้อง2", "ห้อง3" };
    int[] roomPlayers   = { 3, 2, 4 };
    int maxPlayers      = 4;

    JButton backButton   = UI.button("<", UI.WOOD_DARK, Color.WHITE);
    JTextField nameField = new JTextField("ห้องของเรา");
    JComboBox<String> countBox = new JComboBox<String>(new String[] { "2 คน", "3 คน", "4 คน" });
    JButton createButton = UI.button("สร้างห้อง", UI.GOLD, Color.BLACK);
    JButton[] joinButtons = new JButton[roomNames.length];

    public LobbyPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(null);
        setBackground(UI.WOOD);

        // ----- หัวหน้า -----
        backButton.setBounds(30, 25, 50, 40);
        add(backButton);
        JLabel header = UI.label("สร้างห้องเกม", UI.HEADER);
        header.setBounds(100, 25, 400, 40);
        add(header);

        // ----- ส่วนสร้างห้อง -----
        JLabel nameLabel = UI.label("ชื่อห้อง :", UI.NORMAL);
        nameLabel.setBounds(150, 95, 150, 35);
        add(nameLabel);
        nameField.setFont(UI.NORMAL);
        nameField.setBounds(320, 95, 300, 35);
        add(nameField);

        JLabel countLabel = UI.label("จำนวนผู้เล่น :", UI.NORMAL);
        countLabel.setBounds(150, 145, 160, 35);
        add(countLabel);
        countBox.setFont(UI.NORMAL);
        countBox.setSelectedIndex(2);   // ค่าเริ่ม = 4 คน
        countBox.setBounds(320, 145, 150, 35);
        add(countBox);

        createButton.setBounds(320, 200, 200, 45);
        add(createButton);
        createButton.addActionListener(this);

        // ----- รายการห้องที่มีอยู่ -----
        JLabel listLabel = UI.label("รายการห้องที่มีอยู่", UI.BOLD);
        listLabel.setBounds(150, 280, 300, 30);
        add(listLabel);

        for (int i = 0; i < roomNames.length; i++) {
            int y = 320 + i * 60;

            // แถวพื้นเข้ม 1 แถว = 1 ห้อง
            JPanel row = new JPanel(null);
            row.setBackground(UI.WOOD_DARK);
            row.setBounds(150, y, 600, 50);

            JLabel room = UI.label(roomNames[i], UI.NORMAL);
            room.setBounds(20, 8, 150, 34);
            row.add(room);

            JLabel count = UI.label("(" + roomPlayers[i] + "/" + maxPlayers + ")", UI.NORMAL);
            count.setBounds(200, 8, 100, 34);
            row.add(count);

            // ห้องเต็ม = ปุ่ม "เต็ม" กดไม่ได้ / ไม่เต็ม = ปุ่ม "เข้าร่วม"
            if (roomPlayers[i] >= maxPlayers) {
                joinButtons[i] = UI.button("เต็ม", Color.DARK_GRAY, Color.LIGHT_GRAY);
                joinButtons[i].setEnabled(false);
            } else {
                joinButtons[i] = UI.button("เข้าร่วม", UI.CRIMSON, Color.WHITE);
            }
            joinButtons[i].setBounds(460, 8, 120, 34);
            joinButtons[i].addActionListener(this);
            row.add(joinButtons[i]);

            add(row);
        }

        backButton.addActionListener(this);
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
            return;
        }
        if (e.getSource() == createButton) {
            // ส่งชื่อห้องที่พิมพ์ไปแสดงในหน้าถัดไป
            frame.showPage(new WaitingPanel(frame, nameField.getText()));
            return;
        }
        for (int i = 0; i < joinButtons.length; i++) {
            if (e.getSource() == joinButtons[i]) {
                frame.showPage(new WaitingPanel(frame, roomNames[i]));
            }
        }
    }
}
