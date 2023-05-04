package com.example.autosilentapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.autosilentapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.homeFragment)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.alarm_fragment -> replaceFragment(HomeFragment())
                R.id.stopWatch_fragment -> replaceFragment(StopWatchFragment())
                R.id.timer_fragment -> replaceFragment(TimerFragment())
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
        .replace(R.id.nav_host_fragment, fragment)
        .commit()
    }
}