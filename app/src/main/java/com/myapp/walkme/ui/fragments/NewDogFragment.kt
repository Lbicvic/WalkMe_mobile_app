package com.myapp.walkme.ui.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.myapp.walkme.databinding.FragmentNewDogBinding
import java.net.URI
import java.util.*



class NewDogFragment: Fragment() {
    lateinit var binding: FragmentNewDogBinding
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var storageRef: FirebaseStorage
    lateinit var imageUri : Uri
    lateinit var downloadUri: Uri
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var longitude : Double = 0.0;
    var latitude : Double = 0.0;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        storageRef= FirebaseStorage.getInstance()
        binding = FragmentNewDogBinding.inflate(layoutInflater)
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.bNewDogPicture.setOnClickListener { choosePicture() }
        binding.bUploadNewDogPicture.setOnClickListener { uploadPicture() }
        binding.bNewDog.setOnClickListener{showDogListFragment()}
        return binding.root
    }

    fun uploadPicture(){
        val progressDialog = ProgressDialog(this.context)
        progressDialog.setMessage("Uploading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val imagesRef = storageRef.getReference("images/"+ UUID.randomUUID().toString())
        var uploadTask = imagesRef.putFile(imageUri)

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            if(progressDialog.isShowing){
                progressDialog.dismiss()
            }
            Log.w(TAG, "Upload failed!!!")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            if(progressDialog.isShowing){
                progressDialog.dismiss()
            }
            Log.w(TAG, "Upload successful!!!")
        }.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
               downloadUri = task.result
            } else {
                // Handle failures
                // ...
            }
        }
    }
    fun choosePicture(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action= Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == RESULT_OK && data != null && data.data != null){
            imageUri = data.data!!
            binding.ivDogPictureNewDog.setImageURI(imageUri)
        }
    }


    @SuppressLint("MissingPermission")
    private fun showDogListFragment() {
        val currentUser = auth.currentUser
        var dog: HashMap<String, Comparable<*>?> = hashMapOf()
        if(currentUser != null){
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            }
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                Log.w(TAG, "${it.latitude} and ${it.longitude}")


                dog = hashMapOf(
                    "name" to binding.etDogNameInputNewDog.text.toString(),
                    "favoriteTreat" to binding.etFavTreatInputNewDog.text.toString(),
                    "walkDate" to binding.etWalkDateInputNewDog.text.toString(),
                    "owner" to currentUser.displayName,
                    "contact" to binding.etContactInputNewDog.text.toString(),
                    "imageSrc" to downloadUri,
                    "latitude" to it.latitude,
                    "longitude" to it.longitude
                )

            db.collection("dogs")
                .add(dog)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }}
        val action = NewDogFragmentDirections.actionNewDogFragmentToDogListFragment()
        findNavController().navigate(action)
    }
}