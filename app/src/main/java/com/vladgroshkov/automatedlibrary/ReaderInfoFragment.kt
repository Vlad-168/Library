package com.vladgroshkov.automatedlibrary

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.vladgroshkov.automatedlibrary.adapters.BooksListViewAdapter
import com.vladgroshkov.automatedlibrary.adapters.ReaderBooksListViewAdapter
import com.vladgroshkov.automatedlibrary.utils.GivenBooksUtil
import com.vladgroshkov.automatedlibrary.utils.UriUtil.Companion.drawableToUri

private const val USER_ID = "userId"

class ReaderInfoFragment : Fragment() {

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_reader_info, container, false)

        val imageViewReaderLogo = view.findViewById(R.id.imageViewReaderLogo) as ImageView
        val name = view.findViewById(R.id.nameReader) as TextView
        val surnameReader = view.findViewById(R.id.surnameReader) as TextView
        val phoneReaderInfo = view.findViewById(R.id.phoneReaderInfo) as TextView
        val emailReaderInfo = view.findViewById(R.id.emailReaderInfo) as TextView
        val booksReaderListView = view.findViewById(R.id.booksReaderListView) as ListView
        val reverseButtonReaders = view.findViewById(R.id.reverseButtonReaders) as Button
        val giveButtonReaders = view.findViewById(R.id.giveButtonReaders) as Button
        val takeButtonReaders = view.findViewById(R.id.takeButtonReaders) as Button

        val firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val database = Firebase.database
        val myRef = database.getReference("").child("users").child(userId!!)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(user: DataSnapshot) {
                name.text = user.child("name").value.toString()
                surnameReader.text = user.child("surname").value.toString()
                phoneReaderInfo.text = user.child("phone").value.toString()
                emailReaderInfo.text = firebaseUser.email
                var imageUri = Uri.parse(user.child("imageUri").value.toString())
                if (!imageUri.isAbsolute) {
                    imageUri = view.context.drawableToUri(R.drawable.skill)
                }
                imageViewReaderLogo.setImageURI(imageUri)
                val books = user.child("books")
                GivenBooksUtil.showGivenBooks(view, requireActivity(), books, booksReaderListView)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, error.message)
            }
        })

        reverseButtonReaders.setOnClickListener {
            val flag = PupilsFragment.newInstance()
            (activity as MainActivity).replaceFragment(flag, PupilsFragment.TAG)
        }

        giveButtonReaders.setOnClickListener {
            val flag = GiveFragment.newInstance(userId!!)
            (activity as MainActivity).replaceFragment(flag, GiveFragment.TAG)
        }

        takeButtonReaders.setOnClickListener {
            val flag = TakeFragment.newInstance(userId!!)
            (activity as MainActivity).replaceFragment(flag, TakeFragment.TAG)
        }

        return view
    }

    companion object {

        var TAG: String = ReaderInfoFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(userId: String) =
            ReaderInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_ID, userId)
                }
            }
    }
}