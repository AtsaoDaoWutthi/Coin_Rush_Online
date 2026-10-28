import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CreateRoomPanel extends JPanel implements ActionListener {

    MainFrame frame;

    JButton backButton   = UI.button("<", UI.WOOD_DARK, Color.WHITE);
    JTextField nameField = new JTextField("ห้องของเรา");
    JComboBox<String> countBox = new JComboBox<String>(new String[] { "2 คน", "3 คน", "4 คน" });
    JButton createButton = UI.button("สร้างห้อง", UI.GOLD, Color.BLACK);

    public CreateRoomPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(null);
        setBackground(UI.WOOD);

        backButton.setBounds(30, 25, 50, 40);
        add(backButton);
        backButton.addActionListener(this);
        JLabel header = UI.label("สร้างห้องเกม", UI.HEADER);
        header.setBounds(100, 25, 400, 40);
        add(header);

        JLabel nameLabel = UI.label("ชื่อห้อง :", UI.NORMAL);
        nameLabel.setBounds(200, 170, 150, 35);
        add(nameLabel);
        nameField.setFont(UI.NORMAL);
        nameField.setBounds(370, 170, 300, 35);
        add(nameField);

        JLabel countLabel = UI.label("จำนวนผู้เล่น :", UI.NORMAL);
        countLabel.setBounds(200, 225, 160, 35);
        add(countLabel);
        countBox.setFont(UI.NORMAL);
        countBox.setSelectedIndex(2);
        countBox.setBounds(370, 225, 150, 35);
        add(countBox);

        createButton.setBounds(370, 290, 200, 45);
        add(createButton);
        createButton.addActionListener(this);
    }

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
            int maxPlayers = countBox.getSelectedIndex() + 2;
            GameServer server;
            try {
                server = new GameServer(nameField.getText(), maxPlayers);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "เปิดห้องไม่ได้ (พอร์ต " + GameServer.PORT + " ถูกใช้อยู่)");
                return;
            }
            server.start();
            GameClient client = JoinRoomPanel.connect(frame, "127.0.0.1", server);
            if (client == null) {
                server.close();
            }
        }
    }
}
