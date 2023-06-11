package com.example.autosilentapp

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.autosilentapp.databinding.FragmentPrayerTimeBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class PrayerTimeFragment : Fragment() {

    lateinit var binding: FragmentPrayerTimeBinding
    var simpleDateFormat = SimpleDateFormat("hh:mm")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prayer_time, container, false)

        // Call the loadData() function to fetch and display default prayer times for Peshawar
        loadData()
        binding.btnSearch.setOnClickListener {
            loadData()
            val locationName = binding.edtSearch.text.toString()
            binding.locationName.text = locationName
        }
        return binding.root
    }
    private fun loadData() {
        // Default location coordinates for Peshawar
        val defaultLat = 34.0150
        val defaultLong = 71.5250

        // Check if the user has entered a search query
        val searchQuery = binding.edtSearch.text.toString()
        if (searchQuery.isNotEmpty()) {
            // User has entered a search query, make API call for the searched location
            val geocoder = Geocoder(requireContext())
            val addressList: List<Address>?
            try {
                addressList = geocoder.getFromLocationName(searchQuery, 5)
                if (addressList != null && addressList.isNotEmpty()) {
                    val searchedLat = addressList[0].latitude
                    val searchedLong = addressList[0].longitude
                    fetchPrayerTimes(searchedLat, searchedLong)
                } else {
                    // Handle case when no matching location is found
                    displayDefaultPrayerTimes()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle IO error
            }
        } else {
            // No search query, fetch prayer times for the default location (Peshawar)
            fetchPrayerTimes(defaultLat, defaultLong)
        }
    }
    private fun fetchPrayerTimes(latitude: Double, longitude: Double) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "https://api.aladhan.com/v1/calendar?latitude=$latitude&longitude=$longitude"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val jsonDate: JSONArray = response.getJSONArray("data")
                    val timing = jsonDate.getJSONObject(0)
                    val tim = timing.getJSONObject("timings")

                    // Update prayer times
                    binding.tvFajar.text = simpleDateFormat.parse(tim.getString("Fajr"))
                        ?.let { simpleDateFormat.format(it) }
                    binding.tvZuhr.text = simpleDateFormat.parse(tim.getString("Dhuhr"))
                        ?.let { simpleDateFormat.format(it) }
                    binding.tvAsr.text = simpleDateFormat.parse(tim.getString("Asr"))
                        ?.let { simpleDateFormat.format(it) }
                    binding.tvMaghrib.text = simpleDateFormat.parse(tim.getString("Maghrib"))
                        ?.let { simpleDateFormat.format(it) }
                    binding.tvIsha.text = simpleDateFormat.parse(tim.getString("Isha"))
                        ?.let { simpleDateFormat.format(it) }

                    // Handle the response here
                    // You can access the response using the 'response' parameter
                    // Example: val data = response.getJSONObject("data")
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // Handle JSON parsing error
                    displayDefaultPrayerTimes()
                }
            },
            { error ->
                Log.d("the error is ", error.message!!)
                // Handle error case here
                // You can access the error using the 'error' parameter
                displayDefaultPrayerTimes()
            }
        )

        queue.add(jsonObjectRequest)
    }
    private fun displayDefaultPrayerTimes() {
        val defaultLat = 34.0150
        val defaultLong = 71.5250

        fetchPrayerTimes(defaultLat, defaultLong)
    }
}