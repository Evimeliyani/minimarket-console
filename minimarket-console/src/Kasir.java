import java.util.Scanner;

public class Kasir extends Pengguna {
    private String username;
    private String password;

    public Kasir(String id, String nama, String username, String password) {
        super(id, nama);
        this.username = username;
        this.password = password;
    }
    public boolean login(String u, String p) {
        return this.username.equals(u) && this.password.equals(p);
    }
    @Override public void tambahData(Scanner sc) {
        System.out.println("Tambah data Kasir: ID=" + id + ", Nama=" + nama + ", Username=" + username);
    }
    @Override public void lihatData() {
        System.out.println("Data Kasir: ID=" + id + ", Nama=" + nama + ", Username=" + username);
    }
}
