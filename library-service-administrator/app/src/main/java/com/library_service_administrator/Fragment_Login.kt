package com.library_service_administrator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.library_service_administrator.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Fragment_Login : Fragment() {
    // ViewBinding
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Retrofit API
    val api_login = API_Login.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        // login 버튼 클릭 이벤트 처리
        binding.btnLogin.setOnClickListener {
            val id = binding.etId.text.toString()
            val password = binding.etPassword.text.toString()
            var pl = PostLogin(id, password)
            api_login.post_input(
                pl
            ).enqueue(object: Callback<PostLoginResult> {
                override fun onResponse(call: Call<PostLoginResult>, response: Response<PostLoginResult>) {

                    if(!response.body().toString().isNullOrEmpty()) {
                        Log.i("Request Info", "Login User Success!!!")
                        Toast.makeText(context, response.body()?.content.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PostLoginResult>, t: Throwable) {
                    Log.i("Request Info", "Login User Failed!!!")
                    Log.e("Request Error", t.toString())
                }
            })

        }

        return view
    }
}