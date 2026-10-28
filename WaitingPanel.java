import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WaitingPanel extends JPanel implements ActionListener {

    MainFrame frame;
    GameClient client;

    JLabel header = UI.label("ห้อง : ", UI.HEADER);
    JLabel countLabel = UI.label("ผู้เล่น 0/4", UI.BOLD);
    JPanel[] rows = new JPanel[4];
    JLabel[] names = new JLabel[4];
    JLabel[] statuses = new JLabel[4];

    JButton startButton = UI.button("เริ่มเกม", UI.CRIMSON, Color.WHITE);

    JButton cancelButton = UI.button("ยกเลิก", UI.WOOD_DARK, Color.WHITE);

    public WaitingPanel(MainFrame frame, GameClient client) {
        this.frame = frame;
        this.client = client;
        setLayout(null);
        setBackground(UI.WOOD);

        cancelButton.setBounds(30, 25, 100, 40);
        add(cancelButton);
        cancelButton.addActionListener(this);

        header.setBounds(150, 30, 450, 40);
        add(header);

        countLabel.setBounds(620, 30, 150, 40);
        add(countLabel);

        String ipText;
        if (client.server != null) {
            ipText = "IP ห้อง : " + myIpList() + "   (Port " + GameServer.PORT + ")";
        } else {
            ipText = "IP ห้อง : " + client.ip;
        }
        JLabel ipLabel = UI.label(ipText, new Font("Tahoma", Font.PLAIN, 15));
        ipLabel.setForeground(UI.GOLD);
        ipLabel.setBounds(150, 68, 620, 26);
        add(ipLabel);

        for (int i = 0; i < 4; i++) {
            rows[i] = new JPanel(null);
            rows[i].setBackground(UI.WOOD_DARK);
            rows[i].setBounds(150, 100 + i * 75, 600, 60);

            Image small = UI.PLAYER_PICS[i].getScaledInstance(40, 40, Image.SCALE_DEFAULT);
            JLabel pic = new JLabel(new ImageIcon(small));
            pic.setBounds(15, 10, 40, 40);
            rows[i].add(pic);

            names[i] = UI.label("Player" + (i + 1), UI.NORMAL);
            names[i].setBounds(75, 13, 300, 34);
            rows[i].add(names[i]);

            statuses[i] = UI.label("", UI.NORMAL);
            statuses[i].setBounds(440, 13, 150, 34);
            rows[i].add(statuses[i]);

            add(rows[i]);
        }

        startButton.setBounds(330, 420, 240, 50);
        add(startButton);
        startButton.addActionListener(this);

        refresh();
    }

    public void refresh() {
        header.setText("ห้อง : " + client.roomName);

        int count = 0;
        for (int i = 0; i < 4; i++) {
            if (client.players[i].joined) count++;
        }
        countLabel.setText("ผู้เล่น " + count + "/" + client.maxPlayers);

        boolean allReady = true;
        for (int i = 0; i < 4; i++) {
            Player p = client.players[i];
            rows[i].setVisible(i < client.maxPlayers);

            String name = "Player" + (i + 1);
            if (i == client.myId) name += " (คุณ)";
            if (i == 0) name += " [เจ้าของห้อง]";
            names[i].setText(name);

            if (!p.joined) {
                statuses[i].setText("รอผู้เล่น...");
                statuses[i].setFont(UI.NORMAL);
                statuses[i].setForeground(Color.GRAY);
            } else if (p.ready) {
                statuses[i].setText("พร้อม");
                statuses[i].setFont(UI.BOLD);
                statuses[i].setForeground(UI.GREEN);
            } else {
                statuses[i].setText("ยังไม่พร้อม");
                statuses[i].setFont(UI.NORMAL);
                statuses[i].setForeground(Color.ORANGE);
                allReady = false;
            }
        }

        if (client.myId == 0) {
            startButton.setText("เริ่มเกม");
            startButton.setEnabled(allReady);
        } else if (client.myId > 0) {
            startButton.setText(client.players[client.myId].ready ? "ยกเลิกพร้อม" : "พร้อม");
            startButton.setEnabled(true);
        } else {
            startButton.setEnabled(false);
        }
    }

    private String myIpList() {
        String radmin = "", others = "";
        try {
            Enumeration<NetworkInterface> cards = NetworkInterface.getNetworkInterfaces();
            while (cards.hasMoreElements()) {
                NetworkInterface card = cards.nextElement();
                if (!card.isUp() || card.isLoopback()) continue;
                String name = card.getDisplayName().toLowerCase();
                String tag = "";
                if (name.contains("radmin")) tag = " (Radmin)";
                else if (name.contains("wi-fi") || name.contains("wireless") || name.contains("wlan")) tag = " (Wi-Fi)";

                Enumeration<InetAddress> addrs = card.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress a = addrs.nextElement();
                    if (!(a instanceof Inet4Address)) continue;
                    String one = a.getHostAddress() + tag;
                    if (tag.equals(" (Radmin)") || a.getHostAddress().startsWith("26.")) {
                        radmin += (radmin.isEmpty() ? "" : ", ") + one;
                    } else {
                        others += (others.isEmpty() ? "" : ", ") + one;
                    }
                }
            }
        } catch (SocketException e) {
        }
        String all = radmin;
        if (!others.isEmpty()) all += (all.isEmpty() ? "" : ", ") + others;
        return all.isEmpty() ? "ไม่พบ (ดูจาก ipconfig)" : all;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(UI.STONE_BG, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            boolean isHost = (client.server != null);
            client.close();
            if (isHost) {
                frame.showPage(new CreateRoomPanel(frame));
            } else {
                frame.showPage(new JoinRoomPanel(frame));
            }
            return;
        }
        if (e.getSource() == startButton) {
            if (client.myId == 0) {
                client.send("START");
            } else {
                client.send("READY");
            }
        }
    }
}
