package com.myapp.walkme.ui.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.myapp.walkme.databinding.FragmentNewDogBinding
import java.time.ZonedDateTime
import java.util.*


class NewDogFragment : Fragment() {
    lateinit var binding: FragmentNewDogBinding
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var storageRef: FirebaseStorage
    lateinit var imageUri: Uri
    lateinit var downloadUri: Uri
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var date: String = "Today"
    val documentID = UUID.randomUUID().toString()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()
        binding = FragmentNewDogBinding.inflate(layoutInflater)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        var calendar = Calendar.getInstance()
        binding.dpWalkDateInputNewDog.init(
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            val month = month + 1
            date = "$day/$month/$year"
        }
        binding.bNewDogPicture.setOnClickListener { choosePicture() }
        binding.bUploadNewDogPicture.setOnClickListener { uploadPicture() }
        binding.bNewDog.setOnClickListener { showDogListFragment() }
        return binding.root
    }

    fun uploadPicture() {
        val progressDialog = ProgressDialog(this.context)
        progressDialog.setMessage("Uploading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val imagesRef = storageRef.getReference("images/$documentID")
        if(!this::imageUri.isInitialized){
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            Toast.makeText(
                context,
                "Must add a picture first",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        var uploadTask = imagesRef.putFile(imageUri)

        uploadTask.addOnFailureListener {
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            Toast.makeText(context, "Upload Failed! Try again", Toast.LENGTH_SHORT).show()

        }.addOnSuccessListener { taskSnapshot ->
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            Toast.makeText(context, "Upload Successful!", Toast.LENGTH_SHORT).show()
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
            }
        }
    }

    fun choosePicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            binding.ivDogPictureNewDog.setImageURI(imageUri)
        }
    }


    @SuppressLint("MissingPermission")
    private fun showDogListFragment() {
        val currentUser = auth.currentUser
        var dog: HashMap<String, Comparable<*>?> = hashMapOf()
        if (currentUser != null) {
            if (this::downloadUri.isInitialized.not() || binding.etDogNameInputNewDog.text.toString()
                    .isNullOrEmpty() || binding.etFavTreatInputNewDog.text.toString()
                    .isNullOrEmpty() || binding.etContactInputNewDog.text.toString().isNullOrEmpty()
            ) {
                Toast.makeText(
                    context,
                    "Must fill empty input fields and add/upload picture",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            if (PhoneNumberUtils.formatNumberToE164(binding.etContactInputNewDog.text.toString(), "HR") == null
            ) {
                Toast.makeText(context, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
                return
            }
            val phoneNumber: String = PhoneNumberUtils.formatNumberToE164(binding.etContactInputNewDog.text.toString(), "HR")

            if (PhoneNumberUtils.isGlobalPhoneNumber(binding.etContactInputNewDog.text.toString()).not()
            ) {
                Toast.makeText(context, "Invalid Global Phone Number", Toast.LENGTH_SHORT).show()
                return
            }
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
            }
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                Log.w(TAG, "${it.latitude} and ${it.longitude}")

                dog = hashMapOf(
                    "name" to binding.etDogNameInputNewDog.text.toString(),
                    "favoriteTreat" to binding.etFavTreatInputNewDog.text.toString(),
                    "walkDate" to date,
                    "owner" to currentUser.displayName,
                    "contact" to phoneNumber,
                    "imageSrc" to downloadUri,
                    "latitude" to it.latitude,
                    "longitude" to it.longitude,
                    "timestamp" to ZonedDateTime.now().toInstant().toEpochMilli(),
                    "userID" to currentUser.uid
                )

                db.collection("dogs").document(documentID).set(dog)
                    .addOnSuccessListener { documentReference ->
                        Log.w(TAG, "Document added to dogs")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
        }
        val action = NewDogFragmentDirections.actionNewDogFragmentToDogListFragment()
        findNavController().navigate(action)
    }
}

