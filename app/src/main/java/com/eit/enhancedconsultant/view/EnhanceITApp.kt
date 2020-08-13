package com.eit.enhancedconsultant.view

import android.app.Application
import android.content.Context

class EnhanceITApp : Application()
{
    companion object{
        lateinit var enhanceContext: Context
    }

    override fun onCreate()
    {
        super.onCreate()
        enhanceContext = this
    }// end of function onCreate

}// end of class EnhanceITApp