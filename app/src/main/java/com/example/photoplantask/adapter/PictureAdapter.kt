package com.example.photoplantask.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.photoplantask.NextActivity
import com.example.photoplantask.R
import com.google.firebase.storage.FirebaseStorage


class PictureAdapter(var context: Context, val list: ArrayList<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "UseCompatLoadingForDrawables")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.item_picture, null)
        val pictureView = view.findViewById<ImageView>(R.id.pictureView)
        val checkDelete = view.findViewById<CheckBox>(R.id.checkDelete)

        val listItem = list[position]

        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child("images/${listItem}")
        var imageUrlForFull = ""
        storageReference.downloadUrl.addOnSuccessListener { Uri->
            val imageURL = Uri.toString()
            imageUrlForFull = imageURL

            Glide.with(context)
                .load(imageURL)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(pictureView)
        }

        pictureView.setOnClickListener {
            val myIntent = Intent(context, NextActivity::class.java)
            myIntent.putExtra("key", imageUrlForFull)
            context.startActivity(myIntent)
        }

        pictureView.setOnLongClickListener {
            Toast.makeText(context, "click looong", Toast.LENGTH_SHORT).show()
            checkDelete.visibility = View.VISIBLE
            return@setOnLongClickListener true
        }

//        checkDelete.setOnClickListener {
//            Toast.makeText(context, "check", Toast.LENGTH_SHORT).show()
//            if (checkDelete.background == context.getDrawable(R.drawable.not_selected)) {
//                checkDelete.setBackgroundResource(R.drawable.selected)
//            }
//            else {
//                checkDelete.setBackgroundResource(R.drawable.not_selected)
//            }
//        }

        return view
    }
}