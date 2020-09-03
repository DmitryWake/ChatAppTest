package com.example.chatapp.ui.message_recycler_view.view_holder

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.database.CURRENT_UID
import com.example.chatapp.database.getFileFromStorage
import com.example.chatapp.ui.message_recycler_view.views.MessageView
import com.example.chatapp.utilities.*
import kotlinx.android.synthetic.main.message_item_file.view.*
import java.io.File
import java.lang.Exception

class HolderFileMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private val blockReceivedFileMessage: ConstraintLayout = view.block_received_file_message
    private val chatReceivedFileMessageTime: TextView = view.chat_received_file_message_time
    private val chatReceivedFileBtnDownload: ImageView = view.chat_received_btn_download
    private val chatReceivedFileName: TextView = view.chat_received_file_name
    private var chatReceivedProgressBar: ProgressBar = view.chat_received_progress_bar

    private val blockUserFileMessage: ConstraintLayout = view.block_user_file_message
    private val chatUserFileMessageTime: TextView = view.chat_user_file_message_time
    private val chatUserFileBtnDownload: ImageView = view.chat_user_btn_download
    private val chatUserFileName: TextView = view.chat_user_file_name
    private var chatUserProgressBar: ProgressBar = view.chat_user_progress_bar

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockReceivedFileMessage.visibility = View.GONE
            blockUserFileMessage.visibility = View.VISIBLE
            chatUserFileName.text = view.text
            chatUserFileMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blockReceivedFileMessage.visibility = View.VISIBLE
            blockUserFileMessage.visibility = View.GONE
            chatReceivedFileName.text = view.text
            chatReceivedFileMessageTime.text =
                view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
        if (view.from == CURRENT_UID)
            chatUserFileBtnDownload.setOnClickListener { clickBtnFile(view) }
        else
            chatReceivedFileBtnDownload.setOnClickListener { clickBtnFile(view) }
    }

    private fun clickBtnFile(view: MessageView) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )
        if (file.exists() && file.length() > 0 && file.isFile) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.fromFile(file)
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(APP_ACTIVITY, intent, null)
            return
        }

        if (view.from == CURRENT_UID) {
            chatUserFileBtnDownload.visibility = View.INVISIBLE
            chatUserProgressBar.visibility = View.VISIBLE
        } else {
            chatReceivedFileBtnDownload.visibility = View.INVISIBLE
            chatReceivedProgressBar.visibility = View.VISIBLE
        }
        try {
            if (checkPermission(WRITE_FILES)) {
                file.createNewFile()
                getFileFromStorage(file, view.fileUrl) {
                    if (view.from == CURRENT_UID) {
                        chatUserFileBtnDownload.visibility = View.VISIBLE
                        chatUserProgressBar.visibility = View.INVISIBLE
                    } else {
                        chatReceivedFileBtnDownload.visibility = View.VISIBLE
                        chatReceivedProgressBar.visibility = View.INVISIBLE
                    }
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    override fun onDetach() {
        chatUserFileBtnDownload.setOnClickListener(null)
        chatReceivedFileBtnDownload.setOnClickListener(null)
    }
}