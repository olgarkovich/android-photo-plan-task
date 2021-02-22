package com.example.photoplantask.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoplan.adapter.LocationAdapter
import com.example.photoplantask.R
import com.example.photoplantask.model.Location
import com.example.photoplantask.model.Section
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class LocationFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var section: Section
    private lateinit var sectionName: EditText
    private var locations = mutableListOf<Location>()
    private lateinit var locationList: RecyclerView
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var addLocation: FloatingActionButton
    private lateinit var filepath: Uri
    private var currentPosition = -1
    private var currentId = ""
    val request = 1

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
        addLocation = view.findViewById(R.id.addLocation)
        locationList = view.findViewById(R.id.locationList)
        locationList.layoutManager = LinearLayoutManager(requireContext())
        locationAdapter = LocationAdapter()
        locationList.adapter = locationAdapter

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
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

        locationAdapter.setOnFocusChangedListener(object : LocationAdapter.OnFocusChangeListener {
            override fun onFocusChanged(position: Int, locationName: String) {
                if (locationName != locations[position].name)
                    saveLocationName(locations[position].id, locationName)
            }
        })

        locationAdapter.setOnItemClickListener(object : LocationAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                currentPosition = position
                currentId = locations[position].id
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, request)
            }
        })

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

        addLocation.setOnClickListener {
            addLocation()
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            firebaseAuth.signInAnonymously()
        }

    }

    private fun addLocation() {
        val location = Location()
        locations.add(location)
        firebaseFirestore.collection("Section")
            .document(section.id)
            .collection("Location")
            .add(location)
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
                    setData()
                } else {
                    locations.add(Location("gg"))
                }
            }
    }

    private fun setData() {
        locationAdapter.setLocations(locations)
        locationAdapter.notifyDataSetChanged()
    }

    private fun saveLocationName(id: String, name: String) {
        firebaseFirestore.collection("Section").document(section.id).collection("Location")
            .document(
                id
            ).update(
                "name", name
            )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == request && data != null) {
            filepath = data.data!!

            uploadImage()
        }
    }

    private fun uploadImage() {
        val imageName = chooseNameFromFilepath()
        val imageRef = firebaseStorage.reference.child("images/${imageName}")
        locations[currentPosition].pictures.add(imageName)
        addPictureRef(imageName)

        imageRef.putFile(filepath)
    }

    private fun addPictureRef(imageName: String) {
        locations[currentPosition].pictures.add(imageName)
        firebaseFirestore.collection("Section").document(section.id).collection("Location")
            .document(currentId).update("pictures", locations[currentPosition].pictures)
    }

    private fun chooseNameFromFilepath(): String {
        val strList = filepath.toString().split("/")
        return strList[strList.size - 1]
    }

}