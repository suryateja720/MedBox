@file:Suppress("DEPRECATION")

package com.project.medbox.authentication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.*
import com.project.medbox.R
import com.project.medbox.databasegettersetter.NewUser
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {

    private var isGstinValid: Boolean = false
    private var isMobileVerified: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private var newUser: NewUser = object : NewUser() {}

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var verificationCode: String? = null

    private var registerType: RadioGroup? = null
    private var registerSelectedType: RadioButton? = null
    private var registerGstin: TextInputEditText? = null
    private var registerGstinContainer: TextInputLayout? = null
    private var agreeTermsCheckBox: MaterialCheckBox? = null
    private var privacyTerms: TextView? = null

    private var registerBusinessNameContainer: TextInputLayout? = null
    private var registerOwnerNameContainer: TextInputLayout? = null
    private var registerPasswordContainer: TextInputLayout? = null
    private var registerConfirmPasswordContainer: TextInputLayout? = null
    private var registerBusinessName: TextInputEditText? = null
    private var registerOwnerName: TextInputEditText? = null
    private var registerPassword: TextInputEditText? = null
    private var registerConfirmPassword: TextInputEditText? = null

    private var registerEmailIdContainer: TextInputLayout? = null
    private var registerMobileContainer: TextInputLayout? = null
    private var registerAddressContainer: TextInputLayout? = null
    private var registerEmailId: TextInputEditText? = null
    private var registerMobile: TextInputEditText? = null
    private var registerAddress: TextInputEditText? = null

    private var getStartedButton: Button? = null

    private var backToUserIdButton: Button? = null
    private var nextToContactsButton: Button? = null

    private var backToBasicDetailsButton: Button? = null
    private var doneButton: Button? = null

    private var userIdCardText: TextView? = null
    private var userIdLayout: RelativeLayout? = null
    private var userIdCardView: CardView? = null

    private var basicDetailsCardText: TextView? = null
    private var basicDetailsLayout: RelativeLayout? = null
    private var basicDetailsCardView: CardView? = null

    private var contactCardText: TextView? = null
    private var contactLayout: RelativeLayout? = null
    private var contactCardView: CardView? = null

    private var registerSendOtp: TextView? = null
    private var otpLayout: RelativeLayout? = null
    private var registerOtp: TextInputEditText? = null
    private var registerVerifyOtp: TextView? = null
    private var registerEditMobile: ImageView? = null
    private var registerOtpTimer: TextView? = null


    private var registerButton: Button? = null
    private var registerCardView: CardView? = null


    private var loginLink: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

// Find Views

        registerType = findViewById(R.id.register_type)
        registerGstin = findViewById(R.id.register_gstin_number)
        registerGstinContainer = findViewById(R.id.register_gstin_container)
        agreeTermsCheckBox = findViewById(R.id.gstin_check_box)
        privacyTerms = findViewById(R.id.privacy_policy_terms_conditions_link)

        registerBusinessName = findViewById(R.id.register_business_name)
        registerOwnerName = findViewById(R.id.register_owner_name)
        registerPassword = findViewById(R.id.register_password)
        registerConfirmPassword = findViewById(R.id.register_confirm_password)
        registerBusinessNameContainer = findViewById(R.id.register_business_name_container)
        registerOwnerNameContainer = findViewById(R.id.register_owner_name_container)
        registerPasswordContainer = findViewById(R.id.register_password_container)
        registerConfirmPasswordContainer = findViewById(R.id.register_confirm_password_container)

        registerEmailId = findViewById(R.id.register_email_id)
        registerMobile = findViewById(R.id.register_mobile)
        registerAddress = findViewById(R.id.register_address)
        registerEmailIdContainer = findViewById(R.id.register_email_id_container)
        registerMobileContainer = findViewById(R.id.register_mobile_container)
        registerAddressContainer = findViewById(R.id.register_address_container)

        userIdCardText = findViewById(R.id.register_user_id_gstin_card_text)
        userIdCardView = findViewById(R.id.register_user_id_gstin_card_view)
        userIdLayout = findViewById(R.id.register_user_id_gstin_layout)

        basicDetailsCardText = findViewById(R.id.basic_details_card_text)
        basicDetailsCardView = findViewById(R.id.basic_details_container)
        basicDetailsLayout = findViewById(R.id.basic_details_layout)

        contactCardView = findViewById(R.id.contact_container)
        contactLayout = findViewById(R.id.contact_card_layout)
        contactCardText = findViewById(R.id.contact_card_text)

        registerSendOtp = findViewById(R.id.register_send_otp)
        otpLayout = findViewById(R.id.register_otp_layout)
        registerOtp = findViewById(R.id.register_otp)
        registerEditMobile = findViewById(R.id.register_edit_mobile)
        registerVerifyOtp = findViewById(R.id.register_verify_otp)
        registerOtpTimer = findViewById(R.id.register_otp_timer)

        getStartedButton = findViewById(R.id.getting_started_button)
        backToUserIdButton = findViewById(R.id.back_to_user_id_button)
        nextToContactsButton = findViewById(R.id.next_to_contacts_button)
        backToBasicDetailsButton = findViewById(R.id.back_to_basic_details_button)
        doneButton = findViewById(R.id.done_button)

        registerButton = findViewById(R.id.proceed_to_register_button)
        registerCardView = findViewById(R.id.register_button_container)

        loginLink = findViewById(R.id.login_link)



        val checkImageGreen = this.resources.getDrawable(R.drawable.ic_check_teal_24dp)
        val checkImageWhite = this.resources.getDrawable(R.drawable.ic_check_white_24dp)
        checkImageGreen.setBounds(0, 0, 40, 40)
        checkImageWhite.setBounds(0, 0, 40, 40)


// Text Watchers

        registerGstin?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val validatePattern =
                    "[0-9][0-9][A-Z][A-Z][A-Z][A-Z][A-Z][0-9][0-9][0-9][0-9][A-Z][1-9A-Z]Z[0-9A-Z]"

                if (registerGstin?.length() == 0) {
                    registerGstin?.setCompoundDrawables(null, null, checkImageWhite, null)
                    registerGstinContainer?.error = "GSTIN cannot be empty"
                    isGstinValid = false
                } else if (!registerGstin?.text.toString().matches(validatePattern.toRegex())) {
                    registerGstin?.setCompoundDrawables(null, null, checkImageWhite, null)
                    registerGstinContainer?.error = "Please Enter a valid GSTIN"
                    isGstinValid = false
                } else {
                    registerGstin?.setCompoundDrawables(null, null, checkImageGreen, null)
                    registerGstinContainer?.error = null
                    isGstinValid = true
                }

            }

        })
        registerBusinessName?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (registerBusinessName?.length() == 0) {
                    registerBusinessNameContainer?.error = "Business Name cannot be empty"
                } else {
                    registerBusinessNameContainer?.error = null
                }
            }

        })
        registerOwnerName?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (registerOwnerName?.length() == 0) {
                    registerOwnerNameContainer?.error = "Business Name cannot be empty"
                } else {
                    registerOwnerNameContainer?.error = null
                }
            }

        })
        registerPassword?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (registerConfirmPassword?.text.toString() != registerPassword?.text.toString()) {
                    registerConfirmPasswordContainer?.error = "Password doesn't match"
                } else {

                    registerConfirmPasswordContainer?.error = null
                }
                if (registerPassword?.length()!! < 6) {
                    registerPasswordContainer?.error = "Password should have minimum 6 characters"
                } else {
                    registerPasswordContainer?.error = null
                }
            }

        })
        registerConfirmPassword?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (registerConfirmPassword?.text.toString() != registerPassword?.text.toString()) {
                    registerConfirmPasswordContainer?.error = "Password doesn't match"
                } else {
                    registerConfirmPasswordContainer?.error = null
                }
            }

        })
        registerMobile?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (registerMobile?.length() == 0) {
                    registerMobileContainer?.error = "Mobile number cannot be empty"
                } else {
                    registerMobileContainer?.error = null
                }

            }

        })
        registerAddress?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (registerAddress?.length() == 0) {
                    registerAddressContainer?.error = "Address cannot be empty"
                } else {
                    registerAddressContainer?.error = null
                }

            }

        })

// OnCLickListeners
        loginLink?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            this.finish()
        }
        getStartedButton?.setOnClickListener {

            registerSelectedType = findViewById(registerType!!.checkedRadioButtonId)
            val validationFlag = isGstinValid && agreeTermsCheckBox?.isChecked!!



            if (registerSelectedType?.text == null) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->

                }
                builder.setTitle("User Type")
                builder.setMessage("\nPlease select a user type!")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else if (!isGstinValid) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->

                }
                builder.setTitle("Invalid GSTIN")
                builder.setMessage("\nPlease enter a valid GSTIN")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else if (!agreeTermsCheckBox?.isChecked!!) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->

                }
                builder.setTitle("Terms and Conditions")
                builder.setMessage("\nPlease agree to our Terms & Conditions")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else if (validationFlag) {

                newUser.gstinNumber = registerGstin?.text?.toString()

                TransitionManager.beginDelayedTransition(
                    userIdCardView!!,
                    AutoTransition().setDuration(100)
                )
                userIdLayout?.visibility = View.GONE
                TransitionManager.beginDelayedTransition(
                    basicDetailsCardView!!,
                    AutoTransition().setDuration(200)
                )
                basicDetailsLayout?.visibility = View.VISIBLE

                Handler().postDelayed(
                    {
                        val img = this.resources.getDrawable(R.drawable.ic_check_white_24dp)
                        img.setBounds(0, 0, 40, 40)
                        userIdCardText?.setCompoundDrawables(null, null, img, null)
                    }, 500
                )

            }
        }
        backToUserIdButton?.setOnClickListener {

            userIdCardText?.setCompoundDrawables(null, null, null, null)

            TransitionManager.beginDelayedTransition(
                basicDetailsCardView!!,
                AutoTransition().setDuration(100)
            )
            basicDetailsLayout?.visibility = View.GONE

            TransitionManager.beginDelayedTransition(
                userIdCardView!!,
                AutoTransition().setDuration(200)
            )
            userIdLayout?.visibility = View.VISIBLE

        }
        nextToContactsButton?.setOnClickListener {
            if (registerBusinessName?.length() == 0) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->

                }
                builder.setTitle("Business Name")
                builder.setMessage("\nBusiness Name cannot be empty.")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else if (registerOwnerName?.length() == 0) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->

                }
                builder.setTitle("Owner Name")
                builder.setMessage("\nOwner Name cannot be empty.")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else if (registerPassword?.length()!! < 6) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->

                }
                builder.setTitle("Password")
                builder.setMessage("\nPassword should have minimum 6 characters.")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()

            } else if (registerConfirmPassword?.text.toString() != registerPassword?.text.toString()) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->

                }
                builder.setTitle("Password")
                builder.setMessage("\nPassword doesn't matches!")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else {

                newUser.businessName = registerBusinessName?.text.toString()
                newUser.ownerName = registerOwnerName?.text.toString()
                newUser.password = registerPassword?.text.toString()

                TransitionManager.beginDelayedTransition(
                    basicDetailsCardView!!,
                    AutoTransition().setDuration(100)
                )
                basicDetailsLayout?.visibility = View.GONE

                TransitionManager.beginDelayedTransition(
                    contactCardView!!,
                    AutoTransition().setDuration(200)
                )
                contactLayout?.visibility = View.VISIBLE

                Handler().postDelayed(
                    {
                        val img = this.resources.getDrawable(R.drawable.ic_check_white_24dp)
                        img.setBounds(0, 0, 40, 40)
                        basicDetailsCardText?.setCompoundDrawables(null, null, img, null)
                    }, 500
                )
            }


        }
        backToBasicDetailsButton?.setOnClickListener {

            basicDetailsCardText?.setCompoundDrawables(null, null, null, null)

            TransitionManager.beginDelayedTransition(
                contactCardView!!,
                AutoTransition().setDuration(50)
            )
            contactLayout?.visibility = View.GONE

            TransitionManager.beginDelayedTransition(
                basicDetailsCardView!!,
                AutoTransition().setDuration(200)
            )
            basicDetailsLayout?.visibility = View.VISIBLE

        }
        registerSendOtp?.setOnClickListener {


            if (registerMobile?.length() == 0) {
                registerMobileContainer?.error = "Please enter the mobile number"
            } else {
                val mobileNumber = "+91" + registerMobile?.text

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    mobileNumber,
                    120,
                    TimeUnit.SECONDS,
                    this,
                    object : OnVerificationStateChangedCallbacks() {

                        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                            // Do Nothing
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            if (e is FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Invalid Number",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (e is FirebaseTooManyRequestsException) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Please try with another number",
                                    Toast.LENGTH_LONG
                                ).show()
                                otpLayout?.visibility = View.GONE

                            }

                        }

                        override fun onCodeSent(
                            s: String,
                            forceResendingToken: ForceResendingToken
                        ) {
                            super.onCodeSent(s, forceResendingToken)

                            Toast.makeText(this@RegisterActivity, "OTP sent", Toast.LENGTH_LONG)
                                .show()

                            registerSendOtp?.text = getString(R.string.resend_otp)
                            registerEditMobile?.visibility = View.VISIBLE
                            countDownTimer?.cancel()

                            countDownTimer = object : CountDownTimer(120000, 1000) {
                                @SuppressLint("SetTextI18n")
                                override fun onTick(millisUntilFinished: Long) {
                                    registerOtpTimer?.text =
                                        String.format("%02d",(millisUntilFinished / 60000)) + ":" + String.format("%02d",((millisUntilFinished % 60000) / 1000))
                                }

                                override fun onFinish() {
                                    Toast.makeText(this@RegisterActivity,"OTP time out!",Toast.LENGTH_LONG).show()
                                    otpLayout?.visibility=View.GONE
                                }
                            }
                            countDownTimer?.start()

                            registerMobile?.isEnabled = false
                            registerMobileContainer?.error = null
                            otpLayout?.visibility = View.VISIBLE
                            verificationCode = s
                        }
                    }
                )


            }


        }
        registerVerifyOtp?.setOnClickListener {
            val otp = registerOtp?.text.toString()

            val credential =
                PhoneAuthProvider.getCredential(verificationCode!!, otp)
            verifyOTP(credential)
        }
        registerEditMobile?.setOnClickListener {
            otpLayout?.visibility = View.GONE

            registerSendOtp?.text = getString(R.string.send_otp)

            registerMobile?.isEnabled = true

            registerEditMobile?.visibility = View.GONE

        }
        doneButton?.setOnClickListener {

            if (registerMobile?.length() == 0) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->

                }
                builder.setTitle("Mobile Number")
                builder.setMessage("\nMobile Number is invalid!")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else if (!isMobileVerified) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->

                }
                builder.setTitle("Mobile Number Verification")
                builder.setMessage("\n Please verify your mobile number with otp.")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else if (registerAddress?.length() == 0) {
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->

                }
                builder.setTitle("Address")
                builder.setMessage("\nAddress cannot be empty")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else {


                newUser.emailId = registerEmailId?.text.toString()
                newUser.mobileNumber = registerMobile?.text.toString()
                newUser.address = registerAddress?.text.toString()

                TransitionManager.beginDelayedTransition(
                    contactCardView!!,
                    AutoTransition().setDuration(150)
                )
                contactLayout?.visibility = View.GONE

                Handler().postDelayed(
                    {
                        val img = this.resources.getDrawable(R.drawable.ic_check_white_24dp)
                        img.setBounds(0, 0, 40, 40)
                        contactCardText?.setCompoundDrawables(null, null, img, null)
                    }, 500
                )

                TransitionManager.beginDelayedTransition(
                    registerCardView!!,
                    AutoTransition().setDuration(500)
                )
                registerButton?.visibility = View.VISIBLE
            }


        }
        registerButton?.setOnClickListener {


            val progress = ProgressDialog(this)
            progress.setTitle("Please Wait !")
            progress.setMessage("Please wait while we are fetching the data!")
            progress.setCanceledOnTouchOutside(false)
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progress.show()

            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
            val childReference1 =
                databaseReference.child("Distributor").orderByChild("gstinNumber")
                    .equalTo(registerGstin?.text.toString())

            childReference1.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        progress.cancel()
                        promptUserExists()
                    } else {

                        val childReference2 =
                            databaseReference.child("Retailer").orderByChild("gstinNumber")
                                .equalTo(registerGstin?.text.toString())

                        childReference2.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    progress.cancel()
                                    promptUserExists()
                                } else {
                                    progress.cancel()
                                    proceedToRegister()
                                }

                            }

                        })
                    }

                }

            })
        }

    }

    private fun verifyOTP(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    registerOtpTimer?.text = ""

                    registerEditMobile?.visibility = View.GONE
                    otpLayout?.visibility = View.GONE
                    registerSendOtp?.visibility = View.GONE
                    val img = this.resources.getDrawable(R.drawable.ic_check_teal_24dp)
                    img.setBounds(0, 0, 40, 40)
                    registerMobile?.setCompoundDrawables(null, null, img, null)
                    isMobileVerified = true
                    Toast.makeText(
                        this@RegisterActivity,
                        "mobile number verified",
                        Toast.LENGTH_LONG
                    ).show()
                } else {

                    Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show()

                }
            }
    }
    private fun proceedToRegister() {

        val databaseReference =
            FirebaseDatabase.getInstance().reference.child(registerSelectedType?.text.toString())

        databaseReference.push().setValue(newUser)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        this.finish()

        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_LONG).show()
    }
    private fun promptUserExists() {
        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.med_box_icon)
        builder.setPositiveButton("ok") { _, _ ->

        }
        builder.setTitle("User Already Exist")
        builder.setMessage("\nUser with this GSTIN already exist")
        val loginTypeAlertDialog: AlertDialog = builder.create()
        loginTypeAlertDialog.setCancelable(false)
        loginTypeAlertDialog.show()

        userIdCardText?.setCompoundDrawables(null, null, null, null)

        TransitionManager.beginDelayedTransition(
            userIdCardView!!,
            AutoTransition().setDuration(200)
        )
        userIdLayout?.visibility = View.VISIBLE

        TransitionManager.beginDelayedTransition(
            registerCardView!!,
            AutoTransition().setDuration(100)
        )
        registerButton?.visibility = View.GONE

    }

}
