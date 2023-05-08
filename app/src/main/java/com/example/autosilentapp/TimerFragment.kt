package com.example.autosilentapp

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.autosilentapp.databinding.FragmentTimerBinding
import java.util.concurrent.TimeUnit

class TimerFragment : Fragment() {

    private lateinit var binding: FragmentTimerBinding
    private var durationInMillis = 0L
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private lateinit var mediaPlayer: MediaPlayer
    private var shouldPlayRingtone = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alarmringtone)

        binding.btnStop.visibility = View.INVISIBLE
        binding.btnStart.visibility = View.INVISIBLE
        binding.btnStopMusic.visibility = View.INVISIBLE
        binding.btnReset.visibility = View.INVISIBLE


        binding.btnAdd.setOnClickListener {
            binding.btnStart.visibility = View.VISIBLE
            binding.btnStopMusic.visibility = View.INVISIBLE
            binding.btnReset.visibility = View.VISIBLE
            showAddTimerDialog()
        }

        binding.btnStart.setOnClickListener {
            startCountDownTimer(durationInMillis)
            binding.btnStop.visibility = View.VISIBLE
            binding.btnStart.visibility = View.INVISIBLE
            binding.btnStopMusic.visibility = View.INVISIBLE
        }

        binding.btnStopMusic.setOnClickListener {
            binding.btnStopMusic.visibility = View.INVISIBLE
            stopRingtone()
        }
        binding.btnStop.setOnClickListener {
            pauseCountDownTimer()
        }
        binding.btnReset.setOnClickListener {
            resetCountDownTimer()
        }

        return binding.root
    }

    private fun showAddTimerDialog() {
        // create the dialog
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_timer)
        dialog.setTitle("Add Timer")

        // get references to the views in the dialog
        val hourPicker = dialog.findViewById<NumberPicker>(R.id.hours_picker)
        val minPicker = dialog.findViewById<NumberPicker>(R.id.minutes_picker)
        val secPicker = dialog.findViewById<NumberPicker>(R.id.seconds_picker)
        val addButton = dialog.findViewById<Button>(R.id.add_button)
        val edtName = dialog.findViewById<EditText>(R.id.edt_timer_name)

        // set up the number pickers
        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        minPicker.minValue = 0
        minPicker.maxValue = 59
        secPicker.minValue = 1
        secPicker.maxValue = 59
        // set up the add button click listener
        addButton.setOnClickListener {


            // get the values from the number pickers
            val hours = hourPicker.value
            val minutes = minPicker.value
            val seconds = secPicker.value

            // calculate the duration in milliseconds
            durationInMillis = ((hours * 60 * 60) + (minutes * 60) + seconds) * 1000L
            binding.tvTimer.text = getFormattedTime(durationInMillis)
            binding.tvNameOfTimer.text = edtName.text
            dialog.dismiss()
        }
        // show the dialog
        dialog.show()
    }

    private fun startCountDownTimer(durationInMillis: Long) {
        // cancel any previous timer

        countDownTimer?.cancel()

        // create a new count down timer
        countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                // update the view with the remaining time
                binding.tvTimer.text = getFormattedTime(millisUntilFinished)
            }

            override fun onFinish() {
                // update the view when the timer is finished
                binding.tvTimer.text = "00:00:00"
                binding.btnStart.visibility = View.INVISIBLE
                binding.btnStop.visibility = View.INVISIBLE
                binding.btnReset.visibility = View.INVISIBLE
                binding.btnStopMusic.visibility = View.VISIBLE

                shouldPlayRingtone = true
                playRingtone()
            }
        }

        // start the count down timer
        countDownTimer?.start()
    }

    private fun getFormattedTime(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun pauseCountDownTimer() {
        countDownTimer?.cancel()
        binding.btnStart.visibility = View.VISIBLE
        binding.btnStop.visibility = View.INVISIBLE
    }

    private fun resetCountDownTimer() {
        countDownTimer?.cancel()
        binding.tvTimer.text = "00:00:00"
        binding.tvNameOfTimer.text = ""
        binding.btnStart.visibility = View.VISIBLE
        binding.btnStop.visibility = View.INVISIBLE
        binding.btnReset.visibility = View.INVISIBLE
        timeLeftInMillis = 0
    }

    private fun playRingtone() {
        // Only play the ringtone if shouldPlayRingtone is true
        if (shouldPlayRingtone) {
            mediaPlayer.start()
        }
    }

    private fun stopRingtone() {
        mediaPlayer.stop()
        mediaPlayer.prepare()
        shouldPlayRingtone = false
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
}