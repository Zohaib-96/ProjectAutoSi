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

        binding.seatchBtn.setOnClickListener {
            loadData()
        }
        return binding.root
    }
    private fun loadData() {
        val geocoder = Geocoder(requireContext())
        val addressList: List<Address>?
        try {
            addressList = geocoder.getFromLocationName(binding.edtSearch.text.toString(), 5)
            if (addressList != null) {
                val doubleLat = addressList[0].latitude
                val doubleLong = addressList[0].longitude
                val queue = Volley.newRequestQueue(requireContext())
                val url =
                    "https://api.aladhan.com/v1/calendar?latitude=$doubleLat&longitude=$doubleLong"

                val jsonObjectRequest =
                    JsonObjectRequest(
                        Request.Method.GET, url, null,
                        { response ->
                            // Handle the response here
                            val jsonDate: JSONArray = response.getJSONArray("data")
                            val timing = jsonDate.getJSONObject(0)
                            val tim = timing.getJSONObject("timings")
                            binding.tvFajar.text = simpleDateFormat.parse(tim.getString("Fajr"))
                                ?.let { simpleDateFormat.format(it) }
                            binding.tvZuhr.text = simpleDateFormat.parse(tim.getString("Dhuhr"))
                                ?.let { simpleDateFormat.format(it) }
                            binding.tvAsr.text = simpleDateFormat.parse(tim.getString("Asr"))
                                ?.let { simpleDateFormat.format(it) }
                            binding.tvMaghrib.text =
                                simpleDateFormat.parse(tim.getString("Maghrib"))
                                    ?.let { simpleDateFormat.format(it) }
                            binding.tvIsha.text = simpleDateFormat.parse(tim.getString("Isha"))
                                ?.let { simpleDateFormat.format(it) }
                        },
                        { error ->
                            Log.d("error message is " , error.message!!)
                            // Handle error case here
                        }
                    )
                queue.add(jsonObjectRequest)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}