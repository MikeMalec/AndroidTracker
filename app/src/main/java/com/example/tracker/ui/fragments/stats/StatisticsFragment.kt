package com.example.tracker.ui.fragments.stats

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.tracker.R
import com.example.tracker.databinding.StatisticsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.statistics_fragment) {
    private lateinit var binding: StatisticsFragmentBinding

    private val statisticsViewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = StatisticsFragmentBinding.bind(view)
        observeStats()
    }

    private fun observeStats() {
        lifecycleScope.launchWhenStarted {
            statisticsViewModel.totalTime.observe(viewLifecycleOwner, Observer {
                it?.also {
                    var ms = it
                    val hours = TimeUnit.MILLISECONDS.toHours(ms)
                    ms -= TimeUnit.HOURS.toMillis(hours)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
                    ms -= TimeUnit.MINUTES.toMillis(minutes)
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms)
                    binding.tvTimeValue.text =
                        "$hours GODZIN \n $minutes MINUT \n $seconds SEKUND"
                }
            })
        }
        lifecycleScope.launchWhenStarted {
            statisticsViewModel.totalDistance.observe(viewLifecycleOwner, Observer {
                it?.also {
                    if (it > 10000) {
                        val km = it / 1000f
                        val total = round(km * 10f) / 10f
                        binding.tvDistanceValue.text = "$total KILOMETRÓW"
                    } else {
                        binding.tvDistanceValue.text = "$it METRÓW"
                    }
                }
            })
        }
        lifecycleScope.launchWhenStarted {
            statisticsViewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
                it?.also {
                    binding.tvAvgSpeedValue.text = "$it m/s"
                }
            })
        }

        lifecycleScope.launchWhenStarted {
            statisticsViewModel.totalSteps.observe(viewLifecycleOwner, Observer {
                it?.also {
                    binding.tvTotalStepsValue.text = "$it"
                }
            })
        }
    }
}