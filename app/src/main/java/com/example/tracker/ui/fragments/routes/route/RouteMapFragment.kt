package com.example.tracker.ui.fragments.routes.route

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.tracker.R
import com.example.tracker.data.db.Route
import com.example.tracker.databinding.RouteMapFragmentBinding
import com.example.tracker.utils.POLYLINE_WIDTH
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions

class RouteMapFragment : Fragment(R.layout.route_map_fragment) {
    private val args: RouteMapFragmentArgs by navArgs()

    private val route: Route
        get() = args.route

    private lateinit var binding: RouteMapFragmentBinding

    private var map: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RouteMapFragmentBinding.bind(view)
        binding.routeMapView.onCreate(savedInstanceState)
        setMap()
    }

    private fun setMap() {
        binding.routeMapView.getMapAsync {
            map = it
            setPosition()
        }
    }

    private fun setPosition() {
        val bounds = LatLngBounds.Builder()
        route.positions.forEach {
            bounds.include(it)
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.routeMapView.width,
                binding.routeMapView.height,
                (binding.routeMapView.width * 0.05f).toInt()
            )
        )
        drawRoute()
    }

    private fun drawRoute() {
        map?.apply {
            clear()
            val mainColor = ContextCompat.getColor(requireContext(), R.color.main_green)
            val polylineOptions = PolylineOptions().color(mainColor).width(POLYLINE_WIDTH)
            route.positions.forEach { polylineOptions.add(it) }
            addPolyline(polylineOptions)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.routeMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.routeMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.routeMapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.routeMapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.routeMapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.routeMapView.onDestroy()
    }
}