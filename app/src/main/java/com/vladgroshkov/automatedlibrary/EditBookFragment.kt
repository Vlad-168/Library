package com.vladgroshkov.automatedlibrary

import AlertCustomDialog
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vladgroshkov.automatedlibrary.utils.ValidationUtil

private const val NAME_BOOK = "name_book"
private const val AUTHOR_BOOK = "author_book"
private const val PUBLISH_BOOK = "publish_book"
private const val BOOK_ID = "book_id"
private const val IMAGE_URI = "imageUri"


class EditBookFragment : Fragment() {

    private var nameBook: String? = null
    private var authorBook: String? = null
    private var publishBook: String? = null
    private var book_id: String? = null
    private var imageUri: String? = null

    private lateinit var image_uri: Uri


    private val pickImage = 100

    private lateinit var customDialog: AlertCustomDialog
    private lateinit var user: FirebaseUser

    private lateinit var imageViewBook: ImageView
    private lateinit var saveButtonBook: Button
    private lateinit var nameEditTextBook: EditText
    private lateinit var authorEditTextBook: EditText
    private lateinit var publishEditTextBook: EditText
    private lateinit var deleteButtonBook: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nameBook = it.getString(NAME_BOOK)
            authorBook = it.getString(AUTHOR_BOOK)
            publishBook = it.getString(PUBLISH_BOOK)
            book_id = it.getString(BOOK_ID)
            imageUri = it.getString(IMAGE_URI)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_book, container, false)

        imageViewBook = view.findViewById(R.id.imageViewBook)
        saveButtonBook = view.findViewById(R.id.saveButtonBook)
        nameEditTextBook = view.findViewById(R.id.nameEditTextBook)
        authorEditTextBook = view.findViewById(R.id.authorEditTextBook)
        publishEditTextBook = view.findViewById(R.id.publishEditTextBook)
        deleteButtonBook = view.findViewById(R.id.deleteButtonBook)

        nameEditTextBook.setText(nameBook)
        authorEditTextBook.setText(authorBook)
        publishEditTextBook.setText(publishBook)

        imageViewBook.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
        image_uri = Uri.parse(imageUri)
        imageViewBook.setImageURI(image_uri)

        deleteButtonBook.setOnClickListener {
            val database = Firebase.database
            val myRef = database.getReference("").child("books").child(book_id!!)
            myRef.removeValue()
            val flag = CatalogBooksFragment.newInstance()
            (activity as MainActivity).replaceFragment(flag, CatalogBooksFragment.TAG)
        }

        saveButtonBook.setOnClickListener {
            nameBook = nameEditTextBook.text.toString()
            authorBook = authorEditTextBook.text.toString()
            publishBook = publishEditTextBook.text.toString()
            if (nameBook!!.isEmpty()) {
                customDialog.showErrorDialog(getString(R.string.book_name_error))
            } else if (authorBook!!.isEmpty()) {
                customDialog.showErrorDialog(getString(R.string.book_author_error))
            } else if (publishBook!!.isEmpty()) {
                customDialog.showErrorDialog(getString(R.string.book_publish_error))
            } else {
                if (imageUri == null) {
                    image_uri = view.context.drawableToUri(R.drawable.skill)
                }
                val database = Firebase.database
                val myRef = database.getReference("").child("books").child(book_id!!)

                myRef.child("nameBook").setValue(nameBook)
                myRef.child("authorBook").setValue(authorBook)
                myRef.child("publishBook").setValue(publishBook)
                myRef.child("imageBook").setValue(image_uri.toString())

                val flag = BookFragment.newInstance(book_id!!)
                (activity as MainActivity).replaceFragment(flag, BookFragment.TAG)
            }
        }
        return view
    }

    fun Context.drawableToUri(drawable: Int): Uri {
        return Uri.parse("android.resource://$packageName/$drawable")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImage) {
            image_uri = data?.data!!
            imageViewBook.setImageURI(image_uri)
        }
    }


    companion object {
        var TAG: String = EditBookFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(name: String, author: String, publish: String, book_id: String, imageUri: String) =
            EditBookFragment().apply {
                arguments = Bundle().apply {
                    putString(NAME_BOOK, name)
                    putString(AUTHOR_BOOK, author)
                    putString(PUBLISH_BOOK, publish)
                    putString(BOOK_ID, book_id)
                    putString(IMAGE_URI, imageUri)
                }
            }
    }
}