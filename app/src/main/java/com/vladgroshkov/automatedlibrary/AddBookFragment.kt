package com.vladgroshkov.automatedlibrary

import AlertCustomDialog
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
import android.app.Activity
import android.content.Context
import java.util.*


class AddBookFragment : Fragment() {
    private val pickImage = 100
    private var imageUri: Uri? = null

    private lateinit var name: String
    private lateinit var author: String
    private lateinit var publish: String
    private lateinit var quantity: String
    private lateinit var customDialog: AlertCustomDialog
    private lateinit var user: FirebaseUser

    private lateinit var imageViewBookAdd: ImageView
    private lateinit var saveButtonBookAdd: Button
    private lateinit var nameEditTextBookAdd: EditText
    private lateinit var authorEditTextBookAdd: EditText
    private lateinit var publishEditTextBookAdd: EditText
    private lateinit var quantityEditTextBookAdd: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_book, container, false)
        imageViewBookAdd = view.findViewById(R.id.imageViewBookAdd)
        saveButtonBookAdd = view.findViewById(R.id.saveButtonBookAdd)
        nameEditTextBookAdd = view.findViewById(R.id.nameEditTextBookAdd)
        authorEditTextBookAdd = view.findViewById(R.id.authorEditTextBookAdd)
        publishEditTextBookAdd = view.findViewById(R.id.publishEditTextBookAdd)
        quantityEditTextBookAdd = view.findViewById(R.id.quantityEditTextBookAdd)

        imageViewBookAdd.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        saveButtonBookAdd.setOnClickListener {
            name = nameEditTextBookAdd.text.toString()
            author = authorEditTextBookAdd.text.toString()
            publish = publishEditTextBookAdd.text.toString()
            quantity = quantityEditTextBookAdd.text.toString()
            when {
                name.isEmpty() -> {
                    customDialog.showErrorDialog(getString(R.string.book_name_error))
                }
                author.isEmpty() -> {
                    customDialog.showErrorDialog(getString(R.string.book_author_error))
                }
                publish.isEmpty() -> {
                    customDialog.showErrorDialog(getString(R.string.book_publish_error))
                }
                quantity.isEmpty() -> {
                    customDialog.showErrorDialog(getString(R.string.book_quantity_error))
                }
                else -> {
                    if (imageUri == null) {
                        imageUri = view.context.drawableToUri(R.drawable.skill)
                    }
                    val database = Firebase.database
                    val uuid: UUID = UUID.randomUUID()
                    val bookUid: String = uuid.toString()
                    val myRef = database.getReference("").child("books").child(bookUid)

                    myRef.child("nameBook").setValue(name)
                    myRef.child("authorBook").setValue(author)
                    myRef.child("publishBook").setValue(publish)
                    myRef.child("quantityBook").setValue(quantity)
                    myRef.child("imageBook").setValue(imageUri.toString())

                    val flag = CatalogBooksFragment.newInstance()
                    (activity as MainActivity).replaceFragment(flag, CatalogBooksFragment.TAG)
                }
            }

        }


        return view
    }

    private fun Context.drawableToUri(drawable: Int): Uri {
        return Uri.parse("android.resource://$packageName/$drawable")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data!!
            imageViewBookAdd.setImageURI(imageUri)
        }
    }

    companion object {
        var TAG: String = AddBookFragment::class.java.simpleName

        @JvmStatic
        fun newInstance() =
            AddBookFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}