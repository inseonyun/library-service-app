package com.library_service_administrator

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Image_Upload_Loan_Interface {
    @Multipart
    @POST("GetBookStatus_Loan_useAPI.php")

    fun post_photo_loan_request(
        @Part imageFile : MultipartBody.Part): Call<List<PostPhoto>>
}