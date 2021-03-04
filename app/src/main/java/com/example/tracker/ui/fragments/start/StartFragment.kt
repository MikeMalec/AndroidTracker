package com.example.tracker.ui.fragments.start

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tracker.R
import com.example.tracker.databinding.StartFragmentBinding
import com.example.tracker.utils.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class StartFragment : Fragment(R.layout.start_fragment), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: StartFragmentBinding

    @Inject
    lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.showSavedRoutesMenuItem -> navigateToSavedRoutes()
            R.id.showStats -> navigateToStatsFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToStatsFragment() {
        StartFragmentDirections.actionStartFragmentToStatisticsFragment().run {
            findNavController().navigate(this)
        }
    }

    private fun navigateToSavedRoutes() {
        StartFragmentDirections.actionStartFragmentToRoutesFragment().run {
            findNavController().navigate(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = StartFragmentBinding.bind(view)
        checkPermissions()
        setTrackClick()
    }

    private fun setTrackClick() {
        binding.btnStartTracking.setOnClickListener {
            if (permissionManager.checkLocationPermissions()) {
                navigateToMapFragment()
            } else {
                permissionManager.requestLocationPermission(this)
            }
        }
    }

    private fun navigateToMapFragment() {
        StartFragmentDirections.actionStartFragmentToMapFragment().run {
            findNavController().navigate(this)
        }
    }

    private fun checkPermissions() {
        if (!permissionManager.checkLocationPermissions()) {
            permissionManager.requestLocationPermission(this)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            checkPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}