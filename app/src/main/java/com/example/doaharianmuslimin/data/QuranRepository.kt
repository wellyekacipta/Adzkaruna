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
        ),
        QuranSurah(
            number = 67,
            name = "Al-Mulk",
            arabicName = "الملك",
            englishMeaning = "Kerajaan",
            totalAyat = 30,
            revelation = "Makkiyah",
            ayats = listOf(
                QuranAyat(1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Tabaarakalladzii biyadihil-mulku wa huwa 'alaa kulli syai-in qadiir.", "Maha Suci Allah yang di tangan-Nya lah segala kerajaan, dan Dia Maha Kuasa atas segala sesuatu,"),
                QuranAyat(2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا وَهُوَ الْعَزِيزُ الْغَفُورُ", "Alladzii khalaqal-mauta wal-hayaata liyabluwakum ayyukum ahsanu 'amalaa, wa huwal-'aziizul-ghafuur.", "Yang menciptakan mati dan hidup, untuk menguji kamu, siapa di antara kamu yang lebih baik amalnya. Dan Dia Maha Perkasa, Maha Pengampun.")
            )
        )
    )
}
