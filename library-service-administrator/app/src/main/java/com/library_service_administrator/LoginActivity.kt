package com.library_service_administrator

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.library_service_administrator.databinding.ActivityLoginBinding
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

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

        // 토큰 정보 내부 저장소에 저장
        val shared = getSharedPreferences("library-service-token", MODE_PRIVATE)

        // shared_prefs에 토큰 정보가 있으면, 토큰 체킹하고, 자동 로그인 하도록 함
        if(!shared.getString("user_accesstoken", null).isNullOrEmpty()) {
            // https://www.blancetnoir.info/api/users/{사용자 ID}
            if(shared.getString("user_id", null) != null) {
                var url = "https://www.blancetnoir.info/api/users/" + shared.getString("user_id", null)

                val builder = OkHttpClient.Builder()
                val trustAllCerts: Array<TrustManager> = arrayOf(
                        object : X509TrustManager {
                            @Throws(CertificateException::class)
                            override fun checkClientTrusted(chain: Array<X509Certificate?>?,
                                                            authType: String?) = Unit

                            @Throws(CertificateException::class)
                            override fun checkServerTrusted(chain: Array<X509Certificate?>?,
                                                            authType: String?) = Unit

                            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                        }
                )
                // Install the all-trusting trust manager
                val sslContext: SSLContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
                builder.sslSocketFactory(sslSocketFactory,
                        trustAllCerts[0] as X509TrustManager
                )
                .hostnameVerifier { _, session -> true }

                val client = builder.build()

                val request = Request.Builder()
                        .url(url)
                        .addHeader("user_accesstoken", shared.getString("user_accesstoken", "")!!)
                        .build()

                client.newCall(request).enqueue(object: okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        Log.i("Request Info", "AutoLogin Failed!!!")
                        Log.e("Error", e.toString())
                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        Log.i("Request Info", "AutoLogin Success!!!")

                        // json data로 생성
                        var response_data = response.body()!!.string()
                        Log.i("data : ", response_data)
                        val json_object: JSONObject = JSONObject(response_data)

                        Log.i("data : ", json_object["flag"].toString())
                        if(json_object["flag"].toString().equals("true")) {
                            // 화면 전환
                            changeActivity("자동 로그인에 성공하였습니다.")
                        }else {
                            // 토큰 만료 혹은 존재 x 이므로, 리프레쉬 토큰을 통해 재발급 진행
                            val refresh_url = "https://www.blancetnoir.info/api/tokens"

                            val refresh_client = builder.build()

                            val refresh_request = Request.Builder()
                                    .url(refresh_url)
                                    .put(RequestBody.create(MediaType.parse("text/plain"), ""))
                                    .addHeader("user_accesstoken", shared.getString("user_accesstoken", "")!!)
                                    .addHeader("user_refreshtoken", shared.getString("user_refreshtoken", "")!!)
                                    .build()
                            refresh_client.newCall(refresh_request).enqueue(object: okhttp3.Callback {
                                override fun onFailure(call: okhttp3.Call, e: IOException) {
                                    Log.i("Request Info", "Refresh Failed!!!")
                                    Log.e("Error", e.toString())
                                }

                                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                                    Log.i("Request Info", "Refresh Success!!!")

                                    // json data로 생성
                                    var response_data = response.body()!!.string()
                                    Log.i("data : ", response_data)
                                    val json_object: JSONObject = JSONObject(response_data)
                                    Log.i("data : ", json_object["flag"].toString())

                                    // 토큰 정보 삭제
                                    val id = shared.getString("user_id", "")
                                    val shared_editor = shared.edit()

                                    if(json_object["flag"] == true) {
                                        shared_editor.clear()

                                        // 새롭게 갱신된 토큰 정보 작성
                                        val new_accesstoken = response.header("user_accesstoken")
                                        val new_refreshtoken = response.header("user_refreshtoken")
                                        shared_editor.putString("user_id", id)
                                        shared_editor.putString("user_accesstoken", new_accesstoken)
                                        shared_editor.putString("user_refreshtoken", new_refreshtoken)

                                        // 파일 저장
                                        shared_editor.apply()

                                        Log.i("Request Info", "refresh Token Save Success!!!")
                                        Log.i("Request Info", new_accesstoken.toString())
                                        Log.i("Request Info", new_refreshtoken.toString())


                                        // 화면 전환
                                        changeActivity("자동 로그인에 성공하였습니다.")
                                    } else {
                                        // 갱신 실패로, 재 로그인 요청하도록 함
                                        Log.i("Error", "토큰 갱신 실패")
                                        shared_editor.clear()
                                        shared_editor.apply()
                                        runOnUiThread{Toast.makeText(applicationContext, "자동 로그인 실패 : 재로그인 바랍니다.", Toast.LENGTH_SHORT).show()}
                                    }
                                }
                            })
                        }
                    }

                })
            }
        }

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

                        // 자동로그인 checking 여부 확인
                        if(binding.checkboxAutoLogin.isChecked() == true) {
                            val shared_editor = shared.edit()

                            // 토큰 정보 저장
                            shared_editor.putString("user_id", id)
                            shared_editor.putString("user_accesstoken", response.raw().header("user_accesstoken").toString().trim())
                            shared_editor.putString("user_refreshtoken", response.raw().header("user_refreshtoken").toString().trim())

                            // 파일 저장
                            shared_editor.apply()

                            Log.i("Request Info", "Token Save Success!!!")
                        }

                        //화면 전환
                        changeActivity(response.body()?.content.toString())
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

    fun changeActivity(message: String) {
        //화면 전환
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)

        runOnUiThread{Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()}

        // 액티비티 종료
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}