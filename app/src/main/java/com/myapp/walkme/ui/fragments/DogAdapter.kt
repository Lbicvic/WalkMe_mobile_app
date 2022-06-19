package com.myapp.walkme.ui.fragments

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myapp.walkme.R
import com.myapp.walkme.databinding.ItemDogBinding
import com.myapp.walkme.model.Dog

class DogAdapter : RecyclerView.Adapter<DogViewHolder>(){
    var dogs = mutableListOf<Dog>()
    var onDogSelectedListener: OnDogSelectedListener? = null
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        db = FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dog,parent,false)
        return DogViewHolder(ItemDogBinding.bind(view))
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val currentUser = auth.currentUser
        if(currentUser != null){

            val docRef = db.collection("dogs")
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    snapshot.documents.forEachIndexed { index, document ->
                        val doggo = Dog("1",
                            document.data?.getValue("name").toString(),
                            document.data?.getValue("favoriteTreat").toString(),
                            document.data?.getValue("walkDate").toString(),
                            document.data?.getValue("bonus").toString(),
                            document.data?.getValue("contact").toString()
                        )
                        dogs.add(doggo)
                    }
                    //snapshot.documents[0].data
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
            val dog = dogs[position]
            holder.bind(dog)
        }
        //TODO Popraviti ucitavanje podataka sa firebasea
        /*onDogSelectedListener?.let { listener ->
            holder.itemView.setOnClickListener { listener.onDogSelected(position) }
        }*/
    }

    override fun getItemCount(): Int {
        return dogs.count()
    }

}

class DogViewHolder( val binding: ItemDogBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(dog : Dog){
        //binding.itemDogImage.setImageURI(Uri.parse(dog.imgSrc))
        binding.itemDogName.text= dog.name
        binding.itemDogFavTreat.text = dog.favTreat
        binding.itemDogWalkDate.text = dog.walkDate
        binding.itemDogBonus.text = dog.bonus
        binding.itemDogContact.text = dog.contact
    }
}
