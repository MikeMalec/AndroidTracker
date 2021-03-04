package com.example.tracker.ui.fragments.map

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tracker.R
import com.example.tracker.service.TrackingService
import com.example.tracker.databinding.MapFragmentBinding
import com.example.tracker.ui.MainActivity
import com.example.tracker.ui.TrackingBottomSheetDialog
import com.example.tracker.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.map_fragment.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.map_fragment) {

    private val mapViewModel: MapViewModel by viewModels()

    private lateinit var binding: MapFragmentBinding

    private var map: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MapFragmentBinding.bind(view)
        binding.mapView.onCreate(savedInstanceState)
        setMap()
        checkIfStartTracking()
        setInfoClick()
        observeSaveResponse()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            binding.mapView.visibility = View.GONE
            findNavController().popBackStack()
        }
    }

    private fun setMap() {
        binding.mapView.getMapAsync {
            lifecycleScope.launch {
                delay(250)
//                binding.mapView.visibility = View.VISIBLE
            }
            map = it
            observePosition()
            observePositions()
        }
    }

    private fun checkIfStartTracking() {
        if (!TrackingService.isTracking.value!!) {
            startTracking()
        }
    }

    private fun setInfoClick() {
        binding.fabTrackingInfo.setOnClickListener {
            TrackingBottomSheetDialog(this).show(requireFragmentManager(), TRACKING_BOTTOM_SHEET)
        }
    }

    private fun observeSaveResponse() {
        lifecycleScope.launchWhenStarted {
            mapViewModel.saveResponse.observe(viewLifecycleOwner, Observer {
                it.getContent()?.also { msg ->
                    (requireActivity() as MainActivity).shortToast(msg)
                }
            })
        }
    }

    private fun observePosition() {
        lifecycleScope.launchWhenStarted {
            TrackingService.position.observe(viewLifecycleOwner, Observer {
                moveCameraToUserPosition(it)
            })
        }
    }

    private fun observePositions() {
        lifecycleScope.launchWhenStarted {
            TrackingService.positions.observe(viewLifecycleOwner, Observer {
                drawPolyline(it)
            })
        }
    }

    private fun drawPolyline(positions: List<LatLng>) {
        map?.apply {
            clear()
            val mainColor = ContextCompat.getColor(requireContext(), R.color.main_green)
            val polylineOptions = PolylineOptions().color(mainColor).width(POLYLINE_WIDTH)
            positions.forEach { polylineOptions.add(it) }
            addPolyline(polylineOptions)
        }
    }

    var canMoveCamera = true
    private fun moveCameraToUserPosition(position: LatLng) {
        if (canMoveCamera) {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, MAP_ZOOM))
        }
    }

    private fun zoomOutToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        TrackingService.positions.value?.also {
            it.forEach { latLng ->
                bounds.include(latLng)
            }
            map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.build(),
                    mapView.width,
                    mapView.height,
                    (mapView.height * 0.05f).toInt()
                )
            )
        }
    }

    private fun saveRouteToDb() {
        map?.snapshot { bmp ->
            mapViewModel.saveRoute(
                totalTime = TrackingService.time.value!!,
                totalDistance = TrackingService.meters.value!!,
                averageSpeed = TrackingService.avgSpeedMetersPerMinute.value!!,
                totalSteps = TrackingService.steps.value!!,
                img = bmp,
                positions = TrackingService.positions.value!!,
            )
            canMoveCamera = true
            stopTracking()
        }
    }

    fun startTracking() {
        (requireActivity() as AppCompatActivity).shortToast("Zaczęto trase")
        Intent(requireContext(), TrackingService::class.java).let { intent ->
            intent.action = START_SERVICE
            requireActivity().startService(intent)
        }
    }

    private fun stopTracking() {
        Intent(requireContext(), TrackingService::class.java).let { intent ->
            intent.action = STOP_SERVICE
            requireActivity().startService(intent)
        }
    }

    fun saveAndStopTracking() {
        canMoveCamera = false
        (requireActivity() as AppCompatActivity).shortToast("Koniec trasy")
        zoomOutToSeeWholeTrack()
        saveRouteToDb()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }
}