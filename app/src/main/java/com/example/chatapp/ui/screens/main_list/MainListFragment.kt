package com.example.chatapp.ui.screens.main_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.database.*
import com.example.chatapp.models.CommonModel
import com.example.chatapp.utilities.*
import kotlinx.android.synthetic.main.fragment_main_list.*


class MainListFragment : Fragment(R.layout.fragment_main_list) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainListAdapter

    private val refMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val refUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val refMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)

    private var listItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "ChatApp"
        APP_ACTIVITY.appDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
    }

    /*TODO Можно переделать в 2 запроса*/
    private fun initRecyclerView() {
        recyclerView = main_list_recycler_view
        adapter = MainListAdapter()
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
                                when (tmpList.first().type) {
                                    TYPE_MESSAGE_VOICE -> newModel.lastMessage = "Голосовое сообщение"
                                    TYPE_MESSAGE_IMAGE -> newModel.lastMessage = "Изображение"
                                    else -> newModel.lastMessage = tmpList.first().text
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
