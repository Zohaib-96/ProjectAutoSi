package com.example.autosilentapp

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.util.Calendar
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment(), TimeAdapter.OnTimeClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: TimeDB
    private lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        // Initialize the handle variable
        handler = Handler(Looper.getMainLooper())

        database = TimeDB.getDatabase(requireContext())

        binding.btnFloating.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_homeFragment_to_addTimerFragment)
        }
        bindRecycleViewAndAdapter()
        swipeToDelete()

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
                showDeleteConfirmationDialog(position, adapter)/* val timeToDelete = adapter.getTime(position)

                 // Remove the item from the database and adapter
                 lifecycleScope.launch {
                     database.TimeDao().deleteTime(timeToDelete)
                 }*/
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
                        requireContext(), R.color.red
                    )
                ).addSwipeLeftActionIcon(R.drawable.delete_icon).addSwipeRightBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.red)
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
        if (!nm.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
        handler.post(updateTime)
    }

    private fun showDeleteConfirmationDialog(position: Int, adapter: TimeAdapter) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to delete this item?").setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                val timeToDelete = adapter.getTime(position)
                val pendingIntent = createPendingIntent(timeToDelete)
                val alarmManager =
                    requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                MyAlarmManager(requireContext()).cancelAlarms(pendingIntent, alarmManager)
                // Remove the item from the database and adapter
                lifecycleScope.launch {
                    database.TimeDao().deleteTime(timeToDelete)
                }
            }.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                adapter.notifyItemChanged(position)
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun createPendingIntent(time: TimeEntities): PendingIntent {
        val intent = Intent(requireContext(), SilentModeReceiver::class.java)
        intent.action = SilentModeReceiver.ACTION_SET_TIMER
        intent.putExtra(SilentModeReceiver.EXTRA_TIMER_ID, time.id)
        return PendingIntent.getBroadcast(
            requireContext(),
            time.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val updateTime: Runnable = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val formattedTime = dateFormat.format(Date(currentTime))
            binding.currentTime.text = formattedTime
            handler.postDelayed(this, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateTime)
    }
}

