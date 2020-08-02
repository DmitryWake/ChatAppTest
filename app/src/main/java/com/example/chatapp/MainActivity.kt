package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.chatapp.activities.RegisterActivity
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.models.User
import com.example.chatapp.ui.objects.AppDrawer
import com.example.chatapp.ui.fragments.ChatFragment
import com.example.chatapp.utilities.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar

    lateinit var appDrawer: AppDrawer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFireBase()
        initUser {
            initFields() //Инициализация обьектов
            initFunc()
        }
        APP_ACTIVITY = this
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    private fun initFunc() {
        if (AUTH.currentUser != null) {
            setSupportActionBar(toolbar)
            appDrawer.create()
            replaceFragment(ChatFragment(), false)
        } else {
            replaceActivity(RegisterActivity())
        }
    }

    private fun initFields() {
        toolbar = binding.mainToolBar
        appDrawer = AppDrawer(this, toolbar)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }
}