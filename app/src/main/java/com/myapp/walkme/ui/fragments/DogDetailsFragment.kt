package com.myapp.walkme.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.myapp.walkme.databinding.FragmentDogDetailsBinding

class DogDetailsFragment: Fragment() {
    lateinit var binding: FragmentDogDetailsBinding
   // private val args: DogDetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDogDetailsBinding.inflate(layoutInflater)
        binding.locationBtn.setOnClickListener{showMapFragment()}
        return binding.root
    }
    private fun showMapFragment() {
        val action = DogDetailsFragmentDirections.actionDogDetailsFragmentToMapFragment()
        findNavController().navigate(action)
    }
}