package com.vladgroshkov.automatedlibrary.utils

import AlertCustomDialog
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.ListView
import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vladgroshkov.automatedlibrary.BookFragment
import com.vladgroshkov.automatedlibrary.MainActivity
import com.vladgroshkov.automatedlibrary.R
import com.vladgroshkov.automatedlibrary.ReaderInfoFragment
import com.vladgroshkov.automatedlibrary.adapters.ReaderBooksListViewAdapter
import com.vladgroshkov.automatedlibrary.utils.UriUtil.Companion.drawableToUri

class GivenBooksUtil {
    companion object {
        fun showGivenBooks(view: View, activity: Activity, books: DataSnapshot, booksReaderListView: ListView) {
            var customDialog = AlertCustomDialog(view.context)
            var listBooks: ArrayList<HashMap<String, String>> = ArrayList()
            var listViewAdapter = ReaderBooksListViewAdapter(activity, listBooks)
            val database = Firebase.database
            for (book in books.children) run {
                val myRefBooks =
                    database.getReference("").child("books").child(book.key.toString())
                myRefBooks.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(book: DataSnapshot) {
                        var hashmap: HashMap<String, String> = HashMap()
                        var imageUri = book.child("imageBook").value.toString()
                        if (imageUri.equals("null")) {
                            hashmap["imageBook"] =
                                view.context.drawableToUri(R.drawable.skill).toString()
                        } else {
                            hashmap["imageBook"] = imageUri
                        }
                        hashmap["nameBook"] = book.child("nameBook").value.toString()
                        hashmap["authorBook"] = book.child("authorBook").value.toString()
                        hashmap["bookId"] = book.key.toString()
                        listBooks.add(hashmap)
                        listViewAdapter = ReaderBooksListViewAdapter(activity, listBooks)
                        if (!listViewAdapter.isEmpty) {
                            booksReaderListView.adapter = listViewAdapter
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(ReaderInfoFragment.TAG, error.message)
                    }
                })
            }


            booksReaderListView.setOnItemClickListener{ parent, view, position, id ->
                val element = listViewAdapter.getItem(position) as HashMap<*, *>
                val frag = BookFragment.newInstance(element["bookId"].toString())
                (activity as MainActivity).replaceFragment(frag, BookFragment.TAG)
            }

        }
    }
}