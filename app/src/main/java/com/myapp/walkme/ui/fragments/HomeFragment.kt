package com.myapp.walkme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.myapp.walkme.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.loginBtn.setOnClickListener{showLoginFragment()}
        binding.registerBtn.setOnClickListener{showRegisterFragment()}
        return binding.root
    }
    private fun showLoginFragment() {
        val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
        findNavController().navigate(action)
    }
    private fun showRegisterFragment() {
        val action = HomeFragmentDirections.actionHomeFragmentToRegisterFragment()
        findNavController().navigate(action)
    }
}