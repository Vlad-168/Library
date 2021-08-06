package com.vladgroshkov.automatedlibrary

import AlertCustomDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vladgroshkov.automatedlibrary.adapters.BooksListViewAdapter
import com.vladgroshkov.automatedlibrary.models.BookModel
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class CatalogBooksFragment : Fragment() {

    private lateinit var user: FirebaseUser

    private lateinit var listViewAdapter: BooksListViewAdapter
    private lateinit var booksListView: ListView
    private lateinit var searchBar: androidx.appcompat.widget.SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_catalog_books, container, false)
        var customDialog = AlertCustomDialog(view.context)

        val database = Firebase.database


        booksListView = view.findViewById(R.id.booksListView) as ListView
        searchBar = view.findViewById(R.id.searchBar) as androidx.appcompat.widget.SearchView
        val fabBooks = view.findViewById(R.id.fabBooks) as FloatingActionButton

        user = FirebaseAuth.getInstance().currentUser!!

        booksListView.isScrollingCacheEnabled = false

        val librarian = database.getReference("").child("users").child(user.uid).child("librarian")
        librarian.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    fabBooks.visibility = View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        fabBooks.setOnClickListener{
            val flag = AddBookFragment.newInstance()
            (activity as MainActivity).replaceFragment(flag, AddBookFragment.TAG)
        }

        val myRef = database.getReference("").child("books")
        myRef.get().addOnSuccessListener { task ->
            getBooksList(task)
        }

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
//                        booksListView.setFilterText(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                booksListView.setFilterText(newText)
                return true
            }
        })

        return view
    }

    private fun getBooksList(dataSnapshot: DataSnapshot) {
        var list: ArrayList<HashMap<String, String>> = ArrayList()
        for (book in dataSnapshot.children) run {
            var hashmap: HashMap<String, String> = HashMap()
            var imageUri = book.child("imageBook").value.toString()
            if (imageUri.equals("null")) {
                hashmap["imageBook"] =
                    requireContext().drawableToUri(R.drawable.skill).toString()
            } else {
                hashmap["imageBook"] = imageUri
            }
            hashmap["nameBook"] = book.child("nameBook").value.toString()
            hashmap["authorBook"] = book.child("authorBook").value.toString()
            hashmap["quantityBook"] = book.child("quantityBook").value.toString()
            hashmap["bookId"] = book.key.toString()
            list.add(hashmap)
        }

        listViewAdapter = BooksListViewAdapter(requireActivity(), list)

        if (!listViewAdapter.isEmpty) {
            booksListView.adapter = listViewAdapter
        } else {
//                    customDialog.showInfoDialog(
//                        "К сожалению, в бибилиотеке пока нет книг",
//                        getDrawable(view.context, R.drawable.splash_image)
//                    )
        }
        booksListView.isTextFilterEnabled = true


//                booksListView.setOnItemClickListener { parent, view, position, id ->
//                    val element = listViewAdapter.getItem(position) as HashMap<*, *>
//                    val frag = BookFragment.newInstance(element["bookId"].toString())
//                    (activity as MainActivity).replaceFragment(frag, BookFragment.TAG)
//                }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        super.onCreateOptionsMenu(menu, inflater)
    }

    fun Context.drawableToUri(drawable: Int): Uri {
        return Uri.parse("android.resource://$packageName/$drawable")
    }

    companion object {
        var TAG: String = "CatalogFragment"

        @JvmStatic
        fun newInstance() =
            CatalogBooksFragment().apply {
            }
    }
}