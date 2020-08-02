package com.example.chatapp.ui.fragments

import com.example.chatapp.R
import com.example.chatapp.utilities.*
import kotlinx.android.synthetic.main.fragment_change_username.*
import java.util.*

class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {

    lateinit var newUsername: String

    override fun onResume() {
        super.onResume()
        settings_input_username.setText(USER.username)
    }

    override fun change() {
        newUsername = settings_input_username.text.toString().toLowerCase(Locale.getDefault())
        if (newUsername.isEmpty()) {
            showToast("Введите имя пользователя (username)")
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES).addListenerForSingleValueEvent(
                AppValueEventListener {
                if (it.hasChild(newUsername)) {
                    showToast("Такой пользователь уже существует")
                } else {
                    changeUsername()
                }
            })
        }
    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(newUsername).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername()
                }
            }
    }

    private fun updateCurrentUsername() {
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME).setValue(newUsername)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    deleteOldUsername()
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }

    private fun deleteOldUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.isSuccessful) {
                        showToast(getString(R.string.toast_data_update))
                        fragmentManager?.popBackStack()
                        USER.username = newUsername
                    } else {
                        showToast(it.exception?.message.toString())
                    }
                }
            }
    }
}
