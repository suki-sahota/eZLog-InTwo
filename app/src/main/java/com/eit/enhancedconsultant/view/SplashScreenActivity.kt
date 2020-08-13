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
import java.util.*

@Suppress("DEPRECATION")
class SplashScreenActivity : Activity()
{
    private val appBroadcastReceiver: BroadcastReceiver = AppBroadcastReceiver()
    private val systemBroadcastReceiver: BroadcastReceiver = SystemBroadcastReceiver()
    private lateinit var loadingImage: ImageView
    private lateinit var txvNoInternet: TextView
    private var connectedToInternet = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_activity)

        // Register broadcast receivers
        val appBroadcastFilter = IntentFilter()
        appBroadcastFilter.addAction(MainActivity.FIREBASE_CONNECTION_MESSAGE)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            appBroadcastReceiver, appBroadcastFilter)

        val systemFilter = IntentFilter()
        systemFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        this.registerReceiver(systemBroadcastReceiver, systemFilter)

        loadingImage = findViewById(R.id.setup_loader_imv)
        txvNoInternet = findViewById(R.id.no_internet_txv)

        startLoading()
        connectedToInternet =
            hasNetworkAccess(this@SplashScreenActivity)



        // Show the splash screen for at least three seconds
        val task: TimerTask = object: TimerTask(){
            override fun run()
            {
                // If an internet connection is present
                if(connectedToInternet)
                {
                    // Authenticate with firebase
                    getFirebaseConnection(enhanceContext)
                }// end of if block
                else
                {
                    noInternet()
                }// end of else block
            }// end of function run
        }// end of task timer object

        val opening = Timer()
        opening.schedule(task, 3000)
    }// end of function onCreate

    private fun noInternet()
    {
        stopLoading()
        loadingImage.setImageResource(R.drawable.ec_no_internet)
        txvNoInternet.visibility = View.VISIBLE
        Toast.makeText(this, "Check your internet connection!"
                ,Toast.LENGTH_LONG).show()
    }// end of function noInternet

    /***
     * Initiates the loading animation
     */
    private fun startLoading()
    {
        val rotateAnim = AnimationUtils.loadAnimation(
            this@SplashScreenActivity, R.anim.rotate)

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

    override fun onPause()
    {
        super.onPause()
        overridePendingTransition(android.R.anim.fade_in,
            android.R.anim.fade_out)
    }

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
                if(isConnected)
                {
                    stopLoading()
                    finish()
                    startMainActivity()
                }// end of if block
                else
                {
                    txvNoInternet.text = getString(
                        R.string.server_connect_message)
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
                connectedToInternet = true
                startLoading()
            }// end of if block
            else
            {
                connectedToInternet = false
                noInternet()
            }// end of else block
        }// end of method onReceive
    }// end of class SystemBroadcastReceiver
}// end of class SplashScreenActivity