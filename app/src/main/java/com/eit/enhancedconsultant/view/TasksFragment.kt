package com.eit.enhancedconsultant.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eit.enhancedconsultant.R
import com.eit.enhancedconsultant.model.TaskModel

class TasksFragment : Fragment()
{
    companion object
    {
        private lateinit var listener: EnhancedView
        private lateinit var mAdapter: TaskAdapter
        private lateinit var fragmentView: View
        private var taskRecycler: RecyclerView? = null
        private var linearLayoutManager: LinearLayoutManager? = null

        var refreshLayout: SwipeRefreshLayout? = null

        fun newInstance(): TasksFragment
        {
            return TasksFragment()
        }// end of function newInstance

        fun displayData(dataSet: MutableList<TaskModel>)
        {
            mAdapter = TaskAdapter(dataSet, listener)
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

        linearLayoutManager = LinearLayoutManager(context)

        taskRecycler?.layoutManager = linearLayoutManager

        refreshLayout?.setOnRefreshListener {
            refreshTasks()
        }

        displayData(MainActivity.taskList)

        return fragmentView
    }// end of function onCreate

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        listener = context as EnhancedView
    }

    override fun onDetach()
    {
        super.onDetach()
        activity?.supportFragmentManager?.popBackStackImmediate()
    }

    private fun refreshTasks()
    {
        MainActivity.refreshRequested = true
        listener.refreshTasks()
    }// end of function refreshPlaylist
}// end of class TasksFragment