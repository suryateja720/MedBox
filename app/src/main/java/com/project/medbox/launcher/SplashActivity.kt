package com.project.medbox.launcher

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.project.medbox.R
import com.project.medbox.authentication.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            if (isOnline()) {
                startActivity(Intent(this, LoginActivity::class.java))
                this.finish()
            } else {
                startActivity(Intent(this,
                    NoInternetAlertActivity::class.java))
                this.finish()
            }
        },300)
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return (netInfo != null && netInfo.isConnectedOrConnecting)
    }
}
