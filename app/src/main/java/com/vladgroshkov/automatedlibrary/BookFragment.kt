package com.vladgroshkov.automatedlibrary

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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

private const val BOOK_ID = "bookId"

class BookFragment : Fragment() {

    private var bookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookId = it.getString(BOOK_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_book, container, false)

        val imageViewBookLogo = view.findViewById(R.id.imageViewBookLogo) as ImageView
        val nameBookBook = view.findViewById(R.id.nameBookBook) as TextView
        val authorBookBook = view.findViewById(R.id.authorBookBook) as TextView
        val publishBookBook = view.findViewById(R.id.publishBookBook) as TextView
        val quantityBookBook = view.findViewById(R.id.quantityBookBook) as TextView
        val qrImageViewBook = view.findViewById(R.id.qrImageViewBook) as ImageView
        val catalogButton = view.findViewById(R.id.reverseButtonBook) as Button
        val fab = view.findViewById(R.id.fab) as FloatingActionButton

        var nameBook: String
        var authorBook: String
        var publishBook: String

        val database = Firebase.database

        val user = FirebaseAuth.getInstance().currentUser!!
        val librarian = database.getReference("").child("users").child(user.uid).child("librarian")
        librarian.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    fab.visibility = View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        val myRef = database.getReference("").child("books").child(bookId!!)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(book: DataSnapshot) {
                nameBook = book.child("nameBook").value.toString()
                authorBook = book.child("authorBook").value.toString()
                publishBook = book.child("publishBook").value.toString()
                nameBookBook.text = String.format(
                    "Название: %s", nameBook
                )
                authorBookBook.text = String.format(
                    "Автор(ы): %s", authorBook
                )
                publishBookBook.text = String.format(
                    "Издательство: %s", publishBook
                )
                quantityBookBook.text = String.format(
                    "Осталось книг: %s", book.child("quantityBook").value.toString()
                )
                var imageUri = Uri.parse(book.child("imageBook").value.toString())
                if (!imageUri.isAbsolute) {
                    imageUri = view.context.drawableToUri(R.drawable.books)
                }
                imageViewBookLogo.setImageURI(imageUri)

                fab.setOnClickListener {
                    val flag = EditBookFragment.newInstance(
                        nameBook,
                        authorBook,
                        publishBook,
                        bookId!!,
                        imageUri.toString()
                    )
                    (activity as MainActivity).replaceFragment(flag, EditBookFragment.TAG)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(CatalogBooksFragment.TAG, error.message)
            }
        })

        val bitmapQr = BarcodeEncoder().encodeBitmap(bookId, BarcodeFormat.QR_CODE, 512, 512)
        qrImageViewBook.setImageBitmap(bitmapQr)

        catalogButton.setOnClickListener {
            val flag = CatalogBooksFragment.newInstance()
            (activity as MainActivity).replaceFragment(flag, CatalogBooksFragment.TAG)
        }

        return view
    }

    fun Context.drawableToUri(drawable: Int): Uri {
        return Uri.parse("android.resource://$packageName/$drawable")
    }

    companion object {

        var TAG: String = "BookFragment"

        @JvmStatic
        fun newInstance(bookId: String) =
            BookFragment().apply {
                arguments = Bundle().apply {
                    putString(BOOK_ID, bookId)
                }
            }
    }
}