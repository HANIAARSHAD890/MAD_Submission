package com.example.contactapp

data class Contact(
    var name: String,
    var phone: String,
    var imageUri: String? = null
) :java.io.Serializable