package com.example.chatapp.ui.fragments.message_recycler_view.view_holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.database.CURRENT_UID
import com.example.chatapp.ui.fragments.message_recycler_view.views.MessageView
import com.example.chatapp.utilities.asTime
import com.example.chatapp.utilities.downloadAndSetImage
import kotlinx.android.synthetic.main.message_item_image.view.*

class HolderImageMessage(view: View): RecyclerView.ViewHolder(view) {
    val blockReceivedImageMessage: ConstraintLayout = view.block_received_image_message
    val chatReceivedImage: ImageView = view.chat_received_image
    val chatReceivedImageMessageTime: TextView = view.chat_received_image_message_time

    val blockUserImageMessage: ConstraintLayout = view.block_user_image_message
    val chatUserImage: ImageView = view.chat_user_image
    val chatUserImageMessageTime: TextView = view.chat_user_image_message_time

    fun drawMessageImage(holder: HolderImageMessage, view: MessageView) {
        if (view.from == CURRENT_UID) {
            holder.blockReceivedImageMessage.visibility = View.GONE
            holder.blockUserImageMessage.visibility = View.VISIBLE

            holder.chatUserImage.downloadAndSetImage(view.fileUrl)
            holder.chatUserImageMessageTime.text =
                view.timeStamp.asTime()
        } else {
            holder.blockReceivedImageMessage.visibility = View.VISIBLE
            holder.blockUserImageMessage.visibility = View.GONE

            holder.chatReceivedImage.downloadAndSetImage(view.fileUrl)
            holder.chatReceivedImageMessageTime.text =
                view.timeStamp.asTime()
        }
    }
}