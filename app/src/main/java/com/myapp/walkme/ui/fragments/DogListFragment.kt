package com.myapp.walkme.ui.fragments


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.myapp.walkme.databinding.FragmentDogListBinding
import com.myapp.walkme.model.Dog

class DogListFragment : Fragment(), OnDogSelectedListener {
    lateinit var binding: FragmentDogListBinding
    lateinit var adapter: DogAdapter
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    var dogs = mutableListOf<Dog>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDogListBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        setupRecyclerView()
        binding.addPostBtn.setOnClickListener { showNewDogFragment() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = auth.currentUser
        if(currentUser != null){
            Log.d(TAG, "onViewCreated: RADI")
            val docRef = db.collection("dogs").orderBy("timestamp", Query.Direction.DESCENDING)

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    dogs.clear()
                    snapshot.documents.forEachIndexed { index, document ->
                        val doggo = Dog( document.data?.getValue("imageSrc").toString(),
                            document.data?.getValue("name").toString(),
                            document.data?.getValue("favoriteTreat").toString(),
                            document.data?.getValue("walkDate").toString(),
                            document.data?.getValue("owner").toString(),
                            document.data?.getValue("contact").toString()
                        )
                        dogs.add(doggo)
                        adapter.addDogs(dogs)
                    }
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
        }
    }
    private fun setupRecyclerView() {
        binding.dogListRv.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )
        adapter = DogAdapter()
        adapter.onDogSelectedListener = this
        binding.dogListRv.adapter = adapter
    }
    override fun onDogSelected(position: Long) {
        val action =
            DogListFragmentDirections.actionDogListFragmentToDogDetailsFragment(position)
        findNavController().navigate(action)
    }

    private fun showNewDogFragment() {
        val action = DogListFragmentDirections.actionDogListFragmentToNewDogFragment()
        findNavController().navigate(action)
    }

}