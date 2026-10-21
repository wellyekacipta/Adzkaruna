package com.example.doaharianmuslimin.data

import com.example.doaharianmuslimin.data.model.QuranAyat
import com.example.doaharianmuslimin.data.model.QuranSurah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object QuranRepository {

    // In-memory cache for loaded surahs
    private val surahCache = mutableMapOf<Int, QuranSurah>()

    // Complete catalog of all 114 Surahs in Al-Qur'an
    val allSurahMeta: List<QuranSurah> = listOf(
        QuranSurah(1, "Al-Fatihah", "الفاتحة", "Pembukaan", 7, "Makkiyah", emptyList()),
        QuranSurah(2, "Al-Baqarah", "البقرة", "Sapi Betina", 286, "Madaniyyah", emptyList()),
        QuranSurah(3, "Ali 'Imran", "آل عمران", "Keluarga Imran", 200, "Madaniyyah", emptyList()),
        QuranSurah(4, "An-Nisa'", "النساء", "Wanita", 176, "Madaniyyah", emptyList()),
        QuranSurah(5, "Al-Ma'idah", "المائدة", "Hidangan", 120, "Madaniyyah", emptyList()),
        QuranSurah(6, "Al-An'am", "الأنعام", "Binatang Ternak", 165, "Makkiyah", emptyList()),
        QuranSurah(7, "Al-A'raf", "الأعراف", "Tempat Tertinggi", 206, "Makkiyah", emptyList()),
        QuranSurah(8, "Al-Anfal", "الأنفال", "Rampasan Perang", 75, "Madaniyyah", emptyList()),
        QuranSurah(9, "At-Taubah", "التوبة", "Pengampunan", 129, "Madaniyyah", emptyList()),
        QuranSurah(10, "Yunus", "يونس", "Nabi Yunus", 109, "Makkiyah", emptyList()),
        QuranSurah(11, "Hud", "هود", "Nabi Hud", 123, "Makkiyah", emptyList()),
        QuranSurah(12, "Yusuf", "يوسف", "Nabi Yusuf", 111, "Makkiyah", emptyList()),
        QuranSurah(13, "Ar-Ra'd", "الرعد", "Guruh", 43, "Madaniyyah", emptyList()),
        QuranSurah(14, "Ibrahim", "إبراهيم", "Nabi Ibrahim", 52, "Makkiyah", emptyList()),
        QuranSurah(15, "Al-Hijr", "الحجر", "Gunung Al-Hijr", 99, "Makkiyah", emptyList()),
        QuranSurah(16, "An-Nahl", "النحل", "Lebah", 128, "Makkiyah", emptyList()),
        QuranSurah(17, "Al-Isra'", "الإسراء", "Memperjalankan Malam", 111, "Makkiyah", emptyList()),
        QuranSurah(18, "Al-Kahf", "الكهف", "Gua", 110, "Makkiyah", emptyList()),
        QuranSurah(19, "Maryam", "مريم", "Maryam", 98, "Makkiyah", emptyList()),
        QuranSurah(20, "Taha", "طه", "Thaahaa", 135, "Makkiyah", emptyList()),
        QuranSurah(21, "Al-Anbiya'", "الأنبياء", "Nabi-Nabi", 112, "Makkiyah", emptyList()),
        QuranSurah(22, "Al-Hajj", "الحج", "Haji", 78, "Madaniyyah", emptyList()),
        QuranSurah(23, "Al-Mu'minun", "المؤمنون", "Orang Mukmin", 118, "Makkiyah", emptyList()),
        QuranSurah(24, "An-Nur", "النور", "Cahaya", 64, "Madaniyyah", emptyList()),
        QuranSurah(25, "Al-Furqan", "الفرقان", "Pembeda", 77, "Makkiyah", emptyList()),
        QuranSurah(26, "Asy-Syu'ara'", "الشعراء", "Para Penyair", 227, "Makkiyah", emptyList()),
        QuranSurah(27, "An-Naml", "النمل", "Semut", 93, "Makkiyah", emptyList()),
        QuranSurah(28, "Al-Qasas", "القصص", "Kisah-Kisah", 88, "Makkiyah", emptyList()),
        QuranSurah(29, "Al-'Ankabut", "العنكبوت", "Laba-Laba", 69, "Makkiyah", emptyList()),
        QuranSurah(30, "Ar-Rum", "الروم", "Bangsa Romawi", 60, "Makkiyah", emptyList()),
        QuranSurah(31, "Luqman", "لقمان", "Keluarga Luqman", 34, "Makkiyah", emptyList()),
        QuranSurah(32, "As-Sajdah", "السجدة", "Sujud", 30, "Makkiyah", emptyList()),
        QuranSurah(33, "Al-Ahzab", "الأحزاب", "Golongan Bersekutu", 73, "Madaniyyah", emptyList()),
        QuranSurah(34, "Saba'", "سبأ", "Kaum Saba'", 54, "Makkiyah", emptyList()),
        QuranSurah(35, "Fatir", "فاطر", "Pencipta", 45, "Makkiyah", emptyList()),
        QuranSurah(36, "Yasin", "يس", "Yaasiin", 83, "Makkiyah", emptyList()),
        QuranSurah(37, "As-Saffat", "الصافات", "Barisan-Barisan", 182, "Makkiyah", emptyList()),
        QuranSurah(38, "Sad", "ص", "Shaad", 88, "Makkiyah", emptyList()),
        QuranSurah(39, "Az-Zumar", "الزمر", "Rombongan", 75, "Makkiyah", emptyList()),
        QuranSurah(40, "Ghafir", "غافر", "Maha Pengampun", 85, "Makkiyah", emptyList()),
        QuranSurah(41, "Fussilat", "فصلت", "Yang Dijelaskan", 54, "Makkiyah", emptyList()),
        QuranSurah(42, "Asy-Syura", "الشورى", "Musyawarah", 53, "Makkiyah", emptyList()),
        QuranSurah(43, "Az-Zukhruf", "الزخرف", "Perhiasan", 89, "Makkiyah", emptyList()),
        QuranSurah(44, "Ad-Dukhan", "الدخان", "Kabut", 59, "Makkiyah", emptyList()),
        QuranSurah(45, "Al-Jasiyah", "الجاثية", "Berlutut", 37, "Makkiyah", emptyList()),
        QuranSurah(46, "Al-Ahqaf", "الأحقاف", "Bukit Pasir", 35, "Makkiyah", emptyList()),
        QuranSurah(47, "Muhammad", "محمد", "Nabi Muhammad", 38, "Madaniyyah", emptyList()),
        QuranSurah(48, "Al-Fath", "الفتح", "Kemenangan", 29, "Madaniyyah", emptyList()),
        QuranSurah(49, "Al-Hujurat", "الحجرات", "Kamar-Kamar", 18, "Madaniyyah", emptyList()),
        QuranSurah(50, "Qaf", "ق", "Qaaf", 45, "Makkiyah", emptyList()),
        QuranSurah(51, "Az-Zariyat", "الذاريات", "Angin Menerbangkan", 60, "Makkiyah", emptyList()),
        QuranSurah(52, "At-Tur", "الطور", "Bukit Thur", 49, "Makkiyah", emptyList()),
        QuranSurah(53, "An-Najm", "النجم", "Bintang", 62, "Makkiyah", emptyList()),
        QuranSurah(54, "Al-Qamar", "القمر", "Bulan", 55, "Makkiyah", emptyList()),
        QuranSurah(55, "Ar-Rahman", "الرحمن", "Maha Pemurah", 78, "Madaniyyah", emptyList()),
        QuranSurah(56, "Al-Waqi'ah", "الواقعة", "Hari Kiamat", 96, "Makkiyah", emptyList()),
        QuranSurah(57, "Al-Hadid", "الحديد", "Besi", 29, "Madaniyyah", emptyList()),
        QuranSurah(58, "Al-Mujadilah", "المجادلة", "Wanita Menggugat", 22, "Madaniyyah", emptyList()),
        QuranSurah(59, "Al-Hasyr", "الحشر", "Pengusiran", 24, "Madaniyyah", emptyList()),
        QuranSurah(60, "Al-Mumtahanah", "الممتحنة", "Wanita Diuji", 13, "Madaniyyah", emptyList()),
        QuranSurah(61, "As-Saff", "الصف", "Barisan", 14, "Madaniyyah", emptyList()),
        QuranSurah(62, "Al-Jumu'ah", "الجمعة", "Hari Jum'at", 11, "Madaniyyah", emptyList()),
        QuranSurah(63, "Al-Munafiqun", "المنافقون", "Orang Munafik", 11, "Madaniyyah", emptyList()),
        QuranSurah(64, "At-Tagabun", "التغابن", "Hari Kerugian & Keuntungan", 18, "Madaniyyah", emptyList()),
        QuranSurah(65, "At-Talaq", "الطلاق", "Perceraian", 12, "Madaniyyah", emptyList()),
        QuranSurah(66, "At-Tahrim", "التحريم", "Mengharamkan", 12, "Madaniyyah", emptyList()),
        QuranSurah(67, "Al-Mulk", "الملك", "Kerajaan", 30, "Makkiyah", emptyList()),
        QuranSurah(68, "Al-Qalam", "القلم", "Pena", 52, "Makkiyah", emptyList()),
        QuranSurah(69, "Al-Haqqah", "الحاقة", "Hari Kiamat Pasti", 52, "Makkiyah", emptyList()),
        QuranSurah(70, "Al-Ma'arij", "المعارج", "Tempat-Tempat Naik", 44, "Makkiyah", emptyList()),
        QuranSurah(71, "Nuh", "نوح", "Nabi Nuh", 28, "Makkiyah", emptyList()),
        QuranSurah(72, "Al-Jinn", "الجن", "Bangsa Jin", 28, "Makkiyah", emptyList()),
        QuranSurah(73, "Al-Muzzammil", "المزمل", "Orang Berselimut", 20, "Makkiyah", emptyList()),
        QuranSurah(74, "Al-Muddassir", "المدثر", "Orang Berkemul", 56, "Makkiyah", emptyList()),
        QuranSurah(75, "Al-Qiyamah", "القيامة", "Hari Kiamat", 40, "Makkiyah", emptyList()),
        QuranSurah(76, "Al-Insan", "الإنسان", "Manusia", 31, "Madaniyyah", emptyList()),
        QuranSurah(77, "Al-Mursalat", "المرسلات", "Malaikat-Malaikat Diutus", 50, "Makkiyah", emptyList()),
        QuranSurah(78, "An-Naba'", "النبأ", "Berita Besar", 40, "Makkiyah", emptyList()),
        QuranSurah(79, "An-Nazi'at", "النازعات", "Malaikat Mencabut", 46, "Makkiyah", emptyList()),
        QuranSurah(80, "'Abasa", "عبس", "Bermuka Masam", 42, "Makkiyah", emptyList()),
        QuranSurah(81, "At-Takwir", "التكوير", "Menggulung Matahari", 29, "Makkiyah", emptyList()),
        QuranSurah(82, "Al-Infitar", "الانفطار", "Terbelah", 19, "Makkiyah", emptyList()),
        QuranSurah(83, "Al-Mutaffifin", "المطففين", "Orang-Orang Curang", 36, "Makkiyah", emptyList()),
        QuranSurah(84, "Al-Insyiqaq", "الانشقاق", "Terbelah", 25, "Makkiyah", emptyList()),
        QuranSurah(85, "Al-Buruj", "البروج", "Gugusan Bintang", 22, "Makkiyah", emptyList()),
        QuranSurah(86, "At-Tariq", "الطارق", "Bintang Pagi", 17, "Makkiyah", emptyList()),
        QuranSurah(87, "Al-A'la", "الأعلى", "Maha Tinggi", 19, "Makkiyah", emptyList()),
        QuranSurah(88, "Al-Gasyiyah", "الغاشية", "Hari Pembalasan", 26, "Makkiyah", emptyList()),
        QuranSurah(89, "Al-Fajr", "الفجر", "Waktu Fajar", 30, "Makkiyah", emptyList()),
        QuranSurah(90, "Al-Balad", "البلد", "Negeri", 20, "Makkiyah", emptyList()),
        QuranSurah(91, "Asy-Syams", "الشمس", "Matahari", 15, "Makkiyah", emptyList()),
        QuranSurah(92, "Al-Lail", "الليل", "Malam", 21, "Makkiyah", emptyList()),
        QuranSurah(93, "Ad-Duha", "الضحى", "Waktu Dhuha", 11, "Makkiyah", emptyList()),
        QuranSurah(94, "Al-Insyirah", "الشرح", "Kelapangan Dada", 8, "Makkiyah", emptyList()),
        QuranSurah(95, "At-Tin", "التين", "Buah Tin", 8, "Makkiyah", emptyList()),
        QuranSurah(96, "Al-'Alaq", "العلق", "Segumpal Darah", 19, "Makkiyah", emptyList()),
        QuranSurah(97, "Al-Qadr", "القدر", "Malam Kemuliaan", 5, "Makkiyah", emptyList()),
        QuranSurah(98, "Al-Bayyinah", "البينة", "Bukti yang Nyata", 8, "Madaniyyah", emptyList()),
        QuranSurah(99, "Az-Zalzalah", "الزلزلة", "Guncangan Dahsyat", 8, "Madaniyyah", emptyList()),
        QuranSurah(100, "Al-'Adiyat", "العاديات", "Kuda Perang", 11, "Makkiyah", emptyList()),
        QuranSurah(101, "Al-Qari'ah", "القارعة", "Hari Kiamat", 11, "Makkiyah", emptyList()),
        QuranSurah(102, "At-Takasur", "التكاثر", "Bermegah-Megahan", 8, "Makkiyah", emptyList()),
        QuranSurah(103, "Al-'Asr", "العصر", "Demi Masa", 3, "Makkiyah", emptyList()),
        QuranSurah(104, "Al-Humazah", "الهمزة", "Pengumpat", 9, "Makkiyah", emptyList()),
        QuranSurah(105, "Al-Fil", "الفيل", "Pasukan Gajah", 5, "Makkiyah", emptyList()),
        QuranSurah(106, "Quraisy", "قريش", "Suku Quraisy", 4, "Makkiyah", emptyList()),
        QuranSurah(107, "Al-Ma'un", "الماعون", "Bantuan Bermanfaat", 7, "Makkiyah", emptyList()),
        QuranSurah(108, "Al-Kausar", "الكوثر", "Nikmat Berlimpah", 3, "Makkiyah", emptyList()),
        QuranSurah(109, "Al-Kafirun", "الكافرون", "Orang-Orang Kafir", 6, "Makkiyah", emptyList()),
        QuranSurah(110, "An-Nasr", "النصر", "Pertolongan Allah", 3, "Madaniyyah", emptyList()),
        QuranSurah(111, "Al-Lahab", "اللهب", "Gejolak Api", 5, "Makkiyah", emptyList()),
        QuranSurah(112, "Al-Ikhlas", "الإخلاص", "Kemurnian Keesaan Allah", 4, "Makkiyah", emptyList()),
        QuranSurah(113, "Al-Falaq", "الفلق", "Waktu Fajar", 5, "Madaniyyah", emptyList()),
        QuranSurah(114, "An-Nas", "الناس", "Umat Manusia", 6, "Madaniyyah", emptyList())
    )

    // Pre-bundled offline popular surahs
    val bundledSurahs: Map<Int, QuranSurah> = mapOf(
        1 to QuranSurah(
            number = 1,
            name = "Al-Fatihah",
            arabicName = "الفاتحة",
            englishMeaning = "Pembukaan",
            totalAyat = 7,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Bismillaahir-rahmaanir-rahiim.", "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang."),
                QuranAyat(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Al-hamdu lillaahi rabbil-'aalamiin.", "Segala puji bagi Allah, Tuhan seluruh alam."),
                QuranAyat(3, "الرَّحْمَٰنِ الرَّحِيمِ", "Ar-rahmaanir-rahiim.", "Yang Maha Pengasih, Maha Penyayang."),
                QuranAyat(4, "مَالِكِ يَوْمِ الدِّينِ", "Maaliki yaumid-diin.", "Pemilik hari pembalasan."),
                QuranAyat(5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "Iyyaaka na'budu wa iyyaaka nasta'iin.", "Hanya kepada Engkaulah kami menyembah dan hanya kepada Engkaulah kami memohon pertolongan."),
                QuranAyat(6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Ihdinash-shiraathal-mustaqiim.", "Tunjukilah kami jalan yang lurus,"),
                QuranAyat(7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "Shiraathal-ladziina an'amta 'alaihim ghairil-maghdhuubi 'alaihim wa ladh-dhaalliin.", "(yaitu) jalan orang-orang yang telah Engkau beri nikmat kepadanya; bukan (jalan) mereka yang dimurkai, dan bukan (pula jalan) mereka yang sesat.")
            )
        ),
        18 to QuranSurah(
            number = 18,
            name = "Al-Kahf",
            arabicName = "الكهف",
            englishMeaning = "Gua (Pelindung Fitnah Dajjal)",
            totalAyat = 110,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "الْحَمْدُ لِلَّهِ الَّذِي أَنزَلَ عَلَىٰ عَبْدِهِ الْكِتَابَ وَلَمْ يَجْعَل لَّهُ عِوَجًا", "Alhamdu lillaahil-ladzii anzala 'alaa 'abdihil-kitaaba wa lam yaj'al lahuu 'iwajaa.", "Segala puji bagi Allah yang telah menurunkan Kitab (Al-Qur'an) kepada hamba-Nya dan Dia tidak menjadikannya bengkok,"),
                QuranAyat(2, "قَيِّمًا لِّيُنذِرَ بَأْسًا شَدِيدًا مِّن لَّدُنْهُ وَيُبَشِّرَ الْمُؤْمِنِينَ الَّذِينَ يَعْمَلُونَ الصَّالِحَاتِ أَنَّ لَهُمْ أَجْرًا حَسَنًا", "Qayyimal liyundzira ba'san syadiidam mil ladunhu wa yubasysyiral-mu'miniinal-ladziina ya'maluunash-shaalihaati anna lahum ajran hasanaa.", "sebagai bimbingan yang lurus, untuk memperingatkan akan siksaan yang sangat pedih dari sisi-Nya dan memberikan kabar gembira kepada orang-orang mukmin yang mengerjakan kebajikan bahwa mereka akan mendapat balasan yang baik,"),
                QuranAyat(3, "مَّاكِثِينَ فِيهِ أَبَدًا", "Maakitsiina fiihi abadaa.", "mereka kekal di dalamnya untuk selama-lamanya,"),
                QuranAyat(4, "وَيُنذِرَ الَّذِينَ قَالُوا اتَّخَذَ اللَّهُ وَلَدًا", "Wa yundziral-ladziina qaalut-takhadzallaahu waladaa.", "dan untuk memperingatkan kepada orang yang berkata, 'Allah mengangkat seorang anak.'"),
                QuranAyat(5, "مَّا لَهُم بِهِ مِنْ عِلْمٍ وَلَا لِآبَائِهِمْ كَبُرَتْ كَلِمَةً تَخْرُجُ مِنْ أَفْوَاهِهِمْ إِن يَقُولُونَ إِلَّا كَذِبًا", "Maa lahum bihii min 'ilmiw wa laa li-aabaa-ihim, kaburat kalimatan takhruju min afwaahihim, iy yaquuluuna illaa kadzibaa.", "Mereka sama sekali tidak mempunyai pengetahuan tentang hal itu, begitu pula nenek moyang mereka. Alangkah besarnya perkataan bohong yang keluar dari mulut mereka."),
                QuranAyat(10, "إِذْ أَوَى الْفِتْيَةُ إِلَى الْكَهْفِ فَقَالُوا رَبَّنَا آتِنَا مِن لَّدُنكَ رَحْمَةً وَهَيِّئْ لَنَا مِنْ أَمْرِنَا رَشَدًا", "Idz awal-fityatu ilal-kahfi fa qaaluu rabbanaa aatinaa mil ladunka rahmatan wa hayyi' lanaa min amrinaa rasyadaa.", "(Ingatlah) tatkala para pemuda itu mencari tempat berlindung ke dalam gua, lalu mereka berdoa: 'Wahai Tuhan kami, berikanlah rahmat kepada kami dari sisi-Mu dan sempurnakanlah petunjuk bagi kami dalam urusan kami.'")
            )
        ),
        36 to QuranSurah(
            number = 36,
            name = "Yasin",
            arabicName = "يس",
            englishMeaning = "Yaasiin (Jantung Al-Qur'an)",
            totalAyat = 83,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "يس", "Yaa-Siiin.", "Ya Sin."),
                QuranAyat(2, "وَالْقُرْآنِ الْحَكِيمِ", "Wal-qur-aanil-hakiim.", "Demi Al-Qur'an yang penuh hikmah,"),
                QuranAyat(3, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "Innaka laminal-mursaliin.", "sungguh, engkau (Muhammad) adalah salah seorang dari rasul-rasul,"),
                QuranAyat(4, "عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ", "'Alaa shiraathim mustaqiim.", "(yang berada) di atas jalan yang lurus,"),
                QuranAyat(5, "تَنزِيلَ الْعَزِيزِ الرَّحِيمِ", "Tanziilal-'aziizir-rahiim.", "(sebagai wahyu) yang diturunkan oleh (Allah) Yang Maha Perkasa, Maha Penyayang,")
            )
        ),
        67 to QuranSurah(
            number = 67,
            name = "Al-Mulk",
            arabicName = "الملك",
            englishMeaning = "Kerajaan (Penyelamat Siksa Kubur)",
            totalAyat = 30,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Tabaarakal-ladzii biyadihil-mulku wa huwa 'alaa kulli syai-in qadiir.", "Maha Suci Allah yang di tangan-Nya lah segala kerajaan, dan Dia Maha Kuasa atas segala sesuatu,"),
                QuranAyat(2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا وَهُوَ الْعَزِيزُ الْغَفُورُ", "Alladzii khalaqal-mauta wal-hayaata liyabluwakum ayyukum ahsanu 'amalan wa huwal-'aziizul-ghafuur.", "Yang menciptakan mati dan hidup, untuk menguji kamu, siapa di antara kamu yang lebih baik amalnya. Dan Dia Maha Perkasa, Maha Pengampun,"),
                QuranAyat(3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ", "Alladzii khalaqa sab'a samaawaatin thibaaqan maa taraa fii khalqir-rahmaani min tafaawut.", "Yang menciptakan tujuh langit berlapis-lapis. Kamu sekali-kali tidak melihat pada ciptaan Tuhan Yang Maha Pemurah sesuatu yang tidak seimbang.")
            )
        ),
        108 to QuranSurah(
            number = 108,
            name = "Al-Kausar",
            arabicName = "الكوثر",
            englishMeaning = "Nikmat Berlimpah",
            totalAyat = 3,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Innaa a'thainaakal-kautsar.", "Sungguh, Kami telah memberimu (Muhammad) nikmat yang banyak."),
                QuranAyat(2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "Fashalli lirabbika wanhar.", "Maka laksanakanlah salat karena Tuhanmu, dan berkurbanlah."),
                QuranAyat(3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Inna syaani-aka huwal-abtar.", "Sungguh, orang-orang yang membencimu dialah yang terputus (dari rahmat Allah).")
            )
        ),
        112 to QuranSurah(
            number = 112,
            name = "Al-Ikhlas",
            arabicName = "الإخلاص",
            englishMeaning = "Kemurnian Keesaan Allah",
            totalAyat = 4,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Qul huwallaahu ahad.", "Katakanlah (Muhammad): Dialah Allah, Yang Maha Esa."),
                QuranAyat(2, "اللَّهُ الصَّمَدُ", "Allaahush-shamad.", "Allah tempat meminta segala sesuatu."),
                QuranAyat(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "Lam yalid wa lam yuulad.", "(Allah) tidak beranak dan tidak pula diperanakkan,"),
                QuranAyat(4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Wa lam yakun lahuu kufuwan ahad.", "dan tidak ada sesuatu yang setara dengan Dia.")
            )
        ),
        113 to QuranSurah(
            number = 113,
            name = "Al-Falaq",
            arabicName = "الفلق",
            englishMeaning = "Waktu Subuh",
            totalAyat = 5,
            revelation = "Madaniyyah",
            ayats = listOf(
                QuranAyat(1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Qul a'uudzu birabbil-falaq.", "Katakanlah, 'Aku berlindung kepada Tuhan yang menguasai subuh (fajar),'"),
                QuranAyat(2, "مِن شَرِّ مَا خَلَقَ", "Min syarri maa khalaq.", "dari kejahatan (makhluk yang) Dia ciptakan,"),
                QuranAyat(3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "Wa min syarri ghaasiqin idzaa waqab.", "dan dari kejahatan malam apabila telah gelap gulita,"),
                QuranAyat(4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "Wa min syarrin-naffaatsaati fil-'uqad.", "dan dari kejahatan (perempuan-perempuan) penyihir yang meniup pada buhul-buhul (talinya),"),
                QuranAyat(5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "Wa min syarri haasidin idzaa hasad.", "dan dari kejahatan orang yang dengki apabila dia dengki.")
            )
        ),
        114 to QuranSurah(
            number = 114,
            name = "An-Nas",
            arabicName = "الناس",
            englishMeaning = "Umat Manusia",
            totalAyat = 6,
            revelation = "Madaniyyah",
            ayats = listOf(
                QuranAyat(1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Qul a'uudzu birabbin-naas.", "Katakanlah, 'Aku berlindung kepada Tuhannya manusia,'"),
                QuranAyat(2, "مَلِكِ النَّاسِ", "Malikin-naas.", "Raja manusia,"),
                QuranAyat(3, "إِلَٰهِ النَّاسِ", "Ilaahin-naas.", "sembahan manusia,"),
                QuranAyat(4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "Min syarril-waswaasil-khannaas.", "dari kejahatan (bisikan) setan yang bersembunyi,"),
                QuranAyat(5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Alladzii yuwaswisu fii shuduurin-naas.", "yang membisikkan (kejahatan) ke dalam dada manusia,"),
                QuranAyat(6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "Minal-jinnati wan-naas.", "dari (golongan) jin dan manusia.")
            )
        )
    )

    init {
        // Pre-fill cache with bundled surahs
        bundledSurahs.forEach { (number, surah) ->
            surahCache[number] = surah
        }
    }

    /**
     * Retrieve or fetch Surah details (Ayats, Latin, Kemenag Indonesian Translation)
     * Automatically loads and caches on IO thread.
     */
    suspend fun getSurahDetail(surahNumber: Int): QuranSurah? {
        // 1. Check in-memory cache
        val cached = surahCache[surahNumber]
        if (cached != null && cached.ayats.isNotEmpty()) {
            return cached
        }

        // 2. Fetch asynchronously from public equran API
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://equran.id/api/v2/surat/$surahNumber")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 6000
                connection.readTimeout = 6000

                if (connection.responseCode == 200) {
                    val responseStr = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(responseStr)
                    val data = json.getJSONObject("data")

                    val number = data.getInt("nomor")
                    val name = data.getString("namaLatin")
                    val arabicName = data.getString("nama")
                    val meaning = data.getString("arti")
                    val totalAyat = data.getInt("jumlahAyat")
                    val revelation = if (data.getString("tempatTurun").equals("Mekah", ignoreCase = true)) "Makkiyah" else "Madaniyyah"

                    val ayatsList = mutableListOf<QuranAyat>()
                    val ayatsArray = data.getJSONArray("ayat")
                    for (i in 0 until ayatsArray.length()) {
                        val item = ayatsArray.getJSONObject(i)
                        val audioObj = item.optJSONObject("audio")
                        val audioUrl = audioObj?.optString("05", "")
                            ?: audioObj?.optString("01", "")
                            ?: ""

                        ayatsList.add(
                            QuranAyat(
                                number = item.getInt("nomorAyat"),
                                arabic = item.getString("teksArab"),
                                latin = item.optString("teksLatin", ""),
                                translation = item.getString("teksIndonesia"),
                                audioUrl = audioUrl
                            )
                        )
                    }

                    val surah = QuranSurah(
                        number = number,
                        name = name,
                        arabicName = arabicName,
                        englishMeaning = meaning,
                        totalAyat = totalAyat,
                        revelation = revelation,
                        ayats = ayatsList
                    )

                    // Cache for instantaneous re-opening
                    surahCache[number] = surah
                    surah
                } else {
                    // Fallback to meta if network fails
                    allSurahMeta.find { it.number == surahNumber }
                }
            } catch (e: Exception) {
                // If offline and bundled exists, return it, else fallback
                bundledSurahs[surahNumber] ?: allSurahMeta.find { it.number == surahNumber }
            }
        }
    }

    // In-memory cache for loaded Tafsir Kemenag RI
    private val tafsirCache = mutableMapOf<Int, Map<Int, String>>()

    /**
     * Fetch complete Tafsir Kemenag RI for a specific surah and ayat
     */
    suspend fun getTafsir(surahNumber: Int, ayatNumber: Int): String {
        // 1. Check in-memory cache
        val cachedMap = tafsirCache[surahNumber]
        if (cachedMap != null && cachedMap.containsKey(ayatNumber)) {
            return cachedMap[ayatNumber] ?: "Tafsir Kemenag tidak ditemukan."
        }

        // 2. Fetch from Kemenag API asynchronously
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://equran.id/api/v2/tafsir/$surahNumber")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 7000
                connection.readTimeout = 7000

                if (connection.responseCode == 200) {
                    val responseStr = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(responseStr)
                    val data = json.getJSONObject("data")
                    val tafsirArray = data.getJSONArray("tafsir")

                    val map = mutableMapOf<Int, String>()
                    for (i in 0 until tafsirArray.length()) {
                        val tObj = tafsirArray.getJSONObject(i)
                        map[tObj.getInt("ayat")] = tObj.getString("teks")
                    }
                    tafsirCache[surahNumber] = map
                    return@withContext map[ayatNumber] ?: "Tafsir ayat ini sedang dalam penyusunan Kemenag RI."
                }
            } catch (_: Exception) {}

            "Tafsir resmi Kementerian Agama RI (Kemenag) memerlukan koneksi internet untuk unduhan pertama. Silakan periksa koneksi internet Anda."
        }
    }
}
