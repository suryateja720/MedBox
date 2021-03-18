package com.project.medbox.users.distributor.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.medbox.R
import com.project.medbox.databasegettersetter.NewCompany
import com.project.medbox.databasegettersetter.NewProduct
import com.project.medbox.users.distributor.adapters.CompanyAdapter
import com.project.medbox.users.distributor.entries.NewCompanyActivity
import java.util.*

class CompanyProductFragment : Fragment() {

    var newCompanyButton: FloatingActionButton? = null

    var companyArrayList: ArrayList<NewCompany> = object : ArrayList<NewCompany>() {}
    var productsArrayList: ArrayList<NewProduct> = object : ArrayList<NewProduct>() {}

    var companyAdapter: CompanyAdapter? = null


    var gstin: String? = null
    var userKey: String? = null
    var recyclerView: RecyclerView? = null
    var isScrollerSettled = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Company & Products"
        return inflater.inflate(R.layout.fragment_company_product, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

// Find Views

        newCompanyButton = view.findViewById(R.id.new_company_floating_button)
        val appBar: AppBarLayout? = activity?.findViewById(R.id.app_bar)
        val searchView: androidx.appcompat.widget.SearchView? =
            appBar?.findViewById(R.id.search_bar)
        recyclerView = view.findViewById(R.id.company_list_holder)

// New Customer Data

        gstin = arguments?.getString("gstin")
        userKey = arguments?.getString("userKey")

// On Click Listener

        newCompanyButton?.setOnClickListener {
            newCompanyButton?.isEnabled = false
            Handler().postDelayed({
                newCompanyButton?.isEnabled = true
            }, 500)

            val intent = Intent(this.context, NewCompanyActivity::class.java)
            intent.putExtra("gstin", gstin)
            intent.putExtra("userKey", userKey)
            startActivity(intent)
        }

// Search bar and App bar

        searchView?.onActionViewCollapsed()
        searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText.toString())
                return true
            }

        })

// Recycler View

        recyclerView?.layoutManager = LinearLayoutManager(this.context)

        // Scrolling Action

//        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (dy > 25 && isScrollerSettled) {
//                    isScrollerSettled = false
//                    appBar?.visibility = View.GONE
//                    Handler().postDelayed({
//                        isScrollerSettled = true
//                    }, 200)
//
//                } else if (
//                    (dy < -30 && isScrollerSettled)
//                    ||
//                    (appBar?.visibility == View.GONE
//                            && !recyclerView.canScrollVertically(-1))
//                ) {
//                    isScrollerSettled = false
//                    appBar?.visibility = View.VISIBLE
//                    Handler().postDelayed({
//                        isScrollerSettled = true
//                    }, 200)
//                }
//
//
//            }
//
//
//        })

        createCompanyList(userKey)

    }


    private fun createCompanyList(userKey: String?) {

        val progress = ProgressDialog(this.context)
        progress.setTitle("Please Wait !")
        progress.setMessage("Please wait while we are fetching the data!")
        progress.setCanceledOnTouchOutside(false)
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()

        val databaseReference = FirebaseDatabase.getInstance().reference
            .child("Distributor")
            .child(userKey.toString())
            .child("Company")
        val childKey = databaseReference.orderByChild("companyName")

        childKey.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                recyclerView?.adapter = null
                companyArrayList.clear()
                for (ds in snapshot.children) {
                    val newCompany: NewCompany = ds.getValue(
                        NewCompany::class.java)!!
                    companyArrayList.add(newCompany)

                }
                if (companyArrayList.isEmpty()) {
                    progress.cancel()
                    promptEmptyList()
                } else {
                    progress.cancel()
                    createProductList()

                }

            }

        })

    }
    private fun createProductList() {
        val progress = ProgressDialog(this.context)
        progress.setTitle("Please Wait !")
        progress.setMessage("Please wait while we are fetching the data!")
        progress.setCanceledOnTouchOutside(false)
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        val productDatabaseReference = FirebaseDatabase.getInstance().reference
            .child("Distributor")
            .child(userKey.toString())
            .child("Product")
        val productChildKey = productDatabaseReference
            .orderByChild("productCompanyName")


        productChildKey.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(productSnapshot: DataSnapshot) {
                productsArrayList.clear()
                for (pds in productSnapshot.children) {
                    val newProduct: NewProduct = pds.getValue(NewProduct::class.java)!!
                    productsArrayList.add(newProduct)
                }
                progress.cancel()
                fetchAdapter()
            }
        })
    }

    private fun promptEmptyList() {
        Toast.makeText(this.context, "No companies are added so far!", Toast.LENGTH_LONG).show()
    }

    private fun fetchAdapter() {
        companyAdapter = CompanyAdapter(
            this.context,
            companyArrayList,
            productsArrayList,
            userKey
        )
        recyclerView?.adapter = companyAdapter
    }

    private fun filterList(searchText: String) {
        val companyDetailsFilteredArray: ArrayList<NewCompany> =
            object : ArrayList<NewCompany>() {}

        for (i in 0 until companyArrayList.size) {
            //if the existing elements contains the search input
            if (companyArrayList[i].companyName.toLowerCase(Locale.ROOT)
                    .contains(
                        searchText.toLowerCase(
                            Locale.ROOT
                        )
                    )
            ) {
                companyDetailsFilteredArray.add(companyArrayList[i])
            }
        }

        companyAdapter?.filterList(companyDetailsFilteredArray)

    }
}
