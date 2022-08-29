package com.myapp.walkme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.myapp.walkme.R
import com.myapp.walkme.databinding.FragmentMapBinding

class MapFragment: Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentMapBinding
    private val args: MapFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val currentPosition = LatLng(args.latitude.toDouble(), args.longitude.toDouble())
        googleMap.addMarker(
            MarkerOptions()
                .position(currentPosition)
                .title("Dog Location")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,12f))
    }
}
