package com.example.contactapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class AllContactsActivity : AppCompatActivity(), ContactAdapter.OnContactActionListener {

    private lateinit var recyclerViewGrid: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private var contactList = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_contacts)

        recyclerViewGrid = findViewById(R.id.recyclerViewGrid)

        // Receive contacts from MainActivity
        contactList = intent.getSerializableExtra("contacts") as? MutableList<Contact> ?: mutableListOf()

        // Grid layout 2 items per row
        recyclerViewGrid.layoutManager = GridLayoutManager(this, 2)

        contactAdapter = ContactAdapter(contactList, this)
        recyclerViewGrid.adapter = contactAdapter
    }

    override fun onItemClick(position: Int) {
        val contact = contactList[position]
        Toast.makeText(this, "${contact.name} - ${contact.phone}", Toast.LENGTH_SHORT).show()
    }

    override fun onEditClick(position: Int) {
        Toast.makeText(this, "Editing is not allowed here", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteClick(position: Int) {
        Toast.makeText(this, "Deleting is not allowed here", Toast.LENGTH_SHORT).show()
    }
}