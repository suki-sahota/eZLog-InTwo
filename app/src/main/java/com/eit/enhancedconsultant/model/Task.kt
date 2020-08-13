package com.eit.enhancedconsultant.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Task(
    var accepted: Boolean,
    var assignedBy: String,
    var assignedTo: String,
    var completed: Boolean,
    var declined: Boolean,
    var description: String,
    var dueDate: String,
    var name: String,
    var timeStamp: String
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
            "timeStamp" to timeStamp
        )
    }
}