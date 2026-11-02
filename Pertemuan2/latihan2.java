import javax.swing.*;
import java.awt.*;

public class latihan2 {
    private String namaBarang = "Buku"; 
    private int stokSekarang = 3;     
    private int limitStok = 1000;
    
    private JFrame frame = new JFrame("Latihan - Manajemen Stok Barang");
    private JTextField namaField = new JTextField(15);
    private JTextField inputField = new JTextField(10);
    private JLabel infoLabel = new JLabel("", JLabel.CENTER);

    private void updateInfoLabel() {
        infoLabel.setText("<html><center><b>Ringkasan " + (namaBarang.isEmpty() ? "Barang" : namaBarang) + ":</b> " + stokSekarang + 
                          "<br><b>Limit Maksimal:</b> " + limitStok + "</center></html>");
    }

    private void tampilan() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        updateInfoLabel();

        JPanel centerPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        centerPanel.add(infoLabel);
        
        centerPanel.add(new JLabel("Nama Barang:"));
        namaField.setText(namaBarang);
        centerPanel.add(namaField);

        centerPanel.add(new JLabel("Jumlah Stok / Kuantitas:"));
        inputField.setText(String.valueOf(stokSekarang));
        centerPanel.add(inputField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton simpanButton = new JButton("Simpan / Proses");
        JButton ubahButton = new JButton("Ubah Stok");

        simpanButton.addActionListener(e -> {
            String inputNama = namaField.getText().trim();
            
            if (inputNama.isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                    "Peringatan: Nama barang tidak boleh kosong!", 
                    "Dialog Validasi", 
                    JOptionPane.WARNING_MESSAGE);
                return; 
            }

            try {
                String textStok = inputField.getText().trim();
                int jumlah = Integer.parseInt(textStok);

                if (jumlah < 0) {
                    JOptionPane.showMessageDialog(frame, 
                        "Peringatan: Stok tidak boleh bernilai negatif (-1)!", 
                        "Dialog Validasi", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (jumlah > limitStok) {
                    JOptionPane.showMessageDialog(frame, 
                        "Peringatan: Jumlah melebihi batas limit stok (" + limitStok + ")!", 
                        "Limit Terlampaui", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                namaBarang = inputNama;
                stokSekarang = jumlah;
                updateInfoLabel();
                
                JOptionPane.showMessageDialog(frame, 
                    "Ringkasan " + namaBarang + ": " + stokSekarang, 
                    "Sukses", 
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, 
                    "Peringatan: Masukkan angka yang valid (bukan huruf/karakter)!", 
                    "Dialog Validasi", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        ubahButton.addActionListener(e -> {
            try {
                String inputNama = namaField.getText().trim();
                if (inputNama.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Nama barang tidak boleh kosong!", "Dialog Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int jumlahBaru = Integer.parseInt(inputField.getText().trim());
                if (jumlahBaru < 0) {
                    JOptionPane.showMessageDialog(frame, "Stok tidak boleh negatif!", "Dialog Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                namaBarang = inputNama;
                stokSekarang = jumlahBaru;
                updateInfoLabel();
                
                JOptionPane.showMessageDialog(frame, 
                    "Ringkasan mengikuti nilai baru -> " + namaBarang + ": " + stokSekarang, 
                    "Perubahan Disimpan", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Masukkan angka yang valid!", "Dialog Validasi", JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonPanel.add(simpanButton);
        buttonPanel.add(ubahButton);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(420, 320);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new latihan2().tampilan();
        });
    }
}