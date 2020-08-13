package com.eit.enhancedconsultant.view

interface EnhancedView
{
    fun acceptTask(taskId: String, email: String)

    fun declineTask(taskId: String, email: String)

    fun completeTask(taskId: String, email: String)

    fun assignTask(taskName: String, employeeEmail: String, taskDescription: String,
                   assignor: String, dateAssigned: String, dueDate: String,
                   taskId: String)

    fun bindPresenter()

    fun isUserValid(email: String, password: String)

    fun isNewUserValid(firstName: String, lastName: String,
                       email: String, telephone: Long, userName: String,
                       password: String, confirmPass: String)

    fun loadHomeFragment()

    fun logoutUser()

    fun loadSetupFragment()

    fun loadSignInFragment()

    fun loadAssignTasksFragment()

    fun loadUserTasksFragment()

    fun refreshTasks()
}