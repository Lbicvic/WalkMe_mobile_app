package com.myapp.walkme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.myapp.walkme.databinding.FragmentNewDogBinding

class NewDogFragment: Fragment() {
    lateinit var binding: FragmentNewDogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewDogBinding.inflate(layoutInflater)
        binding.bNewDog.setOnClickListener{showDogListFragment()}
        return binding.root
    }
    private fun showDogListFragment() {
        val action = NewDogFragmentDirections.actionNewDogFragmentToDogListFragment()
        findNavController().navigate(action)
    }
}