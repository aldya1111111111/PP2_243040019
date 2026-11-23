import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Aplikasi Inventaris Barang (Swing).
 *
 * Kompilasi        : javac InventarisBarang.java
 * Jalankan aplikasi: java InventarisBarang
 * Uji skenario     : java InventarisBarang --uji   (bukti Bagian 2, tanpa GUI)
 *
 * Data hanya disimpan di memori (tidak bertahan setelah aplikasi ditutup).
 */
public class InventarisBarang extends JFrame {

    // 1. MODEL DATA DAN PERHITUNGAN
    
    static class Barang {
        private static final Pattern ANGKA = Pattern.compile("\\d+(\\.\\d+)?|-\\d+(\\.\\d+)?");

        private final String kode;
        private final String nama;
        private final String kategori;
        private final int jumlah;
        private final double harga;

        Barang(String kode, String nama, String kategori, int jumlah, double harga) {
            this.kode = wajib(kode, "Kode");
            this.nama = wajib(nama, "Nama");
            this.kategori = wajib(kategori, "Kategori");
            if (jumlah < 0) {
                throw new IllegalArgumentException("Jumlah tidak boleh negatif.");
            }
            if (Double.isNaN(harga) || Double.isInfinite(harga)) {
                throw new IllegalArgumentException("Harga bukan angka yang valid.");
            }
            if (harga < 0) {
                throw new IllegalArgumentException("Harga tidak boleh negatif.");
            }
            this.jumlah = jumlah;
            this.harga = harga;
        }

        /** Membuat Barang dari teks form; melempar IllegalArgumentException bila tidak valid. */
        static Barang dariTeks(String kode, String nama, String kategori,
                               String jumlahTeks, String hargaTeks) {
            String k = wajib(kode, "Kode");
            String n = wajib(nama, "Nama");
            String kat = wajib(kategori, "Kategori");
            return new Barang(k, n, kat, bacaJumlah(jumlahTeks), bacaHarga(hargaTeks));
        }

        static int bacaJumlah(String teks) {
            try {
                return Integer.parseInt(teks == null ? "" : teks.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Jumlah harus berupa bilangan bulat (contoh: 10).");
            }
        }

        static double bacaHarga(String teks) {
            String t = teks == null ? "" : teks.trim();
            if (!ANGKA.matcher(t).matches()) {   // menolak huruf, NaN, Infinity, 1e3, 1f, dsb.
                throw new IllegalArgumentException("Harga harus berupa angka (contoh: 15000 atau 15000.50).");
            }
            double h = Double.parseDouble(t);
            if (Double.isInfinite(h)) {
                throw new IllegalArgumentException("Harga terlalu besar.");
            }
            return h;
        }

        private static String wajib(String teks, String label) {
            if (teks == null || teks.trim().isEmpty()) {
                throw new IllegalArgumentException(label + " tidak boleh kosong.");
            }
            return teks.trim();
        }

        String getKode() { return kode; }
        String getNama() { return nama; }
        String getKategori() { return kategori; }
        int getJumlah() { return jumlah; }
        double getHarga() { return harga; }

        /** Nilai persediaan = jumlah x harga. */
        double getNilaiPersediaan() { return jumlah * harga; }
    }

    // 2. TABLEMODEL (menyimpan koleksi Barang di memori)
   
    static class BarangTableModel extends AbstractTableModel {
        private static final String[] KOLOM =
                {"Kode", "Nama", "Kategori", "Jumlah", "Harga", "Nilai Persediaan"};

        private final List<Barang> data = new ArrayList<>();

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return KOLOM.length; }
        @Override public String getColumnName(int c) { return KOLOM[c]; }

        @Override
        public Class<?> getColumnClass(int c) {
            switch (c) {
                case 3:  return Integer.class;
                case 4:
                case 5:  return Double.class;
                default: return String.class;
            }
        }

        @Override
        public Object getValueAt(int r, int c) {
            Barang b = data.get(r);
            switch (c) {
                case 0:  return b.getKode();
                case 1:  return b.getNama();
                case 2:  return b.getKategori();
                case 3:  return b.getJumlah();
                case 4:  return b.getHarga();
                default: return b.getNilaiPersediaan();
            }
        }

        @Override public boolean isCellEditable(int r, int c) { return false; }

        Barang ambil(int indeks) {
            periksaIndeks(indeks);
            return data.get(indeks);
        }

        /** Mencari indeks kode (tidak peduli huruf besar/kecil), mengabaikan indeks 'kecuali'. */
        int cariKode(String kode, int kecuali) {
            for (int i = 0; i < data.size(); i++) {
                if (i != kecuali && data.get(i).getKode().equalsIgnoreCase(kode)) {
                    return i;
                }
            }
            return -1;
        }

        void tambah(Barang b) {
            if (cariKode(b.getKode(), -1) >= 0) {
                throw new IllegalArgumentException("Kode '" + b.getKode() + "' sudah dipakai barang lain.");
            }
            data.add(b);
            fireTableRowsInserted(data.size() - 1, data.size() - 1);
        }

        void ubah(int indeks, Barang baru) {
            periksaIndeks(indeks);
            if (cariKode(baru.getKode(), indeks) >= 0) {
                throw new IllegalArgumentException("Kode '" + baru.getKode() + "' sudah dipakai barang lain.");
            }
            data.set(indeks, baru);
            fireTableRowsUpdated(indeks, indeks);
        }

        Barang hapus(int indeks) {
            periksaIndeks(indeks);
            Barang b = data.remove(indeks);
            fireTableRowsDeleted(indeks, indeks);
            return b;
        }

        double totalNilai() {
            double total = 0;
            for (Barang b : data) {
                total += b.getNilaiPersediaan();
            }
            return total;
        }

        long totalUnit() {
            long total = 0;
            for (Barang b : data) {
                total += b.getJumlah();
            }
            return total;
        }

        private void periksaIndeks(int indeks) {
            if (indeks < 0 || indeks >= data.size()) {
                throw new IllegalArgumentException("Tidak ada baris yang dipilih.");
            }
        }
    }

    /** Minimal 10 barang dengan 3 kategori. */
    static BarangTableModel dataContoh() {
        BarangTableModel m = new BarangTableModel();
        m.tambah(new Barang("E001", "Mouse Wireless", "Elektronik", 25, 150000));
        m.tambah(new Barang("E002", "Keyboard Mekanik", "Elektronik", 10, 650000));
        m.tambah(new Barang("E003", "Monitor 24 inci", "Elektronik", 5, 1800000));
        m.tambah(new Barang("E004", "Headset", "Elektronik", 15, 275000));
        m.tambah(new Barang("A001", "Pulpen Gel", "ATK", 120, 4500));
        m.tambah(new Barang("A002", "Buku Tulis", "ATK", 80, 7000));
        m.tambah(new Barang("A003", "Kertas A4 (rim)", "ATK", 40, 52000));
        m.tambah(new Barang("A004", "Stapler", "ATK", 30, 18000));
        m.tambah(new Barang("P001", "Kursi Kantor", "Perabot", 8, 850000));
        m.tambah(new Barang("P002", "Meja Lipat", "Perabot", 12, 420000));
        m.tambah(new Barang("P003", "Lemari Arsip", "Perabot", 0, 1750000)); // batas: jumlah 0
        return m;
    }

    // 3. FORM DAN INTERAKSI TABEL

    private static final String[] KATEGORI = {"Elektronik", "ATK", "Perabot"};

    private final BarangTableModel model = dataContoh();
    private final JTable table = new JTable(model);

    private final JTextField tfKode = new JTextField(14);
    private final JTextField tfNama = new JTextField(14);
    private final JComboBox<String> cbKategori = new JComboBox<>(KATEGORI);
    private final JTextField tfJumlah = new JTextField(14);
    private final JTextField tfHarga = new JTextField(14);
    private final JLabel lblNilai = new JLabel("-");
    private final JLabel lblTotal = new JLabel();
    private final JLabel lblStatus = new JLabel(" ");

    static String rp(double nilai) {
        NumberFormat f = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"));
        f.setMinimumFractionDigits(0);
        f.setMaximumFractionDigits(2);
        return "Rp " + f.format(nilai);
    }

    static class RupiahRenderer extends DefaultTableCellRenderer {
        RupiahRenderer() { setHorizontalAlignment(RIGHT); }

        @Override
        protected void setValue(Object v) {
            setText(v instanceof Number ? rp(((Number) v).doubleValue()) : "");
        }
    }

    public InventarisBarang() {
        super("Inventaris Barang");
        cbKategori.setEditable(true);

        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Double.class, new RupiahRenderer());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariBaris();
            }
        });
        model.addTableModelListener(e -> perbaruiTotal());

        DocumentListener pantau = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { perbaruiPratinjau(); }
            public void removeUpdate(DocumentEvent e) { perbaruiPratinjau(); }
            public void changedUpdate(DocumentEvent e) { perbaruiPratinjau(); }
        };
        tfJumlah.getDocument().addDocumentListener(pantau);
        tfHarga.getDocument().addDocumentListener(pantau);

        setLayout(new BorderLayout(6, 6));
        add(buatForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new GridLayout(2, 1));
        bawah.setBorder(BorderFactory.createEmptyBorder(2, 8, 6, 8));
        bawah.add(lblTotal);
        bawah.add(lblStatus);
        add(bawah, BorderLayout.SOUTH);

        perbaruiTotal();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
    }

    private JPanel buatForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Form Barang"));

        tambahBaris(p, 0, "Kode", tfKode, "Kategori", cbKategori);
        tambahBaris(p, 1, "Nama", tfNama, "Jumlah", tfJumlah);
        tambahBaris(p, 2, "Harga (Rp)", tfHarga, "Nilai persediaan", lblNilai);

        JButton btnTambah = new JButton("Tambah");
        JButton btnUbah = new JButton("Ubah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnBersih = new JButton("Bersihkan");
        btnTambah.addActionListener(e -> aksiTambah());
        btnUbah.addActionListener(e -> aksiUbah());
        btnHapus.addActionListener(e -> aksiHapus());
        btnBersih.addActionListener(e -> bersihkan());

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tombol.add(btnTambah);
        tombol.add(btnUbah);
        tombol.add(btnHapus);
        tombol.add(btnBersih);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 4;
        g.anchor = GridBagConstraints.WEST;
        p.add(tombol, g);
        return p;
    }

    private void tambahBaris(JPanel p, int baris, String l1, JComponent c1, String l2, JComponent c2) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridy = baris;

        g.gridx = 0; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        p.add(new JLabel(l1), g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(c1, g);
        g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        p.add(new JLabel(l2), g);
        g.gridx = 3; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(c2, g);
    }

    private String teksKategori() {
        Object o = cbKategori.getEditor().getItem();
        return o == null ? "" : o.toString();
    }

    private Barang bacaForm() {
        return Barang.dariTeks(tfKode.getText(), tfNama.getText(), teksKategori(),
                tfJumlah.getText(), tfHarga.getText());
    }

    /** Indeks baris pada model (setelah sorting) atau -1 jika tidak ada yang dipilih. */
    private int indeksModelTerpilih() {
        int v = table.getSelectedRow();
        return v < 0 ? -1 : table.convertRowIndexToModel(v);
    }

    private void aksiTambah() {
        try {
            Barang b = bacaForm();
            model.tambah(b);
            bersihkan();
            lblStatus.setText("Barang " + b.getKode() + " ditambahkan.");
        } catch (IllegalArgumentException ex) {
            peringatan(ex.getMessage());
        }
    }

    private void aksiUbah() {
        int idx = indeksModelTerpilih();
        if (idx < 0) {
            peringatan("Pilih baris yang akan diubah terlebih dahulu.");
            return;
        }
        try {
            Barang b = bacaForm();
            model.ubah(idx, b);
            bersihkan();
            lblStatus.setText("Barang " + b.getKode() + " diubah.");
        } catch (IllegalArgumentException ex) {
            peringatan(ex.getMessage());
        }
    }

    private void aksiHapus() {
        int idx = indeksModelTerpilih();
        if (idx < 0) {
            peringatan("Pilih baris yang akan dihapus terlebih dahulu.");
            return;
        }
        Barang b = model.ambil(idx);
        int jawab = JOptionPane.showConfirmDialog(this,
                "Hapus barang " + b.getKode() + " - " + b.getNama() + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (jawab == JOptionPane.YES_OPTION) {
            model.hapus(idx);
            bersihkan();
            lblStatus.setText("Barang " + b.getKode() + " dihapus.");
        }
    }

    private void isiFormDariBaris() {
        int idx = indeksModelTerpilih();
        if (idx < 0) {
            return;
        }
        Barang b = model.ambil(idx);
        tfKode.setText(b.getKode());
        tfNama.setText(b.getNama());
        cbKategori.setSelectedItem(b.getKategori());
        tfJumlah.setText(String.valueOf(b.getJumlah()));
        tfHarga.setText(hargaTeks(b.getHarga()));
    }

    private static String hargaTeks(double h) {
        return h == Math.rint(h) ? String.valueOf((long) h) : String.valueOf(h);
    }

    private void bersihkan() {
        table.clearSelection();
        tfKode.setText("");
        tfNama.setText("");
        cbKategori.setSelectedIndex(0);
        tfJumlah.setText("");
        tfHarga.setText("");
        tfKode.requestFocusInWindow();
    }

    private void perbaruiPratinjau() {
        try {
            int j = Barang.bacaJumlah(tfJumlah.getText());
            double h = Barang.bacaHarga(tfHarga.getText());
            lblNilai.setText(j < 0 || h < 0 ? "-" : rp(j * h));
        } catch (IllegalArgumentException e) {
            lblNilai.setText("-");
        }
    }

    private void perbaruiTotal() {
        lblTotal.setText("Jenis barang: " + model.getRowCount()
                + "   |   Total unit: " + model.totalUnit()
                + "   |   Total nilai persediaan: " + rp(model.totalNilai()));
    }

    private void peringatan(String pesan) {
        lblStatus.setText(" ");
        JOptionPane.showMessageDialog(this, pesan, "Data tidak valid", JOptionPane.WARNING_MESSAGE);
    }

    // =====================================================================
    // 4. PENGUJIAN SKENARIO (Bagian 2) - java InventarisBarang --uji
    // =====================================================================
    private static int lulus = 0;
    private static int gagal = 0;

    private static void cek(String kasus, boolean ok, String bukti) {
        if (ok) { lulus++; } else { gagal++; }
        System.out.printf("[%s] %s%n        bukti: %s%n", ok ? "LULUS" : "GAGAL", kasus, bukti);
    }

    /** Menjalankan aksi; mengembalikan pesan galat bila ditolak, atau null bila diterima. */
    private static String tolak(Runnable aksi) {
        try {
            aksi.run();
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    private static void ujiSkenario() {
        BarangTableModel m = dataContoh();

        // Data awal
        java.util.Set<String> kat = new java.util.TreeSet<>();
        for (int i = 0; i < m.getRowCount(); i++) {
            kat.add(m.ambil(i).getKategori());
        }
        cek("Data awal minimal 10 barang dan 3 kategori",
                m.getRowCount() >= 10 && kat.size() == 3,
                m.getRowCount() + " barang, kategori = " + kat);

        // Perhitungan
        Barang mouse = m.ambil(0);
        cek("Nilai persediaan = jumlah x harga",
                mouse.getNilaiPersediaan() == 25 * 150000.0,
                mouse.getKode() + ": 25 x 150000 = " + rp(mouse.getNilaiPersediaan()));

        // Tambah
        int n0 = m.getRowCount();
        double t0 = m.totalNilai();
        m.tambah(new Barang("X001", "Flashdisk 64GB", "Elektronik", 10, 80000));
        cek("Tambah barang valid",
                m.getRowCount() == n0 + 1 && m.totalNilai() == t0 + 800000.0,
                "baris " + n0 + " -> " + m.getRowCount() + "; total " + rp(t0) + " -> " + rp(m.totalNilai()));

        // Pilih baris (dengan sorting) -> data baris sesuai
        JTable uji = new JTable(m);
        uji.setAutoCreateRowSorter(true);
        uji.getRowSorter().setSortKeys(java.util.Collections.singletonList(
                new RowSorter.SortKey(3, SortOrder.DESCENDING)));   // urut jumlah menurun
        uji.setRowSelectionInterval(0, 0);
        int idx = uji.convertRowIndexToModel(uji.getSelectedRow());
        String kodeTampil = (String) uji.getValueAt(0, 0);
        cek("Pilih baris setelah sorting memetakan ke data yang benar",
                m.ambil(idx).getKode().equals(kodeTampil),
                "baris tampilan 0 = " + kodeTampil + ", indeks model " + idx + " = " + m.ambil(idx).getKode());

        // Ubah
        int iX = m.cariKode("X001", -1);
        m.ubah(iX, new Barang("X001", "Flashdisk 64GB", "Elektronik", 20, 85000));
        cek("Ubah barang memperbarui nilai persediaan",
                m.ambil(iX).getNilaiPersediaan() == 20 * 85000.0,
                "X001 menjadi 20 x 85000 = " + rp(m.ambil(iX).getNilaiPersediaan()));

        cek("Ubah dengan kode sendiri diperbolehkan", tolak(() ->
                m.ubah(iX, new Barang("X001", "Flashdisk", "Elektronik", 1, 1))) == null,
                "kode X001 tetap diterima");

        // Hapus
        int n1 = m.getRowCount();
        m.hapus(iX);
        cek("Hapus barang", m.getRowCount() == n1 - 1 && m.cariKode("X001", -1) < 0,
                "baris " + n1 + " -> " + m.getRowCount());

        // Penolakan kode
        int n2 = m.getRowCount();
        String p1 = tolak(() -> m.tambah(new Barang("e001", "Duplikat", "Elektronik", 1, 1)));
        cek("Tolak kode duplikat (tidak peduli huruf besar/kecil)",
                p1 != null && m.getRowCount() == n2, "pesan: " + p1);

        String p2 = tolak(() -> Barang.dariTeks("   ", "Nama", "ATK", "1", "1"));
        cek("Tolak kode kosong", p2 != null, "pesan: " + p2);

        String p3 = tolak(() -> m.ubah(0, new Barang("E002", "Mouse", "Elektronik", 1, 1)));
        cek("Tolak ubah ke kode milik barang lain", p3 != null, "pesan: " + p3);

        // Penolakan angka
        String[][] angkaSalah = {
                {"-5", "1000", "jumlah negatif"},
                {"abc", "1000", "jumlah bukan angka"},
                {"3.5", "1000", "jumlah desimal"},
                {"", "1000", "jumlah kosong"},
                {"5", "-100", "harga negatif"},
                {"5", "abc", "harga bukan angka"},
                {"5", "NaN", "harga NaN"},
                {"5", "1e3", "harga notasi ilmiah"},
        };
        for (String[] a : angkaSalah) {
            String pesan = tolak(() -> Barang.dariTeks("Z001", "Uji", "ATK", a[0], a[1]));
            cek("Tolak " + a[2], pesan != null,
                    "jumlah='" + a[0] + "', harga='" + a[1] + "' -> " + pesan);
        }

        // Batas
        Barang nol = Barang.dariTeks("Z002", "Batas", "ATK", "0", "0");
        cek("Terima jumlah 0 dan harga 0 (batas)", nol.getNilaiPersediaan() == 0,
                "nilai persediaan = " + rp(nol.getNilaiPersediaan()));

        // Tanpa baris terpilih
        String u = tolak(() -> m.ubah(-1, new Barang("Z003", "Uji", "ATK", 1, 1)));
        cek("Ubah tanpa baris terpilih ditolak", u != null, "pesan: " + u);
        String h = tolak(() -> m.hapus(-1));
        cek("Hapus tanpa baris terpilih ditolak", h != null, "pesan: " + h);

        System.out.println();
        System.out.println("Ringkasan: " + lulus + " lulus, " + gagal + " gagal");
        System.exit(gagal == 0 ? 0 : 1);
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--uji")) {
            ujiSkenario();
            return;
        }
        SwingUtilities.invokeLater(() -> new InventarisBarang().setVisible(true));
    }
}