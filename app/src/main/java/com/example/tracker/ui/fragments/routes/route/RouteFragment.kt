package com.example.tracker.ui.fragments.routes.route

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.tracker.R
import com.example.tracker.data.db.Route
import com.example.tracker.databinding.RouteFragmentBinding
import com.example.tracker.ui.fragments.routes.RoutesViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RouteFragment : Fragment(R.layout.route_fragment) {
    private val routesViewModel: RoutesViewModel by viewModels()

    private val args: RouteFragmentArgs by navArgs()

    private val route: Route
        get() = args.route

    private lateinit var binding: RouteFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.route_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteRoute -> displayDeleteDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RouteFragmentBinding.bind(view)
        setViews()
        observeRouteDeleted()
        binding.btnShowRouteOnMap.setOnClickListener {
            navigateToRouteMapFragment()
        }
    }

    private fun setViews() {
        binding.apply {
            Glide.with(ivRouteImage).load(route.img).into(ivRouteImage)
            tvRouteDate.text = "Dzień : " + route.createdAtString
            setTotalTime(route.totalTime, tvRouteTime)
            setAvgTime()
            tvRouteDistance.text =
                "Całkowity dystans : " + route.totalDistance.toString() + " meters"
            tvRouteSteps.text = "Ilość kroków : " + route.totalSteps.toString()
        }
    }

    private fun setAvgTime() {
        val speed = "%.2f".format(route.averageSpeed)
        binding.tvRouteAvgSpeed.text = "Przeciętna prędkość : $speed m/s"
    }

    private fun setTotalTime(millis: Long, tv: TextView) {
        var ms = millis
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
        ms -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms)
        if (seconds.toString().length < 2) {
            tv.text = "Całkowity czas : $minutes:0$seconds minutes"
        } else {
            tv.text = "Całkowity czas : $minutes:$seconds minutes"
        }
    }

    private fun observeRouteDeleted() {
        lifecycleScope.launchWhenStarted {
            routesViewModel.routeDeleted.observe(viewLifecycleOwner, Observer {
                if (it) {
                    findNavController().popBackStack()
                }
            })
        }
    }

    private fun displayDeleteDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Usunąć trase?")
            .setIcon(R.drawable.delete_icon)
            .setPositiveButton("Usuń") { d, i ->
                routesViewModel.deleteRoute(route)
            }
            .setNegativeButton("Anuluj") { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

    private fun navigateToRouteMapFragment() {
        RouteFragmentDirections.actionRouteFragment2ToRouteMapFragment(route, route.createdAtString)
            .run {
                findNavController().navigate(this)
            }
    }
}