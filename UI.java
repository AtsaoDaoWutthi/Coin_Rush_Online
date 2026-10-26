import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

// ของที่ทุกหน้าใช้ร่วมกัน: สี ฟอนต์ รูป และตัวช่วยสร้างปุ่ม/ข้อความ
// (static = เรียกได้เลย UI.GOLD ไม่ต้อง new)
public class UI {

    // ===== สี (ธีมยุคอัศวิน: ไม้ ทอง แดงเลือดหมู กระดาษเก่า) =====
    public static final Color WOOD      = new Color(92, 62, 38);    // ไม้สีกลาง (พื้นหัวตาราง)
    public static final Color WOOD_DARK = new Color(58, 38, 24);    // ไม้สีเข้ม (พื้นแถว / แถบคะแนน)
    public static final Color GOLD      = new Color(226, 180, 70);  // ทอง (ปุ่มหลัก / ขอบ)
    public static final Color CRIMSON   = new Color(128, 28, 36);   // แดงเลือดหมู (ปุ่มรอง)
    public static final Color PARCHMENT = new Color(244, 228, 196); // สีกระดาษเก่า (ตัวหนังสือ)
    public static final Color GREEN     = new Color(110, 200, 110); // สถานะพร้อม
    public static final Color[] PLAYER_COLORS = {
        new Color(50, 140, 60), new Color(45, 90, 190),
        new Color(180, 35, 40), new Color(215, 165, 25)
    };

    // ===== ฟอนต์ (Tahoma แสดงภาษาไทยได้บน Windows) =====
    public static final Font TITLE  = new Font("Tahoma", Font.BOLD, 40);
    public static final Font HEADER = new Font("Tahoma", Font.BOLD, 24);
    public static final Font NORMAL = new Font("Tahoma", Font.PLAIN, 18);
    public static final Font BOLD   = new Font("Tahoma", Font.BOLD, 18);

    // ===== รูป (ต้องรันจากโฟลเดอร์ที่มีโฟลเดอร์ img) =====
    public static final Image MENU_BG = load("img/menu_bg.png");
    public static final Image STONE_BG = load("img/stone_bg.png");   // กำแพงหิน (หน้า 2, 3)
    public static final Image MAP     = load("img/map.png");
    public static final Image COIN    = load("img/coin.png");
    public static final Image[] PLAYER_PICS = {
        load("img/player1.png"), load("img/player2.png"),
        load("img/player3.png"), load("img/player4.png")
    };

    public static Image load(String path) {
        return new ImageIcon(path).getImage();
    }

    // ปุ่มสีตามที่กำหนด ตัวหนังสือหนา
    public static JButton button(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(BOLD);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);   // ไม่ต้องมีกรอบโฟกัสบนตัวหนังสือ
        // ขอบทองหนา 2 px ให้ดูเหมือนป้ายโลหะ
        b.setBorder(BorderFactory.createLineBorder(GOLD, 2));
        return b;
    }

    // ข้อความสีกระดาษเก่า
    public static JLabel label(String text, Font font) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(PARCHMENT);
        return l;
    }
}
