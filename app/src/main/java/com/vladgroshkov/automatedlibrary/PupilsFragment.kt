package com.vladgroshkov.automatedlibrary

import AlertCustomDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.SearchView
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
import com.vladgroshkov.automatedlibrary.adapters.ReadersListViewAdapter
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class PupilsFragment : Fragment() {

    private lateinit var user: FirebaseUser

    private lateinit var listViewAdapter: ReadersListViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_pupils, container, false)
        var customDialog = AlertCustomDialog(view.context)

        val database = Firebase.database


        val readersListView = view.findViewById(R.id.readersListView) as ListView
        val searchBarReaders = view.findViewById(R.id.searchBarReaders) as androidx.appcompat.widget.SearchView

        var list: ArrayList<HashMap<String, String>> = ArrayList()
        user = FirebaseAuth.getInstance().currentUser!!

        readersListView.isScrollingCacheEnabled = false
        val myRef = database.getReference("").child("users")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                for (user in datasnapshot.children) run {
                    var hashmap: HashMap<String, String> = HashMap()
                    var imageUri = user.child("imageUri").value.toString()
                    if (imageUri.equals("null")) {
                        hashmap["imageUri"] =
                            view.context.drawableToUri(R.drawable.skill).toString()
                    } else {
                        hashmap["imageUri"] = imageUri
                    }
                    hashmap["name"] = user.child("name").value.toString()
                    hashmap["surname"] = user.child("surname").value.toString()
                    hashmap["userId"] = user.key.toString()
                    list.add(hashmap)
                }

                listViewAdapter = ReadersListViewAdapter(requireActivity(), list)

                if (!listViewAdapter.isEmpty) {
                    readersListView.adapter = listViewAdapter
                } else {
                    customDialog.showInfoDialog(
                        "К сожалению, в бибилиотеке нет зарегистрированных читателей",
                        getDrawable(view.context, R.drawable.splash_image)
                    )
                }
                readersListView.isTextFilterEnabled = true
                var editSearchText =
                    searchBarReaders.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
                editSearchText.setTextColor(view.resources.getColor(R.color.text))
                searchBarReaders.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
                    .setImageResource(R.drawable.search_icon)
                searchBarReaders.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        if (TextUtils.isEmpty(newText)) {
                            readersListView.clearTextFilter()
                        } else {
                            readersListView.setFilterText(newText)
                        }
                        return true
                    }
                })

                readersListView.setOnItemClickListener { parent, view, position, id ->
                    val element = listViewAdapter.getItem(position) as HashMap<*, *>
                    val frag = ReaderInfoFragment.newInstance(element["userId"].toString())
                    (activity as MainActivity).replaceFragment(frag, ReaderInfoFragment.TAG)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, error.message)
            }
        })
        return view
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