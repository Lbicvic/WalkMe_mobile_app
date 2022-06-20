package com.myapp.walkme.ui.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myapp.walkme.databinding.FragmentNewDogBinding


class NewDogFragment: Fragment() {
    lateinit var binding: FragmentNewDogBinding
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        binding = FragmentNewDogBinding.inflate(layoutInflater)
        binding.bNewDog.setOnClickListener{showDogListFragment()}
        return binding.root
    }
    private fun showDogListFragment() {
        val currentUser = auth.currentUser
        if(currentUser != null){
            val dog = hashMapOf(
                "name" to binding.etDogNameInputNewDog.text.toString(),
                "favoriteTreat" to binding.etFavTreatInputNewDog.text.toString(),
                "walkDate" to binding.etWalkDateInputNewDog.text.toString(),
                "bonus" to binding.etBonusInputNewDog.text.toString(),
                "contact" to binding.etContactInputNewDog.text.toString()
            )

            db.collection("dogs")
                .add(dog)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
        val action = NewDogFragmentDirections.actionNewDogFragmentToDogListFragment()
        findNavController().navigate(action)
    }
}