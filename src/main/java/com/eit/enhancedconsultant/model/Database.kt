package com.eit.enhancedconsultant.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class MembersTask(
    var assignedBy: String,
    var assignedTo: String
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "assignedBy" to assignedBy,
            "assignedTo" to assignedTo
        )
    }
}

@IgnoreExtraProperties
data class MetaTask(
    var dueDate: String,
    var name: String,
    var timeStamp: String
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "dueDate" to dueDate,
            "name" to name,
            "timeStamp" to timeStamp
        )
    }
}

@IgnoreExtraProperties
data class CompleteTask(
    var accepted: Boolean,
    var assignedBy: String,
    var assignedTo: String,
    var completed: Boolean,
    var declined: Boolean,
    var description: String,
    var dueDate: String,
    var name: String,
    var timeStamp: String,
    var taskId: String
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "accepted" to accepted,
            "assignedBy" to assignedBy,
            "assignedTo" to assignedTo,
            "completed" to completed,
            "declined" to declined,
            "description" to description,
            "dueDate" to dueDate,
            "name" to name,
            "timeStamp" to timeStamp,
            "taskId" to taskId
        )
    }
}

@IgnoreExtraProperties
data class CompleteUser(
    var tasks: MutableList<CompleteTask> = mutableListOf(), // <-- I want a listener to find out when there is a new task
    var user: User
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "tasks" to tasks,
            "user" to user
        )
    }
}

@IgnoreExtraProperties
data class User(
    var firstName: String,
    var lastName: String,
    var email: String,
    var contactNumber: String,
    var password: String,
    var userName: String,
    var level: Int,
    var loggedIn: Boolean)
{
    @Exclude
    fun toMap(): Map<String, Any?>
    {
        return mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "contactNumber" to contactNumber,
            "password" to password,
            "userName" to userName,
            "level" to level,
            "loggedIn" to loggedIn
        )
    }
}



