package com.example.eventmanagementapplication

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class EventRegistrationScreen : AppCompatActivity() {

    lateinit var name: EditText
    lateinit var phone: EditText
    lateinit var email: EditText
    lateinit var spinner: Spinner
    lateinit var submit: Button
    lateinit var terms: CheckBox
    lateinit var genderGroup: RadioGroup
    lateinit var imageView: ImageView
    lateinit var uploadBtn: Button

    var selectedDate = ""
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_registration_screen)

        name = findViewById(R.id.name)
        phone = findViewById(R.id.phone)
        email = findViewById(R.id.email)
        spinner = findViewById(R.id.eventType)
        submit = findViewById(R.id.submitBtn)
        terms = findViewById(R.id.terms)
        genderGroup = findViewById(R.id.genderGroup)
        imageView = findViewById(R.id.profileImage)
        uploadBtn = findViewById(R.id.uploadBtn)

        val dateBtn = findViewById<Button>(R.id.dateBtn)
        val dateText = findViewById<TextView>(R.id.dateText)

        // Spinner data
        val events = arrayOf("Seminar", "Workshop", "Conference", "Celebration")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, events)
        spinner.adapter = adapter

        // DATE PICKER
        dateBtn.setOnClickListener {

            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->

                    selectedDate = "$day/${month + 1}/$year"
                    dateText.text = selectedDate

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.show()
        }

        // IMAGE PICKER
        uploadBtn.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        // SUBMIT BUTTON
        submit.setOnClickListener {

            if (name.text.isEmpty() || phone.text.isEmpty() || email.text.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!terms.isChecked) {
                Toast.makeText(this, "Accept Terms first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (genderGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedId = genderGroup.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(selectedId)
            val gender = radioButton.text.toString()

            AlertDialog.Builder(this)
                .setTitle("Confirm Registration")
                .setMessage("Do you want to submit registration?")
                .setPositiveButton("Yes") { _, _ ->

                    val intent = Intent(this, ConfirmationScreen::class.java)

                    intent.putExtra("name", name.text.toString())
                    intent.putExtra("phone", phone.text.toString())
                    intent.putExtra("email", email.text.toString())
                    intent.putExtra("event", spinner.selectedItem.toString())
                    intent.putExtra("date", selectedDate)
                    intent.putExtra("gender", gender)
                    intent.putExtra("image", imageUri.toString())

                    startActivity(intent)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    // RECEIVE IMAGE FROM GALLERY
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {

            imageUri = data?.data
            imageView.setImageURI(imageUri)

        }
    }
}