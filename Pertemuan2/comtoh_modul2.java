import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class comtoh_modul2 {
    private String namaBarang = "Laptop Asus ROG";
    private int stokSekarang = 500; // Jumlah stok barang saat ini
    private int limitStok = 1000;   // Batas maksimum limit stok
    
    private JFrame frame = new JFrame("Contoh Modul 2 - Kelola Stok Barang");
    private JTextField inputField = new JTextField(10);
    private JLabel infoLabel = new JLabel("", JLabel.CENTER);

    // Method untuk memperbarui teks informasi stok di antarmuka secara dinamis
    private void updateInfoLabel() {
        infoLabel.setText("<html><center><b>Barang:</b> " + namaBarang + 
                          "<br><b>Stok Saat Ini:</b> " + stokSekarang + 
                          " | <b>Limit Maksimal:</b> " + limitStok + "</center></html>");
    }

    private void tampilan() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        updateInfoLabel();

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.add(infoLabel);
        
        JLabel instruksiLabel = new JLabel("Masukkan jumlah kuantitas barang:");
        centerPanel.add(instruksiLabel);
        centerPanel.add(inputField);

        // Panel untuk tombol Tambah dan Kurangi Stok
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton tambahButton = new JButton("Tambah Stok");
        JButton kurangiButton = new JButton("Kurangi Stok");

        // Logika Tombol Tambah Stok
        tambahButton.addActionListener(e -> {
            try {
                int jumlah = Integer.parseInt(inputField.getText().trim());
                if (jumlah <= 0) {
                    JOptionPane.showMessageDialog(frame, "Masukkan angka lebih besar dari 0!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (stokSekarang + jumlah > limitStok) {
                    JOptionPane.showMessageDialog(frame, 
                        "Peringatan: Total stok akan melebihi batas limit (" + limitStok + ")!", 
                        "Limit Terlampaui", 
                        JOptionPane.WARNING_MESSAGE);
                } else {
                    stokSekarang += jumlah;
                    updateInfoLabel();
                    JOptionPane.showMessageDialog(frame, "Stok berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
                inputField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Masukkan angka yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Logika Tombol Kurangi Stok
        kurangiButton.addActionListener(e -> {
            try {
                int jumlah = Integer.parseInt(inputField.getText().trim());
                if (jumlah <= 0) {
                    JOptionPane.showMessageDialog(frame, "Masukkan angka lebih besar dari 0!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (stokSekarang - jumlah < 0) {
                    JOptionPane.showMessageDialog(frame, 
                        "Gagal: Pengurangan melebihi sisa stok yang ada!", 
                        "Stok Tidak Cukup", 
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    stokSekarang -= jumlah;
                    updateInfoLabel();
                    JOptionPane.showMessageDialog(frame, "Stok berhasil dikurangi!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
                inputField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Masukkan angka yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(tambahButton);
        buttonPanel.add(kurangiButton);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(440, 260);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new comtoh_modul2().tampilan();
        });
    }
}