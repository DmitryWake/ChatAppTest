package com.example.chatapp.ui.screens

/*TODO Сделать картинку в чате кликабельной*/

import com.example.chatapp.R
import com.example.chatapp.utilities.APP_ACTIVITY
import com.example.chatapp.utilities.downloadAndSetImage
import kotlinx.android.synthetic.main.fragment_full_size_image.*

class FullSizeImageFragment(val imageUrl: String) : BaseFragment(R.layout.fragment_full_size_image) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Фотография"
        initFields()
    }

    fun initFields() {
        full_size_image.downloadAndSetImage(imageUrl)
    }
}