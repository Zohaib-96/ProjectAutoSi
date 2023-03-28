package com.example.autosilentapp
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class MyAlarmManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarms(time: TimeEntities?) { // Retrieve the TimeData from the Room database
        if (time != null) {
            scheduleSilentAlarm(time.startTime) // Schedule the silent alarm
            scheduleRingModeAlarm(time.endTime) // Schedule the ring mode alarm
            Log.d("checkTime" , "start time  ----${time.startTime}")
            Log.d("checkTime" , "end time -----${time.endTime}")

        }
    }

    private fun scheduleSilentAlarm(startTime: String) {
        val startTimeMillis = getTimeInMillis(startTime)
        val requestCode = getTimeInMillis(startTime).toInt()
        val silentIntent = Intent(context, SilentModeReceiver::class.java).apply {
            action = "com.example.autosilentapp.ACTION_SILENT_MODE"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            silentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        Log.d("checkTime" , "start time Milli -------- $startTimeMillis")

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTimeMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun scheduleRingModeAlarm(endTime: String) {
        val endTimeMillis = getTimeInMillis(endTime)
        val requestCode = getTimeInMillis(endTime).toInt()
        val ringModeIntent = Intent(context, SilentModeReceiver::class.java).apply {
            action = "com.example.autosilentapp.ACTION_RING_MODE"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            ringModeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        Log.d("checkTime" , "end time Milli ----- $endTimeMillis")

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            endTimeMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun getTimeInMillis(timeString: String): Long {
        val sdf = SimpleDateFormat("hh:mm", Locale.getDefault())
        val time = sdf.parse(timeString)
        val calendar = Calendar.getInstance()
        calendar.time = time
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }
}
