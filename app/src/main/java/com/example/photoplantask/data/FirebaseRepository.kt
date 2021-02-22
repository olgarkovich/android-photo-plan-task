package com.example.photoplantask.data

import com.example.photoplantask.model.Location
import com.example.photoplantask.tools.LOCATION
import com.example.photoplantask.tools.NAME
import com.example.photoplantask.tools.PICTURES
import com.example.photoplantask.tools.SECTION
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository(sectionId: String) {

    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val sectionRef = firebaseFirestore.collection(SECTION).document(sectionId)

    fun addLocation(location: Location) {
        sectionRef.collection(LOCATION)
            .add(location)
    }

    fun saveLocationName(id: String, name: String) {
        sectionRef.collection(LOCATION)
            .document(id)
            .update(NAME, name)
    }

    fun saveSectionName(name: String) {
        sectionRef.update(NAME, name)
    }

    fun addPictureRef(location: Location) {
        sectionRef.collection(LOCATION)
            .document(location.id)
            .update(PICTURES, location.pictures)
    }
}