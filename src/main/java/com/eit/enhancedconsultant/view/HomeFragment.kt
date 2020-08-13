package com.eit.enhancedconsultant.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.eit.enhancedconsultant.R
import com.eit.enhancedconsultant.utils.Utils
import com.eit.enhancedconsultant.utils.Utils.getTimeOfDay

class HomeFragment : Fragment()
{
    private lateinit var txvTimeOfDayGreeting: TextView
    private lateinit var txvCurrentUserName: TextView
    private lateinit var btnLogoutUser: Button
    private lateinit var crvTasks: CardView
    private lateinit var listener: EnhancedView

    companion object{
        fun newInstance(): HomeFragment
        {
            return HomeFragment()
        }// end of function newInstance
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)

        val viewFragment = inflater.inflate(
            R.layout.fragment_welcome_layout, container,
            false)

        txvTimeOfDayGreeting = viewFragment.findViewById(R.id.time_of_day_greeting_txv)
        txvCurrentUserName = viewFragment.findViewById(R.id.user_name_txv)
        btnLogoutUser = viewFragment.findViewById(R.id.logout_btn)
        crvTasks = viewFragment.findViewById(R.id.home_task_card_item)

        btnLogoutUser.setOnClickListener{
            listener.logoutUser()
        }

        crvTasks.setOnClickListener{

            if(MainActivity.currentUser?.employeeLevel ==
                MainActivity.ADMIN)
            {
                listener.loadAssignTasksFragment()
            }//end of if block
            else
            {
                listener.loadUserTasksFragment()
            }// end of else block
        }

        when(getTimeOfDay())
        {
            Utils.TimeOfDay.MORNING->{
                txvTimeOfDayGreeting.text = getString(R.string.morning_greeting)
            }
            Utils.TimeOfDay.AFTERNOON->{
                txvTimeOfDayGreeting.text = getString(R.string.afternoon_greeting)
            }
            Utils.TimeOfDay.EVENING->{
                txvTimeOfDayGreeting.text = getString(R.string.evening_greeting)
            }
            else->{
                txvTimeOfDayGreeting.text = getString(R.string.night_greeting)
            }
        }// end of when block

        val userFullName = "${MainActivity.currentUser?.userFirstName}" +
                " ${MainActivity.currentUser?.userLastName}"

        txvCurrentUserName.text = userFullName

        return viewFragment
    }// end of function onCreate

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        listener = context as EnhancedView
    }
}// end of class SignInFragment