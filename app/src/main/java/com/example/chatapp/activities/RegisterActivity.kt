package com.example.chatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityRegisterBinding
import com.example.chatapp.ui.fragments.EnterPhoneNumberFragment
import com.example.chatapp.utilities.initFireBase
import com.example.chatapp.utilities.replaceFragment

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFireBase()
    }

    override fun onStart() {
        super.onStart()
        toolbar = binding.registerToolBar
        setSupportActionBar(toolbar)
        title = getString(R.string.register_title_yourPhone)
        replaceFragment(EnterPhoneNumberFragment(), false)
    }
}
