package com.example.autosilentapp

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView

class StopWatchFragment : Fragment() {

    private lateinit var StopwatchTextView : TextView
    private lateinit var Start_Button : Button
    private lateinit var Stop_Button : Button
    private lateinit var Reset_Button : Button

    private var running: Boolean = false
    private var milliseconds: Long = 0
    private val handler = Handler()

    private var lastTimeStopped: Long = 0

    private val runnable = object : Runnable {
        override fun run() {
            val elapsedTime = SystemClock.elapsedRealtime() - milliseconds
            val minutes = (elapsedTime / 60000).toInt()
            val seconds = (elapsedTime / 1000).toInt() % 60
            val milliseconds = (elapsedTime % 1000) / 10
            StopwatchTextView.text = String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
            handler.postDelayed(this, 10)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_stop_watch, container, false)

        StopwatchTextView = rootView.findViewById(R.id.timerTextView)
        Start_Button = rootView.findViewById(R.id.start_button)
        Stop_Button = rootView.findViewById(R.id.stop_button)
        Reset_Button = rootView.findViewById(R.id.reset_button)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Start_Button.setOnClickListener {
            startStopwatch()
            Start_Button.visibility = View.INVISIBLE
            Start_Button.visibility = View.VISIBLE
        }
        Stop_Button.setOnClickListener {
            stopStopwatch()
            Stop_Button.visibility = View.INVISIBLE
            Stop_Button.visibility = View.VISIBLE
        }
        Reset_Button.setOnClickListener {
            resetStopwatch()
        }
    }

    private fun startStopwatch() {
        if (!running) {
            running = true
            if (milliseconds == 0L) { // start from 0 if the stopwatch has not been started before
                milliseconds = SystemClock.elapsedRealtime()
            } else { // resume from the last stopped time
                milliseconds += SystemClock.elapsedRealtime() - lastTimeStopped
            }
            handler.post(runnable)
            Start_Button.isEnabled = false
            Stop_Button.isEnabled = true
        }
    }

    private fun stopStopwatch() {
        if (running) {
            running = false
            lastTimeStopped = SystemClock.elapsedRealtime()
            handler.removeCallbacks(runnable)
            Start_Button.isEnabled = true
            Stop_Button.isEnabled = false
        }
    }

    private fun resetStopwatch() {
        if (!running) {
            milliseconds = 0
            StopwatchTextView.text = "00:00.00"
        }
    }
}