import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MinimarketApp {
    private ArrayList<Barang> daftarBarang = new ArrayList<>();
    private ArrayList<Transaksi> riwayatTransaksi = new ArrayList<>();
    private Kasir kasirAktif;
    private final Scanner sc = new Scanner(System.in);

    // ==== Util Path ====
    private final Path dataDir = Paths.get("data");
    private final Path receiptsDir = Paths.get("receipts");
    private Path salesFileToday() {
        String fname = "sales_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";
        return dataDir.resolve(fname);
    }

    // ==== Inisialisasi ====
    public void inisialisasiData() {
        try {
            if (!Files.exists(dataDir)) Files.createDirectories(dataDir);
            if (!Files.exists(receiptsDir)) Files.createDirectories(receiptsDir);
            Path itemsCsv = dataDir.resolve("items.csv");
            if (Files.exists(itemsCsv)) {
                loadItems(itemsCsv);
            } else {
                // seed minimal
                daftarBarang.add(new Barang("111", "Gula 1kg", 14000, 10));
                daftarBarang.add(new Barang("222", "Mie Instan Ayam", 3500, 30));
                saveItems(itemsCsv);
            }
        } catch (IOException e) {
            System.out.println("Gagal inisialisasi data: " + e.getMessage());
        }
    }

    // ==== Login ====
    public boolean loginKasir(String u, String p) {
        // Demo: 1 user default
        Kasir admin = new Kasir("K1", "Admin", "admin", "pass");
        if (admin.login(u, p)) { kasirAktif = admin; return true; }
        return false;
    }

    // ==== Menu ====
    public void menuUtama() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== KASIR MINIMARKET ===");
            System.out.println("1. Barang");
            System.out.println("2. Transaksi");
            System.out.println("3. Laporan Harian");
            System.out.println("4. Keluar");
            System.out.print("Pilih: ");
            String pilih = sc.nextLine();
            switch (pilih) {
                case "1": menuBarang(); break;
                case "2": menuTransaksi(); break;
                case "3": menuLaporan(); break;
                case "4": running = false; break;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // ==== Barang ====
    private void menuBarang() {
        System.out.println("\nMenu Barang");
        System.out.println("1. Tambah  2. Lihat  3. Hapus  0. Kembali");
        System.out.print("Pilih: ");
        String p = sc.nextLine();
        switch (p) {
            case "1": tambahBarang(); break;
            case "2": lihatBarang(); break;
            case "3": hapusBarang(); break;
            default: break;
        }
    }
    private void tambahBarang() {
        try {
            System.out.print("Kode: "); String kode = sc.nextLine().trim();
            if (findBarang(kode) != null) { System.out.println("Kode sudah ada."); return; }
            System.out.print("Nama: "); String nama = sc.nextLine().trim();
            System.out.print("Harga: "); int harga = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Stok: "); int stok = Integer.parseInt(sc.nextLine().trim());
            Barang b = new Barang(kode, nama, harga, stok);
            daftarBarang.add(b);
            b.tambahData(sc);
            saveItems(dataDir.resolve("items.csv"));
        } catch (Exception e) {
            System.out.println("Input tidak valid.");
        }
    }
    private void lihatBarang() {
        System.out.println("Daftar Barang:");
        for (Barang b : daftarBarang) b.lihatData();
    }
    private void hapusBarang() {
        System.out.print("Masukkan Kode: ");
        String kode = sc.nextLine().trim();
        Barang b = findBarang(kode);
        if (b == null) { System.out.println("Kode tidak ada."); return; }
        daftarBarang.remove(b);
        System.out.println("Dihapus: " + kode);
        try { saveItems(dataDir.resolve("items.csv")); } catch (Exception ignored) {}
    }
    private Barang findBarang(String kode) {
        for (Barang b : daftarBarang) if (b.getKode().equalsIgnoreCase(kode)) return b;
        return null;
    }
    private void loadItems(Path itemsCsv) throws IOException {
        List<String> lines = Files.readAllLines(itemsCsv);
        for (String ln : lines) {
            if (ln.trim().isEmpty() || ln.startsWith("#")) continue;
            String[] p = ln.split(";", -1);
            if (p.length >= 4) {
                daftarBarang.add(new Barang(p[0], p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3])));
            }
        }
    }
    private void saveItems(Path itemsCsv) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("#kode;nama;harga;stok\n");
        for (Barang b : daftarBarang) {
            sb.append(b.getKode()).append(";")
              .append(b.getNama()).append(";")
              .append(b.getHarga()).append(";")
              .append(b.getStok()).append("\n");
        }
        Files.write(itemsCsv, sb.toString().getBytes());
    }

    // ==== Transaksi ====
    private void menuTransaksi() {
        String trxId = "TRX" + String.format("%04d", riwayatTransaksi.size() + 1);
        Transaksi trx = new Transaksi(trxId, kasirAktif);
        System.out.println("\nTransaksi Baru (ID=" + trxId + ")");
        while (true) {
            System.out.print("Masukkan Kode (atau 'bayar'): ");
            String in = sc.nextLine().trim();
            if (in.equalsIgnoreCase("bayar")) break;
            Barang b = findBarang(in);
            if (b == null) { System.out.println("Kode tidak ada."); continue; }
            System.out.print("Qty: ");
            int qty;
            try { qty = Integer.parseInt(sc.nextLine().trim()); }
            catch (Exception e) { System.out.println("Qty tidak valid."); continue; }
            if (qty <= 0) { System.out.println("Qty harus > 0"); continue; }
            if (b.getStok() < qty) { System.out.println("Stok tidak cukup."); continue; }
            trx.tambahItem(b, qty);
            System.out.println("=> Keranjang: " + b.getKode() + " x" + qty + " (@" + b.getHarga() + ") = " + (b.getHarga()*qty));
        }

        System.out.print("Diskon global (rupiah, 0 jika tidak): ");
        int diskon = 0;
        try { diskon = Integer.parseInt(sc.nextLine().trim()); } catch (Exception ignored) {}
        trx.setDiskonGlobal(diskon);

        System.out.print("PPN aktif? (y/n) [default y=11%]: ");
        String ppnOn = sc.nextLine().trim();
        if (ppnOn.equalsIgnoreCase("n")) trx.setPpnPersen(0); else trx.setPpnPersen(11);

        trx.hitungSubtotal();
        trx.hitungTotal();
        System.out.println("Ringkasan:");
        System.out.println("Subtotal : " + trx.getSubtotal());
        System.out.println("PPN(" + trx.getPpnPersen() + "%) & Diskon : -"+trx.getDiskonGlobal()+" + PPN dihitung");
        System.out.println("Total    : " + trx.getTotal());

        System.out.print("Bayar: ");
        int bayar = 0;
        try { bayar = Integer.parseInt(sc.nextLine().trim()); } catch (Exception ignored) {}
        if (!trx.prosesPembayaran(bayar)) {
            System.out.println("Uang tidak cukup. Transaksi dibatalkan.");
            return;
        }

        // Update stok & simpan
        for (KeranjangItem it : trx.getItems()) {
            it.getBarang().kurangiStok(it.getQty());
        }
        try {
            saveItems(dataDir.resolve("items.csv"));
            appendSale(trx);
            cetakStruk(trx);
            riwayatTransaksi.add(trx);
            System.out.println("Transaksi sukses. Kembalian: " + trx.getKembali());
        } catch (IOException e) {
            System.out.println("Gagal menyimpan transaksi/struk: " + e.getMessage());
        }
    }

    private void appendSale(Transaksi trx) throws IOException {
        Path f = salesFileToday();
        boolean exists = Files.exists(f);
        try (BufferedWriter bw = Files.newBufferedWriter(f, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (!exists) {
                bw.write("#id;waktu;kasir;kode;nama;qty;harga;subtotal;diskon;ppn%;total;bayar;kembali\n");
            }
            for (KeranjangItem it : trx.getItems()) {
                Barang b = it.getBarang();
                bw.write(String.join(";",
                    trx.getId(),
                    trx.waktuStr(),
                    kasirAktif.getNama(),
                    b.getKode(),
                    b.getNama(),
                    String.valueOf(it.getQty()),
                    String.valueOf(b.getHarga()),
                    String.valueOf(trx.getSubtotal()),
                    String.valueOf(trx.getDiskonGlobal()),
                    String.valueOf(trx.getPpnPersen()),
                    String.valueOf(trx.getTotal()),
                    String.valueOf(trx.getBayar()),
                    String.valueOf(trx.getKembali())
                ));
                bw.newLine();
            }
        }
    }

    // Cetak struk sebagai file .txt
    private void cetakStruk(Transaksi trx) throws IOException {
        Path file = receiptsDir.resolve(trx.getId() + ".txt");
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            bw.write("==== STRUK MINIMARKET ====\n");
            bw.write("Tanggal : " + trx.waktuStr() + "\n");
            bw.write("Kasir   : " + kasirAktif.getNama() + "\n");
            bw.write("---------------------------\n");
            for (KeranjangItem it : trx.getItems()) {
                Barang b = it.getBarang();
                int lineTotal = it.subtotal();
                bw.write(String.format("%s x%d @%d %d\n", b.getKode(), it.getQty(), b.getHarga(), lineTotal));
            }
            bw.write("---------------------------\n");
            bw.write(String.format("SUBTOTAL %d\n", trx.getSubtotal()));
            bw.write(String.format("DISKON   %d\n", trx.getDiskonGlobal()));
            int afterDiskon = Math.max(0, trx.getSubtotal() - trx.getDiskonGlobal());
            int ppn = afterDiskon * trx.getPpnPersen() / 100;
            bw.write(String.format("PPN %d%%  %d\n", trx.getPpnPersen(), ppn));
            bw.write(String.format("TOTAL    %d\n", trx.getTotal()));
            bw.write(String.format("BAYAR    %d\n", trx.getBayar()));
            bw.write(String.format("KEMBALI  %d\n", trx.getKembali()));
            bw.write("===========================\n");
            bw.write("Terima kasih.\n");
        }
        System.out.println("=> Struk tersimpan: " + file.toString());
    }

    // ==== Laporan Harian ====
    private void menuLaporan() {
        Path f = salesFileToday();
        if (!Files.exists(f)) {
            System.out.println("Belum ada transaksi hari ini.");
            return;
        }
        try {
            int totalTrx = 0;
            long omzet = 0;
            List<String> lines = Files.readAllLines(f);
            // baris transaksi per item → hitung berdasarkan ID unik
            Set<String> seenId = new HashSet<>();
            for (String ln : lines) {
                if (ln.startsWith("#") || ln.trim().isEmpty()) continue;
                String[] p = ln.split(";", -1);
                if (p.length >= 13) {
                    String id = p[0];
                    int total = Integer.parseInt(p[10]);
                    if (!seenId.contains(id)) {
                        totalTrx++;
                        omzet += total;
                        seenId.add(id);
                    }
                }
            }
            System.out.println("Laporan Harian (" + LocalDate.now() + ")");
            System.out.println("Jumlah Transaksi : " + totalTrx);
            System.out.println("Omzet            : " + omzet);
        } catch (IOException e) {
            System.out.println("Gagal membaca laporan: " + e.getMessage());
        }
    }
}
