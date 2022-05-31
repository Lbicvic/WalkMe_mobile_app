package com.myapp.walkme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.myapp.walkme.databinding.FragmentDogListBinding

class DogListFragment: Fragment() {
    lateinit var binding: FragmentDogListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDogListBinding.inflate(layoutInflater)
        binding.addPostBtn.setOnClickListener{showNewDogFragment()}
        binding.detailsBtn.setOnClickListener{showDogDetailsFragment()} //TODO drugacije ide kroz adapter
        return binding.root
    }
    private fun showNewDogFragment() {
        val action = DogListFragmentDirections.actionDogListFragmentToNewDogFragment()
        findNavController().navigate(action)
    }
    private fun showDogDetailsFragment() {
        val action = DogListFragmentDirections.actionDogListFragmentToDogDetailsFragment()
        findNavController().navigate(action)
    }
}