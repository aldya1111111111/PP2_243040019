import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class contoh_modul_1 {
    private int jumlahKlik = 0;

    public void tampilan() {
        JFrame frame = new JFrame("Contoh Modul 1");
        JLabel pesan = new JLabel("Tekan tombol ini.");
        JButton tombol = new JButton("Klik saya");

        tombol.addActionListener(e -> {
            jumlahKlik++;
            pesan.setText("Saya sudah mengklik sebanyak: " + jumlahKlik); 
        });

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(pesan, BorderLayout.CENTER);
        panel.add(tombol, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new contoh_modul_1().tampilan();
        });
    }
}