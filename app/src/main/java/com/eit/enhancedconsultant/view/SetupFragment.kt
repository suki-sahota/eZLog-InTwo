package com.eit.enhancedconsultant.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.eit.enhancedconsultant.R
import com.google.android.material.textfield.TextInputLayout

class SetupFragment : Fragment()
{
    private lateinit var tilFirstName: TextInputLayout
    private lateinit var tilLastName: TextInputLayout
    private lateinit var tilEmailAddress: TextInputLayout
    private lateinit var tilContactNumber: TextInputLayout
    private lateinit var tilUserName: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var btnCreateUser: Button
    private lateinit var listener: EnhancedView

    companion object{
        fun newInstance(): SetupFragment
        {
            return SetupFragment()
        }// end of function newInstance
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)

        val viewFragment = inflater.inflate(
            R.layout.fragment_setup_account, container,
            false)

        tilFirstName = viewFragment.findViewById(R.id.first_name_til)
        tilLastName = viewFragment.findViewById(R.id.last_name_til)
        tilEmailAddress = viewFragment.findViewById(R.id.email_til)
        tilContactNumber = viewFragment.findViewById(R.id.user_name_til)
        tilUserName = viewFragment.findViewById(R.id.user_name_til)
        tilPassword = viewFragment.findViewById(R.id.user_password_til)
        tilConfirmPassword = viewFragment.findViewById(R.id.user_confirm_password_til)
        btnCreateUser = viewFragment.findViewById(R.id.create_user_btn)

        btnCreateUser.setOnClickListener{
            listener.isNewUserValid(
                tilFirstName.editText?.text.toString(),
                tilLastName.editText?.text.toString(),
                tilEmailAddress.editText?.text.toString(),
                tilContactNumber.editText?.text.toString(),
                tilUserName.editText?.text.toString(),
                tilPassword.editText?.text.toString(),
                tilConfirmPassword.editText?.text.toString())
        }

        return viewFragment
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
}// end of class SetupFragment