import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;

    public MainFrame() {
        setTitle("Coin Collector Online");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        showPage(new MenuPanel(this));
        setVisible(true);
    }

    public void showPage(JPanel page) {
        getContentPane().removeAll();
        add(page);
        revalidate();
        repaint();
        page.requestFocusInWindow();
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
