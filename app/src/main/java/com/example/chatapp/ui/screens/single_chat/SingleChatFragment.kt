package com.example.chatapp.ui.screens.single_chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.chatapp.R
import com.example.chatapp.database.*
import com.example.chatapp.models.CommonModel
import com.example.chatapp.models.UserModel
import com.example.chatapp.ui.screens.BaseFragment
import com.example.chatapp.ui.message_recycler_view.views.AppViewFactory
import com.example.chatapp.utilities.*
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var listenerInfoToolbar: AppValueEventListener
    private lateinit var receivingUser: UserModel
    private lateinit var toolbarInfo: View
    private lateinit var refUser: DatabaseReference
    private lateinit var refMessages: DatabaseReference
    private lateinit var adapter: SingleChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messagesListener: AppChildEventListener
    private var countMessages = 10
    private var isScrolling = false
    private var isRecording = false
    private var smoothScrollToPosition = true
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var appVoiceRecorder: AppVoiceRecorder

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecyclerView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        appVoiceRecorder = AppVoiceRecorder()
        swipeRefreshLayout = chat_swipe_refresh
        layoutManager = LinearLayoutManager(this.context)
        chat_input_message.addTextChangedListener(AppTextWatcher {
            val string = chat_input_message.text.toString()
            if (string.isEmpty()) {
                chat_btn_send_message.visibility = View.GONE
                chat_btn_attach.visibility = View.VISIBLE
                chat_btn_voice.visibility = View.VISIBLE
                if (!isRecording)
                    AppStates.updateState(AppStates.ONLINE)
            } else {
                chat_btn_send_message.visibility = View.VISIBLE
                chat_btn_attach.visibility = View.GONE
                chat_btn_voice.visibility = View.GONE
                if (!isRecording)
                    AppStates.updateState(AppStates.TYPING)
            }
        })

        chat_btn_attach.setOnClickListener { attachFile() }

        CoroutineScope(Dispatchers.IO).launch {
            chat_btn_voice.setOnTouchListener { v, event ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        AppStates.updateState(AppStates.RECORDING)
                        isRecording = true
                        chat_input_message.setText("Запись")
                        chat_btn_voice.setColorFilter(
                            ContextCompat.getColor(
                                APP_ACTIVITY,
                                R.color.primary
                            )
                        )
                        val messageKey = getMessageKey(contact.id)
                        appVoiceRecorder.startRecord(messageKey)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        AppStates.updateState(AppStates.ONLINE)
                        isRecording = false
                        chat_input_message.setText("")
                        chat_btn_voice.colorFilter = null
                        appVoiceRecorder.stopRecord { file, messageKey ->
                            uploadFileToStorage(Uri.fromFile(file), messageKey, contact.id, TYPE_MESSAGE_VOICE)
                            smoothScrollToPosition = true
                        }
                    }
                }
                true
            }
        }
    }

    private fun attachFile() {
        CropImage.activity()
            //.setAspectRatio(1, 1)
            //.setRequestedSize(300, 300)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecyclerView() {
        recyclerView = chat_recycler_view

        adapter = SingleChatAdapter()

        refMessages = REF_DATABASE_ROOT.child(
            NODE_MESSAGES
        ).child(CURRENT_UID).child(contact.id)

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true) //Все ViewHolders одиннакового размера
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = layoutManager

        messagesListener = AppChildEventListener {
            val message = it.getCommonModel()

            if (smoothScrollToPosition) {
                adapter.addItemToBottom(AppViewFactory.getView(message)) {
                    recyclerView.smoothScrollToPosition(adapter.itemCount)
                }
            } else {
                adapter.addItemToTop(AppViewFactory.getView(message)) {
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        refMessages.limitToLast(countMessages).addChildEventListener(messagesListener)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isScrolling && dy < 0 && layoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }
        })

        swipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        smoothScrollToPosition = false
        isScrolling = false
        countMessages += 10
        refMessages.removeEventListener(messagesListener)
        refMessages.limitToLast(countMessages).addChildEventListener(messagesListener)
    }

    private fun initToolbar() {
        toolbarInfo = APP_ACTIVITY.toolbar.toolbar_info
        toolbarInfo.visibility = View.VISIBLE
        listenerInfoToolbar = AppValueEventListener {
            receivingUser = it.getUserModel()
            initInfoToolbar()
        }
        refUser = REF_DATABASE_ROOT.child(
            NODE_USERS
        ).child(contact.id)
        refUser.addValueEventListener(listenerInfoToolbar)
        chat_btn_send_message.setOnClickListener {
            smoothScrollToPosition = true
            val message = chat_input_message.text.toString()
            if (message.isEmpty()) {
                showToast("Введите сообщение")
            } else sendMessage(
                message,
                contact.id,
                TYPE_TEXT
            ) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            val uri = CropImage.getActivityResult(data).uri
            val messageKey = getMessageKey(contact.id)
            uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_IMAGE)
            smoothScrollToPosition = true
        }
    }

    override fun onPause() {
        super.onPause()
        toolbarInfo.visibility = View.GONE
        refUser.removeEventListener(listenerInfoToolbar)
        refMessages.removeEventListener(messagesListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        appVoiceRecorder.releaseRecorder()
        adapter.onDestroy()
    }
}