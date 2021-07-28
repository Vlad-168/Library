package com.vladgroshkov.automatedlibrary

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.layout_navigation_header.*

class NavHeaderMenu : AppCompatActivity() {

    private lateinit var user: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_navigation_header)

        user = FirebaseAuth.getInstance().currentUser!!

        emailTextHeader.text = user.email
        val database = Firebase.database
        val myRef = database.getReference("").child("users").child(user.uid)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (dataSnapshot.child("name").value.toString() + " " +
                        dataSnapshot.child("surname").value.toString()).also { nameTextHeader.text = it }
                imageProfile.setImageURI(
                    Uri.parse(
                        dataSnapshot.child("imageUri").value.toString()
                    )
                )
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}