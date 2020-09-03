package com.example.chatapp.database

import android.net.Uri
import com.example.chatapp.R
import com.example.chatapp.models.CommonModel
import com.example.chatapp.models.UserModel
import com.example.chatapp.utilities.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

fun initFireBase() {
    AUTH =
        FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
    USER =
        UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()

}

inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(
        CURRENT_UID
    ).child(CHILD_PHOTO_URL)
        .setValue(url)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener {
            function(it.toString())
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}

inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(
        CURRENT_UID
    )
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER =
                it.getValue(UserModel::class.java)
                    ?: UserModel()
            if (USER.username.isEmpty()) {
                USER.username =
                    CURRENT_UID
            }
            function()
        })
}

fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(
            AppValueEventListener {
                it.children.forEach { snapshot ->
                    arrayContacts.forEach { contact ->
                        if (snapshot.key == contact.phone.formatPhoneNumber()) {
                            REF_DATABASE_ROOT.child(
                                NODE_PHONES_CONTACTS
                            ).child(CURRENT_UID)
                                .child(snapshot.value.toString())
                                .child(CHILD_ID)
                                .setValue(snapshot.value.toString())
                                .addOnFailureListener {
                                    showToast(it.message.toString())
                                }

                            REF_DATABASE_ROOT.child(
                                NODE_PHONES_CONTACTS
                            ).child(CURRENT_UID)
                                .child(snapshot.value.toString())
                                .child(CHILD_FULLNAME)
                                .setValue(contact.fullname)
                                .addOnFailureListener {
                                    showToast(it.message.toString())
                                }
                        }
                    }
                }
            })
    }
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()

fun sendMessage(message: String, receivingUserID: String, typeText: String, function: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"
    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] =
        CURRENT_UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] =
        ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun updateCurrentUsername(newUsername: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(
        CURRENT_UID
    ).child(CHILD_USERNAME).setValue(newUsername)
        .addOnSuccessListener {
            showToast(
                APP_ACTIVITY.getString(
                    R.string.toast_data_update
                )
            )
            deleteOldUsername(newUsername)
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

private fun deleteOldUsername(newUsername: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(
        USER.username
    ).removeValue()
        .addOnSuccessListener {
            showToast(
                APP_ACTIVITY.getString(
                    R.string.toast_data_update
                )
            )
            APP_ACTIVITY.supportFragmentManager.popBackStack()
            USER.username = newUsername
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun setBioToDatabase(newBio: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(
        CURRENT_UID
    ).child(CHILD_BIO).setValue(newBio)
        .addOnSuccessListener {
            showToast(
                APP_ACTIVITY.getString(
                    R.string.toast_data_update
                )
            )
            USER.bio = newBio
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun setFullnameToDatabase(fullname: String) {
    REF_DATABASE_ROOT.child(
        NODE_USERS
    ).child(CURRENT_UID).child(
        CHILD_FULLNAME
    ).setValue(fullname).addOnSuccessListener {
        showToast(
            APP_ACTIVITY.getString(
                R.string.toast_data_update
            )
        )
        USER.fullname = fullname
        APP_ACTIVITY.appDrawer.updateHeader()
        APP_ACTIVITY.supportFragmentManager.popBackStack()
    }.addOnFailureListener {
        showToast(it.message.toString())
    }
}

fun sendMessageAsFile(
    receivingUserID: String,
    fileUrl: String,
    messageKey: String,
    typeMessage: String,
    filename: String
) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] =
        CURRENT_UID
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] =
        ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun getMessageKey(id: String) =
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(
        CURRENT_UID
    ).child(id)
        .push().key.toString()

fun uploadFileToStorage(
    uri: Uri,
    messageKey: String,
    receivedID: String,
    typeMessage: String,
    filename: String = ""
) {
    val path = REF_STORAGE_ROOT.child(
        FOLDER_FILES
    ).child(messageKey)

    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFile(
                receivedID,
                it,
                messageKey,
                typeMessage,
                filename
            )
        }
    }
}

fun getFileFromStorage(file: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(file)
        .addOnSuccessListener { function() }
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun checkVersion() {
    REF_DATABASE_ROOT.child(CHILD_VERSION).addValueEventListener(AppValueEventListener {
        if (APP_VERSION.toDouble() > it.value.toString().toDouble())
            updateVersionInDatabase()
        else if (APP_VERSION.toDouble() != it.value.toString().toDouble())
            updateVersion()
    })
}

fun updateVersionInDatabase() {
    REF_DATABASE_ROOT.child(CHILD_VERSION).setValue(APP_VERSION).addOnFailureListener {
        showToast(it.message.toString())
    }
}

fun saveToMainList(id: String, type: String) {
    val refUser = "$NODE_MAIN_LIST/$CURRENT_UID/$id"
    val refReceiver = "$NODE_MAIN_LIST/$id/$CURRENT_UID"

    val mapUser = hashMapOf<String, Any>()
    val mapReceiver = hashMapOf<String, Any>()

    mapUser[CHILD_ID] = id
    mapUser[CHILD_TYPE] = type

    mapReceiver[CHILD_ID] = CURRENT_UID
    mapReceiver[CHILD_TYPE] = type

    val commonMap = hashMapOf<String, Any>()
    commonMap[refUser] = mapUser
    commonMap[refReceiver] = mapReceiver

    REF_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun deleteChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID).child(id).removeValue()
        .addOnFailureListener {
            showToast(it.message.toString())
        }.addOnSuccessListener { function() }
}

fun clearChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(id).removeValue()
        .addOnFailureListener {
            showToast(it.message.toString())
        }.addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_MESSAGES).child(id).child(CURRENT_UID).removeValue()
                .addOnFailureListener {
                    showToast(it.message.toString())
                }.addOnSuccessListener { function() }
        }
}
