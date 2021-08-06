package com.vladgroshkov.automatedlibrary.adapters


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageSize
import com.vladgroshkov.automatedlibrary.R
import java.util.*


class ReaderBooksListViewAdapter
    (
    private val context: Activity,
    private val list: ArrayList<HashMap<String, String>>
) :
    ArrayAdapter<HashMap<String, String>?>(
        context, R.layout.books_list,
        list as List<HashMap<String, String>?>
    ) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView: View = inflater.inflate(R.layout.books_list, null, true)
        val nameBook = rowView.findViewById<View>(R.id.nameTextViewBook) as TextView
        val imageViewBook = rowView.findViewById<View>(R.id.imageViewBook) as ImageView
        val authorBook = rowView.findViewById<View>(R.id.authorTextViewBook) as TextView
        val quantityBook = rowView.findViewById<View>(R.id.quantityTextViewBook) as TextView
        nameBook.text = list[position]["nameBook"]
        authorBook.text = list[position]["authorBook"]
        var imageUri = list[position]["imageBook"]
        val configuration = ImageLoaderConfiguration.createDefault(context)
        val imageLoader = ImageLoader.getInstance()
        if (!imageLoader.isInited) {
            imageLoader.init(configuration)
        }
        imageLoader.displayImage(imageUri, imageViewBook, ImageSize(70, 70, 180))
        quantityBook.visibility = View.GONE
        return rowView
    }
}

