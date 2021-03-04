package com.example.tracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.tracker.service.TrackingService
import com.example.tracker.databinding.TrackingBottomSheetDialogBinding
import com.example.tracker.ui.fragments.map.MapFragment
import com.example.tracker.utils.START_SERVICE
import com.example.tracker.utils.STOP_SERVICE
import com.example.tracker.utils.shortToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.concurrent.TimeUnit

class TrackingBottomSheetDialog(private val mapFragment: MapFragment? = null) :
    BottomSheetDialogFragment() {

    private lateinit var binding: TrackingBottomSheetDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TrackingBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mapFragment != null) {
            observeIfTracking()
            setStopClick()
            observeMeters()
            observeTime()
            observeSpeed()
            observeSteps()
        } else {
            dismiss()
        }
    }

    private var isTracking = true
    private fun observeIfTracking() {
        lifecycleScope.launchWhenStarted {
            TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
                isTracking = it
                when (it) {
                    true -> binding.btnStopTracking.text = "Zakończ trase"
                    false -> binding.btnStopTracking.text = "Zacznij trase"
                }
            })
        }
    }

    private fun setStopClick() {
        binding.btnStopTracking.setOnClickListener {
            if (isTracking) {
                mapFragment?.saveAndStopTracking()
            } else {
                mapFragment?.startTracking()
            }
            dismiss()
        }
    }

    private fun observeMeters() {
        lifecycleScope.launchWhenStarted {
            TrackingService.meters.observe(viewLifecycleOwner, Observer {
                setTotalDistance(it)
            })
        }
    }

    private fun observeTime() {
        lifecycleScope.launchWhenStarted {
            TrackingService.time.observe(viewLifecycleOwner, Observer {
                setTotalTime(it)
            })
        }
    }

    private fun observeSpeed() {
        lifecycleScope.launchWhenStarted {
            TrackingService.avgSpeedMetersPerMinute.observe(
                viewLifecycleOwner,
                Observer { setAvgSpeed(it) })
        }
    }

    private fun observeSteps() {
        lifecycleScope.launchWhenStarted {
            TrackingService.steps.observe(
                viewLifecycleOwner,
                Observer { setStepsAmount(it) }
            )
        }
    }

    private fun setTotalDistance(meters: Int) {
        binding.tvTotalDistance.text = "Całkowity dystans : $meters m"
    }

    private fun setTotalTime(millis: Long) {
        var ms = millis
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
        ms -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms)
        if (seconds.toString().length < 2) {
            binding.tvTotalTime.text = "Całkowity czas : $minutes:0$seconds minutes"
        } else {
            binding.tvTotalTime.text = "Całkowity czas : $minutes:$seconds minutes"
        }
    }

    private fun setAvgSpeed(avgSpeed: Double) {
        val speed = "%.2f".format(avgSpeed)
        binding.tvAvgSpeed.text = "Średnia prędkość : $speed m/sec"
    }

    private fun setStepsAmount(amount: Int) {
        binding.tvAmountOfSteps.text = "Ilość kroków: $amount steps"
    }
}