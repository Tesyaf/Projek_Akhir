# 💻 TopTop – Aplikasi Rekomendasi Laptop Cerdas

TopTop adalah aplikasi mobile Android yang dirancang untuk membantu pengguna memilih laptop secara lebih cerdas, cepat, dan sesuai dengan kebutuhan mereka. Dengan memanfaatkan sistem rekomendasi berbasis preferensi pengguna, aplikasi ini menyederhanakan proses pemilihan laptop yang selama ini rumit karena banyaknya pilihan dan spesifikasi teknis yang kompleks.

Proyek ini dikembangkan oleh mahasiswa Universitas Lampung sebagai bagian dari tugas mata kuliah Teknologi dan Aplikasi Mobile (TAM).

---

## 📱 Fitur-Fitur Utama

TopTop menyediakan berbagai fitur unggulan yang dapat membantu pengguna:

### ✅ Rekomendasi Laptop Personal
Pengguna cukup mengisi kuisioner sederhana terkait aktivitas utama (belajar, bekerja, gaming, dll.), spesifikasi yang diinginkan, dan anggaran. Sistem akan secara otomatis menampilkan laptop yang paling sesuai.

### 🔎 Filter Pencarian Lengkap
Fitur ini memungkinkan pengguna menyaring laptop berdasarkan:
- Harga (min - max)
- RAM dan penyimpanan
- Prosesor (Intel/AMD, generasi)
- GPU (onboard/dedicated)
- Ukuran layar
- Merek laptop
- Tahun rilis
- Berat (untuk mobilitas)

### 📊 Perbandingan Laptop
Bandingkan hingga 3 laptop sekaligus dalam satu tampilan untuk melihat spesifikasi teknis, harga, dan ulasan pengguna secara berdampingan.

### ⭐ Ulasan & Rating
Baca review dari pengguna lain yang telah membeli laptop tersebut, dan berikan rating setelah pembelian agar membantu pengguna lain.

### ❤️ Wishlist
Simpan laptop yang menarik dalam daftar favorit untuk dipertimbangkan atau dibeli nanti.

### 🔔 Notifikasi Harga
Dapatkan notifikasi otomatis ketika harga dari laptop yang ada di wishlist turun.

---

## 🎯 Target Pengguna

TopTop menyasar berbagai kategori pengguna:

| Kategori | Kebutuhan |
|---------|-----------|
| 🎓 Pelajar/Mahasiswa | Laptop ringan, efisien, dengan harga terjangkau untuk tugas dan pembelajaran daring. |
| 👨‍💼 Profesional/Karyawan | Laptop kuat dan stabil untuk multitasking, desain, atau pengembangan software. |
| 🕹️ Gamers | Laptop dengan GPU dedicated, RAM besar, dan performa tinggi. |
| 👪 Pengguna Awam | Laptop fungsional, mudah digunakan, dan memiliki tampilan menarik. |

---

## 🧠 Manfaat Aplikasi

- ⏱️ **Efisiensi Waktu**: Informasi produk lengkap dalam satu aplikasi.
- 🎯 **Rekomendasi Akurat**: Berdasarkan profil pengguna, bukan hanya spesifikasi mentah.
- 🧾 **Edukasi Teknologi**: Penjelasan spesifikasi laptop dengan bahasa sederhana.
- 💡 **Keputusan Lebih Baik**: Fitur ulasan dan perbandingan meminimalkan kesalahan beli.
- 🏷️ **Harga Real-Time**: Selalu menampilkan harga terbaru yang tersedia di pasaran.

---

## 🖌️ Desain UI/UX

TopTop mengusung desain modern, bersih, dan ramah pengguna.

- 🌈 Warna dominan biru-putih yang elegan
- 👆 Navigasi utama: **Beranda**, **Pencarian**, **Wishlist**, **Notifikasi**, **Profil**
- 📱 Responsif di semua ukuran layar smartphone Android
- 🎨 Desain diimplementasikan dari [Figma](https://www.figma.com/design/jZM2vyGnga8KvIGSUHeu6X/TopTop?node-id=0-1&t=ho4GLYKEOIqNJixc-1)

---

## 🛠️ Teknologi yang Digunakan

| Komponen | Teknologi |
|----------|-----------|
| Bahasa Pemrograman | Java / Kotlin |
| IDE | Android Studio |
| Desain UI | Figma |
| Basis Data | Firebase / SQLite (tergantung implementasi akhir) |
| Dependency | Glide, Retrofit, Material Design, dsb |

---

## 🧱 Arsitektur Aplikasi

Aplikasi ini menggunakan pendekatan arsitektur modular dengan struktur sebagai berikut:

```
TopTop/
│
├── model/ # Struktur data (Laptop, User, etc)
├── view/ # Layout XML & UI
├── viewmodel/ # Pemrosesan data & komunikasi antara view-model
├── adapter/ # RecyclerView Adapter
├── util/ # Helper functions & constants
├── data/ # Repository dan sumber data
└── MainActivity.java # Entry point aplikasi
```

