package com.example.chatapp.ui.fragments.message_recycler_view.views

interface MessageView {

    val id: String
    val from: String
    val timeStamp: String
    val text: String
    val fileUrl: String

    companion object {
        val MESSAGE_TEXT: Int
            get() = 0
        val MESSAGE_IMAGE: Int
            get() = 1
    }

    fun getTypeView(): Int
}