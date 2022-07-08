package com.library_service_administrator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class Adapter_User_info (val context: Context, val bookList: ArrayList<List_User_info>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_user_info, null)

        val book_Name = view.findViewById<TextView>(R.id.tv_book_name)
        val book_LoanDate = view.findViewById<TextView>(R.id.tv_book_loan_date)
        val book_ReturnDate = view.findViewById<TextView>(R.id.tv_book_return_date)
        val book_status = view.findViewById<Button>(R.id.btn_book_status)


        val user = bookList[position]
        book_Name.text = user.BookName.toString()
        book_LoanDate.text = user.BookLoanDate.toString()
        book_ReturnDate.text = user.BookReturnDate.toString()

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