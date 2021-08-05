package com.vladgroshkov.automatedlibrary

import AlertCustomDialog
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vladgroshkov.automatedlibrary.utils.ValidationUtil


class ProfileFragment : Fragment() {

    private val pickImage = 100
    private var imageUri: Uri? = null

    private lateinit var name: String
    private lateinit var surname: String
    private lateinit var phone: String
    private lateinit var customDialog: AlertCustomDialog
    private lateinit var user: FirebaseUser

    private lateinit var imageViewProfile: ImageView
    private lateinit var reverseButtonBook: Button
    private lateinit var nameEditTextProfile: EditText
    private lateinit var surnameEditTextProfile: EditText
    private lateinit var phoneEditTextProfile: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        user = FirebaseAuth.getInstance().currentUser!!

        imageViewProfile = view.findViewById<ImageView>(R.id.imageViewProfile)
        reverseButtonBook = view.findViewById(R.id.reverseButtonBook)
        nameEditTextProfile = view.findViewById<EditText>(R.id.nameEditTextProfile)
        surnameEditTextProfile = view.findViewById<EditText>(R.id.surnameEditTextProfile)
        phoneEditTextProfile = view.findViewById<EditText>(R.id.phoneEditTextProfile)

        customDialog = AlertCustomDialog(view.context)
        imageViewProfile.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        isUserHaveProfile()

        reverseButtonBook.setOnClickListener {
            name = nameEditTextProfile.text.toString()
            surname = surnameEditTextProfile.text.toString()
            phone = phoneEditTextProfile.text.toString()
            if (!ValidationUtil.isStringValid(name)) {
                customDialog.showErrorDialog(getString(R.string.profile_name_error))
            } else if (!ValidationUtil.isStringValid(surname)) {
                customDialog.showErrorDialog(getString(R.string.profile_surname_error))
            } else if (!ValidationUtil.isPhoneValid(phone)) {
                customDialog.showErrorDialog(getString(R.string.profile_phone_error))
            } else {
                if (imageUri == null) {
                    imageUri = view.context.drawableToUri(R.drawable.skill)
                }
                val database = Firebase.database

                val myUserId = user.uid
                val myRef = database.getReference("").child("users").child(myUserId)

                myRef.child("name").setValue(name)
                myRef.child("surname").setValue(surname)
                myRef.child("phone").setValue(phone)
                myRef.child("imageUri").setValue(imageUri.toString())

                startActivity(Intent(view.context, MainActivity::class.java))
            }

        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageViewProfile.setImageURI(imageUri)
        }
    }

    fun Context.drawableToUri(drawable: Int): Uri {
        return Uri.parse("android.resource://$packageName/$drawable")
    }

    private fun isUserHaveProfile() {
        val database = Firebase.database

        val myRef = database.getReference("").child("users").child(user.uid)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    setProfileInfo()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    @SuppressLint("ResourceType")
    private fun setProfileInfo() {
        val database = Firebase.database

        val myRef = database.getReference("").child("users").child(user.uid)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                nameEditTextProfile.setText(dataSnapshot.child("name").value.toString())
                surnameEditTextProfile.setText(dataSnapshot.child("surname").value.toString())
                phoneEditTextProfile.setText(dataSnapshot.child("phone").value.toString())
                imageUri = Uri.parse(dataSnapshot.child("imageUri").value.toString())
                imageViewProfile.setImageURI(imageUri)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
            }
    }
}