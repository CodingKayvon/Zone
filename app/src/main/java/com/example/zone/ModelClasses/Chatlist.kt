package com.example.zone.ModelClasses

data class Chatlist(private var id: String? = "") {

    fun getId(): String {
        return id!!
    }

    fun setId(id: String){
        this.id = id
    }
}