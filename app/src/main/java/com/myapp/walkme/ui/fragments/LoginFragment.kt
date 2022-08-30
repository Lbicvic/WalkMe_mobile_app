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
import com.myapp.walkme.databinding.FragmentLoginBinding


class LoginFragment: Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        auth=FirebaseAuth.getInstance()
        binding.bLogin.setOnClickListener{showDogListFragment()}
        return binding.root
    }
    private fun showDogListFragment() {
        if(binding.etEmailInputLogin.text.toString().isEmpty() || binding.etPasswordInputLogin.text.toString().isEmpty() ){
            return
        }
        auth.signInWithEmailAndPassword(binding.etEmailInputLogin.text.toString(), binding.etPasswordInputLogin.text.toString())
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    val action = LoginFragmentDirections.actionLoginFragmentToDogListFragment()
                    findNavController().navigate(action)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)

                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

    }
}