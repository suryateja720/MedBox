package com.project.medbox.users.distributor.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.project.medbox.R
import com.project.medbox.users.distributor.entries.NewInwardActivity


class InwardFragment : Fragment() {
    var newInwardButton : FloatingActionButton? =null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inward, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newInwardButton = view.findViewById(R.id._new_inward_floating_button)
        newInwardButton?.setOnClickListener {
            var intent = Intent(this.context,NewInwardActivity::class.java)
            startActivity(intent)
        }

    }

}
