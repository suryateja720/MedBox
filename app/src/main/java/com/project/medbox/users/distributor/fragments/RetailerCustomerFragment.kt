@file:Suppress("DEPRECATION")

package com.project.medbox.users.distributor.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.medbox.R
import com.project.medbox.databasegettersetter.NewRequest
import com.project.medbox.databasegettersetter.NewUser
import com.project.medbox.users.distributor.adapters.CustomerAdapter
import com.project.medbox.users.distributor.adapters.ReceivedRequestAdapter
import com.project.medbox.users.distributor.adapters.SentRequestAdapter

class RetailerCustomerFragment : Fragment() {

    var retailerSearchBarContainer: TextInputLayout? = null
    var retailerSearchBar: TextInputEditText? = null
    private var retailerSearchButton: Button? = null


    private var headerSearchButton: ImageView? = null
    private var headerRequestsButton: ImageView? = null
    private var headerCustomersButton: ImageView? = null

    private var customerListCardView: CardView? = null
    private var searchCardView: CardView? = null
    private var requestsCardView: CardView? = null

    private var nothingToShowTextView: TextView? = null

    private var sentRequestTextView: TextView? = null
    private var receivedRequestTextView: TextView? = null
    private var sentRequestRecyclerView: RecyclerView? = null
    private var receivedRequestRecyclerView: RecyclerView? = null
    private var customerRecyclerView: RecyclerView? = null
    var sentRequestAdapter: SentRequestAdapter? = null
    var receivedRequestAdapter: ReceivedRequestAdapter? = null
    private var customerAdapter: CustomerAdapter? = null

    var gstin: String? = null
    var userKey: String? = null

    var isGstinValid = false
    var isCustomerAlreadyAdded = false
    var isUserExist = false
    var isAlreadyRequested = false

    private var newRequest: NewRequest = object : NewRequest() {}

    var sentRequestArrayList: ArrayList<NewRequest> = object : ArrayList<NewRequest>() {}
    var receivedRequestArrayList: ArrayList<NewRequest> = object : ArrayList<NewRequest>() {}
    var customerArrayList: ArrayList<NewUser> = object : ArrayList<NewUser>() {}

    private var audioManager: AudioManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Retailers"
        audioManager = activity?.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        return inflater.inflate(R.layout.fragment_retailer_customer, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gstin = arguments?.getString("gstin")
        userKey = arguments?.getString("userKey")

// Find Views

        headerRequestsButton = view.findViewById(R.id.header_requests_button)
        headerSearchButton = view.findViewById(R.id.header_search_bar_button)
        headerCustomersButton = view.findViewById(R.id.header_customer_button)

        searchCardView = view.findViewById(R.id.retailer_search_card_view)
        customerListCardView = view.findViewById(R.id.customers_list_card_view)
        requestsCardView = view.findViewById(R.id.sent_received_request_layout)

        nothingToShowTextView = view.findViewById(R.id.retailer_empty_page_text)

        sentRequestTextView = view.findViewById(R.id.sent_request_text)
        receivedRequestTextView = view.findViewById(R.id.received_request_text)

        sentRequestRecyclerView = view.findViewById(R.id.sent_request_list_holder)
        receivedRequestRecyclerView = view.findViewById(R.id.received_request_list_holder)

        customerRecyclerView = view.findViewById(R.id.customer_list_holder)

        retailerSearchBar = view.findViewById(R.id.search_bar_retailer)
        retailerSearchButton = view.findViewById(R.id.search_retailer_button)
        retailerSearchBarContainer = view.findViewById(R.id.search_bar_retailer_container)

// Set Layout Manager

        sentRequestRecyclerView?.layoutManager = LinearLayoutManager(this.context)
        receivedRequestRecyclerView?.layoutManager = LinearLayoutManager(this.context)
        customerRecyclerView?.layoutManager = LinearLayoutManager(this.context)

// Text Watcher

        retailerSearchBar?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val validatePattern =
                    "[0-9][0-9][A-Z][A-Z][A-Z][A-Z][A-Z][0-9][0-9][0-9][0-9][A-Z][1-9A-Z]Z[0-9A-Z]"

                if (!retailerSearchBar?.text.toString().matches(validatePattern.toRegex())) {
                    retailerSearchBarContainer?.error = "Invalid GSTIN"
                    isGstinValid = false
                } else {
                    retailerSearchBarContainer?.error = null
                    isGstinValid = true
                }

            }

        })

// On CLick Listener

        sentRequestTextView?.setOnClickListener {
            sentRequestTextView?.setBackgroundResource(R.drawable.form_gradient_teal)
            sentRequestTextView?.setTextColor(resources.getColor(R.color.colorTeal500))
            receivedRequestTextView?.setBackgroundResource(R.drawable.form_gradient_grey)
            receivedRequestTextView?.setTextColor(resources.getColor(R.color.colorGrey500))
            sentRequestRecyclerView?.visibility = View.VISIBLE
            receivedRequestRecyclerView?.visibility = View.GONE
        }
        receivedRequestTextView?.setOnClickListener {
            receivedRequestTextView?.setBackgroundResource(R.drawable.form_gradient_teal)
            receivedRequestTextView?.setTextColor(resources.getColor(R.color.colorTeal500))
            sentRequestTextView?.setBackgroundResource(R.drawable.form_gradient_grey)
            sentRequestTextView?.setTextColor(resources.getColor(R.color.colorGrey500))
            receivedRequestRecyclerView?.visibility = View.VISIBLE
            sentRequestRecyclerView?.visibility = View.GONE
        }
        headerCustomersButton?.setOnClickListener {

            audioManager?.playSoundEffect(SoundEffectConstants.CLICK, 1F)

            if (customerListCardView?.visibility == View.VISIBLE) {
                headerCustomersButton?.setBackgroundResource(R.drawable.form_gradient_teal)
                customerListCardView?.visibility = View.GONE
                if (customerListCardView?.visibility == View.GONE
                    && searchCardView?.visibility == View.GONE
                    && requestsCardView?.visibility == View.GONE
                ) {
                    nothingToShowTextView?.visibility = View.VISIBLE
                }
            } else if (customerListCardView?.visibility == View.GONE) {
                headerCustomersButton?.setBackgroundResource(R.drawable.form_gradient_grey)
                customerListCardView?.visibility = View.VISIBLE

                nothingToShowTextView?.visibility = View.GONE
            }
        }
        headerSearchButton?.setOnClickListener {

            audioManager?.playSoundEffect(SoundEffectConstants.CLICK, 1F)

            if (searchCardView?.visibility == View.VISIBLE) {
                headerSearchButton?.setBackgroundResource(R.drawable.form_gradient_teal)
                searchCardView?.visibility = View.GONE
                if (customerListCardView?.visibility == View.GONE
                    && searchCardView?.visibility == View.GONE
                    && requestsCardView?.visibility == View.GONE
                ) {
                    nothingToShowTextView?.visibility = View.VISIBLE
                }
            } else if (searchCardView?.visibility == View.GONE) {
                headerSearchButton?.setBackgroundResource(R.drawable.form_gradient_grey)
                searchCardView?.visibility = View.VISIBLE

                nothingToShowTextView?.visibility = View.GONE
            }
        }
        headerRequestsButton?.setOnClickListener {

            audioManager?.playSoundEffect(SoundEffectConstants.CLICK, 1F)

            if (requestsCardView?.visibility == View.VISIBLE) {
                headerRequestsButton?.setBackgroundResource(R.drawable.form_gradient_teal)
                requestsCardView?.visibility = View.GONE
                if (customerListCardView?.visibility == View.GONE
                    && searchCardView?.visibility == View.GONE
                    && requestsCardView?.visibility == View.GONE
                ) {
                    nothingToShowTextView?.visibility = View.VISIBLE
                }
            } else if (requestsCardView?.visibility == View.GONE) {
                headerRequestsButton?.setBackgroundResource(R.drawable.form_gradient_grey)
                requestsCardView?.visibility = View.VISIBLE

                nothingToShowTextView?.visibility = View.GONE
            }
        }
        retailerSearchButton?.setOnClickListener {
            if (!isGstinValid) {
                val builder = AlertDialog.Builder(this.context)
                builder.setIcon(R.drawable.med_box_icon)
                builder.setPositiveButton("ok") { _, _ ->

                }
                builder.setTitle("GSTIN")
                builder.setMessage("\n Please enter a valid GSTIN!")
                val loginTypeAlertDialog: AlertDialog = builder.create()
                loginTypeAlertDialog.setCancelable(false)
                loginTypeAlertDialog.show()

            } else {
                val progress = ProgressDialog(this.context)
                progress.setTitle("Please Wait !")
                progress.setMessage("Please wait while we are fetching the data!")
                progress.setCanceledOnTouchOutside(false)
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progress.show()

                val databaseReference = FirebaseDatabase.getInstance().reference
                    .child("Retailer")
                val childKey = databaseReference.orderByChild("gstinNumber")
                    .equalTo(retailerSearchBar?.text.toString())
                childKey.addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        isUserExist = false
                        for (ds in snapshot.children) {
                            val retailerDetails =
                                ds.getValue(NewUser::class.java)

                            if (retailerDetails?.gstinNumber == retailerSearchBar?.text.toString()) {
                                progress.cancel()
                                isUserExist = true
                                checkForCustomer(retailerDetails)
                            }
                        }
                        if (!isUserExist) {
                            progress.cancel()
                            promptUserDoestExist()
                        }
                    }

                })
            }
        }

// Creates Array Lists

        createSentRequestList()
        createReceivedRequestList()
        createCustomerList()

    }

    private fun createCustomerList() {
        val databaseReference = FirebaseDatabase.getInstance().reference
            .child("Distributor")
            .child(userKey!!)
            .child("Customer")
        val childKey = databaseReference.orderByChild("gstinNumber")
        childKey.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                customerArrayList.clear()
                for (ds in snapshot.children) {
                    val customer = ds.getValue(NewUser::class.java)!!
                    customerArrayList.add(customer)
                }
                if (customerArrayList.isEmpty()) {
                    promptEmptyCustomers()
                } else {
                    fetchCustomerAdapter()
                }
            }

        })
    }

    private fun createReceivedRequestList() {
        val databaseReference = FirebaseDatabase.getInstance().reference
            .child("Request")
        val childKey = databaseReference.orderByChild("requestTo").equalTo(gstin)
        childKey.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                receivedRequestArrayList.clear()
                for (ds in snapshot.children) {
                    val request = ds.getValue(NewRequest::class.java)!!
                    receivedRequestArrayList.add(request)
                }
                if (receivedRequestArrayList.isEmpty()) {
                    receivedRequestAdapter?.updateList(receivedRequestArrayList)
                    promptNoRequestsReceived()
                } else {
                    fetchReceivedRequestAdapter()
                }
            }

        })
    }

    private fun createSentRequestList() {
        val dataBaseReference = FirebaseDatabase.getInstance().reference
            .child("Request")
        val childKey = dataBaseReference.orderByChild("requestFrom").equalTo(gstin)
        childKey.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                sentRequestArrayList.clear()
                for (ds in snapshot.children) {
                    val request = ds.getValue(NewRequest::class.java)!!
                    sentRequestArrayList.add(request)
                }
                if (sentRequestArrayList.isEmpty()) {
                    sentRequestAdapter?.updateList(sentRequestArrayList)
                    promptNoRequestsSent()
                } else {
                    fetchSentRequestAdapter()
                }

            }

        })
    }

    private fun fetchSentRequestAdapter() {
        sentRequestAdapter = SentRequestAdapter(
            this.context,
            sentRequestArrayList
        )
        sentRequestRecyclerView?.adapter = sentRequestAdapter
    }

    private fun fetchReceivedRequestAdapter() {
        receivedRequestAdapter =
            ReceivedRequestAdapter(
                this.context,
                receivedRequestArrayList,
                userKey
            )
        receivedRequestRecyclerView?.adapter = receivedRequestAdapter
    }

    private fun fetchCustomerAdapter() {
        Toast.makeText(this.context, customerArrayList[0].gstinNumber, Toast.LENGTH_LONG).show()
        customerAdapter = CustomerAdapter(
            this.context,
            customerArrayList
        )
        customerRecyclerView?.adapter = customerAdapter
    }

    private fun checkForCustomer(retailerDetails: NewUser) {
        val databaseReference = FirebaseDatabase.getInstance().reference
            .child("Distributor")
            .child(userKey!!)
            .child("Customer")
        val childKey =
            databaseReference.orderByChild("gstinNumber").equalTo(retailerDetails.gstinNumber)
        childKey.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                isCustomerAlreadyAdded = false
                for (ds in snapshot.children) {
                    val dbCustomerDetails = ds.getValue(NewUser::class.java)!!
                    if (dbCustomerDetails.gstinNumber == retailerDetails.gstinNumber) {
                        isCustomerAlreadyAdded = true
                    }
                }
                if (isCustomerAlreadyAdded) {
                    promptAlreadyAddedToCustomer()
                } else {
                    checkForRequest(retailerDetails)
                }

            }

        })
    }

    private fun checkForRequest(retailerDetails: NewUser) {
        isAlreadyRequested = false
        val databaseReference = FirebaseDatabase.getInstance().reference
            .child("Request")
        val childKey = databaseReference.orderByChild("requestFrom").equalTo(gstin)
        childKey.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val request = ds.getValue(NewRequest::class.java)
                    if (request?.requestTo == retailerDetails.gstinNumber) {
                        isAlreadyRequested = true
                    }
                }
                if (isAlreadyRequested) {
                    promptAlreadyRequested()
                } else {
                    promptUserExist(retailerDetails)
                }
            }

        })
    }

    private fun proceedToSendRequest() {
        val databaseReference = FirebaseDatabase.getInstance().reference
            .child("Request")
        newRequest.requestFrom = gstin
        newRequest.requestTo = retailerSearchBar?.text.toString()
        newRequest.requestStatus = "requested"

        databaseReference.push().setValue(newRequest)

    }

    private fun promptAlreadyRequested() {
        val builder = AlertDialog.Builder(this.context)
        builder.setIcon(R.drawable.med_box_icon)
        builder.setPositiveButton("ok") { _, _ ->
            retailerSearchBar?.text = null
            retailerSearchBarContainer?.error = null
        }
        builder.setTitle("Requests")
        builder.setMessage("\n Retailer with this GSTIN is already requested by you!")
        val loginTypeAlertDialog: AlertDialog = builder.create()
        loginTypeAlertDialog.setCancelable(false)
        loginTypeAlertDialog.show()
    }

    private fun promptAlreadyAddedToCustomer() {
        val builder = AlertDialog.Builder(this.context)
        builder.setIcon(R.drawable.med_box_icon)
        builder.setPositiveButton("ok") { _, _ ->
            retailerSearchBar?.text = null
            retailerSearchBarContainer?.error = null
        }
        builder.setTitle("Customer")
        builder.setMessage("\n Retailer with this GSTIN is already added to your customers!")
        val loginTypeAlertDialog: AlertDialog = builder.create()
        loginTypeAlertDialog.setCancelable(false)
        loginTypeAlertDialog.show()
    }

    private fun promptNoRequestsSent() {

    }

    private fun promptNoRequestsReceived() {

    }

    private fun promptUserDoestExist() {

        val builder = AlertDialog.Builder(this.context)
        builder.setIcon(R.drawable.med_box_icon)
        builder.setPositiveButton("ok") { _, _ ->

            retailerSearchBar?.text = null
            retailerSearchBarContainer?.error = null
        }
        builder.setTitle("Retailer")
        builder.setMessage("\n Retailer with this GSTIN doesn't exist!")
        val loginTypeAlertDialog: AlertDialog = builder.create()
        loginTypeAlertDialog.setCancelable(false)
        loginTypeAlertDialog.show()
    }

    private fun promptUserExist(retailerDetails: NewUser) {
        val builder = AlertDialog.Builder(this.context)
        builder.setIcon(R.drawable.med_box_icon)
        builder.setPositiveButton("send request") { _, _ ->
            proceedToSendRequest()
            retailerSearchBar?.text = null
            retailerSearchBarContainer?.error = null
        }
        builder.setNegativeButton("cancel") { _, _ ->
            retailerSearchBar?.text = null
            retailerSearchBarContainer?.error = null
        }
        builder.setTitle("User Exist")
        builder.setMessage(
            "\n GSTIN \t : ${retailerDetails.gstinNumber} " +
                    "\n Business Name : ${retailerDetails.businessName} " +
                    "\n Owner Name \t : ${retailerDetails.ownerName}"
        )
        val loginTypeAlertDialog: AlertDialog = builder.create()
        loginTypeAlertDialog.setCancelable(false)
        loginTypeAlertDialog.show()

    }

    private fun promptEmptyCustomers() {

    }
}
