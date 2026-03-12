package com.example.eventmanagementapplication

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ConfirmationScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation_screen)

        Toast.makeText(
            this,
            "Your event registration has been submitted successfully",
            Toast.LENGTH_LONG
        ).show()

        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")
        val email = intent.getStringExtra("email")
        val event = intent.getStringExtra("event")
        val date = intent.getStringExtra("date")
        val gender = intent.getStringExtra("gender")
        val imageUri = intent.getStringExtra("image")

        findViewById<TextView>(R.id.name).text = "Full Name: $name"
        findViewById<TextView>(R.id.phone).text = "Phone: $phone"
        findViewById<TextView>(R.id.email).text = "Email: $email"
        findViewById<TextView>(R.id.event).text = "Event Type: $event"
        findViewById<TextView>(R.id.dateText).text = "Event Date: $date"
        findViewById<TextView>(R.id.genderText).text = "Gender: $gender"

        val img = findViewById<ImageView>(R.id.confirmImage)
        img.setImageURI(Uri.parse(imageUri))
    }
}