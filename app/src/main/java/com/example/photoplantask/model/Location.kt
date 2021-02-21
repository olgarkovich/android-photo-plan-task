package com.example.photoplantask.model

import com.google.firebase.firestore.DocumentId

class Location() {

    @DocumentId
    var id = ""
    var name: String = "Новая локация"
    val pictures = ArrayList<String>()

    constructor(name: String) : this() {
        this.name= name
    }
}