package com.example.contactapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), ContactAdapter.OnContactActionListener {

    private lateinit var etName: EditText
    private lateinit var ivPreview: ImageView
    private var selectedImageUri: String? = null
    private lateinit var etPhone: EditText
    private lateinit var btnSave: Button
    private lateinit var recyclerViewContacts: RecyclerView
    private lateinit var etSearch: EditText
    private var currentDialogImageView: ImageView? = null
    private lateinit var ivSort: ImageView

    private lateinit var contactAdapter: ContactAdapter

    private val contactList = mutableListOf<Contact>()      // MASTER LIST
    private var filteredList = mutableListOf<Contact>()     // DISPLAY LIST
    private var isAscending = true


    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it.toString()

                // ✅ If dialog is open → update dialog preview
                if (currentDialogImageView != null) {
                    currentDialogImageView?.setImageURI(it)
                } else {
                    // ✅ Otherwise update main preview
                    ivPreview.setImageURI(it)
                }
            }
        }

    private val requestContactsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                loadContactsFromPhone()
            } else {
                Toast.makeText(this, "Contacts permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // UI
        etSearch = findViewById(R.id.etSearch)
        ivPreview= findViewById(R.id.ivPreview)
        ivSort = findViewById(R.id.ivSort)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        btnSave = findViewById(R.id.btnSave)
        recyclerViewContacts = findViewById(R.id.recyclerViewContacts)
        val btnPickImage = findViewById<Button>(R.id.btnPickImage)

        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // RecyclerView setup
        filteredList = contactList.toMutableList()
        contactAdapter = ContactAdapter(filteredList, this)
        recyclerViewContacts.layoutManager = LinearLayoutManager(this)
        recyclerViewContacts.adapter = contactAdapter

        // Actions
        btnSave.setOnClickListener { saveContact() }
        setupSearch()
        setupSorting()

        // checkPermissionAndLoadContacts()
    }

    //  SAVE
    private fun saveContact() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()

        if (!validateInputs(name, phone, etName, etPhone)) return


        val newContact = Contact(name, phone, selectedImageUri)

        contactList.add(newContact)
        filteredList.add(newContact)

        contactAdapter.notifyItemInserted(filteredList.size - 1)
        recyclerViewContacts.scrollToPosition(filteredList.size - 1)

        Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT).show()

        etName.text.clear()
        etPhone.text.clear()

        ivPreview.setImageResource(R.drawable.circle_bg)
        currentDialogImageView = null
        selectedImageUri = null
        selectedImageUri = null
        etName.requestFocus()
    }

    // ================= VALIDATION =================
    private fun validateInputs(
        name: String,
        phone: String,
        nameInput: EditText,
        phoneInput: EditText
    ): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            nameInput.error = "Name is required"
            isValid = false
        }

        if (phone.isEmpty()) {
            phoneInput.error = "Phone required"
            isValid = false
        } else if (phone.length < 10 || !phone.all { it.isDigit() || it == '+' }) {
            phoneInput.error = "Invalid phone"
            isValid = false
        }

        return isValid
    }


    override fun onItemClick(position: Int) {
        val contact = filteredList[position]
        Toast.makeText(this, "${contact.name} - ${contact.phone}", Toast.LENGTH_SHORT).show()
    }

    override fun onEditClick(position: Int) {
        showEditDialog(position)
    }

    override fun onDeleteClick(position: Int) {
        showDeleteDialog(position)
    }

    //DELETE
    private fun showDeleteDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Contact")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes") { _, _ ->
                val contact = filteredList[position]

                contactList.remove(contact)
                filteredList.removeAt(position)

                contactAdapter.notifyItemRemoved(position)

                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    // ================= EDIT =================
    private fun showEditDialog(position: Int) {

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.activity_dialog_edit_item, null)

        val ivEditPreview = dialogView.findViewById<ImageView>(R.id.ivEditPreview)
        val etEditName = dialogView.findViewById<EditText>(R.id.etEditName)
        val etEditPhone = dialogView.findViewById<EditText>(R.id.etEditPhone)
        val btnEditImage = dialogView.findViewById<Button>(R.id.btnEditImage)

        val contact = filteredList[position]

        // ✅ Set existing data
        etEditName.setText(contact.name)
        etEditPhone.setText(contact.phone)

        // ✅ Show existing image
        if (contact.imageUri != null) {
            ivEditPreview.setImageURI(Uri.parse(contact.imageUri))
        } else {
            ivEditPreview.setImageResource(R.drawable.ic_launcher_background)
        }

        // ✅ VERY IMPORTANT: link dialog preview
        currentDialogImageView = ivEditPreview

        // ✅ Pick image button
        btnEditImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Contact")
            .setView(dialogView)
            .setPositiveButton("Update", null)
            .setNegativeButton("Cancel") { _, _ ->
                currentDialogImageView = null
            }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            val updatedName = etEditName.text.toString().trim()
            val updatedPhone = etEditPhone.text.toString().trim()

            if (validateInputs(updatedName, updatedPhone, etEditName, etEditPhone)) {

                contact.name = updatedName
                contact.phone = updatedPhone

                // ✅ Update image ONLY if new selected
                if (selectedImageUri != null) {
                    contact.imageUri = selectedImageUri
                }

                contactAdapter.notifyItemChanged(position)

                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                currentDialogImageView = null
            }
        }
    }

    // SEARCH
    private fun setupSearch() {
        etSearch.addTextChangedListener {

            val query = it.toString().lowercase()

            filteredList.clear()

            if (query.isEmpty()) {
                filteredList.addAll(contactList)
            } else {
                for (contact in contactList) {
                    if (contact.name.lowercase().contains(query) ||
                        contact.phone.contains(query)
                    ) {
                        filteredList.add(contact)
                    }
                }
            }

            contactAdapter.notifyDataSetChanged()
        }
    }

    // ================= SORT =================
    private fun setupSorting() {
        ivSort.setOnClickListener {

            if (isAscending) {
                filteredList.sortBy { it.name.lowercase() }
                Toast.makeText(this, "A → Z", Toast.LENGTH_SHORT).show()
            } else {
                filteredList.sortByDescending { it.name.lowercase() }
                Toast.makeText(this, "Z → A", Toast.LENGTH_SHORT).show()
            }

            isAscending = !isAscending
            contactAdapter.notifyDataSetChanged()
        }
    }

    // ================= PERMISSION =================
    private fun checkPermissionAndLoadContacts() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadContactsFromPhone()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                AlertDialog.Builder(this)
                    .setTitle("Permission")
                    .setMessage("Need contacts access")
                    .setPositiveButton("Allow") { _, _ ->
                        requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
                    }
                    .show()
            }

            else -> {
                requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    // ================= LOAD CONTACTS =================
    private fun loadContactsFromPhone() {
        val loadedContacts = mutableListOf<Contact>()

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: ""
                val phone = it.getString(phoneIndex) ?: ""

                if (name.isNotBlank() && phone.isNotBlank()) {
                    loadedContacts.add(Contact(name, phone))
                }
            }
        }

        contactList.clear()
        contactList.addAll(loadedContacts)

        filteredList.clear()
        filteredList.addAll(loadedContacts)

        contactAdapter.notifyDataSetChanged()

        Toast.makeText(this, "${loadedContacts.size} contacts loaded", Toast.LENGTH_SHORT).show()
    }


}
