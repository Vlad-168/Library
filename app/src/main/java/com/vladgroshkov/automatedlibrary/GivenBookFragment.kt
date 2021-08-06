package com.vladgroshkov.automatedlibrary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vladgroshkov.automatedlibrary.utils.GivenBooksUtil

class GivenBookFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_given_book, container, false)
        var givenBooksListView = view.findViewById(R.id.givenBooksListView) as ListView

        val myRef = Firebase.database.getReference("").child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("books")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(books: DataSnapshot) {
                GivenBooksUtil.showGivenBooks(view, requireActivity(), books, givenBooksListView)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ReaderInfoFragment.TAG, error.message)
            }
        })

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            GivenBookFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}