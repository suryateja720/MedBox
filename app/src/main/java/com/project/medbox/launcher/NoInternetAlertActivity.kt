package com.project.medbox.launcher

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.project.medbox.R
import com.project.medbox.authentication.LoginActivity

class NoInternetAlertActivity : AppCompatActivity() {
    private var retryButton: Button? = null
    private var cancelButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet_alert)

        retryButton = findViewById(R.id.no_internet_alert_retry_button)
        cancelButton = findViewById(R.id.no_internet_alert_cancel_button)

        retryButton?.setOnClickListener {
            retryButton?.isClickable = false
            if (isOnline()) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, NoInternetAlertActivity::class.java))
                finish()
            }
        }
        cancelButton?.setOnClickListener {
            cancelButton?.isClickable = false
            this.finish()
        }

    }
    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return (netInfo != null && netInfo.isConnectedOrConnecting)
    }
}
