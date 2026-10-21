package com.example.doaharianmuslimin.data

import com.example.doaharianmuslimin.data.model.Doa
import com.example.doaharianmuslimin.data.model.DoaCategory

object DoaRepository {

    val doaList: List<Doa> = listOf(
        // ==================== AKTIVITAS HARIAN ====================
        Doa(
            id = 1,
            title = "Doa Sebelum Tidur",
            arabic = "بِاسْمِكَ اللّهُمَّ أَحْيَا وَبِاسْمِكَ أَمُوتُ",
            latin = "Bismika Allahumma ahyaa wa bismika amuut.",
            translation = "Dengan nama-Mu ya Allah, aku hidup dan dengan nama-Mu aku mati.",
            source = "HR. Bukhari no. 6324 & Muslim no. 2711",
            category = DoaCategory.AKTIVITAS,
            note = "Dianjurkan dibaca sambil berbaring miring ke sebelah kanan setelah berwudhu."
        ),
        Doa(
            id = 2,
            title = "Doa Bangun Tidur",
            arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            latin = "Alhamdu lillaahil ladzii ahyaanaa ba'da maa amaatanaa wa ilaihin nusyuur.",
            translation = "Segala puji bagi Allah yang telah menghidupkan kami setelah mematikan kami, dan kepada-Nya lah kami dibangkitkan.",
            source = "HR. Bukhari no. 6312 & Muslim no. 2711",
            category = DoaCategory.AKTIVITAS,
            note = "Sunnah dibaca segera saat membuka mata dan mengusap wajah dengan kedua tangan."
        ),
        Doa(
            id = 3,
            title = "Doa Masuk Kamar Mandi (WC)",
            arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبُثِ وَالْخَبَائِثِ",
            latin = "Allahumma innii a'uudzu bika minal khubutsi wal khabaa-its.",
            translation = "Ya Allah, sesungguhnya aku berlindung kepada-Mu dari godaan setan laki-laki dan setan perempuan.",
            source = "HR. Bukhari no. 142 & Muslim no. 375",
            category = DoaCategory.AKTIVITAS,
            note = "Dianjurkan mendahulukan kaki kiri saat melangkah masuk ke dalam kamar mandi."
        ),
        Doa(
            id = 4,
            title = "Doa Keluar Kamar Mandi (WC)",
            arabic = "غُفْرَانَكَ ، الْحَمْدُ لِلَّهِ الَّذِي أَذْهَبَ عَنِّي الْأَذَى وَعَافَانِي",
            latin = "Ghufraanaka, alhamdu lillaahil ladzii adzhaba 'annil adzaa wa 'aafaanii.",
            translation = "Aku memohon ampunan-Mu. Segala puji bagi Allah yang telah menghilangkan kotoran/penyakit dari badanku dan menyenangkanku.",
            source = "HR. Abu Dawud no. 17 & Tirmidzi no. 7",
            category = DoaCategory.AKTIVITAS,
            note = "Dianjurkan melangkah keluar dengan mendahulukan kaki kanan."
        ),
        Doa(
            id = 5,
            title = "Doa Sebelum Makan",
            arabic = "اللَّهُمَّ بَارِكْ لَنَا فِيمَا رَزَقْتَنَا وَقِنَا عَذَابَ النَّارِ ، بِسْمِ اللَّهِ",
            latin = "Allahumma baarik lanaa fiimaa razaqtanaa wa qinaa 'adzaaban naar, bismillaah.",
            translation = "Ya Allah, berkahilah kami dalam rezeki yang telah Engkau berikan kepada kami dan peliharalah kami dari siksa api neraka. Dengan nama Allah.",
            source = "HR. Ibnu Sunni no. 457",
            category = DoaCategory.AKTIVITAS,
            note = "Minimal mengucapkan 'Bismillah' sebelum makan dan makan menggunakan tangan kanan."
        ),
        Doa(
            id = 6,
            title = "Doa Lupa Membaca Basmalah Sebelum Makan",
            arabic = "بِسْمِ اللَّهِ أَوَّلَهُ وَآخِرَهُ",
            latin = "Bismillaahi awwalahu wa aakhirahu.",
            translation = "Dengan menyebut nama Allah pada awal dan akhirnya.",
            source = "HR. Abu Dawud no. 3767 & Tirmidzi no. 1858",
            category = DoaCategory.AKTIVITAS,
            note = "Dibaca jika teringat di pertengahan santap makan."
        ),
        Doa(
            id = 7,
            title = "Doa Sesudah Makan",
            arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ",
            latin = "Alhamdu lillaahil ladzii ath'amanaa wa saqaanaa wa ja'alanaa muslimiin.",
            translation = "Segala puji bagi Allah yang telah memberi makan dan minum kepada kami serta menjadikan kami termasuk orang-orang yang berserah diri (muslim).",
            source = "HR. Abu Dawud no. 3850 & Tirmidzi no. 3457",
            category = DoaCategory.AKTIVITAS
        ),
        Doa(
            id = 8,
            title = "Doa Memakai Pakaian",
            arabic = "الْحَمْدُ لِلَّهِ الَّذِي كَسَانِي هَذَا الثَّوْبَ وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
            latin = "Alhamdu lillaahil ladzii kasaanii haadzats tsauba wa razaqaniihi min ghairi hawlim minnii wa laa quwwah.",
            translation = "Segala puji bagi Allah yang telah memakaikan pakaian ini kepadaku dan mengaruniakannya kepadaku tanpa daya dan kekuatan dariku.",
            source = "HR. Abu Dawud no. 4023",
            category = DoaCategory.AKTIVITAS,
            note = "Barangsiapa membacanya, diampuni dosanya yang telah lalu."
        ),
        Doa(
            id = 9,
            title = "Doa Melepas Pakaian",
            arabic = "بِسْمِ اللَّهِ الَّذِي لَا إِلَهَ إِلَّا هُوَ",
            latin = "Bismillaahil ladzii laa ilaaha illaa huwa.",
            translation = "Dengan nama Allah yang tiada Tuhan selain Dia.",
            source = "HR. Ibnu Sunni no. 270",
            category = DoaCategory.AKTIVITAS,
            note = "Menjadi penghalang (tabir) antara pandangan jin dan aurat manusia."
        ),
        Doa(
            id = 10,
            title = "Doa Bercermin",
            arabic = "اللَّهُمَّ كَمَا حَسَّنْتَ خَلْقِي فَحَسِّنْ خُلُقِي",
            latin = "Allahumma kamaa hassanta khalqii fa hassin khuluqii.",
            translation = "Ya Allah, sebagaimana Engkau telah memperbagus rupa fisikku, maka perbaguslah pula budi pekertiku (akhlakku).",
            source = "HR. Ahmad 1/403 & Baihaqi",
            category = DoaCategory.AKTIVITAS
        ),
        Doa(
            id = 11,
            title = "Doa Keluar Rumah",
            arabic = "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            latin = "Bismillaahi tawakkaltu 'alallaah, laa hawla wa laa quwwata illaa billaah.",
            translation = "Dengan nama Allah, aku bertawakal kepada Allah. Tiada daya dan kekuatan kecuali dengan pertolongan Allah semata.",
            source = "HR. Abu Dawud no. 5095 & Tirmidzi no. 3426",
            category = DoaCategory.AKTIVITAS,
            note = "Malaikat akan berkata: 'Engkau telah diberi petunjuk, dicukupi dan dilindungi.'"
        ),
        Doa(
            id = 12,
            title = "Doa Masuk Rumah",
            arabic = "بِسْمِ اللَّهِ وَلَجْنَا ، وَبِسْمِ اللَّهِ خَرَجْنَا ، وَعَلَى رَبِّنَا تَوَكَّلْنَا",
            latin = "Bismillaahi walajnaa, wa bismillaahi kharajnaa, wa 'alaa rabbinaa tawakkalnaa.",
            translation = "Dengan nama Allah kami masuk, dan dengan nama Allah kami keluar, dan kepada Tuhan kami, kami bertawakal.",
            source = "HR. Abu Dawud no. 5096",
            category = DoaCategory.AKTIVITAS,
            note = "Ucapkan salam kepada penghuni rumah saat masuk."
        ),
        Doa(
            id = 13,
            title = "Doa Masuk Masjid",
            arabic = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
            latin = "Allahummaftah lii abwaaba rahmatik.",
            translation = "Ya Allah, bukakanlah untukku pintu-pintu rahmat-Mu.",
            source = "HR. Muslim no. 713",
            category = DoaCategory.AKTIVITAS,
            note = "Masuklah dengan mendahulukan kaki kanan sambil membaca shalawat."
        ),
        Doa(
            id = 14,
            title = "Doa Keluar Masjid",
            arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
            latin = "Allahumma innii as-aluka min fadhlik.",
            translation = "Ya Allah, sesungguhnya aku memohon sebagian dari karunia kemurahan-Mu.",
            source = "HR. Muslim no. 713",
            category = DoaCategory.AKTIVITAS,
            note = "Keluarlah dengan mendahulukan kaki kiri."
        ),

        // ==================== SHOLAT & IBADAH ====================
        Doa(
            id = 15,
            title = "Doa Sebelum Berwudhu",
            arabic = "بِسْمِ اللَّهِ",
            latin = "Bismillaah.",
            translation = "Dengan menyebut nama Allah.",
            source = "HR. Abu Dawud no. 101 & Ibnu Majah no. 399",
            category = DoaCategory.SHOLAT
        ),
        Doa(
            id = 16,
            title = "Doa Setelah Berwudhu",
            arabic = "أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ ، اللَّهُمَّ اجْعَلْنِي مِنَ التَّوَّابِينَ وَاجْعَلْنِي مِنَ الْمُتَطَهِّرِينَ",
            latin = "Asyhadu allaa ilaaha illallaahu wahdahu laa syariika lahu wa asyhadu anna Muhammadan 'abduhu wa rasuuluh. Allahummaj'alnii minat tawwaabiina waj'alnii minal mutathahhiriin.",
            translation = "Aku bersaksi tiada Tuhan selain Allah Yang Maha Esa tiada sekutu bagi-Nya, dan aku bersaksi bahwa Muhammad adalah hamba dan utusan-Nya. Ya Allah jadikanlah aku golongan orang-orang yang bertaubat dan menyucikan diri.",
            source = "HR. Muslim no. 234 & Tirmidzi no. 55",
            category = DoaCategory.SHOLAT,
            note = "Dibukakan 8 pintu surga baginya dan ia dipersilakan masuk dari pintu mana saja yang dikehendaki."
        ),
        Doa(
            id = 17,
            title = "Doa Setelah Mendengar Adzan",
            arabic = "اللَّهُمَّ رَبَّ هَذِهِ الدَّعْوَةِ التَّامَّةِ وَالصَّلَاةِ الْقَائِمَةِ ، آتِ مُحَمَّدًا الْوَسِيلَةَ وَالْفَضِيلَةَ ، وَابْعَثْهُ مَقَامًا مَحْمُودًا الَّذِي وَعَدْتَهُ",
            latin = "Allahumma rabba haadzihid da'watit taammati wash-shalaatil qaa-imah, aati Muhammadanil wasiilata wal fadhiilah, wab'atshu maqaamam mahmuudanil ladzii wa'adtah.",
            translation = "Ya Allah, Pemilik seruan yang sempurna ini dan shalat yang didirikan, berikanlah kepada Nabi Muhammad al-wasilah (kedudukan tinggi) dan keutamaan, serta bangkitkanlah beliau pada tempat yang terpuji yang telah Engkau janjikan.",
            source = "HR. Bukhari no. 614",
            category = DoaCategory.SHOLAT,
            note = "Pahalanya mendapat syafaat Nabi Muhammad SAW pada hari kiamat."
        ),
        Doa(
            id = 18,
            title = "Doa Qunut Subuh",
            arabic = "اللَّهُمَّ اهْدِنِي فِيمَنْ هَدَيْتَ ، وَعَافِنِي فِيمَنْ عَافَيْتَ ، وَتَوَلَّنِي فِيمَنْ تَوَلَّيْتَ ، وَبَارِكْ لِي فِيمَا أَعْطَيْتَ ، وَقِنِي شَرَّ مَا قَضَيْتَ ، فَإِنَّكَ تَقْضِي وَلَا يُقْضَى عَلَيْكَ ، وَإِنَّهُ لَا يَذِلُّ مَنْ وَالَيْتَ ، وَلَا يَعِزُّ مَنْ عَادَيْتَ ، تَبَارَكْتَ رَبَّنَا وَتَعَالَيْتَ",
            latin = "Allahummahdinii fiiman hadait, wa 'aafinii fiiman 'aafait, wa tawallanii fiiman tawallait, wa baarik lii fiimaa a'thait, wa qinii syarra maa qadhait, fainnaka taqdhii wa laa yuqdhaa 'alaik, wa innahu laa yadzillu mawwaalait, wa laa ya'izzu man 'aadait, tabaarakta rabbanaa wa ta'aalait.",
            translation = "Ya Allah, berilah aku petunjuk sebagaimana orang yang Engkau beri petunjuk, berilah aku keselamatan sebagaimana orang yang Engkau beri keselamatan, peliharalah aku sebagaimana orang yang Engkau pelihara, berkahilah apa yang telah Engkau karuniakan kepadaku, dan jauhkanlah aku dari keburukan takdir-Mu...",
            source = "HR. Abu Dawud no. 1425 & Tirmidzi no. 464",
            category = DoaCategory.SHOLAT
        ),
        Doa(
            id = 19,
            title = "Doa Setelah Sholat Dhuha",
            arabic = "اللَّهُمَّ إِنَّ الضُّحَاءَ ضُحَاؤُكَ ، وَالْبَهَاءَ بَهَاؤُكَ ، وَالْجَمَالَ جَمَالُكَ ، وَالْقُوَّةَ قُوَّتُكَ ، وَالْقُدْرَةَ قُدْرَتُكَ ، وَالْعِصْمَةَ عِصْمَتُكَ . اللَّهُمَّ إِنْ كَانَ رِزْقِي فِي السَّمَاءِ فَأَنْزِلْهُ ، وَإِنْ كَانَ فِي الْأَرْضِ فَأَخْرِجْهُ ، وَإِنْ كَانَ مُعَسَّرًا فَيَسِّرْهُ ، وَإِنْ كَانَ حَرَامًا فَطَهِّرْهُ ، وَإِنْ كَانَ بَعِيدًا فَقَرِّبْهُ ، بِحَقِّ ضُحَائِكَ وَبَهَائِكَ وَجَمَالِكَ وَقُوَّتِكَ وَقُدْرَتِكَ ، آتِنِي مَا آتَيْتَ عِبَادَكَ الصَّالِحِينَ",
            latin = "Allahumma innadh dhuhaa-a dhuhaa-uka, wal bahaa-a bahaa-uka, wal jamaala jamaaluka, wal quwwata quwwatuka, wal qudrata qudratuka, wal 'ishmata 'ishmatuka. Allahumma in kaana rizqii fis samaa-i fa anzilhu, wa in kaana fil ardhi fa akhrijhu...",
            translation = "Ya Allah, sesungguhnya waktu dhuha adalah waktu dhuha-Mu, keagungan adalah keagungan-Mu, keindahan adalah keindahan-Mu, kekuatan adalah kekuatan-Mu... Jika rezekiku masih di atas langit maka turunkanlah, jika di dalam bumi maka keluarkanlah, jika sukar mudahkanlah...",
            source = "Doa Sholat Dhuha Mashur",
            category = DoaCategory.SHOLAT
        ),
        Doa(
            id = 20,
            title = "Doa Sholat Istikharah",
            arabic = "اللَّهُمَّ إِنِّي أَسْتَخِيرُكَ بِعِلْمِكَ وَأَسْتَقْدِرُكَ بِقُدْرَتِكَ وَأَسْأَلُكَ مِنْ فَضْلِكَ الْعَظِيمِ ، فَإِنَّكَ تَقْدِرُ وَلَا أَقْدِرُ ، وَتَعْلَمُ وَلَا أَعْلَمُ ، وَأَنْتَ عَلَّامُ الْغُيُوبِ",
            latin = "Allahumma innii astakhiiruka bi'ilmika wa astaqdiruka biqudratika wa as-aluka min fadhlikal 'azhiim, fa-innaka taqdiru wa laa aqdir, wa ta'lamu wa laa a'lam, wa anta 'allaamul ghuyuub.",
            translation = "Ya Allah, sesungguhnya aku memohon petunjuk pilihan terbaik dengan ilmu-Mu, memohon kemampuan dengan kodrat-Mu, dan memohon karunia-Mu yang agung. Karena Engkau Maha Kuasa sedang aku tidak berdaya, Engkau Maha Mengetahui sedang aku tidak mengetahui...",
            source = "HR. Bukhari no. 1162",
            category = DoaCategory.SHOLAT,
            note = "Dibaca saat menghadapi keraguan dalam menentukan pilihan penting hidup."
        ),
        Doa(
            id = 21,
            title = "Dzikir Selepas Sholat Fardhu (Tasbih, Tahmid, Takbir)",
            arabic = "سُبْحَانَ اللَّهِ (٣٣×) ، الْحَمْدُ لِلَّهِ (٣٣×) ، اللَّهُ أَكْبَرُ (٣٣×)",
            latin = "Subhaanallah (33x), Alhamdulillah (33x), Allahu Akbar (33x).",
            translation = "Maha Suci Allah (33 kali), Segala Puji bagi Allah (33 kali), Allah Maha Besar (33 kali).",
            source = "HR. Muslim no. 597",
            category = DoaCategory.SHOLAT,
            repeatCount = 33,
            note = "Disempurnakan ke-100 dengan: Laa ilaaha illallaahu wahdahu laa syariikalah..."
        ),

        // ==================== PERLINDUNGAN & KESELAMATAN ====================
        Doa(
            id = 22,
            title = "Doa Sapu Jagad (Kebaikan Dunia & Akhirat)",
            arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            latin = "Rabbanaa aatinaa fid-dunyaa hasanatan wa fil aakhirati hasanatan wa qinaa 'adzaaban naar.",
            translation = "Ya Tuhan kami, berilah kami kebaikan di dunia dan kebaikan di akhirat, dan lindungilah kami dari azab siksa api neraka.",
            source = "QS. Al-Baqarah: 201 (Doa yang paling sering dibaca Rasulullah SAW)",
            category = DoaCategory.PERLINDUNGAN
        ),
        Doa(
            id = 23,
            title = "Doa Terhindar dari Hutang, Sedih & Malas",
            arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ ، وَأَعُوذُ بِكَ مِنَ الْعَجْزِ وَالْكَسَلِ ، وَأَعُوذُ بِكَ مِنَ الْجُبْنِ وَالْبُخْلِ ، وَأَعُوذُ بِكَ مِنْ غَلَبَةِ الدَّيْنِ وَقَهْرِ الرِّجَالِ",
            latin = "Allahumma innii a'uudzu bika minal hammi wal hazan, wa a'uudzu bika minal 'ajzi wal kasal, wa a'uudzu bika minal jubni wal bukhl, wa a'uudzu bika min ghalabatid dayni wa qahrir rijaal.",
            translation = "Ya Allah, aku berlindung kepada-Mu dari rasa sedih dan gelisah, aku berlindung dari kelemahan dan kemalasan, aku berlindung dari sifat pengecut dan kikir, serta aku berlindung dari lilitan hutang dan penindasan orang lain.",
            source = "HR. Abu Dawud no. 1555",
            category = DoaCategory.PERLINDUNGAN,
            note = "Dianjurkan dibaca di waktu pagi dan petang."
        ),
        Doa(
            id = 24,
            title = "Doa Perlindungan Bahaya dan Racun",
            arabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            latin = "Bismillaahil ladzii laa yadhurru ma'asmihi syai-un fil ardhi wa laa fis samaa-i wa huwas samii'ul 'aliim.",
            translation = "Dengan nama Allah, yang bersama nama-Nya tidak ada sesuatu pun yang dapat mendatangkan celaka di bumi maupun di langit. Dan Dialah Yang Maha Mendengar lagi Maha Mengetahui.",
            source = "HR. Abu Dawud no. 5088 & Tirmidzi no. 3388",
            category = DoaCategory.PERLINDUNGAN,
            repeatCount = 3,
            note = "Dibaca 3 kali di pagi dan petang hari agar terhindar dari marabahaya tiba-tiba."
        ),
        Doa(
            id = 25,
            title = "Doa Naik Kendaraan / Safar",
            arabic = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ ، وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ",
            latin = "Subhaanal ladzii sakh-khara lanaa haadzaa wa maa kunnaa lahu muqriniin, wa innaa ilaa rabbinaa lamunqalibuun.",
            translation = "Maha Suci Allah yang telah menundukkan semua ini bagi kami padahal sebelumnya kami tidak mampu menguasainya, dan sesungguhnya kami akan kembali kepada Tuhan kami.",
            source = "QS. Az-Zukhruf: 13-14 & HR. Muslim no. 1342",
            category = DoaCategory.PERLINDUNGAN
        ),
        Doa(
            id = 26,
            title = "Doa Menjenguk Orang Sakit",
            arabic = "اللَّهُمَّ رَبَّ النَّاسِ أَذْهِبِ الْبَاسَ ، اشْفِ أَنْتَ الشَّافِي ، لَا شِفَاءَ إِلَّا شِفَاؤُكَ ، شِفَاءً لَا يُغَادِرُ سَقَمًا",
            latin = "Allahumma rabban naas, adzhibil ba's, isyfi antasy syaafii, laa syifaa-a illaa syifaa-uka, syifaa-an laa yughaadiru saqamaa.",
            translation = "Ya Allah, Tuhan segenap manusia, hilangkanlah rasa sakit ini, sembuhkanlah Engkaulah Dzat Penyembuh. Tiada kesembuhan melainkan kesembuhan dari-Mu, kesembuhan yang tidak meninggalkan penyakit.",
            source = "HR. Bukhari no. 5742 & Muslim no. 2191",
            category = DoaCategory.PERLINDUNGAN
        ),
        Doa(
            id = 27,
            title = "Doa Ketika Turun Hujan",
            arabic = "اللَّهُمَّ صَيِّبًا نَافِعًا",
            latin = "Allahumma shayyiban naafi'an.",
            translation = "Ya Allah, turunkanlah kepada kami hujan yang membawa manfaat.",
            source = "HR. Bukhari no. 1032",
            category = DoaCategory.PERLINDUNGAN,
            note = "Waktu turunnya hujan adalah salah satu saat yang mustajab untuk berdoa."
        ),
        Doa(
            id = 28,
            title = "Doa Tertimpa Musibah",
            arabic = "إِنَّا لِلَّهِ وَإِنَّا إِلَيْهِ رَاجِعُونَ ، اللَّهُمَّ أْجُرْنِي فِي مُصِيبَتِي وَأَخْلِفْ لِي خَيْرًا مِنْهَا",
            latin = "Innaa lillaahi wa innaa ilaihi raaji'uun. Allahumma'jurnii fii mushiibatii wa akhlif lii khairan minhaa.",
            translation = "Sesungguhnya kami adalah milik Allah dan sesungguhnya hanya kepada-Nya kami kembali. Ya Allah, berilah aku pahala dalam musibahku ini dan gantikanlah untukku dengan yang lebih baik daripadanya.",
            source = "HR. Muslim no. 918",
            category = DoaCategory.PERLINDUNGAN
        ),
        Doa(
            id = 29,
            title = "Doa Memohon Keteguhan Iman",
            arabic = "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ",
            latin = "Yaa muqallibal quluub, tsabbit qalbii 'alaa diinik.",
            translation = "Wahai Dzat Yang Maha Membolak-balikkan hati, teguhkanlah hatiku di atas agama-Mu.",
            source = "HR. Tirmidzi no. 2140 & Ahmad",
            category = DoaCategory.PERLINDUNGAN
        ),

        // ==================== KELUARGA & ORANG TUA ====================
        Doa(
            id = 30,
            title = "Doa untuk Kedua Orang Tua",
            arabic = "رَبِّ اغْفِرْ لِي وَلِوَالِدَيَّ وَارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            latin = "Rabbighfir lii wa liwaalidayya warhamhumaa kamaa rabbayaanii shaghiiraa.",
            translation = "Wahai Tuhanku, ampunilah aku dan kedua orang tuaku, dan kasihanilah mereka keduanya sebagaimana mereka telah mendidik dan membesarkanku di waktu kecil.",
            source = "QS. Al-Isra': 24",
            category = DoaCategory.KELUARGA,
            note = "Doa utama anak berbakti bagi orang tua baik yang masih hidup maupun sudah wafat."
        ),
        Doa(
            id = 31,
            title = "Doa Memohon Keturunan Shalih",
            arabic = "رَبِّ هَبْ لِي مِنْ لَدُنْكَ ذُرِّيَّةً طَيِّبَةً إِنَّكَ سَمِيعُ الدُّعَاءِ",
            latin = "Rabbi hab lii mil ladunka dzurriyyatan thayyibatan innaka samii'ud du'aa'.",
            translation = "Ya Tuhanku, berilah aku dari sisi-Mu seorang anak yang baik keturunannya. Sesungguhnya Engkau Maha Pendengar doa.",
            source = "QS. Ali 'Imran: 38 (Doa Nabi Zakariya AS)",
            category = DoaCategory.KELUARGA
        ),
        Doa(
            id = 32,
            title = "Doa Kebahagiaan Keluarga & Pasangan",
            arabic = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
            latin = "Rabbanaa hab lanaa min azwaajinaa wa dzurriyyaatinaa qurrata a'yunin waj'alnaa lil muttaqiina imaamaa.",
            translation = "Ya Tuhan kami, anugerahkanlah kepada kami pasangan kami dan keturunan kami sebagai penyejuk hati (kami), dan jadikanlah kami pemimpin bagi orang-orang yang bertakwa.",
            source = "QS. Al-Furqan: 74",
            category = DoaCategory.KELUARGA
        ),

        // ==================== PAGI & PETANG ====================
        Doa(
            id = 33,
            title = "Sayyidul Istighfar (Penghulu Istighfar)",
            arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ ، خَلَقْتَنِي وَأَنَا عَبْدُكَ ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            latin = "Allahumma anta rabbii laa ilaaha illaa anta, khalaqtanii wa anaa 'abduka, wa anaa 'alaa 'ahdika wa wa'dika mastatha'tu, a'uudzu bika min syarri maa shana'tu, abuu-u laka bini'matika 'alayya, wa abuu-u bidzambii faghfir lii fa innahu laa yaghfirudz dzunuuba illaa anta.",
            translation = "Ya Allah, Engkau adalah Tuhanku, tiada Tuhan selain Engkau. Engkau yang menciptakan aku dan aku adalah hamba-Mu. Aku senantiasa setia pada perjanjian dan janjiku kepada-Mu semampuku. Aku berlindung kepada-Mu dari keburukan yang telah kuperbuat. Aku mengakui nikmat-Mu kepadaku dan aku mengakui dosaku, maka ampunilah aku karena tidak ada yang mengampuni dosa selain Engkau.",
            source = "HR. Bukhari no. 6306",
            category = DoaCategory.PAGI_PETANG,
            note = "Barangsiapa membacanya di petang hari lalu meninggal malamnya atau pagi hari lalu meninggal sebelum petang, ia termasuk penghuni surga."
        ),
        Doa(
            id = 34,
            title = "Ayat Kursi",
            arabic = "اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ وَلَا يَئُودُهُ حِفْظُهُمَا وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            latin = "Allahu laa ilaaha illaa huwal hayyul qayyuum, laa ta'khudzuhu sinatuw wa laa naum, lahu maa fis samaawaati wa maa fil ardhi, man dzal ladzii yasyfa'u 'indahuu illaa bi-idznih, ya'lamu maa baina aidiihim wa maa khalfahum wa laa yuhiithuuna bi-syai-im min 'ilmihii illaa bimaa syaa-a, wasi'a kursiyyuhus samaawaati wal ardha wa laa ya-uuduhuu hifzhuhumaa wa huwal 'aliyyul 'azhiim.",
            translation = "Allah, tidak ada Tuhan selain Dia. Yang Maha Hidup, Yang terus-menerus mengurus (makhluk-Nya), tidak mengantuk dan tidak tidur. Milik-Nya apa yang ada di langit dan apa yang ada di bumi. Siapakah yang dapat memberi syafaat di sisi-Nya tanpa izin-Nya? Allah mengetahui apa yang di hadapan mereka dan apa yang di belakang mereka, dan mereka tidak mengetahui sesuatu apa pun tentang ilmu-Nya melainkan apa yang Dia kehendaki. Kursi-Nya meliputi langit dan bumi. Dan Dia tidak merasa berat memelihara keduanya, dan Dia Maha Tinggi, Maha Besar.",
            source = "QS. Al-Baqarah: 255",
            category = DoaCategory.PAGI_PETANG,
            note = "Dibaca sehabis sholat fardhu, pagi dan petang, serta sebelum tidur sebagai pelindung dari godaan setan."
        ),
        Doa(
            id = 35,
            title = "Doa Keridhaan Iman (Radhitu Billahi Rabba)",
            arabic = "رَضِيتُ بِاللَّهِ رَبًّا ، وَبِالْإِسْلَامِ دِينًا ، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا وَرَسُولًا",
            latin = "Radhiitu billaahi rabbaa, wa bil Islaami diinaa, wa bi Muhammadin shallallaahu 'alaihi wa sallama nabiyyan wa rasuulaa.",
            translation = "Aku rela Allah sebagai Tuhanku, Islam sebagai agamaku, dan Nabi Muhammad shallallahu 'alaihi wa sallam sebagai Nabi dan Rasul.",
            source = "HR. Abu Dawud no. 5072 & Tirmidzi no. 3389",
            category = DoaCategory.PAGI_PETANG,
            repeatCount = 3,
            note = "Wajib bagi Allah untuk meridhai orang yang membacanya 3x setiap pagi dan petang."
        )
    )

    fun getDoaById(id: Int): Doa? = doaList.find { it.id == id }

    fun getDailyFeaturedDoa(): Doa {
        // Daily rotation based on day of year
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return doaList[dayOfYear % doaList.size]
    }
}
