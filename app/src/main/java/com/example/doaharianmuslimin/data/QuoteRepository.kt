package com.example.doaharianmuslimin.data

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.doaharianmuslimin.R
import com.example.doaharianmuslimin.data.model.QuoteItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class QuoteRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("adzkaruna_quotes_prefs", Context.MODE_PRIVATE)

    companion object {
        val defaultQuotes: List<QuoteItem> = listOf(
            QuoteItem(
                id = "quote_default_1",
                text = "Sedekah tidaklah mengurangi harta.",
                source = "HR. Muslim no. 2588",
                category = "HADIS"
            ),
            QuoteItem(
                id = "quote_default_2",
                text = "Sesungguhnya setiap amalan tergantung pada niatnya.",
                source = "HR. Al-Bukhari no. 1 & Muslim no. 1907",
                category = "HADIS"
            ),
            QuoteItem(
                id = "quote_default_3",
                text = "Senyummu di hadapan saudaramu adalah sedekah bagimu.",
                source = "HR. At-Tirmidzi no. 1956",
                category = "HADIS"
            ),
            QuoteItem(
                id = "quote_default_4",
                text = "Barangsiapa bertakwa kepada Allah niscaya Dia akan membukakan jalan keluar baginya.",
                source = "QS. At-Talaq: 2-3",
                category = "AL-QUR'AN"
            ),
            QuoteItem(
                id = "quote_default_5",
                text = "Maka sesungguhnya bersama kesulitan ada kemudahan.",
                source = "QS. Al-Insyirah: 5-6",
                category = "AL-QUR'AN"
            ),
            QuoteItem(
                id = "quote_default_6",
                text = "Sebaik-baik manusia adalah yang paling bermanfaat bagi orang lain.",
                source = "HR. Ahmad & Ath-Thabrani",
                category = "HADIS"
            ),
            QuoteItem(
                id = "quote_default_7",
                text = "Kekayaan sejati bukanlah banyaknya harta, melainkan kekayaan jiwa.",
                source = "HR. Al-Bukhari no. 6446 & Muslim no. 1051",
                category = "HADIS"
            )
        )
    }

    // ==================== SAVED QUOTES (BOOKMARKS & CUSTOM) ====================
    fun getSavedQuotes(): List<QuoteItem> {
        val json = prefs.getString("saved_quotes_list", null) ?: return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<QuoteItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    QuoteItem(
                        id = obj.getString("id"),
                        text = obj.getString("text"),
                        source = obj.getString("source"),
                        category = obj.optString("category", "HADIS"),
                        isCustom = obj.optBoolean("isCustom", false),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            list.sortedByDescending { it.timestamp }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun persistSavedQuotes(list: List<QuoteItem>) {
        val array = JSONArray()
        for (item in list) {
            array.put(JSONObject().apply {
                put("id", item.id)
                put("text", item.text)
                put("source", item.source)
                put("category", item.category)
                put("isCustom", item.isCustom)
                put("timestamp", item.timestamp)
            })
        }
        prefs.edit().putString("saved_quotes_list", array.toString()).apply()
    }

    fun isQuoteSaved(quoteId: String): Boolean {
        return getSavedQuotes().any { it.id == quoteId }
    }

    fun toggleSaveQuote(quote: QuoteItem): Boolean {
        val current = getSavedQuotes().toMutableList()
        val existingIdx = current.indexOfFirst { it.id == quote.id }
        val nowSaved = if (existingIdx >= 0) {
            current.removeAt(existingIdx)
            false
        } else {
            current.add(0, quote.copy(timestamp = System.currentTimeMillis()))
            true
        }
        persistSavedQuotes(current)
        return nowSaved
    }

    fun addCustomQuote(text: String, source: String): QuoteItem {
        val newQuote = QuoteItem(
            id = "custom_" + System.currentTimeMillis(),
            text = text.trim(),
            source = if (source.isBlank()) "Kutipan Pribadi" else source.trim(),
            category = "KUTIPAN",
            isCustom = true,
            timestamp = System.currentTimeMillis()
        )
        val current = getSavedQuotes().toMutableList()
        current.add(0, newQuote)
        persistSavedQuotes(current)
        return newQuote
    }

    fun deleteQuote(quoteId: String) {
        val current = getSavedQuotes().toMutableList()
        current.removeAll { it.id == quoteId }
        persistSavedQuotes(current)
    }

    // ==================== EXPORT & SHARE AS IMAGE (SESUAI GAMBAR 1) ====================
    enum class ExportColorTheme(val title: String, val bgColor: Int, val textColor: Int, val subTextColor: Int) {
        BIRU("Biru", 0xFFD2EBF7.toInt(), 0xFF1B4D68.toInt(), 0xFF356985.toInt()),
        TOSCA("Hijau Tosca", 0xFFD5F3EE.toInt(), 0xFF0D5247.toInt(), 0xFF236E62.toInt()),
        MERAH("Merah", 0xFFFFE5E8.toInt(), 0xFF6B1B22.toInt(), 0xFF8A3038.toInt()),
        GELAP("Mode Gelap", 0xFF191D24.toInt(), 0xFFFFFFFF.toInt(), 0xFF9EABB8.toInt())
    }

    fun generateQuoteCardBitmap(quote: QuoteItem, theme: ExportColorTheme): Bitmap {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background fill
        val bgPaint = Paint().apply {
            color = theme.bgColor
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Subtle gradient vignette / aesthetic Islamic decorative arch line at the top
        val archPaint = Paint().apply {
            color = theme.textColor
            alpha = 18
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawCircle((width / 2).toFloat(), -200f, 900f, archPaint)

        // 2. Header text: "KUTIPAN HARI INI • HADIS"
        val headerPaint = TextPaint().apply {
            color = theme.textColor
            textSize = 34f
            isFakeBoldText = true
            letterSpacing = 0.22f
            isAntiAlias = true
        }
        val headerText = "KUTIPAN HARI INI • ${quote.category.uppercase()}"
        canvas.drawText(headerText, 90f, 130f, headerPaint)

        // 3. Main Quote text: “...”
        val quoteText = "“${quote.text}”"
        val quotePaint = TextPaint().apply {
            color = theme.textColor
            textSize = 62f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val textWidth = width - 180
        @Suppress("DEPRECATION")
        val quoteLayout = StaticLayout(
            quoteText,
            quotePaint,
            textWidth,
            Layout.Alignment.ALIGN_NORMAL,
            1.25f,
            0f,
            false
        )

        // Position quote in center/upper third matching Image 1
        val quoteY = 820f
        canvas.save()
        canvas.translate(90f, quoteY)
        quoteLayout.draw(canvas)
        canvas.restore()

        // 4. Source text: e.g. "HR. Muslim no. 2588"
        val sourcePaint = TextPaint().apply {
            color = theme.subTextColor
            textSize = 42f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val sourceY = quoteY + quoteLayout.height + 65f
        canvas.drawText(quote.source, 90f, sourceY, sourcePaint)

        // 5. Footer with Adzkaruna Logo & Branding (No Cat!)
        val logoSize = 130
        val footerY = height - 160f
        try {
            val logoOptions = BitmapFactory.Options().apply { inScaled = false }
            val logoBmp = BitmapFactory.decodeResource(context.resources, R.drawable.app_logo_kufi, logoOptions)
            if (logoBmp != null) {
                val scaledLogo = Bitmap.createScaledBitmap(logoBmp, logoSize, logoSize, true)
                canvas.drawBitmap(scaledLogo, 90f, footerY - logoSize + 15f, null)
                scaledLogo.recycle()
            }
        } catch (_: Exception) {}

        // Right side footer: "Meowslim" -> "Adzkaruna"
        val brandPaint = TextPaint().apply {
            color = theme.textColor
            textSize = 48f
            isFakeBoldText = true
            letterSpacing = 0.05f
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        canvas.drawText("Adzkaruna", (width - 90).toFloat(), footerY - 20f, brandPaint)

        val taglinePaint = TextPaint().apply {
            color = theme.subTextColor
            textSize = 28f
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        canvas.drawText("Doa & Dzikir Harian Muslimin", (width - 90).toFloat(), footerY + 24f, taglinePaint)

        return bitmap
    }

    fun shareQuoteImage(quote: QuoteItem, theme: ExportColorTheme) {
        try {
            val bitmap = generateQuoteCardBitmap(quote, theme)
            val cachePath = File(context.cacheDir, "quote_images")
            cachePath.mkdirs()

            val file = File(cachePath, "adzkaruna_kutipan_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, "“${quote.text}” — ${quote.source}\n\nDibagikan melalui aplikasi Adzkaruna")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Kutipan"))
        } catch (e: Exception) {
            // Fallback to text share
            shareQuoteText(quote)
        }
    }

    fun shareQuoteText(quote: QuoteItem) {
        val text = buildString {
            appendLine("📜 *Kutipan Harian Adzkaruna*")
            appendLine()
            appendLine("“${quote.text}”")
            appendLine()
            appendLine("📌 ${quote.source}")
            appendLine()
            append("Dibagikan melalui Adzkaruna • Doa & Dzikir Harian")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan Kutipan"))
    }
}
