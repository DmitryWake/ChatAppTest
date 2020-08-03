package com.example.chatapp.ui.objects

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.chatapp.R
import com.example.chatapp.ui.fragments.ContactsFragment
import com.example.chatapp.ui.fragments.SettingsFragment
import com.example.chatapp.utilities.APP_ACTIVITY
import com.example.chatapp.utilities.USER
import com.example.chatapp.utilities.downloadAndSetImage
import com.example.chatapp.utilities.replaceFragment
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader

class AppDrawer() {

    private lateinit var drawer: Drawer
    private lateinit var header: AccountHeader
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var currentProfile: ProfileDrawerItem

    fun create() {
        initLoader()
        createHeader()
        createDrawer()
        drawerLayout = drawer.drawerLayout
    }

    fun disableDrawer() {
        drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
    }

    fun enableDrawer() {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener {
            drawer.openDrawer()
        }
    }

    private fun createDrawer() {
        drawer = DrawerBuilder()
            .withActivity(APP_ACTIVITY)
            .withToolbar(APP_ACTIVITY.toolbar)
            .withActionBarDrawerToggle(true)
            .withSelectedItem(-1)
            .withAccountHeader(header)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(100)
                    .withIconTintingEnabled(true)
                    .withName("Создать беседу")
                    .withIcon(R.drawable.ic_menu_create_groups)
                    .withSelectable(false),
                PrimaryDrawerItem().withIdentifier(101)
                    .withIconTintingEnabled(true)
                    .withName("Создать канал")
                    .withIcon(R.drawable.ic_menu_create_channel)
                    .withSelectable(false),
                PrimaryDrawerItem().withIdentifier(102)
                    .withIconTintingEnabled(true)
                    .withName("Контакты")
                    .withIcon(R.drawable.ic_menu_contacts)
                    .withSelectable(false),
                PrimaryDrawerItem().withIdentifier(103)
                    .withIconTintingEnabled(true)
                    .withIcon(R.drawable.ic_menu_phone)
                    .withName("Звонки")
                    .withSelectable(false),
                PrimaryDrawerItem().withIdentifier(104)
                    .withIconTintingEnabled(true)
                    .withName("Избранное")
                    .withIcon(R.drawable.ic_menu_favorites)
                    .withSelectable(false),
                PrimaryDrawerItem().withIdentifier(105)
                    .withIconTintingEnabled(true)
                    .withName("Настройки")
                    .withIcon(R.drawable.ic_menu_settings)
                    .withSelectable(false),
                DividerDrawerItem(),
                PrimaryDrawerItem().withIdentifier(106)
                    .withIconTintingEnabled(true)
                    .withName("Поделиться приложением")
                    .withIcon(R.drawable.ic_menu_invate)
                    .withSelectable(false),
                PrimaryDrawerItem().withIdentifier(100)
                    .withIconTintingEnabled(true)
                    .withName("Справка")
                    .withIcon(R.drawable.ic_menu_help)
                    .withSelectable(false)
            )
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    clickToItem(position)
                    return false
                }
            })
            .build()
    }

    private fun clickToItem(position: Int) {
        when (position) {
            6 -> APP_ACTIVITY.replaceFragment(SettingsFragment())
            3 -> APP_ACTIVITY.replaceFragment(ContactsFragment())
        }
    }

    private fun createHeader() {
        currentProfile = ProfileDrawerItem().withName(USER.fullname).withEmail(USER.phone)
            .withIcon(USER.photoUrl)
            .withIdentifier(200)
        header = AccountHeaderBuilder()
            .withActivity(APP_ACTIVITY)
            .withHeaderBackground(R.drawable.header)
            .addProfiles(
                currentProfile
            ).build()
    }

    fun updateHeader() {
        currentProfile.withName(USER.fullname).withEmail(USER.phone)
            .withIcon(USER.photoUrl)
        header.updateProfile(currentProfile)
    }

    private fun initLoader() {
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                imageView.downloadAndSetImage(uri.toString())
            }
        })
    }
}