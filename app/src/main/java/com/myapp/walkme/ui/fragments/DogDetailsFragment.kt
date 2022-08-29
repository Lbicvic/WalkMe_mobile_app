package com.myapp.walkme.ui.fragments


import android.content.ContentValues
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myapp.walkme.databinding.FragmentDogDetailsBinding
import com.myapp.walkme.model.Dog

class DogDetailsFragment: Fragment() {
    lateinit var binding: FragmentDogDetailsBinding
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    private val args: DogDetailsFragmentArgs by navArgs()
    var lng : String = ""
    var lat : String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDogDetailsBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        binding.locationBtn.setOnClickListener{showMapFragment()}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = auth.currentUser
        if(currentUser != null){
            Log.d(ContentValues.TAG, "onViewCreated: RADI")
            val docRef = db.collection("dogs").orderBy("name")
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Glide.with(binding.root.context).load(Uri.parse(snapshot.documents[args.position.toInt()].data?.getValue("imageSrc").toString())).into(binding.ivDogPictureDetails)
                        binding.tvDogNameDetails.text ="Name: ${snapshot.documents[args.position.toInt()].data?.getValue("name").toString()}"
                        binding.tvDogFavTreatDetails.text = "Favorite treat: ${snapshot.documents[args.position.toInt()].data?.getValue("favoriteTreat").toString()}"
                        binding.tvDogWalkDateDetails.text = "Walk date: ${snapshot.documents[args.position.toInt()].data?.getValue("walkDate").toString()}"
                        binding.tvDogOwnerDetails.text = "Owner: ${snapshot.documents[args.position.toInt()].data?.getValue("owner").toString()}"
                        binding.tvDogContactDetails.text = "Contact: ${snapshot.documents[args.position.toInt()].data?.getValue("contact").toString()}"
                        lat = snapshot.documents[args.position.toInt()].data?.getValue("latitude").toString()
                        lng = snapshot.documents[args.position.toInt()].data?.getValue("longitude").toString()
                } else {
                    Log.d(ContentValues.TAG, "Current data: null")
                }
            }
        }
    }
    private fun showMapFragment() {
        val action = DogDetailsFragmentDirections.actionDogDetailsFragmentToMapFragment(lat,lng)
        findNavController().navigate(action)
    }
}