package com.myapp.walkme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.myapp.walkme.databinding.FragmentDogListBinding

class DogListFragment : Fragment(), OnDogSelectedListener {
    lateinit var binding: FragmentDogListBinding
    lateinit var adapter: DogAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDogListBinding.inflate(layoutInflater)
        setupRecyclerView()
        binding.addPostBtn.setOnClickListener { showNewDogFragment() }
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.dogListRv.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )
        adapter = DogAdapter()
        adapter.onDogSelectedListener = this
        binding.dogListRv.adapter = adapter
    }
    override fun onDogSelected(position: Int) {
        val action =
            DogListFragmentDirections.actionDogListFragmentToDogDetailsFragment()
        findNavController().navigate(action)
    }

    private fun showNewDogFragment() {
        val action = DogListFragmentDirections.actionDogListFragmentToNewDogFragment()
        findNavController().navigate(action)
    }

}