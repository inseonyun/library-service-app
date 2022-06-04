package com.library_service_administrator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class Adapter_Book_info (val context: Context, val bookList: ArrayList<List_Book_info>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_book_info, null)

        val book_Name = view.findViewById<TextView>(R.id.tv_book_name)
        val book_ISBN = view.findViewById<TextView>(R.id.tv_isbn)
        val book_Writer = view.findViewById<TextView>(R.id.tv_book_writer)
        val book_Quantity = view.findViewById<TextView>(R.id.tv_book_quantity)

        val book = bookList[position]
        book_Name.text = book.BookName
        book_ISBN.text = book.ISBN
        book_Writer.text = book.Writer
        book_Quantity.text = book.Quantity

        return view
    }

    override fun getItem(position: Int): Any {
        return bookList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return bookList.size
    }
}