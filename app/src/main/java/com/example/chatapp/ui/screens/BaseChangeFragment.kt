package com.example.chatapp.ui.screens

import android.view.*
import androidx.fragment.app.Fragment
import com.example.chatapp.R
import com.example.chatapp.utilities.APP_ACTIVITY
import com.example.chatapp.utilities.hideKeyboard


open class BaseChangeFragment(layout: Int) : Fragment(layout) {


    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        APP_ACTIVITY.appDrawer.disableDrawer()
        hideKeyboard()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        APP_ACTIVITY.menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_confirm_change -> change()
        }
        return true
    }

    open fun change() {

    }
}


