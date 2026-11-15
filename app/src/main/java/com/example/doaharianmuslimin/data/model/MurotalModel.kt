package com.example.doaharianmuslimin.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class MurotalQari(
    val id: String,
    val name: String,
    val subtitle: String,
    val audioPath: String
)

object MurotalQariRepository {
    val qariList: List<MurotalQari> = listOf(
        MurotalQari(
            id = "05",
            name = "Misyari Rasyid Al-Afasi",
            subtitle = "Khas, Lembut & Merdu",
            audioPath = "Misyari-Rasyid-Al-Afasi"
        ),
        MurotalQari(
            id = "03",
            name = "Abdurrahman as-Sudais",
            subtitle = "Imam Besar Masjidil Haram",
            audioPath = "Abdurrahman-as-Sudais"
        ),
        MurotalQari(
            id = "01",
            name = "Abdullah Al-Juhany",
            subtitle = "Imam Masjidil Haram",
            audioPath = "Abdullah-Al-Juhany"
        ),
        MurotalQari(
            id = "02",
            name = "Abdul-Muhsin Al-Qasim",
            subtitle = "Imam Masjid Nabawi",
            audioPath = "Abdul-Muhsin-Al-Qasim"
        ),
        MurotalQari(
            id = "04",
            name = "Ibrahim Al-Dossari",
            subtitle = "Khas Tartil Cepat & Jelas",
            audioPath = "Ibrahim-Al-Dossari"
        ),
        MurotalQari(
            id = "06",
            name = "Yasser Al-Dosari",
            subtitle = "Khusyuk & Mendalam",
            audioPath = "Yasser-Al-Dosari"
        )
    )

    val defaultQari: MurotalQari = qariList[0]
}

enum class AmbientSoundType(
    val title: String,
    val audioUrl: String
) {
    HUJAN("Hujan", "https://raw.githubusercontent.com/karthiknvd/noctune/main/sounds/rain.mp3"),
    API("Api", "https://raw.githubusercontent.com/karthiknvd/noctune/main/sounds/campfire.mp3"),
    JANGKRIK("Jangkrik", "https://raw.githubusercontent.com/karthiknvd/noctune/main/sounds/night.mp3"),
    SUNGAI("Sungai", "https://raw.githubusercontent.com/karthiknvd/noctune/main/sounds/river.mp3")
}
