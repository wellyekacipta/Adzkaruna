package com.example.doaharianmuslimin.data

import com.example.doaharianmuslimin.data.model.QuranAyat
import com.example.doaharianmuslimin.data.model.QuranSurah

object QuranRepository {
    val surahs: List<QuranSurah> = listOf(
        QuranSurah(
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
        QuranSurah(
            number = 18,
            name = "Al-Kahfi (1-10)",
            arabicName = "الكهف",
            englishMeaning = "Gua (Pelindung Fitnah Dajjal)",
            totalAyat = 10,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "الْحَمْدُ لِلَّهِ الَّذِي أَنزَلَ عَلَىٰ عَبْدِهِ الْكِتَابَ وَلَمْ يَجْعَل لَّهُ عِوَجًا", "Alhamdu lillaahil-ladzii anzala 'alaa 'abdihil-kitaaba wa lam yaj'al lahuu 'iwajaa.", "Segala puji bagi Allah yang telah menurunkan Kitab (Al-Qur'an) kepada hamba-Nya dan Dia tidak menjadikannya bengkok,"),
                QuranAyat(2, "قَيِّمًا لِّيُنذِرَ بَأْسًا شَدِيدًا مِّن لَّدُنْهُ وَيُبَشِّرَ الْمُؤْمِنِينَ الَّذِينَ يَعْمَلُونَ الصَّالِحَاتِ أَنَّ لَهُمْ أَجْرًا حَسَنًا", "Qayyimal liyundzira ba'san syadiidam mil ladunhu wa yubasysyiral-mu'miniinal-ladziina ya'maluunash-shaalihaati anna lahum ajran hasanaa.", "sebagai bimbingan yang lurus, untuk memperingatkan akan siksaan yang sangat pedih dari sisi-Nya dan memberikan kabar gembira kepada orang-orang mukmin yang mengerjakan kebajikan bahwa mereka akan mendapat balasan yang baik,"),
                QuranAyat(3, "مَّاكِثِينَ فِيهِ أَبَدًا", "Maakitsiina fiihi abadaa.", "mereka kekal di dalamnya untuk selama-lamanya,"),
                QuranAyat(4, "وَيُنذِرَ الَّذِينَ قَالُوا اتَّخَذَ اللَّهُ وَلَدًا", "Wa yundziral-ladziina qaalut-takhadzallaahu waladaa.", "dan untuk memperingatkan kepada orang yang berkata, 'Allah mengangkat seorang anak.'"),
                QuranAyat(5, "مَّا لَهُم بِهِ مِنْ عِلْمٍ وَلَا لِآبَائِهِمْ كَبُرَتْ كَلِمَةً تَخْرُجُ مِنْ أَفْوَاهِهِمْ إِن يَقُولُونَ إِلَّا كَذِبًا", "Maa lahum bihii min 'ilmiw wa laa li-aabaa-ihim, kaburat kalimatan takhruju min afwaahihim, iy yaquuluuna illaa kadzibaa.", "Mereka sama sekali tidak mempunyai pengetahuan tentang hal itu, begitu pula nenek moyang mereka. Alangkah besarnya (dosa) perkataan yang keluar dari mulut mereka; mereka hanya mengatakan (sesuatu) kebohongan belaka.")
            )
        ),
        QuranSurah(
            number = 36,
            name = "Yasin (1-10)",
            arabicName = "يس",
            englishMeaning = "Jantung Al-Qur'an",
            totalAyat = 10,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "يس", "Yaa-Siiin.", "Ya Sin."),
                QuranAyat(2, "وَالْقُرْآنِ الْحَكِيمِ", "Wal-qur-aanil-hakiim.", "Demi Al-Qur'an yang penuh hikmah,"),
                QuranAyat(3, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "Innaka laminal-mursaliin.", "sungguh, engkau (Muhammad) adalah salah seorang dari rasul-rasul,"),
                QuranAyat(4, "عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ", "'Alaa shiraathim mustaqiim.", "(yang berada) di atas jalan yang lurus,"),
                QuranAyat(5, "تَنزِيلَ الْعَزِيزِ الرَّحِيمِ", "Tanziilal-'aziizir-rahiim.", "(sebagai wahyu) yang diturunkan oleh (Allah) Yang Maha Perkasa, Maha Penyayang,")
            )
        ),
        QuranSurah(
            number = 67,
            name = "Al-Mulk",
            arabicName = "الملك",
            englishMeaning = "Kerajaan (Penyelamat Siksa Kubur)",
            totalAyat = 5,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Tabaarakalladzii biyadihil-mulku wa huwa 'alaa kulli syai-in qadiir.", "Maha Suci Allah yang di tangan-Nya lah segala kerajaan, dan Dia Maha Kuasa atas segala sesuatu,"),
                QuranAyat(2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا وَهُوَ الْعَزِيزُ الْغَفُورُ", "Alladzii khalaqal-mauta wal-hayaata liyabluwakum ayyukum ahsanu 'amalaa, wa huwal-'aziizul-ghafuur.", "Yang menciptakan mati dan hidup, untuk menguji kamu, siapa di antara kamu yang lebih baik amalnya. Dan Dia Maha Perkasa, Maha Pengampun,"),
                QuranAyat(3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ فَارْجِعِ الْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ", "Alladzii khalaqa sab'a samaawaatin thibaaqaa, maa taraa fii khalqir-rahmaani min tafaawut, farji'il-bashara hal taraa min futhuur.", "Yang menciptakan tujuh langit berlapis-lapis. Kamu tidak akan melihat sesuatu yang tidak seimbang pada ciptaan Tuhan Yang Maha Pengasih. Maka lihatlah sekali lagi, adakah kamu melihat sesuatu yang cacat?"),
                QuranAyat(4, "ثُمَّ ارْجِعِ الْبَصَرَ كَرَّتَيْنِ يَنقَلِبْ إِلَيْكَ الْبَصَرُ خَاسِئًا وَهُوَ حَسِيرٌ", "Tsummarji'il-bashara karrataini yanqalib ilaikal-basharu khaasi-aw wa huwa hasiir.", "Kemudian ulangi pandanganmu sekali lagi, niscaya pandanganmu akan kembali kepadamu tanpa menemukan cacat dan pandanganmu itu pun dalam keadaan payah.")
            )
        ),
        QuranSurah(
            number = 112,
            name = "Al-Ikhlas",
            arabicName = "الإخلاص",
            englishMeaning = "Kemurnian Keesaan Allah",
            totalAyat = 4,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Qul huwallahu ahad.", "Katakanlah (Muhammad), 'Dialah Allah, Yang Maha Esa.'"),
                QuranAyat(2, "اللَّهُ الصَّمَدُ", "Allahus-samad.", "Allah tempat meminta segala sesuatu."),
                QuranAyat(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "Lam yalid wa lam yuulad.", "(Allah) tidak beranak dan tidak pula diperanakkan,"),
                QuranAyat(4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Wa lam yakul lahuu kufuwan ahad.", "dan tidak ada sesuatu yang setara dengan Dia.")
            )
        ),
        QuranSurah(
            number = 113,
            name = "Al-Falaq",
            arabicName = "الفلق",
            englishMeaning = "Waktu Subuh",
            totalAyat = 5,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Qul a'uudzu birabbil-falaq.", "Katakanlah, 'Aku berlindung kepada Tuhan yang menguasai subuh (fajar),'"),
                QuranAyat(2, "مِن شَرِّ مَا خَلَقَ", "Min syarri maa khalaq.", "dari kejahatan (makhluk yang) Dia ciptakan,"),
                QuranAyat(3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "Wa min syarri ghaasiqin idzaa waqab.", "dan dari kejahatan malam apabila telah gelap gulita,"),
                QuranAyat(4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "Wa min syarrin-naffaatsaati fil-'uqad.", "dan dari kejahatan (perempuan-perempuan) penyihir yang meniup pada buhul-buhul (talinya),"),
                QuranAyat(5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "Wa min syarri haasidin idzaa hasad.", "dan dari kejahatan orang yang dengki apabila dia dengki.")
            )
        ),
        QuranSurah(
            number = 114,
            name = "An-Nas",
            arabicName = "الناس",
            englishMeaning = "Manusia",
            totalAyat = 6,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Qul a'uudzu birabbin-naas.", "Katakanlah, 'Aku berlindung kepada Tuhannya manusia,'"),
                QuranAyat(2, "مَلِكِ النَّاسِ", "Malikin-naas.", "'Raja manusia,'"),
                QuranAyat(3, "إِلَٰهِ النَّاسِ", "Ilaahin-naas.", "'Sembahan manusia,'"),
                QuranAyat(4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "Min syarril-waswaasil-khannaas.", "dari kejahatan (bisikan) setan yang bersembunyi,"),
                QuranAyat(5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Alladzii yuwaswisu fii shuduurin-naas.", "yang membisikkan (kejahatan) ke dalam dada manusia,"),
                QuranAyat(6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "Minal-jinnati wan-naas.", "dari (golongan) jin dan manusia.")
            )
        )
    )
}
