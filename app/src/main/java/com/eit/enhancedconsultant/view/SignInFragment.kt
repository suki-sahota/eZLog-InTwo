package com.eit.enhancedconsultant.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.eit.enhancedconsultant.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout

class SignInFragment : Fragment()
{
    private lateinit var tilUserName: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var btnLoginUser: Button
    private lateinit var fabNewAccount: FloatingActionButton
    private lateinit var listener: EnhancedView

    companion object{
        fun newInstance(): SignInFragment {
            return SignInFragment()
        }// end of function newInstance
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val viewFragment = inflater.inflate(
            R.layout.fragment_sign_in, container,
            false)
        tilUserName = viewFragment.findViewById(R.id.user_name_til)
        tilPassword = viewFragment.findViewById(R.id.user_password_til)
        btnLoginUser = viewFragment.findViewById(R.id.sign_in_btn)
        fabNewAccount = viewFragment.findViewById(R.id.create_new_account_fab)

        btnLoginUser.setOnClickListener{
            listener.isUserValid(tilUserName.editText?.text.toString(),
                tilPassword.editText?.text.toString())
        }

        fabNewAccount.setOnClickListener{
            listener.loadSetupFragment()
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
}// end of class SignInFragment