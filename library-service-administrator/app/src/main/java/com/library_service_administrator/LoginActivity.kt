package com.library_service_administrator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.library_service_administrator.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    // ViewBinding
    lateinit var binding : ActivityLoginBinding

    // Retrofit API
    val api_login = API_Login.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // login 버튼 클릭 이벤트 처리
        binding.btnLogin.setOnClickListener {
            val id = binding.etId.text.toString()
            val password = binding.etPassword.text.toString()
            var pl = PostLogin(id, password)
            api_login.post_input(
                    pl
            ).enqueue(object: Callback<PostLoginResult> {
                override fun onResponse(call: Call<PostLoginResult>, response: Response<PostLoginResult>) {

                    // 400은  로그인 실패를 의미함
                    if(response.raw().code() != 400) {
                        // 로그인 성공
                        Log.i("Request Info", "Login User Success!!!")

                        //화면 전환
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)

                        Toast.makeText(applicationContext, response.body()?.content.toString(), Toast.LENGTH_SHORT).show()

                        // 액티비티 종료
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        // 로그인 실패
                        Toast.makeText(applicationContext, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<PostLoginResult>, t: Throwable) {
                    // 로그인 실패
                    Log.i("Request Info", "Login User Failed!!!")
                    Log.e("Request Error", t.toString())
                }
            })

        }
    }
}