@file:Suppress("DEPRECATION")

package com.project.medbox.users.distributor.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
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
import com.project.medbox.users.distributor.adapters.EmployeeAdapter
import com.project.medbox.databasegettersetter.NewEmployee
import com.project.medbox.users.distributor.entries.NewEmployeeActivity
import java.util.*

class EmployeeFragment : Fragment() {


    var userKey: String? = null

    var newEmployeeActiveList = object : ArrayList<NewEmployee>() {}
    var newEmployeeInactiveList = object : ArrayList<NewEmployee>() {}

    private var employeeActiveAdapter : EmployeeAdapter? = null
    private var employeeInactiveAdapter : EmployeeAdapter? = null

    var employeeActiveRecyclerView: RecyclerView? = null
    var employeeInactiveRecyclerView: RecyclerView? = null

    var employeeListLayout: RelativeLayout? = null

    var noEmployeeActiveText: TextView? = null
    var noEmployeeInactiveText: TextView? = null
    var noEmployeeAddedYetText: TextView? = null

    private var addEmployeeButton: FloatingActionButton? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Employees"
        return inflater.inflate(R.layout.fragment_employee, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userKey = arguments?.getString("userKey")

// Find Views

        val appBar: AppBarLayout? = activity?.findViewById(R.id.app_bar)
        val searchView: androidx.appcompat.widget.SearchView? = appBar?.findViewById(R.id.search_bar)
        employeeActiveRecyclerView = view.findViewById(R.id.employee_active_list_holder)
        employeeInactiveRecyclerView = view.findViewById(R.id.employee_inactive_list_holder)
        employeeListLayout = view.findViewById(R.id.employee_list_layout)
        noEmployeeActiveText = view.findViewById(R.id.no_active_employee_text)
        noEmployeeInactiveText = view.findViewById(R.id.no_inactive_employee_text)
        noEmployeeAddedYetText = view.findViewById(R.id.no_employees_text)
        addEmployeeButton = view.findViewById(R.id.new_employee_floating_button)

// SearchView

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


// Set Layout Manager

        employeeActiveRecyclerView?.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        employeeInactiveRecyclerView?.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

// On Click Listener

        addEmployeeButton?.setOnClickListener {

            val intent = Intent(this.context, NewEmployeeActivity::class.java)
            intent.putExtra("userKey", userKey)
            startActivity(intent)
        }

        createEmployeeList()

    }

    private fun createEmployeeList() {

        val progress = ProgressDialog(this.context)
        progress.setTitle("Please Wait !")
        progress.setMessage("Please wait while we are fetching the data!")
        progress.setCanceledOnTouchOutside(false)
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()

        val databaseReference = FirebaseDatabase.getInstance().reference
            .child("Distributor")
            .child(userKey!!)
            .child("Employee")
        val childKey = databaseReference.orderByChild("employeeName")
        childKey.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                newEmployeeActiveList.clear()
                newEmployeeInactiveList.clear()
                for (ds in snapshot.children) {
                    val newEmployee: NewEmployee = ds.getValue(
                        NewEmployee::class.java)!!
                    if (newEmployee.employeeActiveStatus == "active") {
                        newEmployeeActiveList.add(newEmployee)
                    } else if (newEmployee.employeeActiveStatus == "inactive") {
                        newEmployeeInactiveList.add(newEmployee)
                    }
                }
                fetchActiveAdapter()
                fetchInactiveAdapter()
                employeeListLayout?.visibility = View.VISIBLE
                employeeActiveRecyclerView?.visibility = View.VISIBLE
                employeeInactiveRecyclerView?.visibility = View.VISIBLE
                noEmployeeInactiveText?.visibility = View.GONE
                noEmployeeActiveText?.visibility = View.GONE
                noEmployeeAddedYetText?.visibility = View.GONE

                if (newEmployeeActiveList.isEmpty() && newEmployeeInactiveList.isEmpty()) {
                    employeeListLayout?.visibility = View.GONE
                    noEmployeeAddedYetText?.visibility = View.VISIBLE

                }
                if (newEmployeeActiveList.isEmpty()) {

                    noEmployeeActiveText?.visibility = View.VISIBLE

                }
                if (newEmployeeInactiveList.isEmpty()) {

                    noEmployeeInactiveText?.visibility = View.VISIBLE

                }
                progress.cancel()


            }

        })
    }

    private fun fetchActiveAdapter() {
        employeeActiveAdapter = EmployeeAdapter(
            this.context,
            newEmployeeActiveList,
            userKey
        )
        employeeActiveRecyclerView?.adapter = employeeActiveAdapter

    }
    private fun fetchInactiveAdapter() {
        employeeInactiveAdapter = EmployeeAdapter(
            this.context,
            newEmployeeInactiveList,
            userKey
        )
        employeeInactiveRecyclerView?.adapter = employeeInactiveAdapter

    }

    private fun filterList(searchText: String) {
        val newEmployeeActiveFilteredList : ArrayList<NewEmployee> =
            object : ArrayList<NewEmployee>() {}
        val newEmployeeInactiveFilteredList : ArrayList<NewEmployee> =
            object : java.util.ArrayList<NewEmployee>() {}

        for (i in 0 until newEmployeeActiveList.size) {
            //if the existing elements contains the search input
            if (newEmployeeActiveList[i].employeeName.toLowerCase(Locale.ROOT)
                    .contains(
                        searchText.toLowerCase(
                            Locale.ROOT
                        )
                    )
            ) {
                newEmployeeActiveFilteredList.add(newEmployeeActiveList[i])
            }
        }
        for (i in 0 until newEmployeeInactiveList.size) {
            //if the existing elements contains the search input
            if (newEmployeeInactiveList[i].employeeName.toLowerCase(Locale.ROOT)
                    .contains(
                        searchText.toLowerCase(
                            Locale.ROOT
                        )
                    )
            ) {
                newEmployeeInactiveFilteredList.add(newEmployeeInactiveList[i])
            }
        }



        employeeActiveAdapter?.filterList(newEmployeeActiveFilteredList)
        employeeInactiveAdapter?.filterList(newEmployeeInactiveFilteredList)

    }

}
