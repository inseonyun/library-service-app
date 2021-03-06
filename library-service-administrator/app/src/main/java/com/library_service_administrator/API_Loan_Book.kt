package com.library_service_administrator

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface API_Loan_Book {
    //post -> php 파일 주소
    @FormUrlEncoded
    @POST("/Loan_Book.php")
    @Headers(
        "accept: application/json",
        //"content-type: application/json; charset=utf-8"
        "content-type: application/x-www-form-urlencoded; charset=utf-8"
    )

    //post로 서버에 데이터를 보내는 메서드
    fun post_input(
        @Field("userID") userID: String,
        @Field("BookName") BookName: String
    ): Call<PostLoanReturnResult>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        //서버 IP만 입력
        private const val BASE_URL = "http://여기 ip"
        fun create(): API_Loan_Book {
            val gson: Gson = GsonBuilder().setLenient().create();
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(API_Loan_Book::class.java)
        }
    }
}