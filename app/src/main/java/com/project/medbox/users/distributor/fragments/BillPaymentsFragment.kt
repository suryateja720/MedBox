package com.project.medbox.users.distributor.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.medbox.R

class BillPaymentsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.title = "Bills & Payments"
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bill_payments, container, false)
    }

}
