package com.library_service_administrator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class HomeActivity : AppCompatActivity() {
    val url = "http://yuninseon.ivyro.net/BookStatusRequest.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btn_photo = findViewById<Button>(R.id.btn_photo)
        btn_photo.setOnClickListener {
            // Volley Test
            val input_data_bookName = findViewById<EditText>(R.id.et_photo_url);
            val queue = Volley.newRequestQueue(this);

            val stringRequest = object : StringRequest(Request.Method.POST, url,
                // 요청 성공 시
                Response.Listener<String> {
                    // 여기서 리스트뷰 생성해주고 하면 될듯..?
                },
                // 요청 에러 발생
                Response.ErrorListener {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    // 데이터 전송
                    return mutableMapOf<String, String>("one" to input_data_bookName.text.toString())
                }
            }
            queue.add(stringRequest)

        }
    }
}