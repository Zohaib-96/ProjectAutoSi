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
    }
}
