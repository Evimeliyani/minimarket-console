public class KeranjangItem {
    private Barang barang;
    private int qty;

    public KeranjangItem(Barang barang, int qty) {
        this.barang = barang;
        this.qty = qty;
    }
    public Barang getBarang() { return barang; }
    public int getQty() { return qty; }
    public void setQty(int q) { this.qty = q; }

    public int subtotal() { return barang.getHarga() * qty; }
}
