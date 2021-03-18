package com.project.medbox.launcher

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import com.project.medbox.R
import com.project.medbox.authentication.LoginActivity
import com.project.medbox.common.SettingsFragment
import com.project.medbox.users.distributor.fragments.*
import com.project.medbox.users.retailer.fragments.BillsPaymentsFragment
import com.project.medbox.users.retailer.fragments.MyOrdersFragment
import com.project.medbox.users.retailer.fragments.SupplierCompaniesProductsFragment
import com.project.medbox.users.retailer.fragments.SupplierFragment

@Suppress("DEPRECATION")
class UserDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var gstinNavigationTextView: TextView? = null
    private var userType: String? = null
    private var gstinNumber: String? = null
    private var userKey: String? = null
    private var businessName: String? = null
    private var ownerName: String? = null
    private var navView: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_user_dashboard)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

// Getting Intent String Extra

        gstinNumber = intent.getStringExtra("gstinNumber")
        userType = intent.getStringExtra("userType")
        businessName = intent.getStringExtra("businessName")
        ownerName = intent.getStringExtra("ownerName")
        userKey = intent.getStringExtra("userKey")


// Find Views

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)


// Inflating Menu

        if (userType == "Distributor") {
            title = "Stock & Billing"

            val stockManagerFragment =
                StockBillingFragment()
            val bundle: Bundle? = Bundle()
            bundle?.putString("gstin", gstinNumber)
            bundle?.putString("userKey", userKey)
            stockManagerFragment.arguments = bundle
            this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, stockManagerFragment)
                .commit()

            navView?.inflateMenu(R.menu.distributor_navigation_menu);

            navView?.menu?.findItem(R.id.nav_stock_billing)?.isChecked = true
        }
        else if (userType == "Retailer") {
            title = "Supplier"

            val supplierFragment =
                SupplierFragment()

            val bundle: Bundle? = Bundle()

            bundle?.putString("gstin", gstinNumber)
            bundle?.putString("userKey", userKey)
            supplierFragment.arguments = bundle
            this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, supplierFragment)
                .commit()
            navView?.inflateMenu(R.menu.retailer_navigation_menu);
            navView?.menu?.findItem(R.id.nav_supplier)?.isChecked = true
        }

        val headerView: View? = navView?.getHeaderView(0)

        gstinNavigationTextView = headerView?.findViewById(R.id.gstin_nav_text_view)
        gstinNavigationTextView?.text = gstinNumber


        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView?.setNavigationItemSelectedListener(this)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        if (userType == "Distributor") {
            val size: Int? = navView?.menu?.size()
            when (item.itemId) {
                R.id.nav_stock_billing -> {
                    title = "Stock & Billing"

                    val stockManagerFragment =
                        StockBillingFragment()

                    val bundle: Bundle? = Bundle()
                    bundle?.putString("gstin", gstinNumber)
                    bundle?.putString("userKey", userKey)
                    stockManagerFragment.arguments = bundle
                    this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, stockManagerFragment)
                        .commit()
                }
                R.id.nav_bill_payments -> {

                    val billPaymentsFragment =
                        BillPaymentsFragment()

                    val bundle: Bundle? = Bundle()
                    bundle?.putString("gstin", gstinNumber)
                    bundle?.putString("userKey", userKey)
                    billPaymentsFragment.arguments = bundle
                    this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, billPaymentsFragment)
                        .commit()
                }
                R.id.nav_retailers -> {

                    val retailerCustomerFragment =
                        RetailerCustomerFragment()

                    val bundle: Bundle? = Bundle()
                    bundle?.putString("gstin", gstinNumber)
                    bundle?.putString("userKey", userKey)
                    retailerCustomerFragment.arguments = bundle
                    this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, retailerCustomerFragment)
                        .commit()
                }
                R.id.nav_employee -> {

                    val employeeFragment =
                        EmployeeFragment()

                    val bundle: Bundle? = Bundle()
                    bundle?.putString("gstin", gstinNumber)
                    bundle?.putString("userKey", userKey)
                    employeeFragment.arguments = bundle
                    this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, employeeFragment)
                        .commit()
                }
                R.id.nav_company_products -> {

                    val companyProductFragment =
                        CompanyProductFragment()

                    val bundle: Bundle? = Bundle()
                    bundle?.putString("gstin", gstinNumber)
                    bundle?.putString("userKey", userKey)
                    companyProductFragment.arguments = bundle
                    this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, companyProductFragment)
                        .commit()
                }
                R.id.nav_setting -> {

                    val settingsFragment =
                        SettingsFragment()

                    val bundle: Bundle? = Bundle()
                    bundle?.putString("gstin", gstinNumber)
                    bundle?.putString("userKey", userKey)
                    settingsFragment.arguments = bundle
                    this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, settingsFragment)
                        .commit()
                }
                R.id.nav_log_out -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setIcon(R.drawable.med_box_icon)
                    builder.setPositiveButton("log out") { _, _ ->

                        val sharedPreferences = getSharedPreferences(
                            "login",
                            Context.MODE_PRIVATE
                        )
                        sharedPreferences?.edit()?.putBoolean("logged", false)?.apply()

                        sharedPreferences?.edit()?.putString("userType", null)?.apply()
                        sharedPreferences?.edit()
                            ?.putString("gstinNumber", null)?.apply()
                        sharedPreferences?.edit()
                            ?.putString("businessName", null)?.apply()
                        sharedPreferences?.edit()
                            ?.putString("ownerName", null)?.apply()

                        startActivity(Intent(this, LoginActivity::class.java))
                        this.finish()
                    }
                    builder.setNegativeButton("cancel") { _, _ ->

                    }
                    builder.setTitle("Log Out")
                    builder.setMessage("\n Are you sure to log out?")
                    val loginTypeAlertDialog: AlertDialog = builder.create()
                    loginTypeAlertDialog.setCancelable(false)
                    loginTypeAlertDialog.show()
                }
            }
        } else   if (userType == "Retailer") {
            val size: Int? = navView?.menu?.size()
            when (item.itemId) {
                R.id.nav_supplier -> {
                    title = "Supplier"

                    val supplierFragment =
                        SupplierFragment()

                    val bundle: Bundle? = Bundle()
                    bundle?.putString("gstin", gstinNumber)
                    bundle?.putString("userKey", userKey)
                    supplierFragment.arguments = bundle
                    this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, supplierFragment)
                        .commit()
                }
                R.id.nav_supplier_company_products -> {

                    title = "Companies & Products"
                    val supplierCompaniesProductsFragment =
                        SupplierCompaniesProductsFragment()

                    val bundle: Bundle? = Bundle()
                    bundle?.putString("gstin", gstinNumber)
                    bundle?.putString("userKey", userKey)
                    supplierCompaniesProductsFragment.arguments = bundle
                    this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, supplierCompaniesProductsFragment)
                        .commit()
                }
                R.id.nav_my_orders -> {

                    title = "My Orders"
                    val myOrdersFragment =
                        MyOrdersFragment()

                    val bundle: Bundle? = Bundle()
                    bundle?.putString("gstin", gstinNumber)
                    bundle?.putString("userKey", userKey)
                    myOrdersFragment.arguments = bundle
                    this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, myOrdersFragment)
                        .commit()
                }
                R.id.nav_bills_payments -> {

                    title = "Bills & Payments"
                    val billsPaymentsFragment =
                        BillsPaymentsFragment()

                    val bundle: Bundle? = Bundle()
                    bundle?.putString("gstin", gstinNumber)
                    bundle?.putString("userKey", userKey)
                    billsPaymentsFragment.arguments = bundle
                    this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, billsPaymentsFragment)
                        .commit()
                }
                R.id.nav_setting -> {

                    val settingsFragment =
                        SettingsFragment()

                    val bundle: Bundle? = Bundle()
                    bundle?.putString("gstin", gstinNumber)
                    bundle?.putString("userKey", userKey)
                    settingsFragment.arguments = bundle
                    this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, settingsFragment)
                        .commit()
                }
                R.id.nav_log_out -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setIcon(R.drawable.med_box_icon)
                    builder.setPositiveButton("log out") { _, _ ->

                        val sharedPreferences = getSharedPreferences(
                            "login",
                            Context.MODE_PRIVATE
                        )
                        sharedPreferences?.edit()?.putBoolean("logged", false)?.apply()

                        sharedPreferences?.edit()?.putString("userType", null)?.apply()
                        sharedPreferences?.edit()
                            ?.putString("gstinNumber", null)?.apply()
                        sharedPreferences?.edit()
                            ?.putString("businessName", null)?.apply()
                        sharedPreferences?.edit()
                            ?.putString("ownerName", null)?.apply()

                        startActivity(Intent(this, LoginActivity::class.java))
                        this.finish()
                    }
                    builder.setNegativeButton("cancel") { _, _ ->

                    }
                    builder.setTitle("Log Out")
                    builder.setMessage("\n Are you sure to log out?")
                    val loginTypeAlertDialog: AlertDialog = builder.create()
                    loginTypeAlertDialog.setCancelable(false)
                    loginTypeAlertDialog.show()
                }
            }
        }


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}
