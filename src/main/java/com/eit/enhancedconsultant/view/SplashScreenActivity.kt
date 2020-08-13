package com.eit.enhancedconsultant.view

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.eit.enhancedconsultant.utils.DatabaseManager.getFirebaseConnection
import com.eit.enhancedconsultant.utils.DatabaseManager.isConnected
import com.eit.enhancedconsultant.R
import com.eit.enhancedconsultant.utils.NetworkHelper.hasNetworkAccess
import com.eit.enhancedconsultant.view.EnhanceITApp.Companion.enhanceContext

@Suppress("DEPRECATION")
class SplashScreenActivity : Activity()
{
    private val appBroadcastReceiver: BroadcastReceiver = AppBroadcastReceiver()
    private val systemBroadcastReceiver: BroadcastReceiver = SystemBroadcastReceiver()
    private lateinit var loadingImage: ImageView
    private lateinit var txvNoInternet: TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_activity)

        // Register broadcast receivers
        val appBroadcastFilter = IntentFilter()
        appBroadcastFilter.addAction(MainActivity.APP_READY_MESSAGE)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            appBroadcastReceiver, appBroadcastFilter)

        val systemFilter = IntentFilter()
        systemFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        this.registerReceiver(systemBroadcastReceiver, systemFilter)

        loadingImage = findViewById(R.id.setup_loader_imv)
        txvNoInternet = findViewById(R.id.no_internet_txv)

        startLoading()

        // If an internet connection is present
        if(hasNetworkAccess(this))
        {
            // Authenticate with firebase
            getFirebaseConnection(enhanceContext)
        }// end of if block
        else
        {
            stopLoading()
            loadingImage.setImageResource(R.drawable.ec_no_internet)
            txvNoInternet.visibility = View.VISIBLE
        }// end of else block
    }// end of function onCreate

    /***
     * Initiates the loading animation
     */
    private fun startLoading()
    {
        val rotateAnim = AnimationUtils.loadAnimation(this@SplashScreenActivity,
            R.anim.rotate)

        loadingImage.setImageResource(R.drawable.ec_loader)
        txvNoInternet.visibility = View.INVISIBLE

        loadingImage.animation = rotateAnim
    } // end of method startLoading

    /**
     * Launches the main activity
     */
    private fun startMainActivity()
    {
        startActivity(Intent(this@SplashScreenActivity,
            MainActivity::class.java))
    }// end of function startMainActivity

    /***
     * Terminates the loading animation
     */
    private fun stopLoading()
    {
        loadingImage.animation = null
    } // end of method stopLoading

    override fun onDestroy()
    {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@SplashScreenActivity)
            .unregisterReceiver(appBroadcastReceiver)
        unregisterReceiver(systemBroadcastReceiver)
    }// end of function onDestroy

    /***
     * Private class the listens for local app broadcasts
     */
    private inner class AppBroadcastReceiver : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            val action = intent.action

            if (MainActivity.FIREBASE_CONNECTION_MESSAGE == action)
            {
                stopLoading()

                if(isConnected)
                {
                    startMainActivity()
                    finish()
                }// end of if block
                else
                {
                    Toast.makeText(this@SplashScreenActivity,
                        "Unable to connect to database server!",
                        Toast.LENGTH_LONG).show()
                }// end of else block
            } // end of if block
        } // end of method onReceive
    } // end of class AppBroadcastReceiver

    /***
     * Private class the listens for broadcasts from the operating system
     * Android 10 and up device may need a different callback method
     */
    private inner class SystemBroadcastReceiver: BroadcastReceiver()
    {
        /**
         * {@inheritDoc}
         */
        override fun onReceive(context: Context, intent: Intent)
        {
            val connManager = this@SplashScreenActivity.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = connManager.activeNetworkInfo

            if (activeNetwork?.isConnectedOrConnecting != null)
            {
                stopLoading()
                startMainActivity()
                finish()
            }// end of if block
        }// end of method onReceive
    }// end of class SystemBroadcastReceiver
}// end of class SplashScreenActivity