package com.eit.enhancedconsultant.view

import com.eit.enhancedconsultant.model.User

interface EnhancedView
{
    fun acceptTask(taskId: String, email: String)

    fun declineTask(taskId: String, email: String)

    fun completeTask(taskId: String, email: String)

    fun assignTask(taskName: String, employeeEmail: String, taskDescription: String,
                   assignor: String, dateAssigned: String, dueDate: String)

    fun bindPresenter()

    fun isUserValid(email: String, password: String)

    fun isNewUserValid(firstName: String, lastName: String,
                       email: String, telephone: String, userName: String,
                       password: String, confirmPass: String)

    fun loadHomeFragment()

    fun logoutUser()

    fun loadSetupFragment()

    fun loadSignInFragment()

    fun loadAssignTasksFragment()

    fun loadUserTasksFragment()

    fun refreshTasks()

    fun makeToast(message: String)

    fun logInUser(email: String, password: String)

    fun createUser(firstName: String, lastName: String, email: String, password: String)

    fun loadCurrentUser()
}