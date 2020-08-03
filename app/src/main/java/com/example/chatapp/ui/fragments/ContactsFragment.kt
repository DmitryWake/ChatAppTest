package com.example.chatapp.ui.fragments

import com.example.chatapp.R
import com.example.chatapp.utilities.APP_ACTIVITY

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {
    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Контакты"
    }
}