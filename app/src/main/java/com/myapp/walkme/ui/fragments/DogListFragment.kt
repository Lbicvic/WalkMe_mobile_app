package com.myapp.walkme.ui.fragments


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.myapp.walkme.databinding.FragmentDogListBinding
import com.myapp.walkme.model.Dog

class DogListFragment : Fragment(), OnDogSelectedListener {
    lateinit var binding: FragmentDogListBinding
    lateinit var adapter: DogAdapter
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var firebaseStorage: FirebaseStorage
    var dogs = mutableListOf<Dog>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDogListBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        setupRecyclerView()
        binding.addPostBtn.setOnClickListener { showNewDogFragment() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "onViewCreated: RADI")
            val docRef = db.collection("dogs").orderBy("timestamp", Query.Direction.DESCENDING)

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    dogs.clear()
                    snapshot.documents.forEach { document ->
                        val doggo = Dog(
                            document.data?.getValue("imageSrc").toString(),
                            document.data?.getValue("name").toString(),
                            document.data?.getValue("favoriteTreat").toString(),
                            document.data?.getValue("walkDate").toString(),
                            document.data?.getValue("owner").toString(),
                            document.data?.getValue("contact").toString(),
                            document.id
                        )
                        dogs.add(doggo)
                    }
                    adapter.addDogs(dogs)
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

    override fun onDogLongPress(dog: Dog?): Boolean {
        val currentUser = auth.currentUser
        if(currentUser != null){
                val docRef = db.collection("dogs").orderBy("timestamp", Query.Direction.DESCENDING)

                docRef.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        dog?.let {
                            snapshot.documents.forEach { document ->
                                if(it.id == document.id && currentUser.uid == document.data?.getValue("userID").toString()){
                                    val dogimageRef = firebaseStorage.getReference("images/" + document.id)
                                    dogimageRef.delete().addOnSuccessListener {
                                        Toast.makeText(context, "Dog Image Successfully Deleted", Toast.LENGTH_SHORT).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(context, "Error deleting Dog Image", Toast.LENGTH_SHORT).show()
                                        Log.d(TAG, "SLIKA NECE ${document.id}")
                                    }
                                    db.collection("dogs").document(document.id)
                                        .delete()
                                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                            Toast.makeText(context, "Dog Successfully Deleted", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e)
                                            Toast.makeText(context, "Error deleting dog", Toast.LENGTH_SHORT).show()
                                        }
                                    dogs.remove(it)
                                    adapter.addDogs(dogs)
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                }
        }
        return true
    }

    private fun showNewDogFragment() {
        val action = DogListFragmentDirections.actionDogListFragmentToNewDogFragment()
        findNavController().navigate(action)
    }

}