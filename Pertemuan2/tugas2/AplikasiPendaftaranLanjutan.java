import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

public class AplikasiPendaftaranLanjutan extends JFrame {
    // Deklarasi komponen utama untuk CardLayout (navigasi antar halaman)
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Deklarasi komponen pada Halaman Formulir
    private JTextField txtNama, txtEmail, txtUmur;
    private JRadioButton rbLaki, rbPerempuan;
    private JComboBox<String> cbKategori;
    private JCheckBox chkWorkshop, chkSeminar, chkSertifikat;
    private JTextArea txtCatatan;

    // Deklarasi komponen untuk menampilkan hasil ringkasan
    private JLabel lblRingkasanHasil;

    public AplikasiPendaftaranLanjutan() {
        // Pengaturan dasar Jframe (Jendela Utama)
        setTitle("Aplikasi Pendaftaran Kegiatan - Lanjutan");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Mencegah langsung close agar konfirmasi keluar aktif
        setLocationRelativeTo(null); // Posisi jendela di tengah layar

        // Mengatur Window Listener untuk konfirmasi ketika tombol silang (X) diklik
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                konfirmasiKeluar();
            }
        });

        // Menginisialisasi CardLayout dan Panel Utama penampung halaman
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Menambahkan halaman-halaman ke dalam CardLayout (tanpa membuka jendela baru)
        mainPanel.add(buatPanelFormulir(), "FORM");
        mainPanel.add(buatPanelRingkasan(), "RINGKASAN");
        mainPanel.add(buatPanelBantuan(), "BANTUAN");

        add(mainPanel);
        cardLayout.show(mainPanel, "FORM"); // Menampilkan halaman form saat pertama kali dibuka
    }

    // --- HALAMAN 1: FORMULIR PENDAFTARAN ---
    private JPanel buatPanelFormulir() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Grid untuk input identitas (Nama, Email, Umur, Jenis Kelamin, Kategori)
        JPanel formGrid = new JPanel(new GridLayout(7, 2, 5, 5));
        formGrid.setBorder(BorderFactory.createTitledBorder("Formulir Pendaftaran"));

        formGrid.add(new JLabel("Nama Lengkap:"));
        txtNama = new JTextField();
        formGrid.add(txtNama);

        formGrid.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formGrid.add(txtEmail);

        formGrid.add(new JLabel("Umur (15-100):"));
        txtUmur = new JTextField();
        formGrid.add(txtUmur);

        formGrid.add(new JLabel("Jenis Kelamin:"));
        JPanel pGender = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rbLaki = new JRadioButton("Laki-laki");
        rbPerempuan = new JRadioButton("Perempuan");
        ButtonGroup bgGender = new ButtonGroup(); // Mengelompokkan radio button agar saling eksklusif
        bgGender.add(rbLaki);
        bgGender.add(rbPerempuan);
        pGender.add(rbLaki);
        pGender.add(rbPerempuan);
        formGrid.add(pGender);

        formGrid.add(new JLabel("Kategori:"));
        cbKategori = new JComboBox<>(new String[]{"Mahasiswa", "Dosen", "Umum"});
        formGrid.add(cbKategori);

        // Panel Checkbox untuk Pilihan Kegiatan
        JPanel pKegiatan = new JPanel(new GridLayout(3, 1));
        pKegiatan.setBorder(BorderFactory.createTitledBorder("Pilihan Kegiatan (Wajib Pilih)"));
        chkWorkshop = new JCheckBox("Workshop Java");
        chkSeminar = new JCheckBox("Seminar AI");
        chkSertifikat = new JCheckBox("Cetak Sertifikat");
        pKegiatan.add(chkWorkshop);
        pKegiatan.add(chkSeminar);
        pKegiatan.add(chkSertifikat);

        // Panel Catatan Tambahan menggunakan TextArea di dalam ScrollPane
        JPanel pCatatan = new JPanel(new BorderLayout());
        pCatatan.setBorder(BorderFactory.createTitledBorder("Catatan Tambahan"));
        txtCatatan = new JTextArea(3, 20);
        pCatatan.add(new JScrollPane(txtCatatan), BorderLayout.CENTER);

        // Menggabungkan seluruh bagian form secara vertikal menggunakan BoxLayout
        JPanel pTengah = new JPanel();
        pTengah.setLayout(new BoxLayout(pTengah, BoxLayout.Y_AXIS));
        pTengah.add(formGrid);
        pTengah.add(pKegiatan);
        pTengah.add(pCatatan);

        panel.add(pTengah, BorderLayout.CENTER);

        // Panel Tombol Aksi (Bantuan, Reset, Simpan, Keluar)
        JPanel pTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBantuan = new JButton("Bantuan");
        JButton btnReset = new JButton("Reset");
        JButton btnSimpan = new JButton("Simpan / Tampilkan");
        JButton btnKeluar = new JButton("Keluar");

        pTombol.add(btnBantuan);
        pTombol.add(btnReset);
        pTombol.add(btnSimpan);
        pTombol.add(btnKeluar);
        panel.add(pTombol, BorderLayout.SOUTH);

        // Event Handling (Aksi ketika tombol diklik)
        btnSimpan.addActionListener(e -> validasiDanSimpan());
        btnReset.addActionListener(e -> resetForm());
        btnBantuan.addActionListener(e -> cardLayout.show(mainPanel, "BANTUAN")); // Berpindah ke halaman Bantuan
        btnKeluar.addActionListener(e -> konfirmasiKeluar());

        return panel;
    }

    // --- HALAMAN 2: RINGKASAN DATA ---
    private JPanel buatPanelRingkasan() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("<html><h2>Ringkasan Pendaftaran Berhasil</h2></html>", JLabel.CENTER), BorderLayout.NORTH);

        lblRingkasanHasil = new JLabel();
        lblRingkasanHasil.setVerticalAlignment(JLabel.TOP);
        panel.add(new JScrollPane(lblRingkasanHasil), BorderLayout.CENTER);

        // Tombol untuk kembali ke halaman form
        JButton btnKembali = new JButton("Kembali ke Form");
        JPanel pBawah = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pBawah.add(btnKembali);
        panel.add(pBawah, BorderLayout.SOUTH);

        btnKembali.addActionListener(e -> cardLayout.show(mainPanel, "FORM")); // Berpindah kembali ke form

        return panel;
    }

    // --- HALAMAN 3: BANTUAN ---
    private JPanel buatPanelBantuan() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea txtBantuan = new JTextArea(
            "PANDUAN APLIKASI PENDAFTARAN:\n\n" +
            "1. Isi seluruh data identitas (Nama wajib diisi, Email valid, Umur 15-100).\n" +
            "2. Pilih minimal satu kegiatan pada bagian Pilihan Kegiatan.\n" +
            "3. Klik 'Simpan / Tampilkan' untuk melihat ringkasan data.\n" +
            "4. Tombol 'Reset' untuk mengosongkan form.\n" +
            "5. Pastikan data benar sebelum keluar aplikasi."
        );
        txtBantuan.setEditable(false); // Agar teks bantuan tidak bisa diubah-ubah pengguna
        panel.add(new JScrollPane(txtBantuan), BorderLayout.CENTER);

        JButton btnKembali = new JButton("Kembali");
        JPanel pBawah = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pBawah.add(btnKembali);
        panel.add(pBawah, BorderLayout.SOUTH);

        btnKembali.addActionListener(e -> cardLayout.show(mainPanel, "FORM"));

        return panel;
    }

    // --- VALIDASI INPUT (Sesuai Ketentuan Tugas) ---
    private void validasiDanSimpan() {
        // 1. Validasi Nama Wajib Diisi
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lengkap wajib diisi!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
            txtNama.requestFocus(); // Memfokuskan kembali kursor ke input yang salah
            return;
        }

        // 2. Validasi Format Email yang Wajar
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailRegex, txtEmail.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Format email tidak valid (contoh: user@mail.com)", "Error Validasi", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return;
        }

        // 3. Validasi Umur Harus Angka Bulat dan Rentang 15-100
        try {
            int umur = Integer.parseInt(txtUmur.getText().trim());
            if (umur < 15 || umur > 100) {
                JOptionPane.showMessageDialog(this, "Umur harus berupa angka antara 15 sampai 100 tahun!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                txtUmur.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Umur harus berupa angka bulat!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
            txtUmur.requestFocus();
            return;
        }

        // 4. Validasi Kegiatan Wajib Dipilih Minimal Satu
        if (!chkWorkshop.isSelected() && !chkSeminar.isSelected() && !chkSertifikat.isSelected()) {
            JOptionPane.showMessageDialog(this, "Pilih minimal satu jenis kegiatan atau fasilitas!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Jika semua validasi berhasil, kumpulkan data untuk ditampilkan ke halaman ringkasan
        StringBuilder kegiatanStr = new StringBuilder();
        if (chkWorkshop.isSelected()) kegiatanStr.append("- Workshop Java<br>");
        if (chkSeminar.isSelected()) kegiatanStr.append("- Seminar AI<br>");
        if (chkSertifikat.isSelected()) kegiatanStr.append("- Cetak Sertifikat<br>");

        String gender = rbLaki.isSelected() ? "Laki-laki" : (rbPerempuan.isSelected() ? "Perempuan" : "Belum dipilih");

        lblRingkasanHasil.setText(
            "<html><b>Nama:</b> " + txtNama.getText() + "<br>" +
            "<b>Email:</b> " + txtEmail.getText() + "<br>" +
            "<b>Umur:</b> " + txtUmur.getText() + " tahun<br>" +
            "<b>Jenis Kelamin:</b> " + gender + "<br>" +
            "<b>Kategori:</b> " + cbKategori.getSelectedItem() + "<br>" +
            "<b>Kegiatan Dipilih:</b><br>" + kegiatanStr.toString() +
            "<b>Catatan:</b> " + txtCatatan.getText() + "</html>"
        );

        // Berpindah ke halaman ringkasan menggunakan CardLayout
        cardLayout.show(mainPanel, "RINGKASAN");
    }

    // --- FUNGSI RESET FORMULIR ---
    private void resetForm() {
        txtNama.setText("");
        txtEmail.setText("");
        txtUmur.setText("");
        rbLaki.setSelected(false);
        rbPerempuan.setSelected(false);
        cbKategori.setSelectedIndex(0);
        chkWorkshop.setSelected(false);
        chkSeminar.setSelected(false);
        chkSertifikat.setSelected(false);
        txtCatatan.setText("");
        txtNama.requestFocus(); // Kembalikan fokus ke kolom nama
    }

    // --- FUNGSI KONFIRMASI KELUAR APLIKASI ---
    private void konfirmasiKeluar() {
        int pilihan = JOptionPane.showConfirmDialog(
            this, 
            "Apakah Anda yakin ingin keluar dari aplikasi?", 
            "Konfirmasi Keluar", 
            JOptionPane.YES_NO_OPTION
        );
        if (pilihan == JOptionPane.YES_OPTION) {
            System.exit(0); // Menutup aplikasi
        }
    }

    // --- MAIN METHOD (Dijalankan via Event Dispatch Thread / EDT) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AplikasiPendaftaranLanjutan().setVisible(true);
        });
    }
}