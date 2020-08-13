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