package com.library_service_administrator

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.library_service_administrator.databinding.ActivityHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat

class HomeActivity : AppCompatActivity() {
    val api = APIS.create()
    var bookList = arrayListOf<List_Book_info>()
    // ViewBinding
    lateinit var binding : ActivityHomeBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkPermissions(PERMISSIONS, PERMISSIONS_REQUEST)

        val btn_photo = findViewById<Button>(R.id.btn_photo)
        val lv_book_info = findViewById<ListView>(R.id.lv_book_info)

        // 버튼 이벤트 처리
        binding.btnPhoto.setOnClickListener {

            val intent:Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager).also {
                startActivityForResult(intent, photo)
            }
            val photoFile = File(
                    File("${filesDir}/image").apply{
                        if(!this.exists()){
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
            intent.resolveActivity(packageManager)?.also{
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, photo)
            }



/*
            val input_data_bookName = findViewById<EditText>(R.id.et_photo_url)

            //val data = PostModel(input_data_bookName.text.toString())
            bookList.clear()
            lv_book_info.adapter = null

            // Retrofit Test
            //Post방식으로 서버에 전달할 데이터를 파라미터에 입력
            api.post_input(
                    input_data_bookName.text.toString()
            ).enqueue(object: Callback<List<PostResult>> {
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
            })*/
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode) {
                photo -> {
                    val imageBitmap = photoUri?.let { ImageDecoder.createSource(this.contentResolver, it) }
                    //binding.imageView.setImageBitmap(imageBitmap?.let { ImageDecoder.decodeBitmap(it) })
                    Toast.makeText(this, photoUri?.path, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun newJpgFileName() : String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
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

