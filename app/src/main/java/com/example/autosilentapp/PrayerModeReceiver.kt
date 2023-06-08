package com.example.autosilentapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log

class PrayerModeReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("PrayerModeReceiver", "broadcast Received ")
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when(intent.action){
            "com.example.autosilentapp.ACTION_PRAYER_SILENT_MODE" ->{
                audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
            }
            "com.example.autosilentapp.ACTION_PRAYER_RING_MODE" ->{
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
        }
    }
}