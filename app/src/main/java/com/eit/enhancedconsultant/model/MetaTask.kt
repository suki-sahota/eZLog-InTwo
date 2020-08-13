package com.eit.enhancedconsultant.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

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