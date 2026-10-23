# 🕌 Adzkaruna (Android App)

Aplikasi Kumpulan Doa Harian Muslimin modern berbasis **Android (Jetpack Compose & Material 3)** dengan nuansa visual **Hijau Tosca & Putih Bersih**, tipografi teks Arab **Uthmanic Naskh** asli, serta fitur interaktif untuk sesama muslim.

---

## ✨ Fitur-Fitur Utama

### 1. 📿 Kumpulan Doa Lengkap & Shahih
- **Aktivitas Harian**: Tidur, bangun, makan, kamar mandi, keluar/masuk rumah & masjid, berpakaian, bercermin, dll.
- **Sholat & Ibadah**: Doa wudhu, setelah adzan, qunut subuh, zikir setelah sholat fardhu, sholat dhuha, istikharah, dll.
- **Perlindungan & Keselamatan**: Doa sapu jagad, pelindung marabahaya (*Bismillahilladzi la yadurru...*), turun hujan, menjenguk orang sakit, dll.
- **Keluarga & Orang Tua**: Doa untuk kedua orang tua, keturunan shalih, kebahagiaan keluarga.
- **Dzikir Pagi & Petang**: Sayyidul Istighfar, Ayat Kursi, Radhitu billahi rabba.

### 2. 🌟 Doa Pilihan Hari Ini (Berdasarkan Favorit User)
- Banner harian di beranda utama secara otomatis mengambil dan merotasi doa dari daftar **favorit yang sudah ditandai oleh pengguna**.

### 3. 🤲 Amiinkan Doa Muslimin (Fitur Komunitas)
- Fitur saling mendoakan sesama muslim di seluruh Indonesia.
- Tombol **"Kirim Doa →"** untuk menulis permohonan doa baru (nama/anonim, kota, isi doa).
- Tombol **"Aamiinkan"** dengan penghitung (*counter*) interaktif dan respon getar (*haptic feedback*).
- Tombol **"Suka / Like"** untuk memberikan dukungan moril.

### 4. 🧭 6 Menu Utama Beranda
1. **Promises of Allah**: Janji-janji pasti Allah dalam Al-Qur'an (rezeki orang bersyukur, pengabulan doa, kemudahan bersama kesulitan, solusi bagi orang bertakwa, dll.) lengkap dengan ayat Arab Uthmanic dan hikmahnya.
2. **Waktu Sholat**: Jadwal sholat harian (Imsak, Subuh, Terbit, Dzuhur, Ashar, Maghrib, Isya) dengan penunjuk waktu berikutnya (*Next Prayer Countdown*).
3. **Doa-doa**: Akses langsung ke seluruh daftar doa lengkap.
4. **Jelajah Doa Berdasarkan Topik**: Tematik kebutuhan hidup (Rezeki & Pekerjaan, Kesehatan, Ujian, Ketenangan Hati, Keluarga, dll.).
5. **Qiblat**: Kompas penunjuk arah Ka'bah (295° Arah Barat Laut dari Indonesia) dengan visual derajat dan jarak ke Makkah.
6. **Favorit**: Akses cepat doa-doa yang disimpan.

### 5. 📖 Al-Qur'an Pilihan & Eksplorasi
- **Tab Quran**: Surat-surat pilihan yang rutin dibaca (Al-Fatihah, Al-Ikhlas, Al-Falaq, An-Nas, Al-Mulk).
- **Tab Explore**: Mutiara hadits hari ini, 5 waktu paling mustajab berdoa, dan artikel adab berdoa.
- **Tab Profil**: Profil pengguna, statistik doa, pengaturan tema, dan informasi aplikasi.

### 6. 🎨 Desain & Aksesibilitas
- **Font Uthmanic Asli**: Menggunakan font Mushaf standar Madinah (`res/font/uthmanic.ttf`).
- **Palet Warna Tosca & Putih**: Hijau Tosca Segar (`#00897B`), Hijau Tosca Tua (`#004D40`), dan Putih Bersih (`#FFFFFF`).
- **Mode Gelap & Terang**: Dukungan Dark Mode (*Midnight Deep Tosca*) dan Light Mode dengan saklar dinamis.
- **Pengatur Ukuran Font**: Tombol perbesar/perkecil huruf Arab (`A-` / `A+`) langsung di layar doa.
- **Salin & Bagikan**: Fitur *Copy to Clipboard* dan *Share to WhatsApp/Social Media*.

---

## 🛠️ Tech Stack & Arsitektur
- **Platform**: Android (Min SDK 24, Target SDK 37)
- **Language**: Kotlin 2.x
- **UI Framework**: Jetpack Compose dengan Material Design 3 (Material 3)
- **Icons**: Material Icons Extended & AutoMirrored
- **Typography**: Custom Font Family (Uthmanic Regular TTF)
- **Persistence**: SharedPreferences / Repository Pattern (Siap integrasi ke Room Database / Firebase Cloud Firestore)

---

## 🚀 Cara Menjalankan Project
1. Clone repositori ini:
   ```bash
   git clone https://github.com/wellyekacipta/DoaHarianMuslimin.git
   ```
2. Buka project di **Android Studio Ladybug / Koala / versi terbaru**.
3. Tunggu proses **Gradle Sync** selesai.
4. Hubungkan HP Android atau jalankan Emulator.
5. Klik tombol **Run (▶)** atau build APK melalui terminal:
   ```bash
   ./gradlew assembleDebug
   ```
   File APK hasil build akan berada di `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📄 Lisensi
Project ini dibuat untuk kebaikan dan kemaslahatan umat Muslim. Bebas digunakan dan dikembangkan untuk tujuan dakwah dan edukasi.
