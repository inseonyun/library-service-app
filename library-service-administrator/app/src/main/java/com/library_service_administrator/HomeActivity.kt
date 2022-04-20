package com.library_service_administrator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    val api by lazy { APIS.create() }
    var bookList = arrayListOf<List_Book_info>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btn_photo = findViewById<Button>(R.id.btn_photo)
        val lv_book_info = findViewById<ListView>(R.id.lv_book_info)

        btn_photo.setOnClickListener {
            val input_data_bookName = findViewById<EditText>(R.id.et_photo_url)
            val data = PostModel(input_data_bookName.text.toString())
            bookList.clear()
            // Retrofit Test
            //Post방식으로 서버에 전달할 데이터를 파라미터에 입력
            api.post_input(data).enqueue(object: Callback<List<PostResult>> {
                override fun onResponse(call: Call<List<PostResult>>, response: Response<List<PostResult>>) {
                    if(!response.body().toString().isEmpty()) {
                        val re_size = response.body()?.size

                        for (i in 0 until re_size!!) {
                            val isbn = response.body()!![i].ISBN.toString()
                            val name = response.body()!![i].Name.toString()
                            val writer = response.body()!![i].Writer.toString()
                            val quantity = response.body()!![i].Quantity.toString()
                            val tmp = List_Book_info(isbn, name, writer, quantity)
                            bookList.add(tmp)
                        }

                        val bookAdapter = Adapter_Book_info(applicationContext, bookList)
                        lv_book_info.adapter = bookAdapter
                    }
                }

                override fun onFailure(call: Call<List<PostResult>>, t: Throwable) {
                    Toast.makeText(applicationContext, "fail", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }
}

