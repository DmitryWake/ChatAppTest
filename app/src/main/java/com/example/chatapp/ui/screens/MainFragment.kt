package com.example.chatapp.ui.screens

import androidx.fragment.app.Fragment
import com.example.chatapp.R
import com.example.chatapp.utilities.APP_ACTIVITY
import com.example.chatapp.utilities.hideKeyboard


class MainFragment :  Fragment(R.layout.fragment_chat) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "ChatApp"
        APP_ACTIVITY.appDrawer.enableDrawer()
        hideKeyboard()
    }

}
