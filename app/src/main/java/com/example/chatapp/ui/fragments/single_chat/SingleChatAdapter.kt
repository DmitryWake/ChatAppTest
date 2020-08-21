package com.example.chatapp.ui.fragments.single_chat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.database.CURRENT_UID
import com.example.chatapp.ui.fragments.message_recycler_view.view_holder.AppHolderFactory
import com.example.chatapp.ui.fragments.message_recycler_view.view_holder.HolderImageMessage
import com.example.chatapp.ui.fragments.message_recycler_view.view_holder.HolderTextMessage
import com.example.chatapp.ui.fragments.message_recycler_view.views.MessageView
import com.example.chatapp.utilities.asTime
import com.example.chatapp.utilities.downloadAndSetImage

class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listMessagesCache = mutableListOf<MessageView>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return listMessagesCache[position].getTypeView()
    }

    override fun getItemCount(): Int = listMessagesCache.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HolderImageMessage -> drawMessageImage(holder, position)
            is HolderTextMessage -> drawMessageText(holder, position)
            else -> {
            }
        }
    }

    private fun drawMessageImage(holder: HolderImageMessage, position: Int) {
        if (listMessagesCache[position].from == CURRENT_UID) {
            holder.blockReceivedImageMessage.visibility = View.GONE
            holder.blockUserImageMessage.visibility = View.VISIBLE

            holder.chatUserImage.downloadAndSetImage(listMessagesCache[position].fileUrl)
            holder.chatUserImageMessageTime.text =
                listMessagesCache[position].timeStamp.asTime()
        } else {
            holder.blockReceivedImageMessage.visibility = View.VISIBLE
            holder.blockUserImageMessage.visibility = View.GONE

            holder.chatReceivedImage.downloadAndSetImage(listMessagesCache[position].fileUrl)
            holder.chatReceivedImageMessageTime.text =
                listMessagesCache[position].timeStamp.asTime()
        }
    }

    private fun drawMessageText(holder: HolderTextMessage, position: Int) {
        if (listMessagesCache[position].from == CURRENT_UID) {
            holder.blockUserMessage.visibility = View.VISIBLE
            holder.blockReceivedMessage.visibility = View.GONE

            holder.chatUserMessage.text = listMessagesCache[position].text
            holder.chatUserMessageTime.text =
                listMessagesCache[position].timeStamp.asTime()
        } else {
            holder.blockUserMessage.visibility = View.GONE
            holder.blockReceivedMessage.visibility = View.VISIBLE

            holder.chatReceivedMessage.text = listMessagesCache[position].text
            holder.chatReceivedMessageTime.text =
                listMessagesCache[position].timeStamp.asTime()
        }
    }

    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit) {
        if (!listMessagesCache.contains(item)) {
            listMessagesCache.add(item)
            notifyItemInserted(listMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit) {
        if (!listMessagesCache.contains(item)) {
            listMessagesCache.add(item)
            listMessagesCache.sortBy { it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }
}