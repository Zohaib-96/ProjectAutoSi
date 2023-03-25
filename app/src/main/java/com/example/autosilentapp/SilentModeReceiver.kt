import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log

class SilentModeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BroadcastIsReceived ","Broadcast")
        when (intent.action) {
            ACTION_SILENT_MODE -> setSilentMode(context)
            ACTION_RING_MODE -> setRingMode(context)
        }
    }
    private fun setSilentMode(context: Context?) {
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
    }
    private fun setRingMode(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
    }

    companion object {
        const val ACTION_SILENT_MODE = "com.example.silentmode.SILENT_MODE"
        const val ACTION_RING_MODE = "com.example.silentmode.RING_MODE"
    }
}


