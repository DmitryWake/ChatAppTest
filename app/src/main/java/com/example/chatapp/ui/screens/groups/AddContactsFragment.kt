package com.example.chatapp.ui.screens.groups

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.database.*
import com.example.chatapp.models.CommonModel
import com.example.chatapp.ui.screens.base.BaseFragment
import com.example.chatapp.utilities.*
import kotlinx.android.synthetic.main.fragment_add_contacts.*
import kotlinx.android.synthetic.main.fragment_main_list.*


class AddContactsFragment : BaseFragment(R.layout.fragment_add_contacts) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddContactsAdapter

    private val refMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val refUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val refMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)

    private var listItems = listOf<CommonModel>()

    companion object {
        var listContacts = mutableListOf<CommonModel>()
    }

    override fun onResume() {
        super.onResume()
        listContacts.clear()
        APP_ACTIVITY.title = "Добавить участников"
        hideKeyboard()
        initRecyclerView()
        add_contacts_btn_next.setOnClickListener {
            if (listContacts.isNotEmpty())
                replaceFragment(CreateGroupFragment(listContacts))
            else
                showToast("Выберете участников")
        }
    }

    private fun initRecyclerView() {
        recyclerView = add_contacts_recycler_view
        adapter = AddContactsAdapter()
        //1 запрос
        refMainList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            listItems =
                dataSnapshot.children.map { it.getCommonModel() } //Преобразование в модель

            listItems.forEach { model ->
                //2 запрос
                refUsers.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot_2 ->
                        val newModel = dataSnapshot_2.getCommonModel()
                        //3 запрос
                        refMessages.child(model.id).limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot_3 ->
                                val tmpList = dataSnapshot_3.children.map { it.getCommonModel() }
                                if (tmpList.isEmpty()) {
                                    newModel.lastMessage = ""
                                } else {
                                    when (tmpList.first().type) {
                                        TYPE_MESSAGE_VOICE -> newModel.lastMessage =
                                            "Голосовое сообщение"
                                        TYPE_MESSAGE_IMAGE -> newModel.lastMessage = "Изображение"
                                        else -> newModel.lastMessage = tmpList.first().text
                                    }
                                    if (tmpList.first().from == CURRENT_UID)
                                        newModel.lastMessage = "Вы: ${newModel.lastMessage}"
                                }
                                if (newModel.fullname.isEmpty())
                                    newModel.fullname = newModel.phone

                                adapter.updateListItems(newModel)
                            })
                    })
            }
        })
        recyclerView.adapter = adapter
    }
}
