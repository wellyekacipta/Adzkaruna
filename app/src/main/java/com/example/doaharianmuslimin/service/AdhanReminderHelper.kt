package com.example.doaharianmuslimin.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.doaharianmuslimin.MainActivity
import com.example.doaharianmuslimin.R
import com.example.doaharianmuslimin.data.AdhanVoiceOption
import com.example.doaharianmuslimin.data.PreferencesManager
import com.example.doaharianmuslimin.data.PrayerTimesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

object AdhanReminderHelper {

    const val CHANNEL_ID = "adhan_prayer_channel_v2"
    const val CHANNEL_NAME = "Pengingat Waktu Sholat & Adzan"
    const val ACTION_STOP_ADHAN = "com.example.doaharianmuslimin.ACTION_STOP_ADHAN"
    const val EXTRA_PRAYER_NAME = "extra_prayer_name"

    private var activeMediaPlayer: MediaPlayer? = null

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi suara adzan saat waktu sholat masuk"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Reschedules all prayer alarms based on saved city and preferences
     */
    fun scheduleAllPrayerAlarms(context: Context) {
        val prefs = PreferencesManager(context)
        if (!prefs.isAdhanEnabled()) {
            cancelAllAlarms(context)
            return
        }

        val cityId = prefs.getSavedCityId()
        val cityName = prefs.getSavedCityName()
        val reminders = prefs.getPrayerReminderToggles()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val schedule = PrayerTimesRepository.fetchKemenagSchedule(cityId, cityName)
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                schedule.items.forEachIndexed { index, item ->
                    if (reminders[item.name] == true) {
                        scheduleSingleAlarm(context, alarmManager, item.name, item.time, index + 100)
                    } else {
                        cancelSingleAlarm(context, alarmManager, item.name, index + 100)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    private fun scheduleSingleAlarm(
        context: Context,
        alarmManager: AlarmManager,
        prayerName: String,
        timeStr: String,
        requestCode: Int
    ) {
        val parts = timeStr.split(":")
        if (parts.size < 2) return

        val hour = parts[0].trim().toIntOrNull() ?: return
        val minute = parts[1].trim().toIntOrNull() ?: return

        val targetCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If today's prayer time has passed, schedule for tomorrow
        if (targetCal.timeInMillis <= System.currentTimeMillis()) {
            targetCal.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, AdhanAlarmReceiver::class.java).apply {
            putExtra(EXTRA_PRAYER_NAME, prayerName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    targetCal.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    targetCal.timeInMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                targetCal.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun cancelSingleAlarm(
        context: Context,
        alarmManager: AlarmManager,
        prayerName: String,
        requestCode: Int
    ) {
        val intent = Intent(context, AdhanAlarmReceiver::class.java).apply {
            putExtra(EXTRA_PRAYER_NAME, prayerName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun cancelAllAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val prayerNames = listOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")
        prayerNames.forEachIndexed { index, name ->
            cancelSingleAlarm(context, alarmManager, name, index + 100)
        }
    }

    /**
     * Triggered by BroadcastReceiver when prayer time arrives
     */
    fun onPrayerTimeTriggered(context: Context, prayerName: String) {
        createNotificationChannel(context)
        val prefs = PreferencesManager(context)
        val voiceName = prefs.getSelectedAdhanVoice()
        val voiceOption = try {
            AdhanVoiceOption.valueOf(voiceName)
        } catch (_: Exception) {
            AdhanVoiceOption.MAKKAH
        }

        // 1. Play Adhan audio
        playAdhanAudio(context, voiceOption)

        // 2. Show notification
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, AdhanAlarmReceiver::class.java).apply {
            action = ACTION_STOP_ADHAN
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            999,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Waktu Sholat $prayerName Telah Tiba! 🕌")
            .setContentText("Marilah mendirikan sholat untuk meraih ridho Allah SWT.")
            .setSubText("Adzan Kemenag RI • ${prefs.getSavedCityName()}")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Hentikan Adzan", stopPendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(prayerName.hashCode(), notification)

        // Reschedule for next days
        scheduleAllPrayerAlarms(context)
    }

    private fun playAdhanAudio(context: Context, option: AdhanVoiceOption) {
        stopAdhanAudio()
        try {
            activeMediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setDataSource(option.audioUrl)
                setOnPreparedListener { it.start() }
                setOnCompletionListener {
                    it.release()
                    activeMediaPlayer = null
                }
                setOnErrorListener { _, _, _ ->
                    // Fallback to system alert ringtone
                    playFallbackRingtone(context)
                    true
                }
                prepareAsync()
            }
        } catch (_: Exception) {
            playFallbackRingtone(context)
        }
    }

    private fun playFallbackRingtone(context: Context) {
        try {
            val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, alertUri)
            ringtone?.play()
        } catch (_: Exception) {}
    }

    fun stopAdhanAudio() {
        try {
            activeMediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (_: Exception) {}
        activeMediaPlayer = null
    }
}
