package com.myapp.walkme.ui.fragments

import com.myapp.walkme.model.Dog

interface OnDogSelectedListener {
    fun onDogSelected(position: Long)
    fun onDogLongPress(dog: Dog?): Boolean
}