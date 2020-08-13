package com.eit.enhancedconsultant.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.eit.enhancedconsultant.model.Repository
import com.eit.enhancedconsultant.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

object DatabaseManager
{
    private const val TAG = "DatabaseManager"

    // Firebase variables
    private val database = Firebase.database.reference // Database (Firebase) reference for entire project
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    // User properties (pre-defined by Google; we CANNOT add more fields)
    private var uid: String? = null
    private var email: String? = null
    private var displayName: String? = null
    private var photoUrl: Uri? = null
    private var isEmailVerified: Boolean = false

    lateinit var mContext: Context
    var isConnected = false

    fun broadcastFirebaseConnection(context: Context)
    {
        Log.i(TAG, "Connected to Firebase: $isConnected")
        val jsonIntent = Intent(MainActivity.FIREBASE_CONNECTION_MESSAGE)
        val manager = LocalBroadcastManager.getInstance(context)
        manager.sendBroadcast(jsonIntent)
    }// end of function broadcastJson

    fun acceptTask(email: String, taskId: String)
    {
        // Not yet implemented
    }

    fun addAssignedTask(taskName: String, employeeEmail: String, taskDescription: String,
                        assignor: String, dateAssigned: String, dueDate: String,
                        taskId: String) : Boolean
    {
        // Reuse same key for all three "tables"
        val key = database.child("members").push().key

        if (key == null)
        {
            Log.e(TAG, "Couldn't get push key. Please try again.")
            return false
        }// end of if block

        // Members "table"
        val membersTask
                = Repository.createMembersTask(assignor, employeeEmail)

        // Meta "table"
        val metaTask = Repository.createMetaTask(dueDate, taskName, dateAssigned)

        // Tasks "table"
        val completeTask =
            Repository.createCompleteTask(accepted = false,
                assignedBy =  assignor, assignedTo = employeeEmail,
                completed = false, declined = false,
                description = taskDescription, dueDate = dueDate,
                name = taskName, timeStamp = dateAssigned, taskId = taskId)

        val childUpdates = hashMapOf<String, Any>(
            "/members/$key" to membersTask,
            "/meta/$key" to metaTask,
            "/tasks/$key" to completeTask)

        var success = false

        // Push changes to Firebase
        database.updateChildren(childUpdates)
            .addOnSuccessListener {
                success = true
            }

            .addOnFailureListener{
                success = false
            }

        return success
    }// end of function addAssignedTask

    fun addUser(firstName: String, lastName: String, email: String,
                contactNumber: String, password: String, userName: String,
                level: Int = MainActivity.USER, loggedIn: Boolean = false) : Boolean
    {
        val uid = "eit${SimpleDateFormat("HHmmssSSS", Locale.ENGLISH).format(Date())}"
        val completeUser = Repository.createCompleteUser(firstName, lastName, email,
            contactNumber, password,  userName, level, loggedIn).toMap()
        val childUpdates = hashMapOf<String, Any>("/users/$uid" to completeUser)
        var success = false

        // Push changes to Firebase
        database.updateChildren(childUpdates)
            .addOnSuccessListener {
                success = true
            }

            .addOnFailureListener{
                success = false
            }

        return success
    }// end of function addUser

    fun declineTask(email: String, taskId: String)
    {
        // Not yet implemented
    }

    fun completeTask(email: String, taskId: String)
    {
        // Not yet implemented
    }

    fun getFirebaseConnection(context: Context)
    {
        mContext = context
        val connectedRef = Firebase.database.getReference(".info/connected")
        auth = Firebase.auth // Lazy initialize Firebase Auth...
        user = auth.currentUser

        connectedRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                val connected = snapshot.getValue(Boolean::class.java) ?: false

                isConnected = connected

                broadcastFirebaseConnection(
                    mContext
                )
            }// end of onDataChanged

            override fun onCancelled(error: DatabaseError)
            {
                isConnected = false
            }// end of function onCancelled
        })
    }// end of function getFirebaseConnection

    /**
     * Returns the current user
     */
    fun getUserProfile()
    {
        user = Firebase.auth.currentUser
        user?.let {
            // The user's ID, unique to the Firebase project
            uid = (user as FirebaseUser).uid

            // Name, email address, and profile photo Url
            displayName = (user as FirebaseUser).displayName
            email = (user as FirebaseUser).email
            photoUrl = (user as FirebaseUser).photoUrl

            // Check if user's email is verified
            isEmailVerified = (user as FirebaseUser).isEmailVerified
        }
    }// end of function getUserProfile

    fun logInUser(email: String, password: String)
    {
        // Not yet implemented
    }// end of function

    fun logOutUser(email: String)
    {
        // Not yet implemented
    }// end of function logInUser
}// end of class DatabaseManager