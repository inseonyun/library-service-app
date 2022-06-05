package com.library_service_administrator

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.library_service_administrator.databinding.ActivityHomeBinding
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


class HomeActivity : AppCompatActivity() {
    val api = APIS.create()
    var bookList = arrayListOf<List_Book_info>()
    // ViewBinding
    lateinit var binding : ActivityHomeBinding
    val ipAddress = "http://여기 ip"
    // Permisisons
    val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val PERMISSIONS_REQUEST = 100

    private val photo = 100

    // 원본 사진이 저장되는 Uri
    private var photoUri: Uri? = null

    // 포토 파일네임
    private var photoFileName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkPermissions(PERMISSIONS, PERMISSIONS_REQUEST)

        val lv_book_info = findViewById<ListView>(R.id.lv_book_info)

        // isbn 검색 버튼 이벤트 처리
        binding.btnWrite.setOnClickListener {
            bookList.clear()
            lv_book_info.adapter = null

            val input_data_bookName = findViewById<EditText>(R.id.et_photo_url)

            // Retrofit Test
            //Post방식으로 서버에 전달할 데이터를 파라미터에 입력
            api.post_input(
                    input_data_bookName.text.toString()
            ).enqueue(object: Callback<List<PostResult>> {
                override fun onResponse(call: Call<List<PostResult>>, response: Response<List<PostResult>>) {
                    Log.i("Request Info", "Search ISBN Success!!!")
                    if(!response.body().toString().isEmpty()) {
                        val re_size = response.body()?.size

                        for (i in 0 until re_size!!) {
                            val name = response.body()!![i].BookName.toString()
                            val isbn = response.body()!![i].ISBN.toString()
                            val writer = response.body()!![i].Writer.toString()
                            val quantity = response.body()!![i].Quantity.toString()
                            val tmp = List_Book_info(name, isbn, writer, quantity)
                            bookList.add(tmp)
                        }

                        val bookAdapter = Adapter_Book_info(applicationContext, bookList)
                        lv_book_info.adapter = bookAdapter
                    }
                }

                override fun onFailure(call: Call<List<PostResult>>, t: Throwable) {
                    Log.i("Request Info", "Search ISBN Failed!!!")
                    Log.e("Request Error", t.toString())
                }
            })
        }
        
        // 사진 찍기 버튼 이벤트 처리
        binding.btnPhoto.setOnClickListener {

            val intent:Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val photoFile = File(
                    File("${filesDir}/image").apply {
                        if (!this.exists()) {
                            this.mkdirs()
                        }
                    },
                    newJpgFileName()
            )

            photoUri = FileProvider.getUriForFile(
                    this,
                    "com.blacklog.takepicture.fileprovider",
                    photoFile
            )

            intent.resolveActivity(packageManager).also {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, photo)
            }

            bookList.clear()
            lv_book_info.adapter = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode) {
                photo -> {
                    val imageBitmap = photoUri?.let { ImageDecoder.createSource(this.contentResolver, it) }
                    //binding.imageView.setImageBitmap(imageBitmap?.let { ImageDecoder.decodeBitmap(it) })
                    //Toast.makeText(this, photoUri?.path, Toast.LENGTH_LONG).show()
                    val filePath = File(filesDir, "image")
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
        val lv_book_info = findViewById<ListView>(R.id.lv_book_info)

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

        server.post_photo_request(body).enqueue(object : Callback<List<PostResult>> {
            override fun onFailure(call: Call<List<PostResult>>, t: Throwable) {
                Log.i("Request Info", "UploadPhoto Failed!!!")
                Log.e("Request Error", t.toString())
            }

            override fun onResponse(call: Call<List<PostResult>>, response: Response<List<PostResult>>) {
                Log.i("Request Info", "UploadPhoto Success!!!")

                if(!response.body().toString().isEmpty()) {
                    val re_size = response.body()?.size
                    for(i in 0 until re_size!!) {
                        val name = response.body()!![i].BookName.toString()
                        val isbn = response.body()!![i].ISBN.toString()
                        val writer = response.body()!![i].Writer.toString()
                        val quantity = response.body()!![i].Quantity.toString()
                        val tmp = List_Book_info(name, isbn, writer, quantity)
                        bookList.add(tmp)
                    }

                    val bookAdapter = Adapter_Book_info(applicationContext, bookList)
                    lv_book_info.adapter = bookAdapter
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

    private fun checkPermissions(permissions: Array<String>, permissionsRequest: Int): Boolean {
        val permissionList : MutableList<String> = mutableListOf()
        for(permission in permissions){
            val result = ContextCompat.checkSelfPermission(this, permission)
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(permission)
            }
        }
        if(permissionList.isNotEmpty()){
            ActivityCompat.requestPermissions(this, permissionList.toTypedArray(), PERMISSIONS_REQUEST)
            return false
        }
        return true
    }
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for(result in grantResults){
            if(result != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "권한 승인 부탁드립니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

