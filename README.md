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

### 5. 🎧 Audio Murotal Al-Qur'an & Suara Alam
- **Murotal 114 Surah**: Akses pemutaran audio murotal jernih dengan 6 pilihan Qari ternama dunia (Syaikh Misyari Rasyid Al-Afasi, Abdurrahman as-Sudais, Abdullah Al-Juhany, Abdul-Muhsin Al-Qasim, Ibrahim Al-Dossari, Yasser Al-Dosari).
- **Suara Alam (Nature Ambient Mixer)**: Relaksasi suara alam (Hujan, Api unggun, Jangkrik malam, Gemericik sungai) dengan pengaturan volume terpisah untuk relaksasi dan meditasi saat tadabbur Al-Qur'an.
- **Visual Malam Khusyuk**: Suasana malam tenang dengan pendaran cahaya Al-Qur'an di atas Rehal kayu dan animasi rintik hujan dinamis saat fitur hujan diaktifkan.

### 6. 📊 Tracker Ibadah & Kalender Islam
- **Tilawah (Ngaji)**: Target halaman harian dengan indikator cincin melingkar, pencatatan sesi ngaji, *Heatmap* konsistensi bulanan, serta estimasi perjalanan khatam 30 Juz.
- **Salat Fardhu**: Absensi dan riwayat 5 waktu salat dengan status (Tepat Waktu, Berjamaah, Terlambat, Terlewat) dan metrik bulanan.
- **Salat Sunnah**: Pemantauan rutin Salat Tahajud (dengan pemilihan raka'at) dan Salat Dhuha, serta Rawatib & Witir.
- **Dzikir**: Rutinitas Dzikir Pagi, Dzikir Petang, Dzikir Setelah Salat, dan penghitung Tasbih digital ringkas (+33).
- **Puasa & Kalender Hijriyah**: Kalender Islam lengkap dengan penanda Ayyamul Bidh (13, 14, 15), hari besar Islam, pencatatan puasa sunnah/wajib, serta hitung mundur (*live countdown*) Awal Ramadhan 1448 H.

### 7. 🎨 Desain, Multi-Tema & Aksesibilitas
- **Pilihan 4 Tema Tampilan**: Hijau Tosca (Default), Merah Marun, Biru Islami, serta Mode Gelap (*Midnight Deep*).
- **Font Uthmanic Asli**: Menggunakan font Mushaf standar Madinah (`res/font/uthmanic.ttf`).
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
