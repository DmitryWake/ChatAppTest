package com.example.chatapp.ui.fragments.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.models.CommonModel
import com.example.chatapp.database.CURRENT_UID
import com.example.chatapp.utilities.*
import kotlinx.android.synthetic.main.message_item.view.*

class SingleChatAdapter : RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {

    private var listMessagesCache = mutableListOf<CommonModel>()

    class SingleChatHolder(view: View) : RecyclerView.ViewHolder(view) {
        //Text
        val blockUserMessage: ConstraintLayout = view.block_user_message
        val chatUserMessage: TextView = view.chat_user_message
        val chatUserMessageTime: TextView = view.chat_user_message_time

        val blockReceivedMessage: ConstraintLayout = view.block_received_message
        val chatReceivedMessage: TextView = view.chat_received_message
        val chatReceivedMessageTime: TextView = view.chat_received_message_time

        //Image
        val blockReceivedImageMessage: ConstraintLayout = view.block_received_image_message
        val chatReceivedImage: ImageView = view.chat_received_image
        val chatReceivedImageMessageTime: TextView = view.chat_received_image_message_time

        val blockUserImageMessage: ConstraintLayout = view.block_user_image_message
        val chatUserImage: ImageView = view.chat_user_image
        val chatUserImageMessageTime: TextView = view.chat_user_image_message_time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return SingleChatHolder(view)
    }

    override fun getItemCount(): Int = listMessagesCache.size
    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
        when (listMessagesCache[position].type) {
            TYPE_MESSAGE_TEXT -> drawMessageText(holder, position)
            TYPE_MESSAGE_IMAGE -> drawMessageImage(holder, position)
        }
    }

    private fun drawMessageImage(holder: SingleChatHolder, position: Int) {
        holder.blockUserMessage.visibility = View.GONE
        holder.blockReceivedMessage.visibility = View.GONE

        if (listMessagesCache[position].from == CURRENT_UID) {
            holder.blockReceivedImageMessage.visibility = View.GONE
            holder.blockUserImageMessage.visibility = View.VISIBLE

            holder.chatUserImage.downloadAndSetImage(listMessagesCache[position].imageUrl)
            holder.chatUserImageMessageTime.text =
                listMessagesCache[position].timeStamp.toString().asTime()
        } else {
            holder.blockReceivedImageMessage.visibility = View.VISIBLE
            holder.blockUserImageMessage.visibility = View.GONE

            holder.chatReceivedImage.downloadAndSetImage(listMessagesCache[position].imageUrl)
            holder.chatReceivedImageMessageTime.text =
                listMessagesCache[position].timeStamp.toString().asTime()
        }
    }

    private fun drawMessageText(holder: SingleChatHolder, position: Int) {
        holder.blockReceivedImageMessage.visibility = View.GONE
        holder.blockUserImageMessage.visibility = View.GONE

        if (listMessagesCache[position].from == CURRENT_UID) {
            holder.blockUserMessage.visibility = View.VISIBLE
            holder.blockReceivedMessage.visibility = View.GONE

            holder.chatUserMessage.text = listMessagesCache[position].text
            holder.chatUserMessageTime.text =
                listMessagesCache[position].timeStamp.toString().asTime()
        } else {
            holder.blockUserMessage.visibility = View.GONE
            holder.blockReceivedMessage.visibility = View.VISIBLE

            holder.chatReceivedMessage.text = listMessagesCache[position].text
            holder.chatReceivedMessageTime.text =
                listMessagesCache[position].timeStamp.toString().asTime()
        }
    }

    fun addItemToBottom(item: CommonModel, onSuccess: () -> Unit) {
        if (!listMessagesCache.contains(item)) {
            listMessagesCache.add(item)
            notifyItemInserted(listMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: CommonModel, onSuccess: () -> Unit) {
        if (!listMessagesCache.contains(item)) {
            listMessagesCache.add(item)
            listMessagesCache.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }
}