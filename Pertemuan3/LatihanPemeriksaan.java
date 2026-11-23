import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class LatihanPemeriksaan extends JFrame {

       
    static class StokModel extends DefaultTableModel {
        StokModel() {
            super(new Object[]{"ID", "Nama Barang", "Stok"}, 0);
            addRow(new Object[]{"P001", "Pulpen", 20});
            addRow(new Object[]{"P002", "Buku Tulis", 3});
            addRow(new Object[]{"P003", "Penggaris", 100});
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return col == 2 ? Integer.class : String.class; // stok = angka, agar urut numerik
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false; // model contoh tidak dapat diedit langsung
        }
    }

    private final StokModel model = new StokModel();
    private final JTable table = new JTable(model);
    private final TableRowSorter<StokModel> sorter = new TableRowSorter<>(model);
    private final JLabel status = new JLabel("Status: -");

    // Tabel hasil pemeriksaan: hasil aktual dan bukti dicatat di kolom terpisah
    private final DefaultTableModel hasilModel = new DefaultTableModel(
            new Object[]{"No", "Kasus / Tindakan", "Hasil yang Diharapkan",
                    "Hasil Aktual", "Bukti", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    public LatihanPemeriksaan() {
        super("Latihan Pemeriksaan Data Stok");
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnProses = new JButton("Proses Baris Terpilih");
        btnProses.addActionListener(e -> prosesBaris());

        JButton btnCek = new JButton("Jalankan Pemeriksaan");
        btnCek.addActionListener(e -> jalankanPemeriksaan());

        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        atas.add(btnProses);
        atas.add(btnCek);
        atas.add(status);

        JTable tabelHasil = new JTable(hasilModel);
        tabelHasil.setRowHeight(48);
        int[] lebar = {30, 180, 200, 260, 320, 60};
        for (int i = 0; i < lebar.length; i++) {
            tabelHasil.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
        tabelHasil.setDefaultRenderer(Object.class, new WrapRenderer());

        JScrollPane spData = new JScrollPane(table);
        spData.setPreferredSize(new Dimension(900, 120));
        JPanel tengah = new JPanel(new BorderLayout());
        tengah.add(atas, BorderLayout.NORTH);
        tengah.add(spData, BorderLayout.CENTER);

        setLayout(new BorderLayout(5, 5));
        add(tengah, BorderLayout.NORTH);
        add(new JScrollPane(tabelHasil), BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 450);
        setLocationRelativeTo(null);
    }

    // ---------- Aksi tombol: kembali false jika tidak ada baris dipilih ----------
    private boolean prosesBaris() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {                       // tidak ada pilihan -> jangan proses -1
            status.setText("Status: tidak ada baris dipilih, tidak diproses");
            return false;
        }
        int modelRow = table.convertRowIndexToModel(viewRow); // konversi setelah sorting
        String id = (String) model.getValueAt(modelRow, 0);
        status.setText("Status: memproses ID " + id);
        return true;
    }

    // ---------- Pemeriksaan ----------
    private void jalankanPemeriksaan() {
        hasilModel.setRowCount(0);
        periksaSortingStok();
        periksaPilihSetelahSorting();
        periksaTidakAdaPilihan();
        periksaKlikDuaKaliSel();
        for (int i = 0; i < hasilModel.getRowCount(); i++) {
            System.out.printf("[%s] %s | aktual: %s | bukti: %s%n",
                    hasilModel.getValueAt(i, 5), hasilModel.getValueAt(i, 1),
                    hasilModel.getValueAt(i, 3), hasilModel.getValueAt(i, 4));
        }
    }

    private void catat(String kasus, String harapan, String aktual, String bukti, boolean lulus) {
        hasilModel.addRow(new Object[]{hasilModel.getRowCount() + 1, kasus, harapan,
                aktual, bukti, lulus ? "LULUS" : "GAGAL"});
    }

    private void periksaSortingStok() {
        sorter.setSortKeys(List.of(new RowSorter.SortKey(2, SortOrder.ASCENDING)));
        List<Integer> urutan = new ArrayList<>();
        for (int v = 0; v < table.getRowCount(); v++) {
            urutan.add((Integer) table.getValueAt(v, 2));
        }
        boolean ok = urutan.equals(List.of(3, 20, 100));
        catat("Sorting stok 3,20,100", "Urutan numerik benar (3, 20, 100)",
                "Urutan tampil: " + urutan,
                "Kolom Stok bertipe " + model.getColumnClass(2).getSimpleName()
                        + "; jika String urutan salah (100, 20, 3)", ok);
    }

    private void periksaPilihSetelahSorting() {
        sorter.setSortKeys(List.of(new RowSorter.SortKey(2, SortOrder.ASCENDING)));
        StringBuilder bukti = new StringBuilder();
        boolean ok = true;
        for (int v = 0; v < table.getRowCount(); v++) {
            table.setRowSelectionInterval(v, v);
            String idTampil = (String) table.getValueAt(v, 0);               // ID di baris tampilan
            int m = table.convertRowIndexToModel(table.getSelectedRow());
            String idModel = (String) model.getValueAt(m, 0);                 // ID dari model
            ok &= idTampil.equals(idModel);
            bukti.append("view ").append(v).append("->model ").append(m)
                    .append(" (").append(idModel).append(") ");
        }
        catat("Pilih baris setelah sorting", "ID sesuai baris tampilan",
                ok ? "ID model sama dengan ID tampilan untuk semua baris"
                        : "Ada ID yang tidak sesuai",
                bukti.toString().trim(), ok);
    }

    private void periksaTidakAdaPilihan() {
        table.clearSelection();
        int terpilih = table.getSelectedRow();
        boolean diproses = prosesBaris();
        boolean ok = terpilih == -1 && !diproses;
        catat("Tidak ada pilihan", "Tombol tidak memproses baris -1",
                diproses ? "Baris diproses" : "Tombol tidak memproses baris",
                "getSelectedRow() = " + terpilih + "; teks status: " + status.getText(), ok);
    }

    private void periksaKlikDuaKaliSel() {
        MouseEvent dobelKlik = new MouseEvent(table, MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), 0, 10, 10, 2, false);
        boolean mulaiEdit = table.editCellAt(0, 1, dobelKlik);
        boolean semuaTakEditable = true;
        for (int r = 0; r < model.getRowCount(); r++) {
            for (int c = 0; c < model.getColumnCount(); c++) {
                semuaTakEditable &= !model.isCellEditable(r, c);
            }
        }
        boolean ok = !mulaiEdit && semuaTakEditable && !table.isEditing();
        catat("Klik dua kali sel", "Model contoh tidak dapat diedit langsung",
                mulaiEdit ? "Sel masuk mode edit" : "Sel tidak masuk mode edit",
                "editCellAt(0,1,klik x2) = " + mulaiEdit
                        + "; isEditing = " + table.isEditing()
                        + "; semua sel isCellEditable=false: " + semuaTakEditable, ok);
    }

    // Renderer agar teks panjang membungkus
    static class WrapRenderer extends JTextArea implements TableCellRenderer {
        WrapRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                       boolean foc, int r, int c) {
            setText(v == null ? "" : v.toString());
            setBackground(sel ? t.getSelectionBackground() : t.getBackground());
            if (c == 5) {
                setForeground("LULUS".equals(v) ? new Color(0, 128, 0) : Color.RED);
            } else {
                setForeground(t.getForeground());
            }
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LatihanPemeriksaan().setVisible(true));
    }
}