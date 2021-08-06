package com.vladgroshkov.automatedlibrary.adapters


import AlertCustomDialog
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageSize
import com.vladgroshkov.automatedlibrary.BookFragment
import com.vladgroshkov.automatedlibrary.CatalogBooksFragment
import com.vladgroshkov.automatedlibrary.MainActivity
import com.vladgroshkov.automatedlibrary.R
import java.util.*


class BooksListViewAdapter
    (
    private val context: Activity,
    private var list: ArrayList<HashMap<String, String>>
) :
    ArrayAdapter<HashMap<String, String>?>(
        context, R.layout.books_list,
        list as List<HashMap<String, String>?>
    )  {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView: View = inflater.inflate(R.layout.books_list, null, true)
        val nameBook = rowView.findViewById<View>(R.id.nameTextViewBook) as TextView
        val imageViewBook = rowView.findViewById<View>(R.id.imageViewBook) as ImageView
        val authorBook = rowView.findViewById<View>(R.id.authorTextViewBook) as TextView
        val quantityBook = rowView.findViewById<View>(R.id.quantityTextViewBook) as TextView
        if (list.isNotEmpty()) {
            nameBook.text = list[position]["nameBook"]
            authorBook.text = list[position]["authorBook"]
            var imageUri = list[position]["imageBook"]
            val configuration = ImageLoaderConfiguration.createDefault(context)
            val imageLoader = ImageLoader.getInstance()
            if (!imageLoader.isInited) {
                imageLoader.init(configuration)
            }
            imageLoader.displayImage(imageUri, imageViewBook, ImageSize(70, 70, 180))
            quantityBook.text = list[position]["quantityBook"]

            rowView.setOnClickListener {
                val element = list[position] as HashMap<*, *>
                val frag = BookFragment.newInstance(element["bookId"].toString())
                (context as MainActivity).replaceFragment(frag, BookFragment.TAG)
            }
        } else {
            rowView.visibility = View.GONE
            AlertCustomDialog(context).showInfoDialog(
                "К сожалению, в бибилиотеке пока нет книг",
                AppCompatResources.getDrawable(context, R.drawable.splash_image),
                CatalogBooksFragment.newInstance(), context, true
            )
        }
        return rowView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
               var filterResults = FilterResults()

                if (constraint == null || constraint.isEmpty()) {
                    filterResults.count = list.size
                    filterResults.values = list
                } else {
                    var searchText = constraint.toString().lowercase(Locale.getDefault())
                    var resultData: ArrayList<HashMap<String, String>> = ArrayList()
                    for (book in list) {
                        if (book["nameBook"]!!.contains(searchText) || book["authorBook"]!!.contains(searchText)) {
                            resultData.add(book)

                            filterResults.count = resultData.size
                            filterResults.values = resultData
                        }
                    }
                }


                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                list = results!!.values as ArrayList<HashMap<String, String>>
            }

        }
    }


}

