package com.eit.enhancedconsultant.presenter

import com.eit.enhancedconsultant.utils.DatabaseManager.acceptTask
import com.eit.enhancedconsultant.utils.DatabaseManager.addAssignedTask
import com.eit.enhancedconsultant.utils.DatabaseManager.addUser
import com.eit.enhancedconsultant.utils.DatabaseManager.completeTask
import com.eit.enhancedconsultant.utils.DatabaseManager.declineTask
import com.eit.enhancedconsultant.utils.DatabaseManager.logOutUser
import com.eit.enhancedconsultant.model.Employee
import com.eit.enhancedconsultant.utils.Utils
import com.eit.enhancedconsultant.view.EnhancedView
import com.eit.enhancedconsultant.view.MainActivity

class Presenter
{
    private var view: EnhancedView? = null

    /**
     * Binds this class with a view
     *
     * @param view  The view to be bound to
     */
    fun onBindView(view: EnhancedView)
    {
        this.view = view
    }// end of function onBindView

    /**
     * Allows the user to accept an assigned task
     *
     * @param employeeEmail The email address of the employee
     * @param taskId The id of the task
     */
    fun acceptAssignedTask(employeeEmail: String, taskId: String)
    {
        acceptTask(employeeEmail, taskId)
    }// end of function acceptAssignedTask


    /**
     * Adds a new user to the database
     *
     * @param firstName  The user's first name
     * @param lastName  The user's last name
     * @param email  A valid email address for the user
     * @param contactNumber  A contact number for the user (optional)
     * @param userName  A specified user name (optional)
     * @param password  A valid password matching the user name
     */
    fun addNewUser(firstName: String, lastName: String, email: String,
                   contactNumber: Long, userName: String, password: String)
    {
        // All table columns will accept strings as input
        val contactNumberString = Utils.formatTelephoneNumber(contactNumber.toString(),
        false)
        addUser(firstName, lastName, email, contactNumberString, userName, password)
    }// end of function addNewUser

    /**
     * Allows a task to be assigned to a specific user
     *
     * @param taskName The type of task to be completed
     * @param employeeEmail The email address of the person that should complete the task
     * @param taskDescription A breakdown of the task to be completed
     * @param assignor  The name of the person who assigns the task
     * @param dateAssigned  The date and time tht the task was assigned
     * @param dueDate   The date and time that the task must be completed
     */
    fun assignUserTask(taskName: String, employeeEmail: String, taskDescription: String,
                       assignor: String, dateAssigned: String, dueDate: String, taskId: String)
    {
        addAssignedTask(taskName, employeeEmail, taskDescription, assignor, dateAssigned,
            dueDate, taskId)
    }// end of function assignUserTask

    /**
     * Allows the user to decline an assigned task
     *
     * @param employeeEmail The email address of the employee
     * @param taskId The id of the task
     */
    fun declineAssignedTask(employeeEmail: String, taskId: String)
    {
        declineTask(employeeEmail, taskId)
    }// end of function declineAssignedTask

    /**
     * Allows the user to complete an assigned task
     *
     * @param employeeEmail The email address of the employee
     * @param taskId The id of the task
     */
    fun completeAssignedTask(employeeEmail: String, taskId: String)
    {
        completeTask(employeeEmail, taskId)
    }// end of function declineAssignedTask

    fun logoutCurrentUser(email: String)
    {
        logOutUser(email)
    }// end of function logoutCurrentUser

    fun addDummyTasks()
    {
        // Format -> Mon, Jan 1, 1969, 8:00 AM
//        val parser = SimpleDateFormat("E, MMM d, yyyy, h:mm a",
//            Locale.ENGLISH)
//
//        MainActivity.taskList.add(
//            TaskModel("C# Code Lab",
//                "Please complete the code lab on Google labs by 10:30 p.m. today.",
//                "Antonino",
//                parser.parse("Mon, Aug 10, 2020, 8:00 AM")!!)
//        )
//
//        MainActivity.taskList.add(
//            TaskModel("Firebase Code Lab",
//            "Please complete the code lab on Google labs by 2:30 p.m. today.",
//            "Antonino",
//                parser.parse("Mon, Aug 10, 2020, 8:15 AM")!!)
//        )
//
//        MainActivity.taskList.add(
//            TaskModel("Room Database Code Lab",
//            "Please complete the code lab on Google labs by 4:30 p.m. today.",
//            "Antonino",
//                parser.parse("Mon, Aug 10, 2020, 9:00 AM")!!)
//        )
    }// end of function addDummyTasks

    /**
     * Allows a valid user to sign in to the network
     *
     * @param userEmail  A valid email address known to the network
     * @param password  A valid password matching the user name
     */
    fun loginToNetwork(userEmail: String, password: String)
    {
        // Need data returned from the database to populate this variable
        MainActivity.currentUser = Employee("Paul", "Patterson",
            userEmail, "Rasta", MainActivity.ADMIN)
    }// end of function loginToNetwork

    /**
     * Verifies whether or not this is an authentic user
     *
     * @param userName  A valid user name known to the network
     * @param password  A valid password matching the user name
     * @return A @see <Boolean> value depending on the requirements
     */
    fun validateLoginInput(userName: String, password: String): Boolean
    {
        return userName.isNotEmpty() &&  password.isNotEmpty()
    }// end of function validateLoginInput

    /**
     * Verifies whether or not the credentials entered are acceptable
     *
     * @param firstName  The user's first name
     * @param lastName  The user's last name
     * @param email  A valid email address for the user
     * @param password  A valid password matching the user name
     * @param confirmPassword  A match to the supplied password
     * @return A @see <Boolean> value depending on the requirements
     */
    fun validateNewUser(firstName: String, lastName: String,
                        email: String, password: String,
                        confirmPassword: String): Boolean
    {
        // The only optional fields are telephone and userName
        if(firstName.isEmpty() || lastName.isEmpty() ||
            email.isEmpty()  ||  password.isEmpty() ||
            confirmPassword.isEmpty())
        {
            return false
        }// end of if block

        if(password != confirmPassword)
        {
            return false
        }// end of if block

        return true
    }// end of function validateNewUser

}// end of class Presenter
