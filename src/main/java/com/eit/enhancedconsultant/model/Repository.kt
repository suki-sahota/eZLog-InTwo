package com.eit.enhancedconsultant.model

import com.eit.enhancedconsultant.view.MainActivity

object Repository {

    // For members "table"
    fun createMembersTask(assignedBy: String, assignedTo: String): Map<String, Any?> {
        return MembersTask(assignedBy, assignedTo).toMap()
    }

    // For meta "table"
    fun createMetaTask(dueDate: String, name: String, timeStamp: String): Map<String, Any?> {
        return MetaTask(dueDate, name, timeStamp).toMap()
    }

    // For tasks "table"
    fun createCompleteTask(accepted: Boolean, assignedBy: String,
                           assignedTo: String, completed: Boolean,
                           declined: Boolean, description: String,
                           dueDate: String, name: String,
                           timeStamp: String, taskId: String): Map<String, Any?> {
        return CompleteTask(accepted, assignedBy, assignedTo,
            completed, declined, description, dueDate, name, timeStamp, taskId).toMap()
    }

    // For users "table"
    fun createCompleteUser(firstName: String, lastName: String, email: String,
                           contactNumber: String, password: String, userName: String,
                           level: Int = MainActivity.USER, loggedIn: Boolean = false): Map<String, Any?> {
        val user =
            User(firstName, lastName, email, contactNumber, password, userName,
                level, loggedIn)
        val tasks = mutableListOf<CompleteTask>()
        return CompleteUser(tasks, user).toMap()
    }
}

