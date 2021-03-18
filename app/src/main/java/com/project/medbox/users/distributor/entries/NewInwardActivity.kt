package com.project.medbox.users.distributor.entries

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.project.medbox.R

class NewInwardActivity : AppCompatActivity() {
    var saveAndAddButton : Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_inward)

        saveAndAddButton = findViewById(R.id.new_inward_add_button)
        saveAndAddButton?.setOnClickListener {
            this.finish()
        }
    }
}