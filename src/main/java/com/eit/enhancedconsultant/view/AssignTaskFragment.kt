package com.eit.enhancedconsultant.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.eit.enhancedconsultant.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AssignTaskFragment : Fragment()
{
    private lateinit var listener: EnhancedView

    private lateinit var mStartDate: String
    private lateinit var mDueDate: String
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    private val formatterWithTimeZone = SimpleDateFormat("E, MMM dd, yyyy h:mm a zzz",
        Locale.ENGLISH)
    private val formatterNoTimeZone = SimpleDateFormat("E, MMM dd, yyyy h:mm a",
        Locale.ENGLISH)

    private val shortFormatterDate = SimpleDateFormat("yyyy-MM-dd",
        Locale.ENGLISH)

    private val shortFormatterWeekDay = SimpleDateFormat("E, MMM dd",
        Locale.ENGLISH)

    companion object
    {
        private lateinit var fragmentView: View
        private lateinit var tilEmail: TextInputLayout
        private lateinit var tilTaskTitle: TextInputLayout
        private lateinit var edtTaskDescription: EditText
        private lateinit var txvDueDate: TextView
        private lateinit var txvTaskId: TextView
        private lateinit var btnSetDueDate: Button
        private lateinit var btnAssignTask: Button


        private lateinit var taskId: String

        fun newInstance(): AssignTaskFragment
        {
            return AssignTaskFragment()
        }// end of function newInstance
    }// end of companion object

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)

        auth = Firebase.auth // Lately initialize Firebase Auth...
        user = auth.currentUser
        taskId = "id${SimpleDateFormat("HHmmssSSS", Locale.ENGLISH).format(Date())}"

        fragmentView = inflater.inflate(
            R.layout.fragment_assign_task_layout, container,
            false)

        tilEmail = fragmentView.findViewById(R.id.employee_email_til)
        tilTaskTitle = fragmentView.findViewById(R.id.task_title_til)
        edtTaskDescription = fragmentView.findViewById(R.id.task_description_txv)
        txvTaskId = fragmentView.findViewById(R.id.task_id_txv)
        txvDueDate = fragmentView.findViewById(R.id.due_date_txv)
        btnSetDueDate = fragmentView.findViewById(R.id.set_due_date_btn)
        btnAssignTask = fragmentView.findViewById(R.id.assign_task_btn)

        edtTaskDescription.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus)
            {
                edtTaskDescription.background = fragmentView.resources.getDrawable(
                    R.drawable.ud_edit_text_rounded_company_bg, null)
            }// end of if block
            else
            {
                edtTaskDescription.background = fragmentView.resources.getDrawable(
                    R.drawable.ud_edit_text_rounded_un_focused_bg, null)
            }// end of else block
        }

        btnSetDueDate.setOnClickListener{
            showDateTimeDialog(fragmentView.context)
        }

        btnAssignTask.setOnClickListener {

            if(validateTask())
            {
                val assignor = "${MainActivity.currentUser?.userFirstName} " +
                        "${MainActivity.currentUser?.userFirstName}"
                listener.assignTask(
                    tilTaskTitle.editText.toString(),
                    tilEmail.editText.toString(),
                    edtTaskDescription.text.toString(),
                    assignor, mStartDate, mDueDate, taskId)

                Toast.makeText(fragmentView.context,
                    "Task assigned to ${tilEmail.editText?.text}",
                    Toast.LENGTH_LONG).show()

                tilEmail.editText?.setText("")
                tilTaskTitle.editText?.setText("")
                edtTaskDescription.setText("")
                txvDueDate.text = formatterWithTimeZone.format(Date())

                tilEmail.editText?.requestFocus()
            }// end of if block
        }

        val now = Calendar.getInstance()

        // Next two hours
        now.add(Calendar.HOUR, 2)

        txvTaskId.text = "Task Id: $taskId"
        // Write the initial due date
        txvDueDate.text = formatterWithTimeZone.format(now.time)

        return fragmentView
    }// end of function onCreate

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        listener = context as EnhancedView
    }// end of function onAttach

    /**
     * Show a custom date time picker
     *
     * @param c The context of the current fragment
     */
    private fun showDateTimeDialog(c: Context)
    {
        val messageDialogView =
            View.inflate(c, R.layout.date_time_picker, null)
        val messageDialog: android.app.AlertDialog = android.app.AlertDialog.Builder(c).create()

        val hourOfDay: NumberPicker = messageDialogView.findViewById(R.id.hour_pick_txv)

        val d = Calendar.getInstance()

        hourOfDay.apply {
            minValue = 1
            maxValue = 12
            value = d.get(Calendar.HOUR)
        }

        val minuteOfDay: NumberPicker = messageDialogView.findViewById(R.id.minute_pick_txv)

        minuteOfDay.apply {
            minValue = 0
            maxValue = 59
            value = d.get(Calendar.MINUTE)

            // Format the digits
            setFormatter{
                    i -> String.format("%02d", i)
            }
        }

        val meridiem: NumberPicker =
            messageDialogView.findViewById(R.id.time_meridiem_txv)
        var mer = 0

        if (d.get(Calendar.AM_PM) == Calendar.PM)
        {
            mer = 1
        }// end of if block

        val meridiemArray = arrayOf("AM", "PM")

        meridiem.apply {
            minValue = 0
            maxValue = 1
            displayedValues = meridiemArray
            value = mer
        }

        val aCalendar = Calendar.getInstance()

        // Add -1 month to current month
        aCalendar.add(Calendar.MONTH, -1)

        // Set actual maximum date of previous month
        aCalendar[Calendar.DATE] = aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val lastDateOfPreviousMonth = shortFormatterDate.format(aCalendar.time)

        // Add 2 months to last month for next month
        aCalendar.add(Calendar.MONTH, 2)

        // Set actual maximum date of next month
        aCalendar[Calendar.DATE] = aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val endOfNextMonth = shortFormatterDate.format(aCalendar.time)

        val dates = loadDates(lastDateOfPreviousMonth, endOfNextMonth)
        val today: String = shortFormatterWeekDay.format(d.time)
        var todayIndex = 0

        // Check the array to see it it contains today's date and get its index
        // in the array
        if(Arrays.stream(dates).anyMatch { t -> t == today })
        {
            todayIndex = dates.indexOf(today)
        }// end of if block

        val datesPicker: NumberPicker =
            messageDialogView.findViewById(R.id.day_pick_txv)

        datesPicker.apply{
            minValue = 0
            maxValue = dates.size - 1
            displayedValues = dates
            value = todayIndex
        }

        val btnSetDateTime: Button = messageDialogView.findViewById(R.id.set_date_time_txv)

        btnSetDateTime.setOnClickListener {
            // Format the date the user selected
            val dateSelected = "${dates[datesPicker.value]}, ${d.get(Calendar.YEAR)} " +
                    "${hourOfDay.value}: ${minuteOfDay.value} ${meridiemArray[meridiem.value]}"
            val dd =  formatterNoTimeZone.parse(dateSelected)

            mDueDate = formatterWithTimeZone.format(dd!!)
            mStartDate = formatterWithTimeZone.format(Date())

            val assignedDate = "Due by: $mDueDate"
            txvDueDate.text = assignedDate
            messageDialog.dismiss()
        }

        val btnCancelDateTime: Button = messageDialogView.findViewById(R.id.cancel_date_time_btn)

        btnCancelDateTime.setOnClickListener{
            messageDialog.dismiss()
        }

        messageDialog.setView(messageDialogView)
        messageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        messageDialog.setCancelable(false)
        messageDialog.show()
    } // end of method showMessageDialog

    /**
     * Loads the dates that the date picker will contain usually a
     * two months span
     *
     * @param firstDate The starting date
     * @param secondDate The ending date
     */
    private fun loadDates(firstDate: String, secondDate: String): Array<String>
    {
        var start: Date = shortFormatterDate.parse(firstDate)!!
        val end: Date = shortFormatterDate.parse(secondDate)!!
        val totalDates: MutableList<String> = ArrayList()
        val c = Calendar.getInstance()
        c.time = start

        while (start.before(end))
        {
            totalDates.add(shortFormatterWeekDay.format(start))
            c.add(Calendar.DATE, 1)
            start = c.time
        }// end of while loop

        return totalDates.toTypedArray()
    }// end of function loadDates

    /**
     * Ensures that a valid task is being assigned
     *
     * @return A boolean vale of true/false
     */
    private fun validateTask() : Boolean
    {
        if(tilEmail.editText?.text?.isEmpty()!! &&
            tilTaskTitle.editText?.text?.isEmpty()!! &&
          edtTaskDescription.text.isEmpty())
        {
            Toast.makeText(fragmentView.context, "The email address of" +
                    " the person being assigned the task is required!",
                Toast.LENGTH_SHORT).show()


            return false
        }// end of if block

        if(tilEmail.editText?.text?.isEmpty()!!)
        {
            Toast.makeText(fragmentView.context, "The email address of" +
                    " the person being assigned the task is required!",
                Toast.LENGTH_SHORT).show()

            tilEmail.editText?.requestFocus()
            return false
        }// end of if block

        if(tilTaskTitle.editText?.text?.isEmpty()!!)
        {
            Toast.makeText(fragmentView.context, "The title of" +
                    " the task is required!",
                Toast.LENGTH_SHORT).show()

            tilTaskTitle.editText?.requestFocus()

            return false
        }// end of if block

        if(edtTaskDescription.text?.isEmpty()!!)
        {
            Toast.makeText(fragmentView.context, "A description of" +
                    " the task is required!",
                Toast.LENGTH_SHORT).show()

            edtTaskDescription.requestFocus()
            return false
        }// end of if block

        return true
    }// end of function validateTask()
}// end of class class AssignTaskFragment
