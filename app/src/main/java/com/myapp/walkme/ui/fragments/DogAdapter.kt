package com.myapp.walkme.ui.fragments

import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myapp.walkme.R
import com.myapp.walkme.databinding.ItemDogBinding
import com.myapp.walkme.model.Dog

class DogAdapter : RecyclerView.Adapter<DogViewHolder>(){
    var dogs = mutableListOf<Dog>()
    var onDogSelectedListener: OnDogSelectedListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dog,parent,false)
        return DogViewHolder(ItemDogBinding.bind(view))
    }
    fun addDogs(dog : Dog){
        dogs.add(dog)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        //TODO Popraviti ucitavanje podataka sa firebasea
        val dog = dogs[position]
        holder.bind(dog)
        onDogSelectedListener?.let { listener ->
            holder.itemView.setOnClickListener { listener.onDogSelected(position.toLong()) }
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: ${dogs.count()}")
        return dogs.count()
    }

}

class DogViewHolder( val binding: ItemDogBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(dog : Dog){
        Glide.with(binding.root.context).load(Uri.parse(dog.imageSrc)).into(binding.itemDogImage)
        binding.itemDogName.text="Name: ${dog.name}"
        binding.itemDogFavTreat.text = "Favorite treat: ${dog.favTreat}"
        binding.itemDogWalkDate.text = "Walk date: ${dog.walkDate}"
        binding.itemDogBonus.text = "Bonus: ${dog.bonus}"
        binding.itemDogContact.text = "Contact: ${dog.contact}"
    }
}
