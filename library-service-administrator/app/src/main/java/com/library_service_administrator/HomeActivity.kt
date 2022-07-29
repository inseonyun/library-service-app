package com.library_service_administrator

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    // menubar
    lateinit var navigationView : NavigationView
    lateinit var drawer_layout : DrawerLayout

    // Permisisons
    val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val PERMISSIONS_REQUEST = 100

    // frame_index
    // 0은 serach 화면
    // 1은 대출 / 반납 화면
    var frame_index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // menubar
        drawer_layout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigation_view)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar) // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게
        navigationView.setNavigationItemSelectedListener(this) //navigation 리스너

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_main, Fragment_Search())
            .commit()

        checkPermissions(PERMISSIONS, PERMISSIONS_REQUEST)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home->{ // 메뉴 버튼
                drawer_layout.openDrawer(GravityCompat.START)    // 네비게이션 드로어 열기
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawers()
            // 테스트를 위해 뒤로가기 버튼시 Toast 메시지
            Toast.makeText(this,"back btn clicked",Toast.LENGTH_SHORT).show()
        } else{
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout-> {
                //다이얼로그 표시
                val dialog_builder = AlertDialog.Builder(this)
                dialog_builder.setTitle("로그아웃")
                        .setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("예", DialogInterface.OnClickListener{ dialog, id->
                            supportFragmentManager.beginTransaction()
                                    .remove(Fragment_Loan_Return())
                                    .remove(Fragment_Search())
                                    .commit()

                            // 화면 전환 시 드로어 닫음
                            if(drawer_layout.isDrawerOpen(GravityCompat.START))
                                drawer_layout.closeDrawers()

                            // 자동 로그인 토큰 정보 삭제
                            val shared = getSharedPreferences("library-service-token", MODE_PRIVATE)
                            val shared_editor = shared.edit()
                            shared_editor.clear()
                            shared_editor.apply()

                            // 액티비티 이동
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        })
                        .setNegativeButton("취소", DialogInterface.OnClickListener{ dialog, id->
                            Toast.makeText(this, "취소하였습니다.", Toast.LENGTH_SHORT).show()
                        })
                dialog_builder.show()
            }
            R.id.search-> if(frame_index != 1) {
                supportFragmentManager.beginTransaction()
                    .remove(Fragment_Login())
                    .remove(Fragment_Loan_Return())
                    .replace(R.id.frame_main, Fragment_Search())
                    .commit()
                frame_index = 1

                // 화면 전환 시 드로어 닫음
                if(drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawers()
            }
            R.id.loan_return-> if(frame_index != 2) {
                supportFragmentManager.beginTransaction()
                    .remove(Fragment_Login())
                    .remove(Fragment_Search())
                    .replace(R.id.frame_main, Fragment_Loan_Return())
                    .commit()
                frame_index = 2

                // 화면 전환 시 드로어 닫음
                if(drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawers()
            }
        }
        return false
    }
}

