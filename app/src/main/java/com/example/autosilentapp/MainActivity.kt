package com.example.autosilentapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.autosilentapp.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var timeDB: TimeDB
    private lateinit var timeDao: PrayerTimesDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Set the start destination
        navController.navigate(R.id.homeFragment)

        // Set the listener for the bottom navigation view
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.alarm_fragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.stopWatchFragment2 -> {
                    navController.navigate(R.id.stopWatchFragment2)
                    true
                }
                R.id.timerFragment2 -> {
                    navController.navigate(R.id.timerFragment2)
                    true
                }
                R.id.PrayerTineFragment ->{
                    navController.navigate(R.id.prayerTimeFragment)
                    true
                }
                else -> false
            }
        }
        timeDB = TimeDB.getDatabase(applicationContext)
        timeDao = timeDB.prayerTimesDao()
        GlobalScope.launch {
            timeDao.insertPrayerTimes(PrayerTimesEntity(PrayerName = "fajar", startTime = "4:20", endTime = "4:40"))
            timeDao.insertPrayerTimes(PrayerTimesEntity(PrayerName = "zuhr", startTime = "13:20", endTime = "13:40"))
            timeDao.insertPrayerTimes(PrayerTimesEntity(PrayerName = "asr", startTime = "17:50", endTime = "18:15"))
            timeDao.insertPrayerTimes(PrayerTimesEntity(PrayerName = "maghrib", startTime = "19:20", endTime = "19:40"))
            timeDao.insertPrayerTimes(PrayerTimesEntity(PrayerName = "isha", startTime = "21:05", endTime = "21:30"))
        }
    }
}
