import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MinimarketApp app = new MinimarketApp();
        app.inisialisasiData();

        // Login loop
        Scanner sc = new Scanner(System.in);
        boolean ok = false;
        while (!ok) {
            System.out.println("Login Aplikasi");
            System.out.print("Username: ");
            String u = sc.nextLine();
            System.out.print("Password: ");
            String p = sc.nextLine();
            if (app.loginKasir(u, p)) {
                ok = true;
                System.out.println("Login berhasil!");
            } else {
                System.out.println("Username atau password salah. Coba lagi.");
            }
        }

        // Menu utama
        app.menuUtama();
        sc.close();
    }
}
