/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

/**
 *
 * @author allysaas
 */
import java.util.Scanner;
class Node {

    Node next;
    Status data;
}

class Status {

    int luas;
    String bahanBakar;
    String jenisLantai;
    double frekuensiDaging;
    String kategori;

    public Status(int luas, String bahan, String lantai, double frekuensi, String kat) {
        this.luas = luas;
        this.bahanBakar = bahan;
        this.jenisLantai = lantai;
        this.frekuensiDaging = frekuensi;
        this.kategori = kat;
    }
}

class LinkedList {

    Node head, tail;
    int size = 0;

    public void sisipDiBelakang(Status data) {
        Node baru = new Node();
        baru.data = data;
        if (head == null) {
            head = baru;
            tail = baru;
        } else {
            tail.next = baru;
            tail = baru;
        }
        size++;
    }

    public double rataRata(String dicari, String kategori) {

        double temp = 0; 
        double sizetemp= 0;
        Node n = head;
        while (n != null) {
            if (dicari.equalsIgnoreCase("luas") && n.data.kategori.equalsIgnoreCase(kategori)) {
                temp += n.data.luas;
                sizetemp++;
            } else if (dicari.equalsIgnoreCase("frekuensi") && n.data.kategori.equalsIgnoreCase(kategori)) {
                temp += n.data.frekuensiDaging;
                sizetemp++;
            }
            n = n.next;
        }
        return temp / sizetemp;
    }

    public double varians(String dicari, String kategori) {
        Node n = head;
        double temp = 0;
        while (n != null) {
            if (dicari.equalsIgnoreCase("luas")) {
                temp += Math.pow(n.data.luas - rataRata(dicari, kategori), 2);

            } else if (dicari.equalsIgnoreCase("frekuensi")) {
                temp += Math.pow(n.data.frekuensiDaging - rataRata(dicari, kategori), 2);

            }
            n = n.next;
        }
        return temp / (size - 1);
    }

    //x=kayu bakar w=kaya jenis=bahan
    public double XygW(String x, String w, String jenis) {
        double atas = 0;
        Node n = head;
        while (n != null) {
            if (jenis.equalsIgnoreCase("bahan")) {
                if (n.data.bahanBakar.equalsIgnoreCase(x) && n.data.kategori.equalsIgnoreCase(w)) {
                    atas++;
                }
            } else if (jenis.equalsIgnoreCase("lantai")) {
                if (n.data.jenisLantai.equalsIgnoreCase(x) && n.data.kategori.equalsIgnoreCase(w)) {
                    atas++;
                }
            }
            n = n.next;
        }
        return atas;

    }

    public double getJumlahW(String w) {
        double bawah = 0;
        Node n = head;
        while (n != null) {
            if (n.data.kategori.equalsIgnoreCase(w)) {
                bawah++;
            }
            n = n.next;
        }
        return bawah;
    }
    
    
//
//    public double getNilaiLuas(){
//        return 
//    }
}

public class NaiveBayes {

    int jumlahSemua = 0;
    LinkedList tabel = new LinkedList();

    public void tambahBaris(int luas, String bahan, String lantai, double frekuensi, String kat) {
        Status stat = new Status(luas, bahan, lantai, frekuensi, kat);
        tabel.sisipDiBelakang(stat);
        jumlahSemua++;
    }

    // w = luas atau frekuensi
    public double likelihoodKontinu(double x, String w, String kategori) {
        return (1 / Math.sqrt(2 * 3.14 * tabel.varians(w, kategori))) * Math.exp((-1 * (Math.pow(x - tabel.rataRata(w, kategori), 2))) / (2 * tabel.varians(w, kategori)));
    }

    public double likelihoodDiskrit(String x, String w, String jenis) {
//        System.out.println(tabel.XygW(x, w, jenis));
//        System.out.println(tabel.getJumlahW(w, jenis));
            // System.out.println(tabel.XygW(x, w, jenis));
        return tabel.XygW(x, w, jenis) / tabel.getJumlahW(w);
    }

    public double prior(String w){
        return tabel.getJumlahW(w)/jumlahSemua;
    }
    
    public double posterior(int luas, String bahan, String lantai, double frek, String wwq) {
//        System.out.println(likelihoodSebaran(luas, "luas"));
//        likelihoodKontinu(bahan, wwq, "bahan");
//        likelihoodKontinu(lantai, wwq, "lantai");
        return likelihoodKontinu(luas, "luas", wwq) * likelihoodDiskrit(bahan, wwq, "bahan") * likelihoodDiskrit(lantai, wwq, "lantai") * likelihoodKontinu(frek, "frekuensi", wwq) * prior(wwq);
    }

    public String input(int luas, String bahan, String lantai, double frek) {
        double kaya = posterior(luas, bahan, lantai, frek, "kaya");
        double sedang = posterior(luas, bahan, lantai, frek, "sedang");
        double miskin = posterior(luas, bahan, lantai, frek, "miskin");

        double max = Math.max(Math.max(kaya, sedang), miskin);
        System.out.println(max);
        
        System.out.println(kaya);
        System.out.println(sedang);
        System.out.println(miskin);
        if (max == kaya) {
            return "kaya";
        } else if (max == sedang) {
            return "sedang";
        } else if (max == miskin) {
            return "miskin";
        } else {
            return "error";
        } 

    }
}

class Main {

    public static void main(String[] args) {
        NaiveBayes nav = new NaiveBayes();
        nav.tambahBaris(9, "kayu bakar", "ubin", 3, "sedang");
        nav.tambahBaris(10, "gas lpg", "ubin", 2, "sedang");
        nav.tambahBaris(15, "gas lpg", "plester", 2, "sedang");
        nav.tambahBaris(30, "gas lpg", "ubin", 4, "kaya");
        nav.tambahBaris(16, "kompor listrik", "ubin", 3, "kaya");
        nav.tambahBaris(25, "gas lpg", "ubin", 5, "kaya");
        nav.tambahBaris(9, "gas lpg", "plester", 0.5, "miskin");
        nav.tambahBaris(8, "kayu bakar", "tanah", 1, "miskin");
        nav.tambahBaris(10, "kayu bakar", "tanah", 2, "miskin");
        nav.tambahBaris(14, "gas lpg", "tanah", 1, "miskin");
        System.out.println("PROGRAM MENGHITUNG NAIVE BAYES\n");
        
        System.out.println("Masukkan");
        Scanner in = new Scanner(System.in);
        Scanner input = new Scanner(System.in);
        System.out.print("Luas bangunan: ");
        int luas = in.nextInt();
        System.out.print("Bahan bakar: ");
        String bahan = input.nextLine();
        System.out.print("Jenis lantai: ");
        String lantai = input.nextLine();
        System.out.print("Frekuensi makan daging: ");
        double frek = in.nextDouble();
        System.out.println("\nMaka, statusnya adalah " + nav.input(luas, bahan, lantai, frek));
    }
}

