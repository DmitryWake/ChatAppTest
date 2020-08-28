package com.example.chatapp.ui.screens

import com.example.chatapp.R
import com.example.chatapp.database.*
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
            REF_DATABASE_ROOT.child(
                NODE_USERNAMES
            ).addListenerForSingleValueEvent(
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
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(newUsername).setValue(
            CURRENT_UID
        )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername(newUsername)
                }
            }
    }
}
