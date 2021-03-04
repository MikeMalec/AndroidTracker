package com.example.tracker.ui.fragments.routes

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tracker.R
import com.example.tracker.data.db.Route
import com.example.tracker.databinding.RoutesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class RoutesFragment : Fragment(R.layout.routes_fragment) {

    private val routesViewModel: RoutesViewModel by viewModels()

    private lateinit var binding: RoutesFragmentBinding

    @Inject
    lateinit var routesAdapter: RoutesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RoutesFragmentBinding.bind(view)
        showShimmer()
        routesAdapter.clickCallback = ::navigateToRoute
        setRoutesRecyclerView()
        observeRoutes()
    }

    private fun showShimmer() {
        if (!routesViewModel.didShimmer) {
            binding.shimmer.visibility = View.VISIBLE
            routesViewModel.didShimmer = true
            binding.shimmer.startShimmer()
        }
    }

    private fun stopShimmer() {
        binding.rvRoutes.visibility = View.VISIBLE
        hideShimmer()
    }

    private fun hideShimmer() {
        binding.shimmer.stopShimmer()
        binding.shimmer.visibility = View.GONE
    }

    private fun navigateToRoute(route: Route) {
        RoutesFragmentDirections.actionRoutesFragmentToRouteFragment2(route, route.createdAtString)
            .run {
                findNavController().navigate(this)
            }
    }

    private fun setRoutesRecyclerView() {
        binding.rvRoutes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = routesAdapter
        }
    }


    private fun observeRoutes() {
        lifecycleScope.launchWhenStarted {
            delay(300)
            routesViewModel.routes.observe(viewLifecycleOwner, Observer {
                routesAdapter.submitList(it)
                stopShimmer()
            })
        }
    }
}