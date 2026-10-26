import javax.swing.JFrame;
import javax.swing.JPanel;

// หน้าต่างเดียวของทั้งเกม ทุกหน้าเป็น JPanel ที่สลับเข้า-ออก
public class MainFrame extends JFrame {

    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;

    public MainFrame() {
        setTitle("Coin Collector Online");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);   // กด x ปิดโปรแกรม
        setResizable(false);
        setLocationRelativeTo(null);               // เปิดกลางจอ

        showPage(new MenuPanel(this));             // หน้าแรก = หน้าหลัก
        setVisible(true);
    }

    // เปลี่ยนหน้า: เอาหน้าเก่าออกทั้งหมด แล้วใส่หน้าใหม่
    public void showPage(JPanel page) {
        getContentPane().removeAll();   // ลบหน้าเก่า
        add(page);                      // ใส่หน้าใหม่
        revalidate();                   // ให้ Java จัดวาง component ใหม่ (จำเป็นหลัง add/remove ตอนหน้าต่างเปิดอยู่)
        repaint();                      // วาดจอใหม่
        page.requestFocusInWindow();    // เผื่อหน้าไหนใช้คีย์บอร์ด
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
