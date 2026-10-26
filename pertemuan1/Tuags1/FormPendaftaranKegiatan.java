import javax.swing.*;
import java.awt.*;

public class FormPendaftaranKegiatan extends JFrame {
    public FormPendaftaranKegiatan() {
        setTitle("Formulir Pendaftaran Kegiatan");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel Utama dengan BorderLayout
        setLayout(new BorderLayout(10, 10));

        // --- 1. BAGIAN IDENTITAS (GridBagLayout / GridLayout) ---
        JPanel panelIdentitas = new JPanel(new GridLayout(4, 2, 5, 5));
        panelIdentitas.setBorder(BorderFactory.createTitledBorder("Identitas Peserta"));

        panelIdentitas.add(new JLabel("Nama Lengkap:"));
        JTextField txtNama = new JTextField();
        panelIdentitas.add(txtNama);

        panelIdentitas.add(new JLabel("Nomor Induk/ID:"));
        JTextField txtId = new JTextField();
        panelIdentitas.add(txtId);

        panelIdentitas.add(new JLabel("Jenis Kelamin:"));
        JPanel panelGender = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JRadioButton rbLaki = new JRadioButton("Laki-laki");
        JRadioButton rbPerempuan = new JRadioButton("Perempuan");
        ButtonGroup bgGender = new ButtonGroup();
        bgGender.add(rbLaki);
        bgGender.add(rbPerempuan);
        panelGender.add(rbLaki);
        panelGender.add(rbPerempuan);
        panelIdentitas.add(panelGender);

        panelIdentitas.add(new JLabel("Kategori:"));
        String[] kategoriList = {"Mahasiswa", "Dosen", "Umum"};
        JComboBox<String> cbKategori = new JComboBox<>(kategoriList);
        panelIdentitas.add(cbKategori);

        // --- 2. BAGIAN PILIHAN KEGIATAN (BoxLayout / GridLayout) ---
        JPanel panelKegiatan = new JPanel(new GridLayout(3, 1, 5, 5));
        panelKegiatan.setBorder(BorderFactory.createTitledBorder("Pilihan Kegiatan & Fasilitas"));

        JCheckBox chkWorkshop = new JCheckBox("Workshop Pemrograman Java");
        JCheckBox chkSeminar = new JCheckBox("Seminar AI & Masa Depan Teknologi");
        JCheckBox chkSertifikat = new JCheckBox("Cetak Sertifikat Fisik");
        panelKegiatan.add(chkWorkshop);
        panelKegiatan.add(chkSeminar);
        panelKegiatan.add(chkSertifikat);

        // --- 3. BAGIAN CATATAN (BorderLayout) ---
        JPanel panelCatatan = new JPanel(new BorderLayout(5, 5));
        panelCatatan.setBorder(BorderFactory.createTitledBorder("Catatan Tambahan"));
        JTextArea txtCatatan = new JTextArea(4, 20);
        JScrollPane scrollPane = new JScrollPane(txtCatatan);
        panelCatatan.add(scrollPane, BorderLayout.CENTER);

        // Menggabungkan Panel Identitas dan Kegiatan dalam satu panel vertikal (BoxLayout)
        JPanel panelAtasTengah = new JPanel();
        panelAtasTengah.setLayout(new BoxLayout(panelAtasTengah, BoxLayout.Y_AXIS));
        panelAtasTengah.add(panelIdentitas);
        panelAtasTengah.add(Box.createVerticalStrut(10));
        panelAtasTengah.add(panelKegiatan);
        panelAtasTengah.add(Box.createVerticalStrut(10));
        panelAtasTengah.add(panelCatatan);

        add(panelAtasTengah, BorderLayout.CENTER);

        // --- 4. TOMBOL AKSI (FlowLayout) ---
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSimpan = new JButton("Daftar / Simpan");
        JButton btnReset = new JButton("Reset");
        panelTombol.add(btnReset);
        panelTombol.add(btnSimpan);

        add(panelTombol, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        // Dijalankan melalui Event Dispatch Thread sesuai ketentuan tugas Bagian 1
        SwingUtilities.invokeLater(() -> {
            new FormPendaftaranKegiatan().setVisible(true);
        });
    }
}
