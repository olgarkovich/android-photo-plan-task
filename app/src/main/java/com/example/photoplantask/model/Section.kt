package com.example.photoplantask.model

import com.google.firebase.firestore.DocumentId

class Section() {

    @DocumentId
    var id: String = ""
    var name = ""

    constructor(name: String) : this() {
        this.name= name
    }
}