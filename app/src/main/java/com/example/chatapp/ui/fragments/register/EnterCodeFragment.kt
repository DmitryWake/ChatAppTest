package com.example.chatapp.ui.fragments.register

import androidx.fragment.app.Fragment
import com.example.chatapp.R
import com.example.chatapp.database.*
import com.example.chatapp.utilities.*

import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_code.*


class EnterCodeFragment(val phoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.title = phoneNumber
        register_input_code.addTextChangedListener(AppTextWatcher {
            val string = register_input_code.text.toString()
            if (string.length == 6) {
                enterCode()
            }
        })
    }

    private fun enterCode() {
        val code = register_input_code.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dataMap = mutableMapOf<String, Any>()
                dataMap[CHILD_ID] = uid
                dataMap[CHILD_PHONE] = phoneNumber

                REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        if (!it.hasChild(CHILD_USERNAME)) {
                            dataMap[CHILD_USERNAME] = uid
                        }
                        REF_DATABASE_ROOT.child(
                            NODE_PHONES
                        ).child(phoneNumber).setValue(uid)
                            .addOnFailureListener {
                                showToast(it.message.toString())
                            }
                            .addOnSuccessListener {
                                REF_DATABASE_ROOT.child(
                                    NODE_USERS
                                ).child(uid)
                                    .updateChildren(dataMap)
                                    .addOnSuccessListener {
                                        showToast("Добро пожаловать")
                                        restartActivity()
                                    }
                                    .addOnFailureListener {
                                        showToast(it.message.toString())
                                    }
                            }
                    })
            } else showToast(it.exception?.message.toString())
        }
    }
}
