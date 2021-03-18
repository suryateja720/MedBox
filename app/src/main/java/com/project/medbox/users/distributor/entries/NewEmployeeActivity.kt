package com.project.medbox.users.distributor.entries

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.project.medbox.R
import com.project.medbox.databasegettersetter.NewEmployee

class NewEmployeeActivity : AppCompatActivity() {

    private var newEmployeeName: TextInputEditText? = null
    private var newEmployeeAadhaar: TextInputEditText? = null
    private var newEmployeeMobile: TextInputEditText? = null
    private var newEmployeeAddress: TextInputEditText? = null
    private var newEmployeeActiveStatus: String? = null
    private var addEmployeeButton:Button? =null

    var userKey : String? = null
    var newEmployee : NewEmployee = object : NewEmployee(){}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_employee)

        newEmployeeName = findViewById(R.id.new_employee_name)
        newEmployeeAadhaar = findViewById(R.id.new_employee_aadhaar)
        newEmployeeMobile = findViewById(R.id.new_employee_mobile_number)
        newEmployeeAddress = findViewById(R.id.new_employee_address)

        newEmployeeActiveStatus = "active"
        userKey=intent.getStringExtra("userKey")

        addEmployeeButton = findViewById(R.id.new_employee_add_button)
        addEmployeeButton?.setOnClickListener {
            if (newEmployeeName?.length()==0){
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->
                }
                builder.setTitle("Employee Name")
                builder.setMessage("\nEmployee Name cannot be empty!")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else if (newEmployeeAadhaar?.length()==0){
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->
                }
                builder.setTitle("Aadhaar Number")
                builder.setMessage("\nAadhaar Number cannot be empty!")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else if (newEmployeeMobile?.length()==0){
                val builder = AlertDialog.Builder(this)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->
                }
                builder.setTitle("Mobile Number")
                builder.setMessage("\nMobile Number cannot be empty!")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()
            } else{
                val databaseReference = FirebaseDatabase.getInstance().reference
                    .child("Distributor")
                    .child(userKey!!)
                    .child("Employee")

                newEmployee.employeeName = newEmployeeName?.text.toString()
                newEmployee.employeeAadhaar = newEmployeeAadhaar?.text.toString()
                newEmployee.employeeMobile = newEmployeeMobile?.text.toString()
                newEmployee.employeeAddress = newEmployeeAddress?.text.toString()
                newEmployee.employeeActiveStatus = newEmployeeActiveStatus

                databaseReference.push().setValue(newEmployee)
                Toast.makeText(this,"Employee Added",Toast.LENGTH_LONG).show()
                this.finish()

            }
        }




    }
}
