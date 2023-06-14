package com.example.intro_companion.backend.entities

data class Request(
    var name: String,
    var emailWPI: String,
    var idWPI: String,
    var emailContact: String,
    var phone: String,
    var description: String,
    var onCampus:Boolean,
    var building:String
)