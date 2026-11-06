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
        ),
        Doa(
            id = 36,
            title = "Keutamaan Berdoa Sebelum Tidur",
            arabic = "",
            latin = "",
            translation = """Sekuat apapun manusia, dia menjadi sangat lemah ketika tidur. Orang yang tidur lelap, bisa menjadi sasaran bagi makhluk jahat di sekitarnya. Jika dia bisa merasa aman dari gangguan manusia, tidak ada jaminan aman dari gangguan makhluk yang tidak nampak. Itulah jin yang jahat. Jika mereka tidak mengganggu anda di alam nyata, bisa jadi mereka akan mengganggu anda di alam mimpi. Itulah kesempatan terbesar bagi setan untuk bertindak nakal, mengganggu manusia.

Dari Abu Hurairah radhiallahu 'anhu, Nabi shallallahu 'alaihi wa sallam bersabda, "Mimpi itu ada tiga macam: bisikan hati, ditakuti setan, dan kabar gembira dari Allah." [1]

Jika gangguan dari luar, anda bisa meminta bantuan orang lain untuk mengusirnya. Tapi ini tidak berlaku ketika dalam mimpi. Anda tidak mungkin memanggil perawat atau istri anda untuk mengusir setan yang mengganggu anda dalam dunia mimpi. Karena itulah, orang yang tidur tanpa berdzikir terlebih dahulu, akan menjadi penyesalan baginya.

Dalam Al-Adzkar, An-Nawawi membuat judul bab: "Bab Makruh Tidur tanpa Berdzikir kepada Allah (sebelum tidur)" (Al-Adzkar, hlm. 95). Selanjutnya, An-Nawawi menyebutkan hadis berikut: Dari Abu Hurairah radhiyallahu 'anhu, Nabi shallallahu 'alaihi wa sallam bersabda: "Siapa yang tidur, sementara tidak berdzikir ketika hendak tidur, akan menjadi penyesalan baginya di hadapan Allah." [2]

Malaikat Dan Setan Menghadiri Orang Yang Hendak Tidur
Dalam hadist yang berasal dari Jabir bin Abdullah, dinyatakan: "Apabila manusia berbaring di pembaringannya (akan tidur), malaikat dan syetan segera menghampirinya. Malaikat membisikkan, 'Akhiri (malam-mu) dengan kebaikan,' sedangkan syetan membisikan, 'Akhiri (malam-mu) dengan keburukan.' Apabila dia berdzikir menyebut nama Allah kemudian tidur, maka malaikat melindungi dia di malam itu." [3]

Di saat anda tidur, anda sangat butuh pertolongan dan pengamanan dari Allah. Untuk mendapatkan jaminan keamanan ini, Rasulullah shallallahu 'alaihi wa sallam mengajarkan kepada kita berbagai doa dan dzikir sebelum tidur. Anda bisa rutinkan doa ini, setiap kali anda hendak tidur.

[1] HR. Bukhari dan Muslim.
[2] HR. Abu Daud 4856 dan dishahihkan Al-Albani.
[3] HR. Ibnu Hibban 5533, Hakim dalam Al-Mustadrak 1969 dan beliau shahihkan, kemudian disepakati oleh Adz-Dzahabi.""".trimIndent(),
            source = "HR. Bukhari, Muslim, Abu Daud & Ibnu Hibban",
            category = DoaCategory.TIDUR,
            note = "Faedah dan keutamaan mengamalkan doa dan dzikir sebelum tidur",
            repeatCount = 1
        ),
        Doa(
            id = 37,
            title = "Doa Tidur 1 (Al-Mu'awwidzaat)",
            arabic = """بِسْمِ اللَّهِ الرَّحْمَـٰنِ الرَّحِيمِ
قُلْ هُوَ اللَّهُ أَحَدٌ ﴿١﴾ اللَّهُ الصَّمَدُ ﴿٢﴾ لَمْ يَلِدْ وَلَمْ يُولَدْ ﴿٣﴾ وَلَمْ يَكُن لَّهُۥ كُفُوًا أَحَدٌۢ ﴿٤﴾

بِسْمِ اللَّهِ الرَّحْمَـٰنِ الرَّحِيمِ
قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ﴿١﴾ مِن شَرِّ مَا خَلَقَ ﴿٢﴾ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ﴿٣﴾ وَمِن شَرِّ النَّفَّاثَاتِ فِى الْعُقَدِ ﴿٤﴾ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ ﴿٥﴾

بِسْمِ اللَّهِ الرَّحْمَـٰنِ الرَّحِيمِ
قُلْ أَعُوذُ بِرَبِّ النَّاسِ ﴿١﴾ مَلِكِ النَّاسِ ﴿٢﴾ إِلَـٰهِ النَّاسِ ﴿٣﴾ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ﴿٤﴾ الَّذِى يُوَسْوِسُ فِى صُدُورِ النَّاسِ ﴿٥﴾ مِنَ الْجِنَّةِ وَالنَّاسِ ﴿٦﴾""".trimIndent(),
            latin = "Membaca Surah Al-Ikhlas, Al-Falaq, dan An-Naas.",
            translation = """(Al-Ikhlas [112]: 1-4)
(1) Katakanlah: Dialah Allah Yang Maha Esa. (2) Allah adalah Ilah yang bergantung kepada-Nya segala urusan. (3) Dia tidak beranak dan tiada pula diperanakkan. (4) Dan tidak ada seorang pun yang setara dengan Dia.

(Al-Falaq [113]: 1-5)
(1) Katakanlah: Aku berlindung kepada Rabb yang menguasai Subuh. (2) Dari kejahatan makhluk-Nya. (3) Dan dari kejahatan malam apabila telah gelap gulita. (4) Dan dari kejahatan wanita-wanita tukang sihir yang menghembus pada buhul-buhul. (5) Dan dari kejahatan orang yang dengki apabila ia dengki.

(An-Nas [114]: 1-6)
(1) Katakanlah: Aku berlindung kepada Rabb manusia. (2) Raja manusia. (3) Sembahan manusia. (4) Dari kejahatan (bisikan) syaitan yang biasa bersembunyi. (5) Yang membisikkan (kejahatan) ke dalam dada manusia. (6) Dari jin dan manusia.""".trimIndent(),
            source = "HR. Al-Bukhari 9/62 dengan Fathul Baari dan Muslim 4/1723",
            category = DoaCategory.TIDUR,
            note = "Mengumpulkan dua telapak tangan lalu ditiup dan dibacakan Al-Ikhlas, Al-Falaq, An-Naas. Kemudian mengusap seluruh tubuh yang dapat dijangkau dengannya, dimulai dari kepala, wajah, dan tubuh bagian depan (3x).",
            repeatCount = 3
        ),
        Doa(
            id = 38,
            title = "Doa Tidur 2 (Ayat Kursi)",
            arabic = "اللَّهُ لَآ إِلَـٰهَ إِلَّا هُوَ الْحَىُّ الْقَيُّومُ لَا تَأْخُذُهُۥ سِنَةٌ وَلَا نَوْمٌ لَّهُۥ مَا فِى السَّمَاوَاتِ وَمَا فِى الْأَرْضِ مَن ذَا الَّذِى يَشْفَعُ عِندَهُۥٓ إِلَّا بِإِذْنِهِۦ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ وَلَا يُحِيطُونَ بِشَىْءٍ مِّنْ عِلْمِهِۦٓ إِلَّا بِمَا شَآءَ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ وَلَا يَئُودُهُۥ حِفْظُهُمَا وَهُوَ الْعَلِىُّ الْعَظِيمُ ﴿٢٥٥﴾",
            latin = "Allaahu laa ilaaha illaa huwal hayyul qoyyuum, laa ta'khudzuhuu sinatuw-walaa naum, lahuu maa fis-samaawaati wa maa fil ardh, man dzal-ladzii yasyfa'u 'indahuu illaa bi-idznih, ya'lamu maa baina aidiihim wa maa kholfahum, wa laa yuhiithuuna bisyai-im min 'ilmihii illaa bimaa syaa-a, wasi'a kursiyyuhus-samaawaati wal ardh, wa laa ya-uuduhuu hifzhuhumaa, wa huwal 'aliyyul 'azhiim.",
            translation = "Allah, tidak ada sesembahan yang berhak disembah kecuali Dia Yang Hidup Kekal lagi terus-menerus mengurus (makhluk-Nya). Tidak mengantuk dan tidak tidur. Kepunyaan-Nya apa yang di langit dan di bumi. Siapakah yang dapat memberi syafa'at di sisi Allah tanpa izin-Nya? Allah mengetahui apa-apa yang di hadapan dan di belakang mereka, dan mereka tidak mengetahui apa-apa dari ilmu Allah melainkan apa yang dikehendaki-Nya. Kursi Allah meliputi langit dan bumi, dan Allah tidak merasa berat memelihara keduanya. Dan Allah Maha Tinggi lagi Maha Besar. (Al-Baqarah [2]: 255).",
            source = "HR. Bukhari no. 2311",
            category = DoaCategory.TIDUR,
            note = "Keutamaan besar: Jika membaca ayat kursi hingga selesai sebelum tidur, maka akan senantiasa ada malaikat penjaga dari Allah dan setan tidak akan dapat mendekatimu sampai pagi.",
            repeatCount = 1
        ),
        Doa(
            id = 39,
            title = "Doa Tidur 3 (Dua Ayat Terakhir Al-Baqarah)",
            arabic = "ءَامَنَ الرَّسُولُ بِمَآ أُنزِلَ إِلَيْهِ مِن رَّبِّهِۦ وَالْمُؤْمِنُونَ كُلٌّ ءَامَنَ بِاللَّهِ وَمَلَآئِكَتِهِۦ وَكُتُبِهِۦ وَرُسُلِهِۦ لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِّن رُّسُلِهِۦ وَقَالُوا سَمِعْنَا وَأَطَعْنَا غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ ﴿٢٨٥﴾ لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ رَبَّنَا لَا تُؤَاخِذْنَآ إِن نَّسِينَآ أَوْ أَخْطَأْنَا رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَآ إِصْرًا كَمَا حَمَلْتَهُۥ عَلَى الَّذِينَ مِن قَبْلِنَا رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِۦ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَآ أَنتَ مَوْلَانَا فَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ ﴿٢٨٦﴾",
            latin = "Aamanar-rosuulu bimaa unzila ilaihi mir-robbihii wal mu'minuun, kullun aamana billaahi wa malaa-ikatihii wa kutubihii wa rusulih, laa nufarriqu baina ahadim mir-rusulih, wa qooluu sami'naa wa atho'naa ghufroonaka robbanaa wa ilaikal mashiir. Laa yukallifullaahu nafsan illaa wus'ahaa, lahaa maa kasabat wa 'alaihaa mak-tasabat, robbanaa laa tu-aakhidznaa in nasiinaa au akh-tho'naa, robbanaa wa laa tahmil 'alainaa ishron kamaa hamaltahuu 'alal-ladziina min qoblinaa, robbanaa wa laa tuhammilnaa maa laa thooqota lanaa bih, wa'fu 'annaa waghfir lanaa warhamnaa anta maulaanaa fanshurnaa 'alal qoumil kaafiriin.",
            translation = """(285) Rasul telah beriman kepada Al-Qur'an yang diturunkan kepadanya dari Rabbnya, demikian pula orang-orang yang beriman. Semuanya beriman kepada Allah, malaikat-malaikat-Nya, kitab-kitab-Nya dan rasul-rasul-Nya. (Mereka mengatakan): "Kami tidak membeda-bedakan antara seorang pun (dengan yang lain) dari rasul-rasul-Nya", dan mereka mengatakan: "Kami dengar dan kami ta'at." (Mereka berdoa): "Ampunilah kami ya Rabb kami dan kepada Engkaulah tempat kembali."
(286) Allah tidak membebani seseorang melainkan sesuai dengan kesanggupannya. Ia mendapat pahala (dari kebajikan) yang diusahakannya dan mendapat siksa (dari kejahatan) yang dikerjakannya. (Mereka berdoa): "Ya Rabb kami, janganlah Engkau hukum kami jika kami lupa atau kami bersalah. Ya Rabb kami, janganlah Engkau bebankan kepada kami beban yang berat sebagaimana Engkau bebankan kepada orang-orang yang sebelum kami. Ya Rabb kami, janganlah Engkau pikulkan kepada kami apa yang tak sanggup kami memikulnya. Beri maaflah kami, ampunilah kami, dan rahmatilah kami. Engkaulah Penolong kami, maka tolonglah kami terhadap kaum yang kafir."
(Al-Baqarah [2]: 285-286)""".trimIndent(),
            source = "HR. Bukhari no. 4008 & Muslim no. 807",
            category = DoaCategory.TIDUR,
            note = "Nabi shallallahu 'alaihi wa sallam bersabda: \"Dua ayat di akhir surat Al-Baqarah, siapa yang membacanya di suatu malam, itu sudah cukup baginya.\" (Melindunginya dari marabahaya & mempermudah bangun tahajud).",
            repeatCount = 1
        ),
        Doa(
            id = 40,
            title = "Doa Tidur 4 (Agar Mati Di Atas Fitrah)",
            arabic = "اَللَّهُمَّ أَسْلَمْتُ نَفْسِي إِلَيْكَ، وَوَجَّهْتُ وَجْهِي إِلَيْكَ، وَفَوَّضْتُ أَمْرِي إِلَيْكَ، وَأَلْجَأْتُ ظَهْرِي إِلَيْكَ، رَغْبَةً وَرَهْبَةً إِلَيْكَ، لاَ مَلْجَأَ وَلاَ مَنْجَا مِنْكَ إِلَّا إِلَيْكَ، آمَنْتُ بِكِتَابِكَ الَّذِي أَنْزَلْتَ، وَبِنَبِيِّكَ الَّذِي أَرْسَلْتَ",
            latin = "Allaahumma aslamtu nafsii ilaika, wa wajjahtu wajhii ilaika, wa fawwadhtu amrii ilaika, wa alja'tu zhohrii ilaika, roghbatan wa rohbatan ilaika, laa malja-a wa laa manjaa minka illaa ilaika, aamantu bikitaabikal-ladzii anzalta, wa binabiyyika-lladzi arsalta.",
            translation = "Ya Allah, aku serahkan jiwaku kepada-Mu, aku hadapkan wajahku kepada-Mu, aku serahkan urusanku kepada-Mu, aku sandarkan punggungku kepada-Mu, karena mengharapkan (pahala-Mu) dan takut (adzab-Mu). Tiada tempat bersandar dan menyelamatkan diri dari (hukuman)-Mu kecuali kepada-Mu. Aku beriman kepada Kitab-Mu yang Engkau turunkan, dan kepada Nabi-Mu yang Engkau utus.",
            source = "HR. Bukhari no. 6311 & Muslim no. 2710",
            category = DoaCategory.TIDUR,
            note = "Dianjurkan berwudhu sebelum tidur dan berbaring miring ke kanan. Jika meninggal dunia di malam itu, maka wafat di atas fitrah (Islam). Jadikan bacaan ini yang terakhir diucapkan.",
            repeatCount = 1
        ),
        Doa(
            id = 41,
            title = "Doa Tidur 5 (Bismika Robbii Wa Dho'tu Janbii)",
            arabic = "بِاسْمِكَ رَبِّيْ وَضَعْتُ جَنْبِيْ، وَبِكَ أَرْفَعُهُ، إِنْ أَمْسَكْتَ نَفْسِيْ فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِيْنَ",
            latin = "Bismika robbii wa dho'tu janbii, wa bika arfa'uhu, in amsakta nafsii farhamhaa, wa in arsaltahaa fahfazhhaa bimaa tahfazhu bihi 'ibaadakash-sholihiin.",
            translation = "Dengan nama Engkau, wahai Tuhanku, aku meletakkan lambungku. Dan dengan namaMu pula aku bangun daripadanya. Apabila Engkau menahan rohku (mati), maka berilah rahmat padanya. Tapi apabila Engkau melepaskannya, maka peliharalah, sebagaimana Engkau memelihara hamba-hambaMu yang shalih.",
            source = "HR. Al-Bukhari 11/126, Muslim 4/2084",
            category = DoaCategory.TIDUR,
            note = "Disunnahkan mengibaskan ujung kain ke tempat tidur 3x sambil menyebut nama Allah sebelum berbaring, lalu membaca doa ini.",
            repeatCount = 1
        ),
        Doa(
            id = 42,
            title = "Doa Tidur 6 (Allaahumma Kholaqta Nafsii)",
            arabic = "اَللَّهُمَّ خَلَقْتَ نَفْسِيْ وَأَنْتَ تَوَفَّاهَا، لَكَ مَمَاتُهَا وَمَحْيَاهَا، إِنْ أَحْيَيْتَهَا فَاحْفَظْهَا، وَإِنْ أَمَتَّهَا فَاغْفِرْ لَهَا. اَللَّهُمَّ إِنِّيْ أَسْأَلُكَ الْعَافِيَةَ",
            latin = "Allaahumma kholaqta nafsii wa anta tawaffaahaa, laka mamaatuhaa wa mah-yaahaa, in ah-yaytahaa fah-fazh-haa, wa in amattahaa fagh-fir lahaa, allaahumma innii as-alukal 'aafiyah.",
            translation = "Ya Allah, sesungguhnya Engkau menciptakan diriku, dan Engkaulah yang akan mematikannya. Mati dan hidupnya hanya milikMu. Apabila Engkau menghidupkannya, maka peliharalah. Apabila Engkau mematikannya, maka ampunilah. Ya Allah, sesungguhnya aku memohon kepadaMu keselamatan.",
            source = "HR. Muslim 4/2083, Ahmad 2/79, Ibnu Sunni no. 721",
            category = DoaCategory.TIDUR,
            note = "Permohonan penjagaan jiwa, ampunan ketika wafat, dan afiat keselamatan hidup.",
            repeatCount = 1
        ),
        Doa(
            id = 43,
            title = "Doa Tidur 7 (Allaahumma Qinii 'Adzaabak)",
            arabic = "اَللَّهُمَّ قِنِيْ عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ",
            latin = "Allaahumma qinii 'adzaabaka yauma tab'atsu 'ibaadak.",
            translation = "Ya Allah, jauhkanlah aku dari siksaanMu pada hari Engkau membangkitkan hamba-hambaMu.",
            source = "HR. Abu Dawud 4/311 & Shahih At-Tirmidzi 3/143",
            category = DoaCategory.TIDUR,
            note = "Adalah Rasulullah Shallallahu 'alaihi wasallam apabila ingin tidur, beliau meletakkan tangannya yang kanan di bawah pipinya, kemudian membaca doa ini 3 kali.",
            repeatCount = 3
        ),
        Doa(
            id = 44,
            title = "Doa Tidur 8 (Tidur Dengan Nama Allah)",
            arabic = "بِاسْمِكَ اللَّهُمَّ أَمُوْتُ وَأَحْيَا",
            latin = "Bismika-llaahumma amuutu wa ahyaa.",
            translation = "Dengan Nama-Mu ya Allah, aku mati dan aku hidup.",
            source = "HR. Bukhari no. 6324",
            category = DoaCategory.TIDUR,
            note = "Maknanya: Aku mati saat tidur (jiwa digenggam sementara oleh Allah sesuai QS. Az-Zumar: 42) dan aku hidup ketika bangun.",
            repeatCount = 1
        ),
        Doa(
            id = 45,
            title = "Doa Tidur 9 (Surat As-Sajdah Dan Al-Mulk)",
            arabic = "الم ﴿١﴾ تَنزِيلُ الْكِتَابِ لَا رَيْبَ فِيهِ مِن رَّبِّ الْعَالَمِينَ ﴿٢﴾ ... تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ ﴿١﴾",
            latin = "Membaca surat As-Sajdah [32]: 1-30 dan Al-Mulk [67]: 1-30.",
            translation = "Membaca surah As-Sajdah (30 ayat) dan surah Al-Mulk (30 ayat) sebelum tidur merupakan kebiasaan rutin Rasulullah shallallahu 'alaihi wa sallam sebagai penyelamat dari siksa kubur.",
            source = "HR. Tirmidzi no. 2892, An-Nasa'i, Shahihul Jami' 4/255",
            category = DoaCategory.TIDUR,
            note = "Rasulullah shallallahu 'alaihi wa sallam tidak tidur sebelum membaca dua surah yang mulia ini.",
            repeatCount = 1
        ),
        Doa(
            id = 46,
            title = "Doa Tidur 10 (Dzikir Pelepas Lelah)",
            arabic = "سُبْحَانَ اللَّهِ (٣٣×) ، اَلْحَمْدُ لِلَّهِ (٣٣×) ، اَللَّهُ أَكْبَرُ (٣٤×)",
            latin = "Subhaanallaah (33x). Alhamdulillaah (33x). Allaahu akbar (34x).",
            translation = "Maha suci Allah (33x). Segala puji bagi Allah (33x). Allah Maha Besar (34x).",
            source = "HR. Bukhari no. 3113 & Muslim no. 2727",
            category = DoaCategory.TIDUR,
            note = "Dzikir yang diajarkan Rasulullah SAW kepada putrinya Fatimah dan menantunya Ali bin Abi Thalib saat merasa sangat lelah. Dzikir ini bernilai lebih baik daripada memiliki seorang pembantu rumah tangga dan memberi kekuatan fisik serta batin.",
            repeatCount = 100
        ),
        Doa(
            id = 47,
            title = "Doa Tidur 11 (Pelunas Hutang & Pelindung)",
            arabic = "اَللَّهُمَّ رَبَّ السَّمَاوَاتِ السَّبْعِ وَرَبَّ الْعَرْشِ الْعَظِيْمِ، رَبَّنَا وَرَبَّ كُلِّ شَيْءٍ، فَالِقَ الْحَبِّ وَالنَّوَى، وَمُنْزِلَ التَّوْرَاةِ وَاْلإِنْجِيْلِ وَالْفُرْقَانِ، أَعُوْذُ بِكَ مِنْ شَرِّ كُلِّ شَيْءٍ أَنْتَ آخِذٌ بِنَاصِيَتِهِ. اَللَّهُمَّ أَنْتَ اْلأَوَّلُ فَلَيْسَ قَبْلَكَ شَيْءٌ، وَأَنْتَ اْلآخِرُ فَلَيْسَ بَعْدَكَ شَيْءٌ، وَأَنْتَ الظَّاهِرُ فَلَيْسَ فَوْقَكَ شَيْءٌ، وَأَنْتَ الْبَاطِنُ فَلَيْسَ دُوْنَكَ شَيْءٌ، اِقْضِ عَنَّا الدَّيْنَ وَأَغْنِنَا مِنَ الْفَقْرِ",
            latin = "Allaahumma robbas-samaawaatis-sab'i wa robbal 'arsyil 'azhiim, robbanaa wa robba kulli syai-in, faaliqol habbi wan-nawaa, wa munzilat-taurooti wal injiili wal furqoon, a'uudzu bika min syarri kulli syai-in anta aakhidzun binaashiyatih. Allaahumma antal awwalu falaisa qoblaka syai-un, wa antal aakhiru falaisa ba'daka syai-un, wa antazh-zhoohiru falaisa fauqoka syai-un, wa antal baathinu falaisa duunaka syai-un, iqdhi 'annad-daina wa aghninaa minal faqr.",
            translation = "Ya Allah, Tuhan yang menguasai langit yang tujuh, Tuhan yang menguasai arasy yang agung, Tuhan kami dan Tuhan segala sesuatu. Tuhan yang membelah butir tumbuh-tumbuhan dan biji buah, Tuhan yang menurunkan kitab Taurat, Injil dan Furqan (Al-Qur'an). Aku berlindung kepadaMu dari kejahatan segala sesuatu yang Engkau memegang ubun-ubunnya. Ya Allah, Engkau-lah yang pertama, sebelumMu tidak ada sesuatu. Engkau-lah yang terakhir, setelahMu tidak ada sesuatu. Engkau-lah yang dzahir, tidak ada sesuatu di atasMu, Engkau-lah yang Batin, tidak ada sesuatu yang menghalangiMu, lunasilah hutang kami dan berilah kami kekayaan hingga terlepas dari kefakiran.",
            source = "HR. Muslim 4/2084",
            category = DoaCategory.TIDUR,
            note = "Doa agung perlindungan total serta permohonan pelunasan hutang dan terbebas dari kefakiran.",
            repeatCount = 1
        ),
        Doa(
            id = 48,
            title = "Doa Tidur 12 (Syukur Rezeki & Tempat Berteduh)",
            arabic = "اَلْحَمْدُ لِلَّهِ الَّذِيْ أَطْعَمَنَا، وَسَقَانَا، وَكَفَانَا، وَآوَانَا، فَكَمْ مِمَّنْ لاَ كَافِيَ لَهُ وَلاَ مُؤْوِيَ",
            latin = "Alhamdulillaahil-ladzii ath'amanaa, wa saqoonaa, wa kafaanaa, wa aawaanaa, fakam mimman laa kaafiya lahu wa laa mu'wii.",
            translation = "Segala puji bagi Allah yang memberi kami makan, memberi kami minum, mencukupi kami, dan memberi kami tempat berteduh. Berapa banyak orang yang tidak mendapatkan siapa yang memberi kecukupan dan tempat berteduh.",
            source = "HR. Muslim 4/2085",
            category = DoaCategory.TIDUR,
            note = "Ungkapan rasa syukur mendalam atas nikmat pangan, sandang, dan tempat bernaung yang aman sebelum terlelap.",
            repeatCount = 1
        ),
        Doa(
            id = 49,
            title = "Doa Tidur 13 (Perlindungan Dari Hasutan Setan)",
            arabic = "اَللَّهُمَّ عَالِمَ الْغَيْبِ وَالشَّهَادَةِ، فَاطِرَ السَّمَاوَاتِ وَاْلأَرْضِ، رَبَّ كُلِّ شَيْءٍ وَمَلِيْكَهُ، أَشْهَدُ أَنْ لاَ إِلَـٰهَ إِلاَّ أَنْتَ، أَعُوْذُ بِكَ مِنْ شَرِّ نَفْسِيْ، وَمِنْ شَرِّ الشَّيْطَانِ وَشِرْكِهِ، وَأَنْ أَقْتَرِفَ عَلَى نَفْسِيْ سُوْءًا، أَوْ أَجُرَّهُ إِلَى مُسْلِمٍ",
            latin = "Allaahumma 'aalimal ghoibi wasy-syahaadati, faathiros-samaawaati wal ardh, robba kulli syai-in wa maliikahu, asyhadu al-laa ilaaha illaa anta, a'uudzu bika min syarri nafsii, wa min syarrisy-syaithooni wa syirkih, wa an aqtarifa 'alaa nafsii suu-an, au ajurrohu ilaa muslim.",
            translation = "Ya Allah, yang Maha Mengetahui yang ghaib dan yang nyata, wahai Tuhan pencipta langit dan bumi, Tuhan segala sesuatu dan yang merajainya. Aku bersaksi bahwa tidak ada sesembahan yang berhak disembah kecuali Engkau. Aku berlindung kepadaMu dari kejahatan diriku, setan dan balatentaranya, dan aku (berlindung kepadaMu) dari berbuat kejelekan terhadap diriku atau mendorongnya kepada seorang muslim.",
            source = "HR. Abu Dawud 4/317 & Shahih At-Tirmidzi 3/142",
            category = DoaCategory.TIDUR,
            note = "Doa perlindungan komprehensif dari keburukan hawa nafsu dan godaan setan.",
            repeatCount = 1
        ),
        Doa(
            id = 50,
            title = "Adab Bila Mengalami Mimpi Buruk",
            arabic = "أَعُوْذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيْمِ وَمِنْ شَرِّ هَذِهِ الرُّؤْيَا",
            latin = "A'uudzu billaahi minasy-syaithoonir-rojiimi wa min syarri haadzihir-ru'yaa.",
            translation = """Sunnah & Adab ketika mengalami mimpi buruk:
1. Meludah (meniup pelan berdesis) ke arah kirinya sebanyak 3x.
2. Memohon perlindungan kepada Allah dari godaan setan dan kejelekan mimpinya 3x.
3. Tidak menceritakan mimpi buruk tersebut kepada siapapun.
4. Membalikkan tubuhnya (mengubah posisi tidur dari posisi semula).
5. Berdiri lalu mendirikan shalat sunnah bila berkehendak.""".trimIndent(),
            source = "HR. Muslim no. 1772-1773",
            category = DoaCategory.TIDUR,
            note = "Jika menjalankan tuntunan Nabi SAW ini, maka mimpi buruk tersebut insya Allah tidak akan membahayakan dirinya sama sekali.",
            repeatCount = 1
        ),
        Doa(
            id = 51,
            title = "Doa Bila Takut & Kesepian Ketika Tidur",
            arabic = "أَعُوْذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ غَضَبِهِ وَعِقَابِهِ، وَشَرِّ عِبَادِهِ، وَمِنْ هَمَزَاتِ الشَّيَاطِيْنِ وَأَنْ يَحْضُرُوْنِ",
            latin = "A'uudzu bikalimaatillaahit-taammaati min ghodhobihi wa 'iqoobihi, wa syarri 'ibaadihi, wa min hamazaatisy-syayaathiini wa an yahdhuruun.",
            translation = "Aku berlindung dengan kalimat Allah yang sempurna dari kemarahan dan siksaanNya, serta kejahatan hamba-hambaNya, dan dari godaan setan (bisikannya) serta jangan sampai mereka hadir (kepadaku).",
            source = "HR. Abu Dawud 4/12 & Shahih At-Tirmidzi 3/171",
            category = DoaCategory.TIDUR,
            note = "Sangat dianjurkan dibaca saat seseorang merasa cemas, gelisah, takut, atau merasa kesepian di malam hari.",
            repeatCount = 1
        ),
        Doa(
            id = 52,
            title = "Doa Membalikkan Tubuh Ketika Tidur Malam",
            arabic = "لاَ إِلَـٰهَ إِلاَّ اللَّهُ الْوَاحِدُ الْقَهَّارُ، رَبُّ السَّمَاوَاتِ وَاْلأَرْضِ وَمَا بَيْنَهُمَا الْعَزِيْزُ الْغَفَّارُ",
            latin = "Laa ilaaha illallaahul waahidul qohhaar, robbus-samaawaati wal ardhi wa maa bainahumal 'aziizul ghoffaar.",
            translation = "Tidak ada sesembahan yang berhak disembah kecuali Allah, Yang Maha Esa, Maha Perkasa, Tuhan yang menguasai langit dan bumi dan apa yang di antara keduanya, Yang Maha Mulia lagi Maha Pengampun.",
            source = "HR. Al-Hakim 1/540 (shahih), An-Nasa'i, Shahihul Jami' 4/231",
            category = DoaCategory.TIDUR,
            note = "Rasulullah shallallahu 'alaihi wa sallam membaca doa ini ketika terbangun dan berbalik dari satu sisi ke sisi lain pada malam hari.",
            repeatCount = 1
        ),
        Doa(
            id = 53,
            title = "Doa Bangun Tidur 1 (Menghidupkan Kembali)",
            arabic = "اَلْحَمْدُ لِلَّهِ الَّذِيْ أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُوْرِ",
            latin = "Alhamdulillaahil-ladzii ahyaanaa ba'da maa amaatanaa wa ilaihin-nusyuur.",
            translation = "Segala puji bagi Allah, yang membangunkan kami setelah ditidurkanNya dan kepadaNya kami dibangkitkan.",
            source = "HR. Al-Bukhari Fathul Baari 11/113, Muslim 4/2083",
            category = DoaCategory.TIDUR,
            note = "Sunnah dibaca segera ketika membuka mata di pagi hari sambil mengusap bekas tidur di wajah.",
            repeatCount = 1
        ),
        Doa(
            id = 54,
            title = "Doa Bangun Tidur 2 (Kesehatan Jasad & Ruh)",
            arabic = "اَلْحَمْدُ لِلَّهِ الَّذِيْ عَافَانِيْ فِيْ جَسَدِيْ، وَرَدَّ عَلَيَّ رُوْحِيْ، وَأَذِنَ لِيْ بِذِكْرِهِ",
            latin = "Alhamdulillaahil-ladzii 'aafaanii fii jasadii, wa rodda 'alayya ruuhii, wa adzina lii bidzikrih.",
            translation = "Segala puji bagi Allah yang telah memberikan kesehatan pada jasadku dan mengembalikan ruhku kepadaku serta mengizinkanku untuk berdzikir kepadaNya.",
            source = "HR. At-Tirmidzi 5/473 & Shahih At-Tirmidzi 3/144",
            category = DoaCategory.TIDUR,
            note = "Syukur atas diberikannya kesehatan fisik, dikembalikannya ruh ke dalam raga, dan kesempatan berdzikir kembali.",
            repeatCount = 1
        ),
        Doa(
            id = 55,
            title = "Doa Bangun Tidur 3 (Surat Ali Imran 190-200)",
            arabic = "إِنَّ فِى خَلْقِ السَّمَاوَاتِ وَالْأَرْضِ وَاخْتِلَافِ الَّيْلِ وَالنَّهَارِ لَءَايَاتٍ لِّأُولِى الْأَلْبَابِ ﴿١٩٠﴾ الَّذِينَ يَذْكُرُونَ اللَّهَ قِيَامًا وَقُعُودًا وَعَلَى جُنُوبِهِمْ وَيَتَفَكَّرُونَ فِى خَلْقِ السَّمَاوَاتِ وَالْأَرْضِ رَبَّنَا مَا خَلَقْتَ هَـٰذَا بَاطِلًا سُبْحَانَكَ فَقِنَا عَذَابَ النَّارِ ﴿١٩١﴾ رَبَّنَآ إِنَّكَ مَن تُدْخِلِ النَّارَ فَقَدْ أَخْزَيْتَهُۥ وَمَا لِلظَّالِمِينَ مِنْ أَنصَارٍ ﴿١٩٢﴾ رَّبَّنَآ إِنَّنَا سَمِعْنَا مُنَادِيًا يُنَادِى لِلْإِيمَانِ أَنْ ءَامِنُوا بِرَبِّكُمْ فَئَامَنَّا رَبَّنَا فَاغْفِرْ لَنَا ذُنُوبَنَا وَكَفِّرْ عَنَّا سَيِّئَاتِنَا وَتَوَفَّنَا مَعَ الْأَبْرَارِ ﴿١٩٣﴾ رَبَّنَا وَءَاتِنَا مَا وَعَدتَّنَا عَلَى رُسُلِكَ وَلَا تُخْزِنَا يَوْمَ الْقِيَامَةِ إِنَّكَ لَا تُخْلِفُ الْمِيعَادَ ﴿١٩٤﴾ فَاسْتَجَابَ لَهُمْ رَبُّهُمْ أَنِّى لَآ أُضِيعُ عَمَلَ عَامِلٍ مِّنكُم مِّن ذَكَرٍ أَوْ أُنثَى بَعْضُكُم مِّنۢ بَعْضٍ فَالَّذِينَ هَاجَرُوا وَأُخْرِجُوا مِن دِيَارِهِمْ وَأُوذُوا فِى سَبِيلِى وَقَاتَلُوا وَقُتِلُوا لَأُكَفِّرَنَّ عَنْهُمْ سَيِّئَاتِهِمْ وَلَأُدْخِلَنَّهُمْ جَنَّاتٍ تَجْرِى مِن تَحْتِهَا الْأَنْهَارُ ثَوَابًا مِّنْ عِندِ اللَّهِ وَاللَّهُ عِندَهُۥ حُسْنُ الثَّوَابِ ﴿١٩٥﴾ لَا يَغُرَّنَّكَ تَقَلُّبُ الَّذِينَ كَفَرُوا فِى الْبِلَادِ ﴿١٩٦﴾ مَتَاعٌ قَلِيلٌ ثُمَّ مَأْوَاهُمْ جَهَنَّمُ وَبِئْسَ الْمِهَادُ ﴿١٩٧﴾ لَـٰكِنِ الَّذِينَ اتَّقَوْا رَبَّهُمْ لَهُمْ جَنَّاتٌ تَجْرِى مِن تَحْتِهَا الْأَنْهَارُ خَالِدِينَ فِيهَا نُزُلًا مِّنْ عِندِ اللَّهِ وَمَا عِندَ اللَّهِ خَيْرٌ لِّلْأَبْرَارِ ﴿١٩٨﴾ وَإِنَّ مِنْ أَهْلِ الْكِتَابِ لَمَن يُؤْمِنُ بِاللَّهِ وَمَآ أُنزِلَ إِلَيْكُمْ وَمَآ أُنزِلَ إِلَيْهِمْ خَاشِعِينَ لِلَّهِ لَا يَشْتَرُونَ بِئَايَاتِ اللَّهِ ثَمَنًا قَلِيلًا أُولَـٰٓئِكَ لَهُمْ أَجْرُهُمْ عِندَ رَبِّهِمْ إِنَّ اللَّهَ سَرِيعُ الْحِسَابِ ﴿١٩٩﴾ يَآ أَيُّهَا الَّذِينَ ءَامَنُوا اصْبِرُوا وَصَابِرُوا وَرَابِطُوا وَاتَّقُوا اللَّهَ لَعَلَّكُمْ تُفْلِحُونَ ﴿٢٠٠﴾",
            latin = "Inna fii kholqis-samaawaati wal ardhi wakhtilaafil-laili wan-nahaari la-aayaatil li-ulil albaab... Membaca 10 ayat terakhir Surah Ali 'Imran (190-200).",
            translation = """(190) Sesungguhnya dalam penciptaan langit dan bumi silih bergantinya malam dan siang, terdapat tanda-tanda bagi orang-orang yang berakal.
(191) (Yaitu) orang-orang yang mengingat Allah dalam keadaan berdiri, duduk atau berbaring, dan mereka memikirkan tentang penciptaan langit dan bumi (seraya berkata): "Ya, Tuhan kami! Tidaklah Engkau menciptakan ini dengan sia-sia. Maha Suci Engkau, maka peliharalah kami dari siksa Neraka.
(192) Ya Rabb kami, sesungguhnya barangsiapa yang Engkau masukkan ke dalam Neraka, maka sungguh telah Engkau hinakan ia, dan tidak ada bagi orang-orang yang dzalim seorang penolongpun.
(193) Ya Rabb kami, sesungguhnya kami mendengar (seruan) yang menyeru kepada iman, (yaitu): "Berimanlah kamu kepada Rabbmu", maka kamipun beriman. Ya Rabb kami, ampunilah bagi kami dosa-dosa kami dan hapuskanlah dari kami kesalahan-kesalahan kami, dan wafatkanlah kami beserta orang-orang yang berbakti.
(194) Ya Rabb kami, berilah kami apa yang telah Engkau janjikan kepada kami dengan perantaraan rasul-rasul Engkau. Dan janganlah Engkau hinakan kami di hari Kiamat. Sesungguhnya Engkau tidak menyalahi janji."
(195) Maka Rabb mereka memperkenankan permohonannya (dengan berfirman): "Sesungguhnya Aku tidak menyia-nyiakan amal orang-orang yang beramal di antara kamu, baik laki-laki atau perempuan, (karena) sebagian kamu adalah turunan dari sebagian yang lain. Maka orang-orang yang berhijrah, yang diusir dari kampung halamannya, yang disakiti pada jalan-Ku, yang berperang dan yang dibunuh, pastilah akan Kuhapuskan kesalahan-kesalahan mereka dan pastilah Aku masukkan mereka ke dalam Surga yang mengalir sungai-sungai di bawahnya, sebagai pahala di sisi Allah. Dan Allah pada sisiNya pahala yang baik."
(196) Janganlah sekali-kali kamu terpedaya oleh kebebasan orang-orang kafir bergerak di dalam negeri.
(197) Itu hanyalah kesenangan sementara, kemudian tempat tinggal mereka ialah Jahannam, dan Jahannam itu adalah tempat yang seburuk-buruknya.
(198) Akan tetapi orang-orang yang bertaqwa kepada Rabbnya, bagi mereka Surga yang mengalir sungai-sungai di dalamnya, sedang mereka kekal di dalamnya sebagai tempat tinggal (anugerah) dari sisi Allah. Dan apa yang di sisi Allah adalah lebih baik bagi orang-orang yang berbakti.
(199) Dan sesungguhnya di antara ahli kitab ada orang yang beriman kepada Allah dan kepada apa yang diturunkan kepada kamu dan yang diturunkan kepada mereka sedang mereka berendah hati kepada Allah dan mereka tidak menukarkan ayat-ayat Allah dengan harga yang sedikit. Mereka memperoleh pahala di sisi Rabbnya. Sesungguhnya Allah amat cepat perhitungan-Nya.
(200) Hai orang-orang yang beriman, bersabarlah kamu dan kuatkanlah kesabaranmu dan tetaplah bersiap siaga (di perbatasan negerimu) dan bertaqwalah kepada Allah supaya kamu beruntung.""".trimIndent(),
            source = "HR. Al-Bukhari Fathul Bari 8/237 & Muslim 1/530",
            category = DoaCategory.TIDUR,
            note = "Sunnah dibaca ketika bangun pada malam hari (sebelum qiyamul lail / tahajud) sambil memandang ke langit dan bertafakur.",
            repeatCount = 1
        ),
        Doa(
            id = 56,
            title = "Doa Bila Terjaga Di Malam Hari",
            arabic = "لاَ إِلَـٰهَ إِلاَّ اللَّهُ، وَحْدَهُ لاَ شَرِيْكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيْرٌ، اَلْحَمْدُ لِلَّهِ، وَسُبْحَانَ اللَّهِ، وَلاَ إِلَـٰهَ إِلاَّ اللَّهُ، وَاللَّهُ أَكْبَرُ، وَلاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللَّهِ ، اَللَّهُمَّ اغْفِرْ لِي",
            latin = "Laa ilaaha illallaah, wahdahu laa syariika lah, lahul mulku wa lahul hamdu, wa huwa 'alaa kulli syai-in qodiir, alhamdulillaah, wa subhaanallaah, wa laa ilaaha illallaah, wallaahu akbar, wa laa haula wa laa quwwata illaa billaah. Allaahummaghfir lii.",
            translation = "Tidak ada sesembahan yang berhak disembah kecuali Allah, Yang Maha Esa, tiada sekutu bagiNya. BagiNya kerajaan dan pujian. Dia-lah Yang Maha Kuasa atas segala sesuatu. Segala Puji bagi Allah, Maha Suci Allah, tidak ada sesembahan yang berhak disembah kecuali Allah, Allah Maha Besar, tiada daya dan kekuatan kecuali dengan pertolongan Allah. Ya Allah, ampunilah dosaku.",
            source = "HR. al-Bukhari no. 1103, Abu Dawud 5060, at-Tirmidzi 3414, Ibnu Majah 3878 (Shahih)",
            category = DoaCategory.TIDUR,
            note = "Keutamaan istimewa: Nabi SAW bersabda barangsiapa membaca dzikir ini saat terbangun di malam hari lalu berdoa, niscaya akan dikabulkan doanya. Jika dia berwudhu lalu shalat, maka shalatnya akan diterima.",
            repeatCount = 1
        )
    )

    // Precomputed list untuk Dzikir Pagi & Petang agar pembukaan TasbihScreen instan (0ms)
    val dzikirList: List<Doa> by lazy {
        val filtered = doaList.filter { it.category == DoaCategory.PAGI_PETANG }
        if (filtered.isNotEmpty()) filtered else doaList.take(20)
    }

    fun getDoaById(id: Int): Doa? = doaList.find { it.id == id }

    fun getDailyFeaturedDoa(): Doa {
        // Daily rotation based on day of year
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return doaList[dayOfYear % doaList.size]
    }
}
