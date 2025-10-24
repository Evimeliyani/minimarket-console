import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Transaksi {
    private String id;
    private LocalDateTime waktu;
    private Kasir kasir;
    private ArrayList<KeranjangItem> items = new ArrayList<>();
    private int subtotal = 0;
    private int diskonGlobal = 0; // nominal rupiah
    private int ppnPersen = 11;   // default 11%
    private int total = 0;
    private int bayar = 0;
    private int kembali = 0;

    public Transaksi(String id, Kasir kasir) {
        this.id = id;
        this.kasir = kasir;
        this.waktu = LocalDateTime.now();
    }
    public String getId() { return id; }
    public LocalDateTime getWaktu() { return waktu; }
    public ArrayList<KeranjangItem> getItems() { return items; }
    public int getSubtotal() { return subtotal; }
    public int getDiskonGlobal() { return diskonGlobal; }
    public int getPpnPersen() { return ppnPersen; }
    public int getTotal() { return total; }
    public int getBayar() { return bayar; }
    public int getKembali() { return kembali; }

    public void tambahItem(Barang b, int qty) {
        items.add(new KeranjangItem(b, qty));
    }
    public void setDiskonGlobal(int rupiah) { this.diskonGlobal = Math.max(0, rupiah); }
    public void setPpnPersen(int ppn) { this.ppnPersen = Math.max(0, ppn); }

    public void hitungSubtotal() {
        subtotal = 0;
        for (KeranjangItem it : items) subtotal += it.subtotal();
    }
    public void hitungTotal() {
        int afterDiskon = Math.max(0, subtotal - diskonGlobal);
        int ppn = afterDiskon * ppnPersen / 100;
        total = afterDiskon + ppn;
    }
    public boolean prosesPembayaran(int bayar) {
        this.bayar = bayar;
        if (bayar < total) return false;
        this.kembali = bayar - total;
        return true;
    }

    public String waktuStr() {
        return waktu.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
