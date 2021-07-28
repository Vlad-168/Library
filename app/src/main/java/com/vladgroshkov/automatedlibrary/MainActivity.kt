package com.vladgroshkov.automatedlibrary

import android.Manifest
import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_navigation_header.*
import kotlinx.android.synthetic.main.layout_navigation_header.view.*
import android.view.MenuInflater





class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var user: FirebaseUser

    private var drawerToggle: ActionBarDrawerToggle? = null

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        drawerToggle =
            ActionBarDrawerToggle(this, drawer_layout, R.string.drawer_open, R.string.drawer_close)

        drawer_layout.addDrawerListener(drawerToggle!!)
        drawerToggle!!.syncState()
        navigationView.itemIconTintList = null
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        user = FirebaseAuth.getInstance().currentUser!!
        Log.d(TAG, user.email.toString())

        val navigationView = findViewById<View>(R.id.navigationView) as NavigationView
        val database = Firebase.database
        val myRef = database.getReference("").child("users").child(user.uid).child("librarian")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    navigationView.inflateMenu(R.menu.navigation_menu_librarian)
                } else {
                    navigationView.inflateMenu(R.menu.navigation_menu)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        navigationView.setNavigationItemSelectedListener(this)


        isUserHaveProfile()
    }


    private fun isUserHaveProfile() {
        val database = Firebase.database
        val myRef = database.getReference("").child("users").child(user.uid)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    changeToCatalogBookFragment()
                } else {
                    changeToProfileFragment()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                changeToProfileFragment()
            }
        })
    }

    private fun changeToCatalogBookFragment() {
        setProfileInfo()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_container, CatalogBooksFragment.newInstance())
            .commit()
    }

    private fun changeToProfileFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_container, ProfileFragment.newInstance())
            .commit()
    }

    @SuppressLint("ResourceType")
    private fun setProfileInfo() {
        emailTextHeader.text = user.email
        val database = Firebase.database
        val myRef = database.getReference("").child("users").child(user.uid)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                nameTextHeader.text = dataSnapshot.child("name").value.toString()
                surnameTextHeader.text = dataSnapshot.child("surname").value.toString()
                imageProfile.setImageURI(
                    Uri.parse(
                        dataSnapshot.child("imageUri").value.toString()
                    )
                )
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                drawer_layout!!.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        val fragmentClass: Class<*> = when (item.itemId) {
            R.id.menuBooks -> CatalogBooksFragment::class.java
            R.id.menuProfile -> ProfileFragment::class.java
            R.id.menuExcellence -> ExcellenceFragment::class.java
            R.id.menuStopwatch -> StopwtachFragment::class.java
            R.id.menuPupils -> PupilsFragment::class.java
            else -> CatalogBooksFragment::class.java
        }
        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_container, fragment!!)
            .commit()

        title = item.title
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}