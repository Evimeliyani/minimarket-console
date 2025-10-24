import java.util.Scanner;

public class Barang {
    private String kode;
    private String nama;
    private int harga; // rupiah
    private int stok;

    public Barang(String kode, String nama, int harga, int stok) {
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
    }
    public String getKode() { return kode; }
    public String getNama() { return nama; }
    public int getHarga() { return harga; }
    public int getStok() { return stok; }

    public void setHarga(int harga) { this.harga = harga; }
    public void setStok(int stok) { this.stok = stok; }
    public void kurangiStok(int qty) { if (qty <= stok) stok -= qty; }

    public void tambahData(Scanner sc) {
        System.out.println("Tambah Barang: " + kode + " - " + nama + " @" + harga + " stok " + stok);
    }
    public void lihatData() {
        System.out.println(kode + " - " + nama + " @" + harga + " stok " + stok);
    }
}
