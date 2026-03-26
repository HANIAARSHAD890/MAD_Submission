package com.example.contactapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.net.Uri
import com.bumptech.glide.Glide

class ContactAdapter(
        private val contactList: MutableList<Contact>,
        private val listener: OnContactActionListener,
    ) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

        interface OnContactActionListener {
            fun onItemClick(position: Int)
            fun onEditClick(position: Int)
            fun onDeleteClick(position: Int)
        }

        class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivContactImage: ImageView = itemView.findViewById(R.id.ivContactImage)
            val tvContactName: TextView = itemView.findViewById(R.id.tvContactName)
            val tvContactPhone: TextView = itemView.findViewById(R.id.tvContactPhone)
            val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
            val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_item_contact, parent, false)
            return ContactViewHolder(view)
        }

        override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
            val currentContact = contactList[position]

            holder.tvContactName.text = currentContact.name
            holder.tvContactPhone.text = currentContact.phone

            holder.itemView.setOnClickListener {
                listener.onItemClick(position)
            }

            holder.btnEdit.setOnClickListener {
                listener.onEditClick(position)
            }

            holder.btnDelete.setOnClickListener {
                listener.onDeleteClick(position)
            }
            Glide.with(holder.itemView.context)
                .load(currentContact.imageUri)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .into(holder.ivContactImage)
        }

        override fun getItemCount(): Int {
            return contactList.size
        }
    }