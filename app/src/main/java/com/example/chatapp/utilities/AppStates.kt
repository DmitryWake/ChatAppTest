package com.example.chatapp.utilities

import com.example.chatapp.database.*

enum class AppStates(val state: String) {
    ONLINE("В сети"),
    OFFLINE("Был недавно"),
    TYPING("Печатает..."),
    RECORDING("Записывает голосовое сообщение...");

    companion object {
        fun updateState(appStates: AppStates) {
            if (AUTH.currentUser != null) {
                REF_DATABASE_ROOT.child(
                    NODE_USERS
                ).child(CURRENT_UID).child(
                    CHILD_STATE
                )
                    .setValue(appStates.state)
                    .addOnSuccessListener {
                        USER.state = appStates.state
                    }
                    .addOnFailureListener {
                        showToast(it.message.toString())
                    }
            }
        }
    }
}