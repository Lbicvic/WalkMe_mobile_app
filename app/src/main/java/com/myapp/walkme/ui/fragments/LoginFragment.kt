package com.myapp.walkme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.myapp.walkme.databinding.FragmentLoginBinding


class LoginFragment: Fragment() {
    lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        binding.bLogin.setOnClickListener{showDogListFragment()}
        return binding.root
    }
    private fun showDogListFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToDogListFragment()
        findNavController().navigate(action)
    }
}