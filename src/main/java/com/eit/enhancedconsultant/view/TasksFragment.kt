package com.eit.enhancedconsultant.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eit.enhancedconsultant.R
import com.eit.enhancedconsultant.model.TaskModel

class TasksFragment : Fragment()
{
    private lateinit var listener: EnhancedView

    companion object
    {
        private lateinit var mAdapter: TaskAdapter
        private lateinit var fragmentView: View
        private var taskRecycler: RecyclerView? = null
        private var linearLayoutManager: LinearLayoutManager? = null

        private lateinit var txvTaskName: TextView
        private lateinit var txvTaskAssigned: TextView
        private lateinit var txvAssignedBy: TextView
        private lateinit var txvAssignedDate: TextView
        private lateinit var txvDueDate: TextView
        private lateinit var txvTaskId: TextView
        private lateinit var btnAcceptTask: Button
        private lateinit var btnDeclineTask: Button
        private lateinit var btnCompleteTask: Button

        var refreshLayout: SwipeRefreshLayout? = null

        fun newInstance(): TasksFragment
        {
            return TasksFragment()
        }// end of function newInstance

        fun displayData(dataSet: MutableList<TaskModel>)
        {
            mAdapter = TaskAdapter(dataSet)
            taskRecycler?.adapter = mAdapter

            // Dismiss the SwipeRefreshLayout indicator
            refreshLayout?.isRefreshing = false
        }// end of function displayData
    }// end of companion object

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)

        fragmentView = inflater.inflate(
            R.layout.fragment_assigned_tasks_layout, container,
            false)

        taskRecycler = fragmentView.findViewById(R.id.task_rv)
        refreshLayout = fragmentView.findViewById(R.id.tasks_swr)
        btnAcceptTask = fragmentView.findViewById(R.id.accept_task_btn)
        btnDeclineTask = fragmentView.findViewById(R.id.decline_task_btn)
        btnCompleteTask = fragmentView.findViewById(R.id.complete_task_btn)

        txvTaskName = fragmentView.findViewById(R.id.task_name_txv)
        txvTaskAssigned = fragmentView.findViewById(R.id.task_txv)
        txvAssignedBy = fragmentView.findViewById(R.id.assigned_by_txv)
        txvTaskId = fragmentView.findViewById(R.id.task_id_txv)
        txvAssignedDate = fragmentView.findViewById(R.id.assigned_date_txv)
        txvDueDate = fragmentView.findViewById(R.id.due_date_txv)

        linearLayoutManager = LinearLayoutManager(context)

        taskRecycler?.layoutManager = linearLayoutManager

        refreshLayout?.setOnRefreshListener {
            refreshTasks()
        }

        btnAcceptTask.setOnClickListener{
            listener.acceptTask(
                MainActivity.currentUser.userEmail,
                txvTaskId.text.toString())
        }

        btnDeclineTask.setOnClickListener{
            listener.declineTask(
                MainActivity.currentUser.userEmail,
                txvTaskId.text.toString())
        }

        btnCompleteTask.setOnClickListener{
            listener.completeTask(
                MainActivity.currentUser.userEmail,
                txvTaskId.text.toString())
        }

        displayData(MainActivity.taskList)

        return fragmentView
    }// end of function onCreate

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        listener = context as EnhancedView
}

    private fun refreshTasks()
    {
        MainActivity.refreshRequested = true
        listener.refreshTasks()
    }// end of function refreshPlaylist
}// end of class TasksFragment