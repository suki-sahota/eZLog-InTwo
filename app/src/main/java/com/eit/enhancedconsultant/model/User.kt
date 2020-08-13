package com.eit.enhancedconsultant.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var contactNumber: String,
    var email: String,
    var firstName: String,
    var lastName: String,
    var level: Int,
    var loggedIn: Boolean,
    var password: String,
    var tasks: MutableList<Task> = mutableListOf(),
    val uid: String,
    var userName: String
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "contactNumber" to contactNumber,
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "level" to level,
            "loggedIn" to loggedIn,
            "password" to password,
            "uid" to uid,
            "userName" to userName
        )
    }
}