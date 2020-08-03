package com.example.chatapp.ui.fragments

import android.view.View
import com.example.chatapp.R
import com.example.chatapp.models.CommonModel
import com.example.chatapp.models.UserModel
import com.example.chatapp.utilities.*
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*


class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var listenerInfoToolbar: AppValueEventListener
    private lateinit var receivingUser: UserModel
    private lateinit var toolbarInfo: View
    private lateinit var refUser: DatabaseReference

    override fun onResume() {
        super.onResume()
        toolbarInfo = APP_ACTIVITY.toolbar.toolbar_info
        toolbarInfo.visibility = View.VISIBLE
        listenerInfoToolbar = AppValueEventListener {
            receivingUser = it.getUserModel()
            initInfoToolbar()
        }
        refUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        refUser.addValueEventListener(listenerInfoToolbar)
        chat_btn_send_message.setOnClickListener {
            val message = chat_input_message.text.toString()
            if (message.isEmpty()) {
                showToast("Введите сообщение")
            } else sendMessage(message, contact.id, TYPE_TEXT) {
                chat_input_message.setText("")
            }
        }
    }

    private fun initInfoToolbar() {
        if (receivingUser.fullname.isEmpty())
            toolbarInfo.toolbar_chat_fullname.text = contact.fullname
        else toolbarInfo.toolbar_chat_fullname.text = receivingUser.fullname
        toolbarInfo.toolbar_chat_image.downloadAndSetImage(receivingUser.photoUrl)
        toolbarInfo.toolbar_chat_status.text = receivingUser.state
    }

    override fun onPause() {
        super.onPause()
        toolbarInfo.visibility = View.GONE
        refUser.removeEventListener(listenerInfoToolbar)
    }
}