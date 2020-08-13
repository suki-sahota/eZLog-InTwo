package com.eit.enhancedconsultant.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eit.enhancedconsultant.R
import com.eit.enhancedconsultant.model.TaskModel
import com.eit.enhancedconsultant.utils.Utils
import kotlinx.android.synthetic.main.fragment_assigned_tasks_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(private var tasks: MutableList<TaskModel>):
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>()
{
    private var mLayout: View? = null
    private var animMoveIn: Animation? = null
    private var animMoveOut: Animation? = null

    private lateinit var txvTaskName: TextView
    private lateinit var txvTaskDescription: TextView
    private lateinit var txvTaskAssignedBy: TextView
    private lateinit var txvTaskAssignedDate: TextView

    inner class TaskViewHolder(taskItem: View): RecyclerView.ViewHolder(taskItem)
    {
        fun bindTask(task: TaskModel)
        {
            val formatter = SimpleDateFormat("E, MMM d, yyyy, h:mm a",
                Locale.ENGLISH)

            txvTaskName.text = Utils.toProperCase(task.taskName)
            txvTaskDescription.text = task.taskDescription
            txvTaskAssignedBy.text = Utils.toProperCase(task.assignedBy)
            txvTaskAssignedDate.text = formatter.format(task.dateAssigned)
        }// end of function bindTask
    }// end of inner class TaskViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder
    {
        val taskView = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_assigned_tasks_item, parent, false)

        mLayout = taskView
        animMoveIn = AnimationUtils.loadAnimation(parent.context,
            R.anim.move_in
        )
        animMoveOut = AnimationUtils.loadAnimation(parent.context,
            R.anim.move_out
        )

        return TaskViewHolder(taskView)
    }// end of function onCreateViewHolder

    private fun animationFun(view: View)
    {
        val scaleXUpAnimator = ObjectAnimator.ofFloat(mLayout, View.SCALE_X,
            0f, 1f).apply{
            duration = 450
        }

        val scaleYUpAnimator = ObjectAnimator.ofFloat(mLayout, View.SCALE_Y,
            0f, 1f).apply{
            duration = 450

            addListener(object : AnimatorListenerAdapter()
            {
                override fun onAnimationEnd(animation: Animator)
                {
                    // Assign a colour which will contrast with the card view background
                    // color for them to appear visible
                    view.task_name_txv.setTextColor(Color.parseColor(Utils.visibleColour))
                    view.task_txv.setTextColor(Color.parseColor(Utils.visibleColour))
                    view.assigned_by_txv.setTextColor(Color.parseColor(Utils.visibleColour))

                    // Animate the text views
                    view.task_name_txv.startAnimation(animMoveIn)
                    view.task_txv.startAnimation(animMoveIn)
                    view.assigned_by_txv.startAnimation(animMoveIn)
                }
            })
        }

        val alphaAnimator = ObjectAnimator.ofFloat(mLayout, View.ALPHA,
            0f, 1f).apply{
            duration = 250
        }

        AnimatorSet().apply {
            play(scaleXUpAnimator).with(scaleYUpAnimator).with(alphaAnimator)
            start()
        }
    }// end of function animationFun

    override fun getItemCount(): Int
    {
        return tasks.size
    }// end of function

    override fun getItemId(position: Int): Long
    {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int
    {
        return position
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int)
    {
        txvTaskName = holder.itemView.findViewById(R.id.task_name_txv)
        txvTaskDescription = holder.itemView.findViewById(R.id.task_txv)
        txvTaskAssignedBy = holder.itemView.findViewById(R.id.assigned_by_txv)
        txvTaskAssignedDate = holder.itemView.findViewById(R.id.assigned_date_txv)

        holder.bindTask(tasks[position])

        holder.itemView.accept_task_btn.setOnClickListener{
            //MainActivity.acceptTask(position)
        }

        holder.itemView.decline_task_btn.setOnClickListener{
            //MainActivity.declineTask(position)
        }

        // If the bound view wasn't previously displayed on screen, it's animated
        if (!Utils.rowPositions.contains(position))
        {
            // Assign the text views the same colour as the card view back color
            // so that they appear invisible. Change the visibility will have
            // un-wanted behaviour of the recyclerview object
            holder.itemView.task_name_txv?.setTextColor(Color.parseColor(Utils.hiddenColour))
            holder.itemView.task_txv?.setTextColor(Color.parseColor(Utils.hiddenColour))
            holder.itemView.assigned_by_txv?.setTextColor(Color.parseColor(Utils.hiddenColour))

            Utils.rowPositions.add(position)
            animationFun(holder.itemView)
        }// end of if block
        else
        {
            // Assign a colour which will contrast with the card view background
            // color for them to appear visible
            holder.itemView.task_name_txv?.setTextColor(Color.parseColor(Utils.visibleColour))
            holder.itemView.task_txv?.setTextColor(Color.parseColor(Utils.visibleColour))
            holder.itemView.assigned_by_txv?.setTextColor(Color.parseColor(Utils.visibleColour))
        }// end of else block
    }// end of function onBindViewHolder
}// end of class TaskAdapter