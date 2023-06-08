package com.example.autosilentapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.autosilentapp.databinding.FragmentPrayerTimeBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PrayerTimeFragment : Fragment() {

    lateinit var binding: FragmentPrayerTimeBinding
    private lateinit var timeDB: TimeDB
    private lateinit var timeDao: PrayerTimesDao
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prayer_time, container, false)

        bindView()

        return binding.root
    }

    private fun bindView() {
        GlobalScope.launch {
            timeDB = TimeDB.getDatabase(requireContext())
            timeDao = timeDB.prayerTimesDao()
            val prayerTimes = timeDao.getAllPrayerTimes()

            val timeFormat24 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeFormat12 = SimpleDateFormat("hh:mm a", Locale.getDefault())

            binding.tvFajarTimeStart.text = convertTimeTo12HourFormat(prayerTimes[0].startTime, timeFormat24, timeFormat12)
            binding.tvFajarTimeEnd.text = convertTimeTo12HourFormat(prayerTimes[0].endTime, timeFormat24, timeFormat12)
            binding.tvZuhrTimeStart.text = convertTimeTo12HourFormat(prayerTimes[1].startTime, timeFormat24, timeFormat12)
            binding.tvZuhrTimeEnd.text = convertTimeTo12HourFormat(prayerTimes[1].endTime, timeFormat24, timeFormat12)
            binding.tvAsrTimeStart.text = convertTimeTo12HourFormat(prayerTimes[2].startTime, timeFormat24, timeFormat12)
            binding.tvAsrTimeEnd.text = convertTimeTo12HourFormat(prayerTimes[2].endTime, timeFormat24, timeFormat12)
            binding.tvMaghribTimeStart.text = convertTimeTo12HourFormat(prayerTimes[3].startTime, timeFormat24, timeFormat12)
            binding.tvMaghribTimeEnd.text = convertTimeTo12HourFormat(prayerTimes[3].endTime, timeFormat24, timeFormat12)
            binding.tvIshaTimeStart.text = convertTimeTo12HourFormat(prayerTimes[4].startTime, timeFormat24, timeFormat12)
            binding.tvIshaTimeEnd.text = convertTimeTo12HourFormat(prayerTimes[4].endTime, timeFormat24, timeFormat12)

        }
    }
    private fun convertTimeTo12HourFormat(time: String, inputFormat: SimpleDateFormat, outputFormat: SimpleDateFormat): String {
        val date = inputFormat.parse(time)
        return outputFormat.format(date)
    }
}