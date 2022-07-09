package com.library_service_administrator

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        // 버튼 이벤트 처리
        book_status.setOnClickListener {
            API_Return_Book.create().post_input(
                    user.UserID,
                    user.BookName.toString()
            ).enqueue(object: Callback<PostLoanReturnResult> {
                override fun onResponse(call: Call<PostLoanReturnResult>, response: Response<PostLoanReturnResult>) {
                    Log.i("Request Info", "Return Book Success!!!")

                    if(!response.body().toString().isNullOrEmpty()) {
                        // 리스트뷰 갱신
                       bookList.removeAt(position)
                        notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<PostLoanReturnResult>, t: Throwable) {
                    Log.i("Request Info", "Search Book Failed!!!")
                    Log.e("Request Error", t.toString())
                }
            })
        }

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