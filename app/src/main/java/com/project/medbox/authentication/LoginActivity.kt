package com.project.medbox.authentication

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.project.medbox.R
import com.project.medbox.launcher.UserDashboardActivity

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {


    private var sharedPreferences: SharedPreferences? = null
    private var isUserExists: Boolean = false
    private var isPasswordMatches: Boolean = false
    private var temporaryPassword: String? = null
    private var userType: String? = null
    private var businessName: String? = null
    private var ownerName: String? = null
    private var userKey: String? = null

    private var forgotPasswordLink : TextView? = null
    private var registerLink: TextView? = null
    private var loginButton: Button? = null

    private var loginUserID: TextInputEditText? = null
    private var loginPassword: TextInputEditText? = null
    private var keepMeLoggedIn: MaterialCheckBox? = null

    private var loginUserIdProgress: ProgressBar? = null
    private var loginPasswordProgress: ProgressBar? = null

    private var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        databaseReference = FirebaseDatabase.getInstance().reference

// Find Views

        loginUserID = findViewById(R.id.log_in_user_id)
        loginPassword = findViewById(R.id.log_in_password)
        loginUserIdProgress = findViewById(R.id.log_in_user_id_progress)
        loginPasswordProgress = findViewById(R.id.log_in_password_progress)
        keepMeLoggedIn = findViewById(R.id.log_in_keep_me_logged_in)
        loginButton = findViewById(R.id.log_in_button)
        registerLink = findViewById(R.id.log_in_new_user_register_link)
        forgotPasswordLink = findViewById(R.id.log_in_forgot_password)

// Shared Preferences

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
        if (sharedPreferences?.getBoolean("logged", false)!!) {
            val intent = Intent(this, UserDashboardActivity::class.java)
            intent.putExtra("userType", sharedPreferences?.getString("userType", ""))
            intent.putExtra("gstinNumber", sharedPreferences?.getString("gstinNumber", ""))
            intent.putExtra("businessName", sharedPreferences?.getString("businessName", ""))
            intent.putExtra("ownerName", sharedPreferences?.getString("ownerName", ""))
            intent.putExtra("userKey",sharedPreferences?.getString("userKey",""))
            startActivity(intent)
            this.finish()
        }


// Text Watchers

        loginUserID?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                loginUserID?.setCompoundDrawables(null, null, null, null)
                loginPassword?.setCompoundDrawables(null, null, null, null)
                loginUserIdProgress?.visibility = View.VISIBLE

                if (loginUserID?.length() == 0) {

                    loginUserID?.error = "empty"


                } else {
                    isPasswordMatches = false
                    isUserExists = false
                    temporaryPassword = null


                    val childReference1 =
                        databaseReference?.child("Distributor")?.orderByChild("gstinNumber")
                            ?.equalTo(loginUserID?.text.toString())

                    childReference1?.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (ds in dataSnapshot.children) {
                                    if (ds.child("gstinNumber")
                                            .getValue(String::class.java) == loginUserID?.text.toString()
                                    ) {

                                        val dbPassword =
                                            ds.child("password").getValue(String::class.java)

                                        userType = "Distributor"
                                        businessName =
                                            ds.child("businessName").getValue(String::class.java)
                                        ownerName =
                                            ds.child("ownerName").getValue(String::class.java)
                                        userKey =
                                            ds.key

                                        promptUserExists(dbPassword)
                                    }
                                }
                            } else {

                                val childReference2 =
                                    databaseReference?.child("Retailer")?.orderByChild("gstinNumber")
                                        ?.equalTo(loginUserID?.text.toString())

                                childReference2?.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                    }

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (ds in dataSnapshot.children) {
                                                if (ds.child("gstinNumber")
                                                        .getValue(String::class.java) == loginUserID?.text.toString()
                                                ) {

                                                    val dbPassword = ds.child("password")
                                                        .getValue(String::class.java)
                                                    userType = "Retailer"
                                                    businessName =
                                                        ds.child("businessName")
                                                            .getValue(String::class.java)
                                                    ownerName =
                                                        ds.child("ownerName")
                                                            .getValue(String::class.java)
                                                    userKey = ds.key
                                                    promptUserExists(dbPassword)
                                                }
                                            }
                                        } else {
                                            loginUserIdProgress?.visibility = View.GONE
                                        }

                                    }

                                })
                            }

                        }

                    })
                }
            }
        })
        loginPassword?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isPasswordMatches = false
                loginPassword?.setCompoundDrawables(null, null, null, null)
                loginPasswordProgress?.visibility = View.VISIBLE

                if (isUserExists) {
                    if (loginPassword?.text.toString() == temporaryPassword) {
                        promptPasswordMatches()
                    } else {
                        promptPasswordNotMatches()
                        loginPasswordProgress?.visibility = View.GONE
                    }
                } else {
                    loginPasswordProgress?.visibility = View.GONE
                }
            }

        })

// OnClickListeners

        registerLink?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            this.finish()
        }
        forgotPasswordLink?.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            this.finish()
        }
        loginButton?.setOnClickListener {
            if (loginPasswordProgress?.isVisible!! || loginUserIdProgress?.isVisible!!) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->
                }
                builder.setTitle("Wait!")
                builder.setMessage("\nPlease wait until we verify your userID and password")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else if (isUserExists && isPasswordMatches) {
                if (keepMeLoggedIn?.isChecked!!) {
                    sharedPreferences?.edit()?.putBoolean("logged", true)?.apply()
                    sharedPreferences?.edit()?.putString("userType", userType)?.apply()
                    sharedPreferences?.edit()
                        ?.putString("gstinNumber", loginUserID?.text.toString())?.apply()
                    sharedPreferences?.edit()
                        ?.putString("businessName", businessName)?.apply()
                    sharedPreferences?.edit()
                        ?.putString("ownerName", ownerName)?.apply()
                    sharedPreferences?.edit()
                        ?.putString("userKey", userKey)?.apply()
                }
                val intent = Intent(this, UserDashboardActivity::class.java)
                intent.putExtra("userType", userType)
                intent.putExtra("businessName", businessName)
                intent.putExtra("ownerName", ownerName)
                intent.putExtra("gstinNumber", loginUserID?.text.toString())
                intent.putExtra("userKey",userKey)
                startActivity(intent)
                this.finish()
            } else if (loginUserID?.length() == 0) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)

                builder.setPositiveButton("ok") { _, _ ->
                }
                builder.setTitle("User ID")
                builder.setMessage("\n UserID cannot be empty")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else if (!isUserExists) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("REGISTER") { _, _ ->
                    startActivity(Intent(this, RegisterActivity::class.java))
                    this.finish()
                }
                builder.setNegativeButton("CANCEL") { _, _ ->
                }
                builder.setTitle("User ID")
                builder.setMessage("\n User ID with this GSTIN number is not register yet. Do you want to get register with this GSTIN number")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else if (!isPasswordMatches) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->
                }
                builder.setTitle("Password")
                builder.setMessage("\n Incorrect Password!")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            }

        }

    }

    private fun promptPasswordNotMatches() {
        val img = this.resources.getDrawable(R.drawable.ic_error_outline_red_24dp)
        img.setBounds(0, 0, 40, 40)
        loginPasswordProgress?.visibility = View.GONE
        loginPassword?.setCompoundDrawables(null, null, img, null)
    }

    private fun promptPasswordMatches() {
        isPasswordMatches = true
        val img = this.resources.getDrawable(R.drawable.ic_check_teal_24dp)
        img.setBounds(0, 0, 40, 40)
        loginPasswordProgress?.visibility = View.GONE
        loginPassword?.setCompoundDrawables(null, null, img, null)
    }

    private fun promptUserExists(dbPassword: String?) {
        isUserExists = true
        temporaryPassword = dbPassword
        if (loginPassword?.text.toString() == temporaryPassword) {
            promptPasswordMatches()
        } else {
            promptPasswordNotMatches()
            loginPasswordProgress?.visibility = View.GONE
        }
        val img = this.resources.getDrawable(R.drawable.ic_check_teal_24dp)
        img.setBounds(0, 0, 40, 40)
        loginUserIdProgress?.visibility = View.GONE
        loginUserID?.setCompoundDrawables(null, null, img, null)
    }

}
