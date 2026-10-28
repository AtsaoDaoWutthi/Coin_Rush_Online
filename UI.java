import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

public class UI {

    public static final Color WOOD      = new Color(92, 62, 38);
    public static final Color WOOD_DARK = new Color(58, 38, 24);
    public static final Color GOLD      = new Color(226, 180, 70);
    public static final Color CRIMSON   = new Color(128, 28, 36);
    public static final Color PARCHMENT = new Color(244, 228, 196);
    public static final Color GREEN     = new Color(110, 200, 110);
    public static final Color[] PLAYER_COLORS = {
        new Color(50, 140, 60), new Color(45, 90, 190),
        new Color(180, 35, 40), new Color(215, 165, 25)
    };

    public static final Font TITLE  = new Font("Tahoma", Font.BOLD, 40);
    public static final Font HEADER = new Font("Tahoma", Font.BOLD, 24);
    public static final Font NORMAL = new Font("Tahoma", Font.PLAIN, 18);
    public static final Font BOLD   = new Font("Tahoma", Font.BOLD, 18);

    public static final Image MENU_BG = load("img/menu_bg.png");
    public static final Image STONE_BG = load("img/stone_bg.png");
    public static final Image MAP     = load("img/map.png");
    public static final Image COIN    = load("img/coin.png");
    public static final Image[] PLAYER_PICS = {
        load("img/player1.png"), load("img/player2.png"),
        load("img/player3.png"), load("img/player4.png")
    };

    public static Image load(String path) {
        return new ImageIcon(path).getImage();
    }

    public static JButton button(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(BOLD);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(GOLD, 2));
        return b;
    }

    public static JLabel label(String text, Font font) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(PARCHMENT);
        return l;
    }
}
