package com.example.photoplantask.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoplantask.R
import com.example.photoplantask.adapter.LocationAdapter
import com.example.photoplantask.data.FirebaseRepository
import com.example.photoplantask.model.Location
import com.example.photoplantask.model.Section
import com.example.photoplantask.tools.LOCATION
import com.example.photoplantask.tools.SECTION
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class LocationFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseRepository: FirebaseRepository

    private lateinit var section: Section
    private var locations = mutableListOf<Location>()

    private lateinit var sectionName: EditText
    private lateinit var locationList: RecyclerView
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var addLocation: FloatingActionButton

    private lateinit var filepath: Uri
    private var currentPosition = -1
    private var currentId = ""
    val request = 1

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
        addLocation.setColorFilter(Color.WHITE)
        locationList = view.findViewById(R.id.locationList)
        locationList.layoutManager = LinearLayoutManager(requireContext())
        locationAdapter = LocationAdapter()
        locationList.adapter = locationAdapter

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection(SECTION).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                section = task.result?.toObjects(Section::class.java)?.get(0)!!
                sectionName.setText(section.name)
                firebaseRepository = FirebaseRepository(section.id)
                getLocations()
            } else {
                Log.d("MyLog", "No firestore connection")
            }
        }

        locationAdapter.setOnFocusChangedListener(object : LocationAdapter.OnFocusChangeListener {
            override fun onFocusChanged(position: Int, locationName: String) {
                if (locationName != locations[position].name)
                    firebaseRepository.saveLocationName(locations[position].id, locationName)
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
            if (!hasFocus) {
                if (sectionName.text.toString() != section.name) {
                    firebaseRepository.saveSectionName(sectionName.text.toString())
                }
            }
        }

        addLocation.setOnClickListener {
            val location = Location()
            locations.add(location)
            firebaseRepository.addLocation(location)
            updateView()
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            firebaseAuth.signInAnonymously()
        }
    }

    private fun getLocations() {
        firebaseFirestore.collection(SECTION).document(section.id).collection(LOCATION).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    locations = task.result?.toObjects(Location::class.java)!!
                    setData()
                } else {
                    locations.add(Location())
                }
            }
    }

    private fun setData() {
        locationAdapter.setLocations(locations)
        locationAdapter.notifyDataSetChanged()
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
        firebaseRepository.addPictureRef(locations[currentPosition])

        imageRef.putFile(filepath)
    }

    private fun chooseNameFromFilepath(): String {
        val strList = filepath.toString().split("/")
        return strList[strList.size - 1]
    }

    private fun updateView() {
        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
        ft.detach(this).attach(this).commit()
    }
}