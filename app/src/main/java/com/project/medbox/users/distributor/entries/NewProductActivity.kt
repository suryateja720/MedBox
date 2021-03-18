package com.project.medbox.users.distributor.entries

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.project.medbox.R
import com.project.medbox.databasegettersetter.NewProduct


class NewProductActivity : AppCompatActivity() {

    var userKey: String? = null
    var companyName: String? = null

    var newProductCompanyName: TextView? = null
    var newProductName: TextInputEditText? = null
    var newProductCode: TextInputEditText? = null
    var newProductPack: TextInputEditText? = null
    var newProductPurchaseRate: TextInputEditText? = null
    var newProductMrp: TextInputEditText? = null
    var newProductGst: Spinner? = null
    var newProductAddButton: Button? = null

    var newProduct : NewProduct = object : NewProduct(){}

    val gstList = arrayOf("GST","0%","5%","12%","18%","28%")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        userKey = intent.getStringExtra("userKey")
        companyName = intent.getStringExtra("companyName")

        newProductCompanyName = findViewById(R.id.new_product_company_name)
        newProductCompanyName?.text = companyName

        newProductName = findViewById(R.id.new_product_name)
        newProductCode = findViewById(R.id.new_product_code)
        newProductPack = findViewById(R.id.new_product_pack)
        newProductPurchaseRate = findViewById(R.id.new_product_purchase_rate)
        newProductMrp = findViewById(R.id.new_product_mrp)
        newProductAddButton = findViewById(R.id.new_product_add_button)
        newProductGst = findViewById(R.id.new_product_gst)

        val spinAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            gstList
        )
        newProductGst?.adapter = spinAdapter

        newProductAddButton?.setOnClickListener {
            val progress = ProgressDialog(this)
            progress.setTitle("Please Wait")
            progress.setMessage("Please wait while we fetch your data!!")
            progress.setCanceledOnTouchOutside(false)
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progress.show()

            when {
                newProductName?.length() == 0 -> {
                    progress.cancel()
                    val builder = AlertDialog.Builder(this)
                    builder.setIcon(R.drawable.med_box_icon)
                    builder.setPositiveButton("ok") { _, _ ->
                    }
                    builder.setTitle("Product Name")
                    builder.setMessage("\nProduct Name cannot be empty!")
                    val loginTypeAlertDialog: AlertDialog = builder.create()
                    loginTypeAlertDialog.setCancelable(false)
                    loginTypeAlertDialog.show()
                }
                newProductCode?.length() == 0 -> {
                    progress.cancel()
                    val builder = AlertDialog.Builder(this)
                    builder.setIcon(R.drawable.med_box_icon)
                    builder.setPositiveButton("ok") { _, _ ->
                    }
                    builder.setTitle("HSN Code")
                    builder.setMessage("\nHSN code cannot be empty!")
                    val loginTypeAlertDialog: AlertDialog = builder.create()
                    loginTypeAlertDialog.setCancelable(false)
                    loginTypeAlertDialog.show()
                }
                newProductPack?.length() == 0 -> {
                    progress.cancel()
                    val builder = AlertDialog.Builder(this)
                    builder.setIcon(R.drawable.med_box_icon)
                    builder.setPositiveButton("ok") { _, _ ->
                    }
                    builder.setTitle("Pack")
                    builder.setMessage("\nPack cannot be empty!")
                    val loginTypeAlertDialog: AlertDialog = builder.create()
                    loginTypeAlertDialog.setCancelable(false)
                    loginTypeAlertDialog.show()
                }
                newProductGst?.selectedItem.toString() == "GST" -> {
                    progress.cancel()
                    val builder = AlertDialog.Builder(this)
                    builder.setIcon(R.drawable.med_box_icon)
                    builder.setPositiveButton("ok") { _, _ ->
                    }
                    builder.setTitle("GST")
                    builder.setMessage("\nPlease select the GST Rate Slab.")
                    val loginTypeAlertDialog: AlertDialog = builder.create()
                    loginTypeAlertDialog.setCancelable(false)
                    loginTypeAlertDialog.show()
                }
                else -> {
                    val databaseReference  = FirebaseDatabase.getInstance().reference
                        .child("Distributor")
                        .child(userKey!!)
                        .child("Product")

                    newProduct.productCompanyName = companyName
                    newProduct.productName = newProductName?.text.toString()
                    newProduct.productHsnCode = newProductCode?.text.toString()
                    newProduct.productPack = newProductPack?.text.toString()
                    newProduct.productPurchaseRate = newProductPurchaseRate?.text.toString()
                    newProduct.productMrp = newProductMrp?.text.toString()
                    newProduct.productGst = newProductGst?.selectedItem.toString()

                    databaseReference.push().setValue(newProduct)
                    progress.cancel()
                    Toast.makeText(this,"Product Added",Toast.LENGTH_LONG).show()
                    this.finish()
                }
            }
        }

    }
}
