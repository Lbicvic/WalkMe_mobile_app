package com.myapp.walkme.model

import android.net.Uri

data class Dog(
    val imageSrc: String,
    val name: String,
    val favTreat: String,
    val walkDate: String,
    val owner: String,
    val contact: String
)