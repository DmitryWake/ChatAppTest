package com.example.chatapp.ui.message_recycler_view.view_holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.database.CURRENT_UID
import com.example.chatapp.ui.message_recycler_view.views.MessageView
import com.example.chatapp.ui.screens.FullSizeImageFragment
import com.example.chatapp.utilities.asTime
import com.example.chatapp.utilities.downloadAndSetImage
import com.example.chatapp.utilities.replaceFragment
import kotlinx.android.synthetic.main.message_item_image.view.*

class HolderImageMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {
    private val blockReceivedImageMessage: ConstraintLayout = view.block_received_image_message
    private val chatReceivedImage: ImageView = view.chat_received_image
    private val chatReceivedImageMessageTime: TextView = view.chat_received_image_message_time

    private val blockUserImageMessage: ConstraintLayout = view.block_user_image_message
    private val chatUserImage: ImageView = view.chat_user_image
    private val chatUserImageMessageTime: TextView = view.chat_user_image_message_time

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockReceivedImageMessage.visibility = View.GONE
            blockUserImageMessage.visibility = View.VISIBLE

            chatUserImage.downloadAndSetImage(view.fileUrl)
            chatUserImageMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blockReceivedImageMessage.visibility = View.VISIBLE
            blockUserImageMessage.visibility = View.GONE

            chatReceivedImage.downloadAndSetImage(view.fileUrl)
            chatReceivedImageMessageTime.text =
                view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
        if (view.from == CURRENT_UID) {
            chatUserImage.setOnClickListener {
                replaceFragment(FullSizeImageFragment(view.fileUrl))
            }
        }
        else {
            chatReceivedImage.setOnClickListener {
                replaceFragment(FullSizeImageFragment(view.fileUrl))
            }
        }
    }

    override fun onDetach() {
        chatUserImage.setOnClickListener(null)
        chatReceivedImage.setOnClickListener(null)
    }
}