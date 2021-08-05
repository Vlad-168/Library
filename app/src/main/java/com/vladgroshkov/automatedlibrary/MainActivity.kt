package com.vladgroshkov.automatedlibrary


import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
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





class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var user: FirebaseUser
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private var drawerToggle: ActionBarDrawerToggle? = null
    private lateinit var toolbar: Toolbar
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        navigationView = findViewById<NavigationView>(R.id.navigationView)

        setSupportActionBar(toolbar)
        drawerToggle =
            ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
            )

        drawerLayout.addDrawerListener(drawerToggle!!)
        drawerToggle!!.syncState()
        navigationView.itemIconTintList = null
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        user = FirebaseAuth.getInstance().currentUser!!
        Log.d(TAG, user.email.toString())

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
        toolbar.setTitle(R.string.catalog_books_menu)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_container, CatalogBooksFragment.newInstance())
            .commit()
    }

    private fun changeToProfileFragment() {
        toolbar.setTitle(R.string.profile_menu)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_container, ProfileFragment.newInstance())
            .commit()
    }

    @SuppressLint("ResourceType")
    private fun setProfileInfo() {
        val navHeader = navigationView.inflateHeaderView(R.layout.layout_navigation_header)
        val emailTextHeader = navHeader.findViewById<TextView>(R.id.emailTextHeader)
        val nameTextHeader = navHeader.findViewById<TextView>(R.id.nameTextHeader)
        val surnameTextHeader = navHeader.findViewById<TextView>(R.id.surnameTextHeader)
        val imageProfile = navHeader.findViewById<ImageView>(R.id.imageProfile)
        emailTextHeader.text = user.email
        val database = Firebase.database

        val myRef = database.getReference("").child("users").child(user.uid)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                nameTextHeader.text = dataSnapshot.child("name").value.toString()
                surnameTextHeader.text =
                    dataSnapshot.child("surname").value.toString()
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
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
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
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_container, fragment, tag).addToBackStack("").commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}