package com.vladgroshkov.automatedlibrary.models

class BookModel (private var name: String, private var author: String, private var image: String){

    fun getName(): String {
        return name
    }

    fun getAuthor(): String {
        return author
    }

    fun getImage(): String {
        return image
    }

    fun setName(name: String) {
        this.name = name
    }

    fun setAuthor(author: String) {
        this.author = author
    }

    fun setImage(image: String) {
        this.image = image
    }



}