package com.example.doaharianmuslimin.data

import com.example.doaharianmuslimin.data.model.Hadits

object HaditsRepository {
    val categories: List<String> = listOf(
        "Semua",
        "Niat & Ikhlas",
        "Akhlak Mulia",
        "Sholat & Ibadah",
        "Sabar & Syukur",
        "Ilmu & Amal"
    )

    val haditsList: List<Hadits> = listOf(
        Hadits(
            id = 1,
            title = "Niat Adalah Landasan Setiap Amal",
            narrator = "HR. Al-Bukhari no. 1 & Muslim no. 1907",
            source = "Hadits Arbain An-Nawawi #1",
            category = "Niat & Ikhlas",
            arabic = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى اللَّهِ وَرَسُولِهِ فَهِجْرَتُهُ إِلَى اللَّهِ وَرَسُولِهِ، وَمَنْ كَانَتْ هِجْرَتُهُ لِدُنْيَا يُصِيبُهَا أَوِ امْرَأَةٍ يَنْكِحُهَا فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ",
            latin = "Innamal a'maalu bin niyyaat, wa innamaa likullimri-in maa nawaa...",
            translation = "Sesungguhnya setiap amalan tergantung pada niatnya, dan setiap orang akan mendapatkan sesuai apa yang dia niatkan. Barangsiapa hijrahnya karena Allah dan Rasul-Nya, maka hijrahnya kepada Allah dan Rasul-Nya...",
            faedah = "Ikhlas dalam niat adalah syarat mutlak diterimanya setiap amal ibadah. Niat baik dapat mengubah rutinitas duniawi menjadi bernilai pahala besar."
        ),
        Hadits(
            id = 2,
            title = "Pilar dan Rukun Islam",
            narrator = "HR. Al-Bukhari no. 8 & Muslim no. 16",
            source = "Hadits Arbain An-Nawawi #3",
            category = "Sholat & Ibadah",
            arabic = "بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ: شَهَادَةِ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَإِقَامِ الصَّلاَةِ، وَإِيتَاءِ الزَّكَاةِ، وَحَجِّ البَيْتِ، وَصَوْمِ رَمَضَانَ",
            latin = "Buniyal islaamu 'alaa khamsin: syahaadati an laa ilaaha illallaah wa anna Muhammadan rasuulullaah...",
            translation = "Islam dibangun di atas lima perkara: bersaksi tiada tuhan selain Allah dan Muhammad utusan Allah, mendirikan shalat, menunaikan zakat, haji ke Baitullah, dan puasa Ramadhan.",
            faedah = "Lima pilar utama yang menjadi fondasi keislaman seorang hamba yang wajib ditegakkan sepenuh hati."
        ),
        Hadits(
            id = 3,
            title = "Mencintai Sesama Seperti Mencintai Diri Sendiri",
            narrator = "HR. Al-Bukhari no. 13 & Muslim no. 45",
            source = "Hadits Arbain An-Nawawi #13",
            category = "Akhlak Mulia",
            arabic = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            latin = "Laa yu'minu ahadukum hattaa yuhibba li akhiihi maa yuhibbu linafsih",
            translation = "Tidak beriman (dengan sempurna) salah seorang di antara kalian sampai ia mencintai bagi saudaranya apa yang ia cintai bagi dirinya sendiri.",
            faedah = "Tanda kesempurnaan iman tercermin dari kepedulian tulus dan tidak dengki terhadap kebaikan saudaramu."
        ),
        Hadits(
            id = 4,
            title = "Berkata Baik atau Diam",
            narrator = "HR. Al-Bukhari no. 6018 & Muslim no. 47",
            source = "Hadits Arbain An-Nawawi #15",
            category = "Akhlak Mulia",
            arabic = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَاليَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ",
            latin = "Man kaana yu'minu billaahi wal yaumil aakhiri falyaqul khairan au liyashmut",
            translation = "Barangsiapa yang beriman kepada Allah dan hari akhir, hendaklah ia berkata yang baik atau diam.",
            faedah = "Menjaga lisan dari ghibah, dusta, dan perkataan sia-sia adalah benteng utama keselamatan seorang muslim."
        ),
        Hadits(
            id = 5,
            title = "Larangan Marah & Menjaga Ketenangan",
            narrator = "HR. Al-Bukhari no. 6116",
            source = "Hadits Arbain An-Nawawi #16",
            category = "Akhlak Mulia",
            arabic = "أَنَّ رَجُلًا قَالَ لِلنَّبِيِّ ﷺ: أَوْصِنِي، قَالَ: لاَ تَغْضَبْ. فَرَدَّدَ مِرَارًا، قَالَ: لاَ تَغْضَبْ",
            latin = "Laa taghdhab, laa taghdhab...",
            translation = "Seorang laki-laki berkata kepada Nabi ﷺ: 'Berilah aku wasiat.' Beliau bersabda: 'Janganlah marah.' Beliau mengulangnya berkali-kali: 'Janganlah marah.'",
            faedah = "Menahan amarah adalah bukti kekuatan sejati jiwa dan kunci terhindar dari banyak penyesalan hidup."
        ),
        Hadits(
            id = 6,
            title = "Meninggalkan Hal yang Tidak Bermanfaat",
            narrator = "HR. At-Tirmidzi no. 2317 & Ibnu Majah no. 3976",
            source = "Hadits Arbain An-Nawawi #12",
            category = "Ilmu & Amal",
            arabic = "مِنْ حُسْنِ إِسْلاَمِ المَرْءِ تَرْكُهُ مَا لاَ يَعْنِيهِ",
            latin = "Min husni islaamil mar-i tarkuhu maa laa ya'niih",
            translation = "Di antara tanda kebaikan Islam seseorang adalah dia meninggalkan apa yang tidak bermanfaat baginya.",
            faedah = "Fokus pada perkara yang mendatangkan faedah dunia dan akhirat, menjauh dari kesia-siaan."
        ),
        Hadits(
            id = 7,
            title = "Senyum adalah Sedekah",
            narrator = "HR. At-Tirmidzi no. 1956 (Shahih)",
            source = "Riyadhus Shalihin",
            category = "Akhlak Mulia",
            arabic = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            latin = "Tabassumuka fii wajhi akhiika laka shadaqah",
            translation = "Senyummu di hadapan wajah saudaramu adalah sedekah bagimu.",
            faedah = "Kebaikan tidak selalu diukur dari harta; keramahan, wajah berseri-seri, dan kehangatan membawa pahala besar."
        ),
        Hadits(
            id = 8,
            title = "Menuntut Ilmu Kewajiban Setiap Muslim",
            narrator = "HR. Ibnu Majah no. 224 (Shahih)",
            source = "Riyadhus Shalihin",
            category = "Ilmu & Amal",
            arabic = "طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ",
            latin = "Thalabul 'ilmi fariidhatun 'alaa kulli muslim",
            translation = "Menuntut ilmu itu wajib bagi setiap orang muslim.",
            faedah = "Ilmu agama adalah penerang jalan hidup agar ibadah dan akhlak sesuai dengan syariat Allah."
        ),
        Hadits(
            id = 9,
            title = "Keutamaan Orang yang Membaca dan Menghafal Al-Qur'an",
            narrator = "HR. Al-Bukhari no. 5027",
            source = "Shahih Al-Bukhari",
            category = "Ilmu & Amal",
            arabic = "خَيْرُكُمْ مَنْ تَعَلَّمَ القُرْآنَ وَعَلَّمَهُ",
            latin = "Khairukum man ta'allamal Qur'aana wa 'allamah",
            translation = "Sebaik-baik kalian adalah orang yang belajar Al-Qur'an dan mengajarkannya.",
            faedah = "Kedudukan termulia diraih oleh orang yang senantiasa mendekat, membaca, mentadaburi, dan mengajarkan Al-Qur'an."
        ),
        Hadits(
            id = 10,
            title = "Kekuatan Orang yang Sabar dalam Ujian",
            narrator = "HR. Muslim no. 2999",
            source = "Riyadhus Shalihin",
            category = "Sabar & Syukur",
            arabic = "عَجَبًا لِأَمْرِ الْمُؤْمِنِ، إِنَّ أَمْرَهُ كُلَّهُ خَيْرٌ، وَلَيْسَ ذَاكَ لِأَحَدٍ إِلَّا لِلْمُؤْمِنِ، إِنْ أَصَابَتْهُ سَرَّاءُ شَكَرَ فَكَانَ خَيْرًا لَهُ، وَإِنْ أَصَابَتْهُ ضَرَّاءُ صَبَرَ فَكَانَ خَيْرًا لَهُ",
            latin = "'Ajaban li amril mu'min, inna amrahu kullahu khair...",
            translation = "Sungguh menakjubkan urusan orang beriman, semua urusannya baik. Jika ia mendapat kelapangan ia bersyukur maka itu baik baginya, dan jika ia tertimpa kesempitan ia bersabar maka itu baik baginya.",
            faedah = "Hati seorang mukmin senantiasa damai karena ia menyikapi nikmat dengan syukur dan musibah dengan sabar."
        ),
        Hadits(
            id = 11,
            title = "Sholat Adalah Tiang Agama",
            narrator = "HR. Al-Baihaqi & At-Tirmidzi no. 2616",
            source = "Sunan At-Tirmidzi",
            category = "Sholat & Ibadah",
            arabic = "الصَّلَاةُ عِمَادُ الدِّينِ، فَمَنْ أَقَامَهَا فَقَدْ أَقَامَ الدِّينَ، وَمَنْ هَدَمَهَا فَقَدْ هَدَمَ الدِّينَ",
            latin = "Ash-shalaatu 'imaadud diin, fa man aqaamahaa faqad aqaamad diin...",
            translation = "Sholat adalah tiang agama. Barangsiapa yang mendirikannya maka ia telah menegakkan agama, dan barangsiapa yang meninggalkannya maka ia telah meruntuhkan agama.",
            faedah = "Sholat lima waktu adalah amalan pertama yang dihisab di akhirat kelak."
        ),
        Hadits(
            id = 12,
            title = "Berbakti Kepada Kedua Orang Tua",
            narrator = "HR. At-Tirmidzi no. 1899 & Ibnu Hibban (Shahih)",
            source = "Riyadhus Shalihin",
            category = "Akhlak Mulia",
            arabic = "رِضَا الرَّبِّ فِي رِضَا الْوَالِدِ، وَسَخَطُ الرَّبِّ فِي سَخَطِ الْوَالِدِ",
            latin = "Ridhar Rabbi fii ridhal waalid, wa sakhatur Rabbi fii sakhatil waalid",
            translation = "Ridha Allah terletak pada ridha kedua orang tua, dan murka Allah terletak pada murka kedua orang tua.",
            faedah = "Muliakan dan bahagiakan orang tua sebagai jalan lapang menuju surga-Nya Allah."
        ),
        Hadits(
            id = 13,
            title = "Sedekah Tidak Mengurangi Harta",
            narrator = "HR. Muslim no. 2588",
            source = "Shahih Muslim",
            category = "Sabar & Syukur",
            arabic = "مَا نَقَصَتْ صَدَقَةٌ مِنْ مَالٍ، وَمَا زَادَ اللَّهُ عَبْدًا بِعَفْوٍ إِلَّا عِزًّا، وَمَا تَوَاضَعَ أَحَدٌ لِلَّهِ إِلَّا رَفَعَهُ اللَّهُ",
            latin = "Maa naqashat shadaqatun min maalin...",
            translation = "Sedekah itu tidak akan mengurangi harta, dan tidaklah Allah menambah bagi seorang hamba yang memaafkan kecuali kemuliaan, serta tidaklah seseorang tawadhu karena Allah melainkan Allah meninggikan derajatnya.",
            faedah = "Keberkahan harta dilipatgandakan melalui sedekah yang ikhlas, mendatangkan rezeki tak terduga."
        ),
        Hadits(
            id = 14,
            title = "Dua Kalimat yang Dicintai Allah",
            narrator = "HR. Al-Bukhari no. 6682 & Muslim no. 2694",
            source = "Shahih Al-Bukhari",
            category = "Sholat & Ibadah",
            arabic = "كَلِمَتَانِ خَفِيفَتَانِ عَلَى اللِّسَانِ، ثَقِيلَتَانِ فِي المِيزَانِ، حَبِيبَتَانِ إِلَى الرَّحْمَنِ: سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ العَظِيمِ",
            latin = "Kalimataani khafiifataani 'alal lisaan, tsaqiilataani fil miizaan, habiibataani ilar Rahmaan: Subhaanallaahi wa bihamdih, Subhaanallaahil 'azhiim",
            translation = "Dua kalimat yang ringan di lisan, berat di timbangan mizan, dan dicintai oleh Ar-Rahman: Subhanallah wa bihamdihi, Subhanallahil 'Azhim.",
            faedah = "Dzikir agung yang mudah dibaca kapan saja namun memberikan bobot luar biasa di hari penimbangan kelak."
        ),
        Hadits(
            id = 15,
            title = "Menjaga Silaturahmi Melapangkan Rezeki",
            narrator = "HR. Al-Bukhari no. 2067 & Muslim no. 2557",
            source = "Shahih Al-Bukhari",
            category = "Akhlak Mulia",
            arabic = "مَنْ سَرَّهُ أَنْ يُبْسَطَ لَهُ فِي رِزْقِهِ، وَأَنْ يُنْسَأَ لَهُ فِي أَثَرِهِ، فَلْيَصِلْ رَحِمَهُ",
            latin = "Man sarrahu an yubsatha lahu fii rizqihi wa an yunsa-a lahu fii atsarihi falyashil rahimah",
            translation = "Barangsiapa yang ingin dilapangkan rezekinya dan dipanjangkan umurnya (keberkahan jejak hidupnya), hendaklah ia menyambung tali silaturahmi.",
            faedah = "Menyambung hubungan kekeluargaan membuka pintu rezeki dan menghadirkan keberkahan umur."
        )
    )
}
