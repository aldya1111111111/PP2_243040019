import javax.swing.*;

public class latihan2 {
    public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Latihan2");
            frame.add(new JLabel("hello pp2", JLabel.CENTER));
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
