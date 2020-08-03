package com.example.chatapp.ui.fragments

import androidx.fragment.app.Fragment
import com.example.chatapp.R
import com.example.chatapp.utilities.APP_ACTIVITY


class ChatFragment :  Fragment(R.layout.fragment_chat) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Чаты"
    }

}
