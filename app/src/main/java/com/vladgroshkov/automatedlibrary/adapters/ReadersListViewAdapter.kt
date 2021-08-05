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


class ReadersListViewAdapter
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
        val rowView: View = inflater.inflate(R.layout.readers_list, null, true)
        val nameTextViewReader = rowView.findViewById<View>(R.id.nameTextViewReader) as TextView
        val imageViewReader = rowView.findViewById<View>(R.id.imageViewReader) as ImageView
        val surnameTextViewReader = rowView.findViewById<View>(R.id.surnameTextViewReader) as TextView
        nameTextViewReader.text = list[position]["name"]
        surnameTextViewReader.text = list[position]["surname"]
        var imageUri = list[position]["imageUri"]
        val configuration = ImageLoaderConfiguration.createDefault(context)
        val imageLoader = ImageLoader.getInstance()
        if (!imageLoader.isInited) {
            imageLoader.init(configuration)
        }
        imageLoader.displayImage(imageUri, imageViewReader, ImageSize(70, 70, 180))
        return rowView
    }
}

