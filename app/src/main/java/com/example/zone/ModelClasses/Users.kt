package com.example.zone.ModelClasses

data class Users(
    var uid: String? = "",
    var username: String? ="",
    var profile: String? ="",
    var status: String? ="",
    var description: String? =""
)
/*
class Users {
    private var uid: String = ""
    private var username: String = ""
    private var profile: String = ""
    private var status: String = ""
    private var description: String = ""

    constructor(
        uid: String,
        username: String,
        profile: String,
        status: String,
        description: String
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.status = status
        this.description = description
    }

    fun getUID(): String?{
        return uid
    }

    fun setUID(uid: String){
        this.uid = uid
    }

    fun getUserName(): String? {
        return username
    }

    fun setUserName(username: String){
        this.username = username
    }

    fun getProfile(): String?{
        return profile
    }

    fun setProfile(profile: String){
        this.profile = profile
    }

    fun getStatus(): String?{
        return status
    }

    fun setStatus(status: String){
        this.status = status
    }

    fun getDescription(): String?{
        return description
    }

    fun setDescription(description: String){
        this.description = description
    }
}


 */
