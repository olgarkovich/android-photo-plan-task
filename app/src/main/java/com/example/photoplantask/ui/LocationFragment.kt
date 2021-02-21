package com.example.photoplantask.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.photoplantask.R
import com.example.photoplantask.model.Location
import com.example.photoplantask.model.Section
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LocationFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var section: Section
    private lateinit var sectionName: EditText
    private var locations = mutableListOf<Location>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sectionName = view.findViewById(R.id.sectionName)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("Section").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                section = task.result?.toObjects(Section::class.java)?.get(0) ?: Section("util")
                sectionName.setText(section.name)
                getLocations()
            } else {
                sectionName.setText("ggg")
            }
        }

        sectionName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Toast.makeText(requireContext(), "focused", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "focuse lose", Toast.LENGTH_SHORT).show()
                if (sectionName.text.toString() != section.name) {
                    saveSectionName()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            firebaseAuth.signInAnonymously()
        }

    }

    private fun saveSectionName() {
        firebaseFirestore.collection("Section").document(section.id).update(
            "name", sectionName.text.toString()
        )
    }

    private fun getLocations() {
        firebaseFirestore.collection("Section").document(section.id).collection("Location").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    locations = task.result?.toObjects(Location::class.java)!!
                } else {
                    locations.add(Location("gg"))
                }
            }
    }

}