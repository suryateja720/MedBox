@file:Suppress("DEPRECATION")

package com.project.medbox.users.distributor.entries

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.medbox.R
import com.project.medbox.databasegettersetter.NewCompany

class NewCompanyActivity : AppCompatActivity() {

    var isCompanyExists = false
    var gstin: String? = null
    var userKey: String? = null

    private var companyName: TextInputEditText? = null
    private var companyRepresentativeName: TextInputEditText? = null
    private var companyEmailId: TextInputEditText? = null
    private var companyAddress: TextInputEditText? = null
    private var addCompanyButton: Button? = null

    private var newCompany: NewCompany = object : NewCompany() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "New Company"
        setContentView(R.layout.activity_new_company)

        gstin = intent.getStringExtra("gstin")
        userKey = intent.getStringExtra("userKey")

        companyName = findViewById(R.id.new_company_name)
        companyRepresentativeName = findViewById(R.id.new_company_representative_name)
        companyEmailId = findViewById(R.id.new_company_email_id)
        companyAddress = findViewById(R.id.new_company_address)
        addCompanyButton = findViewById(R.id.new_company_add_button)

        addCompanyButton?.setOnClickListener {
            val progress = ProgressDialog(this)
            progress.setTitle("Please Wait")
            progress.setMessage("Please wait while we fetch your data!!")
            progress.setCanceledOnTouchOutside(false)
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progress.show()

            if (companyName?.length() == 0) {
                progress.cancel()
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->
                }
                builder.setTitle("Company Name")
                builder.setMessage("\nCompany Name cannot be empty!")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()

            } else {
                val companyExistsDatabaseReference  = FirebaseDatabase.getInstance().reference
                    .child("Distributor").child(userKey!!).child("Company")
                val childKey = companyExistsDatabaseReference.orderByChild("companyName").equalTo(companyName?.text.toString())
                childKey.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children) isCompanyExists = ds.exists()
                        if (isCompanyExists){
                            companyExist()
                            progress.cancel()
                        }
                        else{
                            proceedToAdd()
                            progress.cancel()

                        }
                    }

                })

            }
        }

    }
    private fun proceedToAdd() {
        newCompany.companyName = companyName?.text.toString()
        newCompany.companyRepresentativeName = companyRepresentativeName?.text.toString()
        newCompany.companyEmailId = companyEmailId?.text.toString()
        newCompany.companyAddress = companyAddress?.text.toString()

        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("Distributor")
                .child(userKey!!).child("Company")
        databaseReference.push().setValue(newCompany)
        Toast.makeText(this,"Company added",Toast.LENGTH_LONG).show()
        this.finish()
    }
    private fun companyExist() {
        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.med_box_icon)
        builder.setPositiveButton("ok") { _, _ ->
        }
        builder.setTitle("Company Name")
        builder.setMessage("\nCompany with this name is already created")
        val loginTypeAlertDialog: AlertDialog = builder.create()
        loginTypeAlertDialog.setCancelable(false)
        loginTypeAlertDialog.show()
        isCompanyExists = false
    }
}
