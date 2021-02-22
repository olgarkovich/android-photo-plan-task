package com.example.photoplantask

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide


class NextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)

        val fullPicture = findViewById<ImageView>(R.id.fullPicture)
        val intent = intent
        val value = intent.getStringExtra("key")

        Glide.with(this)
            .load(value)
            .placeholder(R.drawable.placeholder_image)
            .into(fullPicture)
    }
}