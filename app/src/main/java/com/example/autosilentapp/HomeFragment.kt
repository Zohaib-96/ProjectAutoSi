package com.example.autosilentapp

import SilentModeReceiver
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.autosilentapp.databinding.FragmentHomeBinding
import com.google.gson.Gson
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), TimeAdapter.OnTimeClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: TimeDB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        database = TimeDB.getDatabase(requireContext())

        binding.btnFloating.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_homeFragment_to_addTimerFragment)
        }
        bindRecycleViewAndAdapter()
        swipeToDelete()

        lifecycleScope.launch {

        }
        return binding.root
    }

    private fun bindRecycleViewAndAdapter() {
        val adapter = TimeAdapter(this)
        binding.myRecycleView.adapter = adapter
        binding.myRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.myRecycleView.setHasFixedSize(true)

        lifecycleScope.launch {
            val getStoredTime: LiveData<List<TimeEntities>> = database.TimeDao().getAllTimes()
            getStoredTime.observe(viewLifecycleOwner, Observer { time ->
                adapter.setData(getStoredTime)
            })
        }
    }

    override fun onTimeClick(time: TimeEntities) {
        val bundle = Bundle()
        bundle.putString("time", Gson().toJson(time))
        findNavController().navigate(R.id.action_homeFragment_to_addTimerFragment, bundle)
    }

    private fun swipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val adapter = binding.myRecycleView.adapter as TimeAdapter
                val timeToDelete = adapter.getTime(position)
                lifecycleScope.launch {
                    database.TimeDao().deleteTime(timeToDelete)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                ).addSwipeLeftBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.purple_200
                    )
                ).addSwipeLeftActionIcon(R.drawable.delete_icon).addSwipeRightBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.purple_200)
                ).addSwipeRightActionIcon(R.drawable.delete_icon).create().decorate()
                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        }).attachToRecyclerView(binding.myRecycleView)
    }

    override fun onResume() {
        super.onResume()
        val nm = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !nm.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
    }
}

