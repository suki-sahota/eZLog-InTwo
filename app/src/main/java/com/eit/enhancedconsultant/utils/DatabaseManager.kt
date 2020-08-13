package com.eit.enhancedconsultant.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.eit.enhancedconsultant.model.Repository
import com.eit.enhancedconsultant.model.User
import com.eit.enhancedconsultant.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object DatabaseManager {
    // Debug
    private const val TAG = "DatabaseManager"
    // Firebase variables
    val database = Firebase.database.reference
    val auth: FirebaseAuth = Firebase.auth
    var user: FirebaseUser? = null // Logged-in User
    var isConnected = false

    lateinit var mContext: Context

    fun broadcastFirebaseConnection(context: Context) {
        Log.i(TAG, "Connected to Firebase: $isConnected")
        val jsonIntent = Intent(MainActivity.FIREBASE_CONNECTION_MESSAGE)

        val manager =
            LocalBroadcastManager
                .getInstance(context)

        manager.sendBroadcast(jsonIntent)
    }// end of function broadcastJson

    fun acceptTask(taskId: String, uid: String) {
        FirebaseDatabase.getInstance().reference // Set true in "members" table
            .child("members")
            .child(taskId)
            .child("accepted")
            .setValue(true)

        FirebaseDatabase.getInstance().reference // Set true in "meta" table
            .child("meta")
            .child(taskId)
            .child("accepted")
            .setValue(true)

        FirebaseDatabase.getInstance().reference // Set true in "tasks" table
            .child("tasks")
            .child(taskId)
            .child("accepted")
            .setValue(true)

        FirebaseDatabase.getInstance().reference // Set true in "users" table
            .child("users")
            .child(uid)
            .child("tasks")
            .child(taskId)
            .child("accepted")
            .setValue(true)
    }

    fun addAssignedTask(taskName: String, employeeEmail: String,
                        taskDescription: String, assignor: String,
                        dateAssigned: String, dueDate: String
    ): Boolean {
        var success = true

        // Reuse same key for all four "tables"
        val key = database
            .child("members")
            .push()
            .key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key. Add task aborted! Please try again.")
            return false
        }

        // members "table"
        val membersTask = Repository.createMembersTask(
            assignedBy = assignor,
            assignedTo = employeeEmail
        )

        // meta "table"
        val metaTask = Repository.createMetaTask(
            dueDate = dueDate,
            name = taskName,
            timeStamp = dateAssigned
        )

        // tasks "table"
        val task =
            Repository.createTask(
                accepted = false, assignedBy = assignor, assignedTo = employeeEmail, 
                completed = false, declined = false, description = taskDescription, 
                dueDate = dueDate, name = taskName, timeStamp = dateAssigned
            )

        var uid = ""
        val usersRef =
            FirebaseDatabase
                .getInstance()
                .reference
                .child("users")
        val uidQuery: Query = usersRef
            .orderByChild("email")
            .equalTo(employeeEmail)
        uidQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    uid = data.key ?: ""
                    if (uid == "") {
                        Log.d(TAG,
                            "onDataChange: No email address matches. Add task aborted!")
                        success = false
                        return
                    }
                }
                val childUpdates = hashMapOf<String, Any>(
                    "/members/$key" to membersTask,
                    "/meta/$key" to metaTask,
                    "/tasks/$key" to task,
                    "/users/$uid/tasks/$key" to task
                )
                database.updateChildren(childUpdates) // Push changes to Firebase
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "onCancelled: No email address matches. Add task aborted!")
                success = false
            }
        })
        return success
    }// end of function addAssignedTask

    /**
     * Used to insert a user object into the database
     */
    fun addUser(firstName: String, lastName: String, email: String,
                contactNumber: String, password: String, userName: String,
                level: Int = MainActivity.USER, loggedIn: Boolean = false
    ): Boolean {
        var success = true
        val uid = auth.currentUser!!.uid
        val user = Repository.createUser(
                contactNumber, email, firstName, lastName,
                level, loggedIn, password, uid, userName
            ).toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/users/$uid" to user
        )
        database.updateChildren(childUpdates) // Push changes to Firebase
            .addOnSuccessListener {
                Log.d(TAG, "addUser: Successfully added user into database.")
            }
            .addOnFailureListener {
                Log.d(TAG, "addUser: Add user failed. Add user aborted!")
                success = false
            }
        return success
    }// end of function addUser

    /**
     * Called when user decides to decline a task
     */
    fun declineTask(taskId: String, uid: String) {
        FirebaseDatabase.getInstance().reference // Remove task from "users" table
            .child("users")
            .child(uid)
            .child("tasks")
            .child(taskId)
            .removeValue()

        FirebaseDatabase.getInstance().reference // Set false in "tasks" table
            .child("tasks")
            .child(taskId)
            .child("decline")
            .setValue(true)
    }

    fun completeTask(taskId: String, uid: String) {
        FirebaseDatabase.getInstance().reference
            .child("users")
            .child(uid)
            .child("tasks")
            .child(taskId)
            .setValue(true)
    }

    fun getFirebaseConnection(context: Context) {
        mContext = context
        val connectedRef = Firebase
                .database
                .getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                isConnected = connected
                broadcastFirebaseConnection(mContext)
            }// end of onDataChanged

            override fun onCancelled(error: DatabaseError) {
                isConnected = false
            }// end of function onCancelled
        })
    }// end of function getFirebaseConnection


    fun logOutUser() {
        user = null
        Firebase.auth.signOut()
    }// end of function logInUser
}// end of class DatabaseManager