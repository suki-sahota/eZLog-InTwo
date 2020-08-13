package com.eit.enhancedconsultant.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eit.enhancedconsultant.R
import com.eit.enhancedconsultant.model.Employee
import com.eit.enhancedconsultant.model.TaskModel
import com.eit.enhancedconsultant.presenter.Presenter

class MainActivity : AppCompatActivity(), EnhancedView
{
    // Field declarations
    private val userSetupFragment = SetupFragment.newInstance()
    private val userSignInFragment = SignInFragment.newInstance()
    private val userTasksFragment = TasksFragment.newInstance()
    private val assignTasksFragment = AssignTaskFragment.newInstance()
    private val homeFragment = HomeFragment.newInstance()

    companion object{
        private lateinit var presenter: Presenter

        const val FIREBASE_CONNECTION_MESSAGE = "com.eit.enhancedconsultant.CONNECTED_TO_FIREBASE"
        const val APP_READY_MESSAGE = "com.eit.enhancedconsultant.READY"
        const val SIGN_IN_FRAGMENT = "sign_in_fragment"
        const val ASSIGN_TASKS_FRAGMENT = "assign_tasks_fragment"
        const val TASK_CHECKER_FRAGMENT = "check_tasks_fragment"
        const val NEW_ACCOUNT_FRAGMENT = "new_account_fragment"
        const val HOME_FRAGMENT = "home_fragment"

        // Sign in levels
        const val ADMIN = 1
        const val USER = 2

        lateinit var currentUser: Employee
        var taskList = mutableListOf<TaskModel>()
        //val taskList = mutableListOf<CompleteTask>()

        var refreshRequested: Boolean = false

    }// end of companion object

    /**
     * Main entry point
     */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        if(supportActionBar != null)
        {
            supportActionBar!!.hide()
        } // end of if block

        setContentView(R.layout.activity_main)

        // Bind the activity with the presenter
        bindPresenter()

        loadSignInFragment()
    }// end of function onCreate

    /**
     * Accept a task that was assigned to a user
     *
     * @param taskId The Id of task to be completed
     * @param email The email address of the person that should complete the task
     */
    override fun acceptTask(taskId: String, email: String)
    {
        presenter.acceptAssignedTask(taskId, email)
    }// nd of function acceptTask

    /**
     * Decline a task that was assigned to a user
     *
     * @param taskId The Id of task to be completed
     * @param email The email address of the person that should complete the task
     */
    override fun declineTask(taskId: String, email: String)
    {
        presenter.declineAssignedTask(taskId, email)
    }// end of function declineTask

    /**
     * Complete a task that was assigned to a user
     *
     * @param taskId The Id of task to be completed
     * @param email The email address of the person that should complete the task
     */
    override fun completeTask(taskId: String, email: String)
    {
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
                            assignor: String, dateAssigned: String, dueDate: String,
                            taskId: String)
    {
        presenter.assignUserTask(taskName, employeeEmail, taskDescription,
            assignor, dateAssigned, dueDate, taskId)
    }// end of function assignTask

    /**
     * Bind the @see <Presenter> to the @see <MainActivity>
     */
    override fun bindPresenter()
    {
        presenter = Presenter()
        presenter.onBindView(this@MainActivity)
    }// end of function bindPresenter

    /**
     * Determines whether or not the user entered valid data
     * @param email  A valid user name known to the network
     * @param password  A valid password matching the user name
     */
    override fun isUserValid(email: String, password: String)
    {
        if(presenter.validateLoginInput(email, password))
        {
            presenter.loginToNetwork(email, password)

            Toast.makeText(this,
                "Welcome back ${currentUser.userFirstName}",
                Toast.LENGTH_LONG).show()

            presenter.addDummyTasks()

            // Display the fragment to the relevant user
            loadHomeFragment()
        }// end of if block
        else
        {
            Toast.makeText(this,
                "Your user credentials are invalid!",
                Toast.LENGTH_LONG).show()
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
                                email: String, telephone: Long,
                                userName: String, password: String,
                                confirmPass: String)
    {
        if(presenter.validateNewUser(firstName, lastName, email, password, confirmPass))
        {
            presenter.addNewUser(firstName, lastName, email, telephone,
                userName, password)
        }// end of if block
        else
        {
            Toast.makeText(this,
                "Your user credentials are invalid/incomplete!",
                Toast.LENGTH_LONG).show()
        }// end of else block
    }// end of function isNewUserValid

    /**
     * Loads the main fragment after successful sign in
     */
    override fun loadHomeFragment()
    {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.animator.enter_from_right,
                R.animator.exit_to_left,
                R.animator.enter_from_left,
                R.animator.exit_to_right)
            .replace(R.id.app_main_container_fl, homeFragment,
                HOME_FRAGMENT)
            .addToBackStack(null)
            .commit()
    }// end of function loadHomeFragment

    /**
     * Logs the current user out
     */
    override fun logoutUser()
    {
        presenter.logoutCurrentUser(currentUser.userEmail)
        loadSignInFragment()
    }// end of function logoutUser

    /**
     * Load the fragment for the new user account
     */
    override fun loadSetupFragment()
    {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.animator.enter_from_right,
                R.animator.exit_to_left,
                R.animator.enter_from_left,
                R.animator.exit_to_right)
            .replace(R.id.app_main_container_fl, userSetupFragment,
                NEW_ACCOUNT_FRAGMENT)
            .addToBackStack(null)
            .commit()
    }// end of function loadSetupFragment

    /**
     * Load the fragment for the user sign in
     */
    override fun loadSignInFragment()
    {
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
    override fun loadAssignTasksFragment()
    {
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
    override fun loadUserTasksFragment()
    {
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
    override fun refreshTasks()
    {
        TODO("Not yet implemented")
    }
}// end of class MainActivity