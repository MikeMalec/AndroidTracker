package com.example.tracker.ui.fragments.routes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tracker.data.db.Route
import com.example.tracker.databinding.RouteItemBinding
import com.example.tracker.utils.DateManager
import javax.inject.Inject

class RoutesAdapter @Inject constructor() : RecyclerView.Adapter<RoutesAdapter.RouteViewHolder>() {

    lateinit var clickCallback: (route: Route) -> Unit

    inner class RouteViewHolder(val binding: RouteItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    val differCallback = object : DiffUtil.ItemCallback<Route>() {
        override fun areItemsTheSame(oldItem: Route, newItem: Route): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Route, newItem: Route): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    fun submitList(routes: List<Route>) = differ.submitList(routes)

    fun getRoutes() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        return RouteViewHolder(
            RouteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = getRoutes()[position]
        holder.binding.apply {
            clRouteItem.setOnClickListener { clickCallback(route) }
            Glide.with(ivRouteItemImage).load(route.img).into(ivRouteItemImage)
            tvRouteItemDate.text = route.createdAtString
        }
    }

    override fun getItemCount(): Int {
        return getRoutes().size
    }
}