package com.eit.enhancedconsultant.model

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
    fun createTask(
        accepted: Boolean, assignedBy: String, assignedTo: String,
        completed: Boolean, declined: Boolean, description: String,
        dueDate: String, name: String, timeStamp: String
    ): Map<String, Any?> {
        return Task(
            accepted, assignedBy, assignedTo, completed,
            declined, description, dueDate, name, timeStamp
        ).toMap()
    }

    // For users "table"
    fun createUser(
        contactNumber: String, email: String, firstName: String, lastName: String,
        level: Int, loggedIn: Boolean, password: String, uid: String, userName: String
    ): Map<String, Any?> {
        val tasks = mutableListOf<Task>()
        return User(
            contactNumber, email, firstName, lastName,
            level, loggedIn, password, tasks, uid, userName
        ).toMap()
    }
}
