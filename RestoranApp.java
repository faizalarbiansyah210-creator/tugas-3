import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

abstract class MenuItem implements Serializable {
    private String nama;
    private double harga;
    private String kategori;

    public MenuItem(String nama, double harga, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
    }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public abstract void tampilMenu();
}

class Makanan extends MenuItem {
    private String jenisMakanan;

    public Makanan(String nama, double harga, String jenisMakanan) {
        super(nama, harga, "Makanan");
        this.jenisMakanan = jenisMakanan;
    }

    public String getJenisMakanan() { return jenisMakanan; }

    @Override
    public void tampilMenu() {
        System.out.println("[Makanan] " + getNama() + " (" + jenisMakanan + ") - Rp " + getHarga());
    }
}

class Minuman extends MenuItem {
    private String jenisMinuman;

    public Minuman(String nama, double harga, String jenisMinuman) {
        super(nama, harga, "Minuman");
        this.jenisMinuman = jenisMinuman;
    }

    public String getJenisMinuman() { return jenisMinuman; }

    @Override
    public void tampilMenu() {
        System.out.println("[Minuman] " + getNama() + " (" + jenisMinuman + ") - Rp " + getHarga());
    }
}

class Diskon extends MenuItem {
    private double persentaseDiskon;

    public Diskon(String nama, double persentaseDiskon) {
        super(nama, 0, "Diskon");
        this.persentaseDiskon = persentaseDiskon;
    }

    public double getPersentaseDiskon() { return persentaseDiskon; }

    @Override
    public void tampilMenu() {
        System.out.println("[Diskon] " + getNama() + " - Potongan: " + persentaseDiskon + "%");
    }
}

class Menu {
    private ArrayList<MenuItem> daftarItem = new ArrayList<>();

    public void tambahItem(MenuItem item) {
        daftarItem.add(item);
    }

    public ArrayList<MenuItem> getDaftarItem() {
        return daftarItem;
    }

    public void tampilkanSemuaMenu() {
        if (daftarItem.isEmpty()) {
            System.out.println("Menu restoran masih kosong.");
            return;
        }
        System.out.println("\n--- DAFTAR MENU RESTORAN ---");
        for (int i = 0; i < daftarItem.size(); i++) {
            System.out.print((i + 1) + ". ");
            daftarItem.get(i).tampilMenu();
        }
    }

    public MenuItem getItem(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= daftarItem.size()) {
            throw new IndexOutOfBoundsException("Item dengan nomor tersebut tidak ada dalam menu!");
        }
        return daftarItem.get(index);
    }
}

class Pesanan {
    private ArrayList<MenuItem> itemPesanan = new ArrayList<>();
    private Diskon diskonTerapan = null;

    public void tambahPesanan(MenuItem item) {
        if (item instanceof Diskon) {
            this.diskonTerapan = (Diskon) item;
            System.out.println("Diskon '" + item.getNama() + "' berhasil diterapkan ke pesanan.");
        } else {
            itemPesanan.add(item);
            System.out.println(item.getNama() + " ditambahkan ke pesanan.");
        }
    }

    public double hitungTotal() {
        double total = 0;
        for (MenuItem item : itemPesanan) {
            total += item.getHarga();
        }
        if (diskonTerapan != null) {
            double potongan = total * (diskonTerapan.getPersentaseDiskon() / 100.0);
            total -= potongan;
        }
        return total;
    }

    public void cetakStruk(String namaFile) {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================\n");
        sb.append("          STRUK PESANAN           \n");
        sb.append("==================================\n");
        if (itemPesanan.isEmpty()) {
            sb.append("Belum ada item makanan/minuman yang dipesan.\n");
        } else {
            for (MenuItem item : itemPesanan) {
                sb.append("- ").append(item.getNama()).append(" : Rp ").append(item.getHarga()).append("\n");
            }
        }
        
        double subtotal = 0;
        for (MenuItem item : itemPesanan) {
            subtotal += item.getHarga();
        }
        sb.append("----------------------------------\n");
        sb.append("Subtotal : Rp ").append(subtotal).append("\n");
        
        if (diskonTerapan != null) {
            double potongan = subtotal * (diskonTerapan.getPersentaseDiskon() / 100.0);
            sb.append("Diskon (").append(diskonTerapan.getNama()).append(" - ").append(diskonTerapan.getPersentaseDiskon()).append("%) : -Rp ").append(potongan).append("\n");
        }
        sb.append("Total Akhir : Rp ").append(hitungTotal()).append("\n");
        sb.append("==================================\n");

        System.out.println(sb.toString());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(namaFile))) {
            writer.write(sb.toString());
            System.out.println("Struk berhasil disimpan ke file: " + namaFile);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan struk ke file: " + e.getMessage());
        }
    }
}

public class RestoranApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Menu menuRestoran = new Menu();
        Pesanan pesananPelanggan = new Pesanan();
        String fileMenuTxt = "menu_restoran.txt";

        muatMenuDariFile(menuRestoran, fileMenuTxt);

        int pilihan = 0;
        do {
            System.out.println("\n=== APLIKASI MANAJEMEN RESTORAN ===");
            System.out.println("1. Tambah Item Baru ke Menu");
            System.out.println("2. Tampilkan Menu Restoran");
            System.out.println("3. Terima Pesanan dari Pelanggan");
            System.out.println("4. Hitung Total Biaya Pesanan");
            System.out.println("5. Tampilkan & Simpan Struk Pesanan");
            System.out.println("6. Keluar");
            System.out.print("Pilih opsi (1-6): ");

            try {
                String inputStr = scanner.nextLine();
                pilihan = Integer.parseInt(inputStr);

                switch (pilihan) {
                    case 1:
                        System.out.println("\nPilih Kategori:");
                        System.out.println("1. Makanan");
                        System.out.println("2. Minuman");
                        System.out.println("3. Diskon");
                        System.out.print("Masukkan pilihan kategori: ");
                        int kat = Integer.parseInt(scanner.nextLine());

                        System.out.print("Masukkan Nama Item/Diskon: ");
                        String nama = scanner.nextLine();

                        if (kat == 1) {
                            System.out.print("Masukkan Harga: ");
                            double harga = Double.parseDouble(scanner.nextLine());
                            System.out.print("Masukkan Jenis Makanan (cth: Berat/Ringan): ");
                            String jenis = scanner.nextLine();
                            menuRestoran.tambahItem(new Makanan(nama, harga, jenis));
                        } else if (kat == 2) {
                            System.out.print("Masukkan Harga: ");
                            double harga = Double.parseDouble(scanner.nextLine());
                            System.out.print("Masukkan Jenis Minuman (cth: Dingin/Hangat): ");
                            String jenis = scanner.nextLine();
                            menuRestoran.tambahItem(new Minuman(nama, harga, jenis));
                        } else if (kat == 3) {
                            System.out.print("Masukkan Persentase Diskon (cth: 15 untuk 15%): ");
                            double disc = Double.parseDouble(scanner.nextLine());
                            menuRestoran.tambahItem(new Diskon(nama, disc));
                        } else {
                            System.out.println("Kategori tidak valid!");
                            break;
                        }
                        simpanMenuKeFile(menuRestoran, fileMenuTxt);
                        System.out.println("Item berhasil ditambahkan ke menu dan disimpan ke file!");
                        break;

                    case 2:
                        menuRestoran.tampilkanSemuaMenu();
                        break;

                    case 3:
                        menuRestoran.tampilkanSemuaMenu();
                        if (menuRestoran.getDaftarItem().isEmpty()) break;
                        System.out.print("Masukkan nomor item yang ingin dipesan: ");
                        int noItem = Integer.parseInt(scanner.nextLine()) - 1;
                        
                        MenuItem terpilih = menuRestoran.getItem(noItem);
                        pesananPelanggan.tambahPesanan(terpilih);
                        break;

                    case 4:
                        System.out.println("\nTotal Biaya Pesanan Saat Ini: Rp " + pesananPelanggan.hitungTotal());
                        break;

                    case 5:
                        System.out.print("Masukkan nama file untuk menyimpan struk (cth: struk_1.txt): ");
                        String namaFileStruk = scanner.nextLine();
                        pesananPelanggan.cetakStruk(namaFileStruk);
                        break;

                    case 6:
                        System.out.println("Terima kasih telah menggunakan aplikasi manajemen restoran.");
                        break;

                    default:
                        System.out.println("Pilihan tidak valid, silakan masukkan angka 1-6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Mohon masukkan angka yang valid!");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Terjadi kesalahan: " + e.getMessage());
            }

        } while (pilihan != 6);

        scanner.close();
    }

    public static void simpanMenuKeFile(Menu menu, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (MenuItem item : menu.getDaftarItem()) {
                if (item instanceof Makanan) {
                    writer.println("MAKANAN," + item.getNama() + "," + item.getHarga() + "," + ((Makanan) item).getJenisMakanan());
                } else if (item instanceof Minuman) {
                    writer.println("MINUMAN," + item.getNama() + "," + item.getHarga() + "," + ((Minuman) item).getJenisMinuman());
                } else if (item instanceof Diskon) {
                    writer.println("DISKON," + item.getNama() + "," + ((Diskon) item).getPersentaseDiskon());
                }
            }
        } catch (IOException e) {
            
        }
    }

    public static void muatMenuDariFile(Menu menu, String filename) {
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals("MAKANAN") && parts.length == 4) {
                    menu.tambahItem(new Makanan(parts[1], Double.parseDouble(parts[2]), parts[3]));
                } else if (parts[0].equals("MINUMAN") && parts.length == 4) {
                    menu.tambahItem(new Minuman(parts[1], Double.parseDouble(parts[2]), parts[3]));
                } else if (parts[0].equals("DISKON") && parts.length == 3) {
                    menu.tambahItem(new Diskon(parts[1], Double.parseDouble(parts[2])));
                }
            }
        } catch (IOException e) {
            
        }
    }
}