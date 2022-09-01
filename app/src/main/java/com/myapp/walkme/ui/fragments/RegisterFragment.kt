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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.myapp.walkme.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {
    lateinit var binding: FragmentRegisterBinding
    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        binding.bRegister.setOnClickListener { showLoginFragment() }
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    private fun showLoginFragment() {
        if (binding.tilPasswordInputRegister.editText?.text.isNullOrEmpty() ||
            binding.tilConfirmPasswordInputRegister.editText?.text.isNullOrEmpty() ||
            binding.tilPasswordInputRegister.editText?.text.toString()
            != binding.tilConfirmPasswordInputRegister.editText?.text.toString()
            || binding.etEmailInputRegister.text.toString().isNullOrEmpty()
            || binding.etFirstnameInputRegister.text.toString().isNullOrEmpty()
            || binding.etLastnameInputRegister.text.toString().isNullOrEmpty()
        ) {
            Toast.makeText(
                context,
                "Must fill empty fields or invalid password",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        auth.createUserWithEmailAndPassword(
            binding.etEmailInputRegister.text.toString(),
            binding.etPasswordInputRegister.text.toString()
        ).addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName =
                            binding.etFirstnameInputRegister.text.toString() + " " + binding.etLastnameInputRegister.text.toString()

                    }
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                            }
                        }
                    updateUI(user)
                    val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                    findNavController().navigate(action)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)

                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }

    }

    private fun updateUI(user: FirebaseUser?) {

    }
}