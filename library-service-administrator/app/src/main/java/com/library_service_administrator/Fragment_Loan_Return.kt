package com.library_service_administrator

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.library_service_administrator.databinding.FragmentLoanReturnBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class Fragment_Loan_Return : Fragment() {
    // ViewBinding
    private var _binding: FragmentLoanReturnBinding? = null
    private val binding get() = _binding!!

    // Retrofit API
    val api_search_user = API_Search_User.create()
    val api_search_isbn = API_Input_ISBN.create()

    // Server IP
    val ipAddress = "http://여기 ip"

    // userList
    //var userList = arrayListOf<>()

    // BookList
    var bookList = arrayListOf<List_Loan_info>()

    private val photo = 100

    // 원본 사진이 저장되는 Uri
    private var photoUri: Uri? = null

    // 포토 파일네임
    private var photoFileName = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentLoanReturnBinding.inflate(inflater, container, false)
        val view = binding.root

        val lv_loan_info = view.findViewById<ListView>(R.id.lv_loan_info)


        // 유저 조회
        binding.btnSearchingUser.setOnClickListener {

        }

        // isbn 입력 조회
        binding.btnWrite.setOnClickListener {
            bookList.clear()
            lv_loan_info.adapter = null

            val input_data_bookName = view.findViewById<EditText>(R.id.et_isbn)

            // Retrofit Test
            //Post방식으로 서버에 전달할 데이터를 파라미터에 입력
            api_search_isbn.post_input_isbn(
                    input_data_bookName.text.toString()
            ).enqueue(object: Callback<List<PostPhoto>> {
                override fun onResponse(call: Call<List<PostPhoto>>, response: Response<List<PostPhoto>>) {
                    Log.i("Request Info", "Search ISBN Success!!!")
                    if(!response.body().toString().isEmpty()) {
                        val re_size = response.body()?.size

                        for (i in 0 until re_size!!) {
                            val book_name = response.body()!![i].BookName.toString()
                            val book_return_date = response.body()!![i].BookReturnDate.toString()
                            val book_status = response.body()!![i].BookStatus.toString()
                            val tmp = List_Loan_info(book_name, book_return_date, book_status)
                            bookList.add(tmp)
                        }

                        val bookAdapter = Adapter_Loan_info(requireContext().applicationContext, bookList)
                        lv_loan_info.adapter = bookAdapter
                    }
                }

                override fun onFailure(call: Call<List<PostPhoto>>, t: Throwable) {
                    Log.i("Request Info", "Search ISBN Failed!!!")
                    Log.e("Request Error", t.toString())
                }
            })
        }

        // 사진으로 책 조회
        binding.btnPhoto.setOnClickListener {
            val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val photoFile = File(
                    File("${requireContext().filesDir}/image").apply {
                        if (!this.exists()) {
                            this.mkdirs()
                        }
                    },
                    newJpgFileName()
            )

            photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.blacklog.takepicture.fileprovider",
                    photoFile
            )
            intent.resolveActivity(requireContext().packageManager).also {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, photo)
            }

            bookList.clear()
            lv_loan_info.adapter = null
        }

        // Inflate the layout for this fragment
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode) {
                photo -> {
                    val imageBitmap = photoUri?.let { ImageDecoder.createSource(requireContext().contentResolver, it) }
                    //binding.imageView.setImageBitmap(imageBitmap?.let { ImageDecoder.decodeBitmap(it) })
                    //Toast.makeText(this, photoUri?.path, Toast.LENGTH_LONG).show()
                    val filePath = File(requireContext().filesDir, "image")
                    //Toast.makeText(this, filePath.toString(), Toast.LENGTH_LONG).show()
                    //Toast.makeText(this, photoFileName, Toast.LENGTH_LONG).show()
                    Log.i("System Info", "UploadPhoto Start!!!")
                    //uploadPhotho(File(filePath, photoFileName), photoFileName)
                    uploadPhotho(File(filePath, "photo.jpg"), "photo.jpg")
                }
            }
        }
    }
    private fun uploadPhotho(img_file: File, name: String) {
        val lv_loan_info = view?.findViewById<ListView>(R.id.lv_loan_info)

        // create requestBody
        var requestBody : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), img_file)
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file", name, requestBody)
        //The gson builder
        var gson : Gson =  GsonBuilder()
                .setLenient()
                .create()

        var okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

        //creating retrofit object
        var retrofit =
                Retrofit.Builder()
                        .baseUrl(ipAddress)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()

        //creating our Image_Upload_Interface
        var server = retrofit.create(Image_Upload_Interface::class.java)

        server.post_photo_loan_request(body).enqueue(object : Callback<List<PostPhoto>> {
            override fun onFailure(call: Call<List<PostPhoto>>, t: Throwable) {
                Log.i("Request Info", "UploadPhoto Failed!!!")
                Log.e("Request Error", t.toString())
            }

            override fun onResponse(call: Call<List<PostPhoto>>, response: Response<List<PostPhoto>>) {
                Log.i("Request Info", "UploadPhoto Success!!!")

                if(!response.body().toString().isEmpty()) {
                    val re_size = response.body()?.size
                    for(i in 0 until re_size!!) {
                        val book_name = response.body()!![i].BookName.toString()
                        val booK_return_date = response.body()!![i].BookReturnDate.toString()
                        val book_status = response.body()!![i].BookStatus.toString()
                        val tmp = List_Loan_info(book_name, booK_return_date, book_status)
                        bookList.add(tmp)
                    }

                    val bookAdapter = Adapter_Loan_info(requireContext().applicationContext, bookList)
                    lv_loan_info?.adapter = bookAdapter
                }
            }
        })
    }

    private fun newJpgFileName() : String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        photoFileName = "${filename}.jpg"
        return "${filename}.jpg"
    }
}