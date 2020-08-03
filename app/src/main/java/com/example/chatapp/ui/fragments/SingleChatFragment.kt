package com.example.chatapp.ui.fragments

import android.view.View
import com.example.chatapp.R
import com.example.chatapp.models.CommonModel
import com.example.chatapp.utilities.APP_ACTIVITY
import kotlinx.android.synthetic.main.activity_main.view.*


class SingleChatFragment(contact: CommonModel) : BaseFragment(R.layout.fragment_single_chat) {
    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.toolbar.toolbar_info.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        APP_ACTIVITY.toolbar.toolbar_info.visibility = View.GONE
    }
}