package com.example.doaharianmuslimin.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AdhanAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AdhanReminderHelper.ACTION_STOP_ADHAN) {
            AdhanReminderHelper.stopAdhanAudio()
            return
        }

        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            AdhanReminderHelper.scheduleAllPrayerAlarms(context)
            return
        }

        val prayerName = intent.getStringExtra(AdhanReminderHelper.EXTRA_PRAYER_NAME) ?: "Sholat"
        AdhanReminderHelper.onPrayerTimeTriggered(context, prayerName)
    }
}
