package com.eit.enhancedconsultant.view

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eit.enhancedconsultant.R
import com.eit.enhancedconsultant.model.Task
import com.eit.enhancedconsultant.model.TaskModel
import com.eit.enhancedconsultant.model.User
import com.eit.enhancedconsultant.presenter.Presenter
import com.eit.enhancedconsultant.utils.DatabaseManager
import com.eit.enhancedconsultant.utils.DatabaseManager.user
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), EnhancedView
{
    // Field declarations
    private val userSetupFragment = SetupFragment.newInstance()
    private val userSignInFragment = SignInFragment.newInstance()
    private val userTasksFragment = TasksFragment.newInstance()
    private val assignTasksFragment = AssignTaskFragment.newInstance()
    private val homeFragment = HomeFragment.newInstance()

    companion object {
        private lateinit var presenter: Presenter

        private const val TAG = "MainActivity"
        const val FIREBASE_CONNECTION_MESSAGE = "com.eit.enhancedconsultant.CONNECTED_TO_FIREBASE"
        const val SIGN_IN_FRAGMENT = "sign_in_fragment"
        const val ASSIGN_TASKS_FRAGMENT = "assign_tasks_fragment"
        const val TASK_CHECKER_FRAGMENT = "check_tasks_fragment"
        const val NEW_ACCOUNT_FRAGMENT = "new_account_fragment"
        const val HOME_FRAGMENT = "home_fragment"

        // Sign in levels
        const val ADMIN = 1
        const val USER = 2

        var auth = Firebase.auth
        lateinit var enhanceMain: Activity
        lateinit var currentUser: User
        var taskList = mutableListOf<TaskModel>()

        var refreshRequested: Boolean = false

    }// end of companion object

    /**
     * Main entry point
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        } // end of if block

        setContentView(R.layout.activity_main)

        enhanceMain = this@MainActivity

        // Bind the activity with the presenter
        bindPresenter()

        loadSignInFragment()
    }// end of function onCreate

    override fun onBackPressed() {
        if (userSetupFragment.isVisible) {
            supportFragmentManager.popBackStack();
            loadSignInFragment()
        }
        else if (homeFragment.isVisible) {
            super.onBackPressed()
        }// end of if block
    }

    /**
     * Accept a task that was assigned to a user
     *
     * @param taskId The Id of task to be completed
     * @param email The email address of the person that should complete the task
     */
    override fun acceptTask(taskId: String, email: String) {
        presenter.acceptAssignedTask(taskId, email)
    }// nd of function acceptTask

    /**
     * Decline a task that was assigned to a user
     *
     * @param taskId The Id of task to be completed
     * @param email The email address of the person that should complete the task
     */
    override fun declineTask(taskId: String, email: String) {
        presenter.declineAssignedTask(taskId, email)
    }// end of function declineTask

    /**
     * Complete a task that was assigned to a user
     *
     * @param taskId The Id of task to be completed
     * @param email The email address of the person that should complete the task
     */
    override fun completeTask(taskId: String, email: String) {
        presenter.completeAssignedTask(taskId, email)
    }// end of function declineTask

    /**
     * Assigns a task to a specific user
     *
     * @param taskName The type of task to be completed
     * @param employeeEmail The email address of the person that should complete the task
     * @param taskDescription  A description of the task
     * @param assignor  The name of the person who assigns the task
     * @param dateAssigned  The date and time tht the task was assigned
     * @param dueDate   The date and time that the task must be completed
     */
    override fun assignTask(taskName: String, employeeEmail: String, taskDescription: String,
                            assignor: String, dateAssigned: String, dueDate: String)
    {
        presenter.assignUserTask(taskName, employeeEmail, taskDescription,
            assignor, dateAssigned, dueDate)
    }// end of function assignTask

    /**
     * Bind the @see <Presenter> to the @see <MainActivity>
     */
    override fun bindPresenter() {
        presenter = Presenter()
        presenter.onBindView(this@MainActivity)
    }// end of function bindPresenter

    /**
     * Determines whether or not the user entered valid data
     * @param email  A valid user name known to the network
     * @param password  A valid password matching the user name
     */
    override fun isUserValid(email: String, password: String) {
        if (presenter.validateLoginInput(email, password)) {
            logInUser(email, password)
        }// end of if block
        else {
            makeToast("Your user credentials are invalid!")
        }// end of else block
    }// end of function isUserValid

    /**
     * Determine whether or not the user entered valid data
     *
     * @param firstName  The user's first name
     * @param lastName  The user's last name
     * @param email  A valid email address for the user
     * @param telephone  A contact number for the user (optional)
     * @param userName  A specified user name (optional)
     * @param password  A valid password matching the user name
     * @param confirmPass  A confirmation of the supplied password
     * @return A @see <Boolean> value depending on the requirements
     */
    override fun isNewUserValid(firstName: String, lastName: String,
                                email: String, telephone: String,
                                userName: String, password: String,
                                confirmPass: String)
    {
        if (presenter.validateNewUser(firstName, lastName, email, password, confirmPass)) {
            createUser(firstName, lastName, email, password) // This creates a user for authentication
            presenter.addNewUser( // This creates an entry for user in database
                firstName, lastName, email,
                telephone, userName, password
            )
        }// end of if block
        else {
            makeToast("Your user credentials are invalid/incomplete!")
        }// end of else block
    }// end of function isNewUserValid

    /**
     * Loads the main fragment after successful sign in
     */
    override fun loadHomeFragment() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.animator.enter_from_right,
                R.animator.exit_to_left,
                R.animator.enter_from_left,
                R.animator.exit_to_right
            )
            .replace(R.id.app_main_container_fl, homeFragment, HOME_FRAGMENT)
            .addToBackStack(null)
            .commit()
    }// end of function loadHomeFragment

    /**
     * Logs the current user out
     */
    override fun logoutUser() {
        presenter.logoutCurrentUser()
        loadSignInFragment()
    }// end of function logoutUser

    /**
     * Load the fragment for the new user account
     */
    override fun loadSetupFragment() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.animator.enter_from_right,
                R.animator.exit_to_left,
                R.animator.enter_from_left,
                R.animator.exit_to_right
            )
            .replace(R.id.app_main_container_fl, userSetupFragment, NEW_ACCOUNT_FRAGMENT)
            .addToBackStack(null)
            .commit()
    }// end of function loadSetupFragment

    /**
     * Load the fragment for the user sign in
     */
    override fun loadSignInFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.app_main_container_fl, userSignInFragment,
                SIGN_IN_FRAGMENT)
            .addToBackStack(null)
            .commit()
    }// end of function loadSignInFragment

    /**
     * Loads the fragment for assigning tasks
     */
    override fun loadAssignTasksFragment() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.animator.card_flip_right_in,
                R.animator.card_flip_right_out,
                R.animator.card_flip_left_in,
                R.animator.card_flip_left_out)
            .replace(R.id.app_main_container_fl, assignTasksFragment,
                ASSIGN_TASKS_FRAGMENT)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Loads the fragment for view user tasks
     */
    override fun loadUserTasksFragment() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.animator.card_flip_right_in,
                R.animator.card_flip_right_out,
                R.animator.card_flip_left_in,
                R.animator.card_flip_left_out)
            .replace(R.id.app_main_container_fl, userTasksFragment,
                TASK_CHECKER_FRAGMENT)
            .addToBackStack(null)
            .commit()
    }// end of function loadUserTasksFragment

    /**
     * Refreshes the list of assigned tasks
     */
    override fun refreshTasks() {
        TODO("Not yet implemented")
    }

    override fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun logInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail: success")
                    makeToast("Welcome back ${ auth.currentUser!!.displayName }")
                    loadCurrentUser() // Save user info here
                }// end of else block
                else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail: failure", task.exception)
                    makeToast("Authentication failed.")
                }// end of else block
            }
    }// end of function

    /**
     * Used to create an account in order to log into the app
     */
    override fun createUser(
        firstName: String, lastName: String, email: String, password: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail: success")
                    makeToast("Authentication succeeded!")
                    val profileUpdates = userProfileChangeRequest {
                        displayName = "$firstName $lastName"
                    }
                    auth.currentUser!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                            }
                        }
                    onBackPressed()
                } else {
                    // If sign in fails, this email/password combo may already be taken
                    Log.w(TAG, "createUserWithEmail: failure", task.exception)
                    makeToast("Authentication failed.")
                }
            }
    }

    /**
     * Returns a User object for consumption
     */
    override fun loadCurrentUser() {
        val user = auth.currentUser
        val userRef =
            FirebaseDatabase
                .getInstance()
                .reference
                .child("users")
                .child(user!!.uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(
                    TAG,
                    "onDataChange: Query successful!"
                )
                var contactNum = ""
                var email = ""
                var firstName = ""
                var lastName = ""
                var level = 1
                var password = ""
                var tasks = mutableListOf<Task>()
                var uid = ""
                var userName = ""
                for (data in dataSnapshot.children) {
                    if (data.key == "contactNumber") contactNum = data.value as String
                    if (data.key == "email") email = data.value as String
                    if (data.key == "firstName") firstName = data.value as String
                    if (data.key == "lastName") lastName = data.value as String
                    if (data.key == "level" && data.value == 2L) level = 2
                    if (data.key == "password") password = data.value as String
                    if (data.key == "tasks") tasks = data.value as MutableList<Task>
                    if (data.key == "uid") uid = data.value as String
                    if (data.key == "userName") userName = data.value as String
                }
                currentUser =
                    User(
                        contactNum, email, firstName, lastName, level,
                        true, password, tasks, uid, userName
                    )
                loadHomeFragment() // Display the fragment to the relevant user
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "onCancelled: No uid matches. Load user aborted!")
                return
            }
        })
    }
}// end of class MainActivity