import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class JoinRoomPanel extends JPanel implements ActionListener {

    MainFrame frame;

    JButton backButton = UI.button("<", UI.WOOD_DARK, Color.WHITE);
    JTextField ipField = new JTextField("127.0.0.1");
    JButton joinButton = UI.button("เข้าร่วม", UI.CRIMSON, Color.WHITE);

    public JoinRoomPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(null);
        setBackground(UI.WOOD);

        backButton.setBounds(30, 25, 50, 40);
        add(backButton);
        backButton.addActionListener(this);
        JLabel header = UI.label("เข้าร่วมห้องเกม", UI.HEADER);
        header.setBounds(100, 25, 400, 40);
        add(header);

        JPanel row = new JPanel(null);
        row.setBackground(UI.WOOD_DARK);
        row.setBounds(150, 220, 600, 50);

        JLabel ipLabel = UI.label("IP ห้อง :", UI.NORMAL);
        ipLabel.setBounds(20, 8, 120, 34);
        row.add(ipLabel);
        ipField.setFont(UI.NORMAL);
        ipField.setBounds(140, 8, 300, 34);
        row.add(ipField);

        joinButton.setBounds(460, 8, 120, 34);
        joinButton.addActionListener(this);
        row.add(joinButton);
        add(row);
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
        if (e.getSource() == joinButton) {
            connect(frame, ipField.getText().trim(), null);
        }
    }

    public static GameClient connect(MainFrame frame, String ip, GameServer server) {
        GameClient client;
        try {
            client = new GameClient(frame, ip);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "เชื่อมต่อ " + ip + " ไม่ได้");
            return null;
        }
        client.server = server;
        WaitingPanel page = new WaitingPanel(frame, client);
        client.waitingPage = page;
        frame.showPage(page);
        client.start();
        return client;
    }
}
