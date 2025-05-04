package com.example.zone.ModelClasses

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
}

