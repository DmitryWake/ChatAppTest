package com.example.chatapp.ui.screens.groups

import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.models.CommonModel
import com.example.chatapp.ui.screens.base.BaseFragment
import com.example.chatapp.utilities.*
import kotlinx.android.synthetic.main.fragment_create_group.*

class CreateGroupFragment(private var listContacts: List<CommonModel>): BaseFragment(R.layout.fragment_create_group) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddContactsAdapter

    private var listItems = listOf<CommonModel>()

    companion object {
        var listContacts = mutableListOf<CommonModel>()
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.create_group)
        hideKeyboard()
        initRecyclerView()
        create_group_btn_complete.setOnClickListener {
            showToast("Работает! :)")
        }
        create_group_input_name.requestFocus()
        create_group_counts.text = getPlurals(listContacts.size)
    }

    private fun initRecyclerView() {
        recyclerView = create_group_recycler_view
        adapter = AddContactsAdapter()

        recyclerView.adapter = adapter
        listContacts.forEach { adapter.updateListItems(it) }
    }

}