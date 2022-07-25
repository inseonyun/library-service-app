package com.library_service_administrator

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.lang.System.load
import java.net.URL
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

interface API_Login {
    @POST("/api/tokens")
    @Headers(
        "accept: application/json",
        "content-type: application/json; charset=utf-8"
        //"content-type: application/json"
    )

    //post로 서버에 데이터를 보내는 메서드
    fun post_input(
        @Body PostLogin: PostLogin
    ): Call<PostLoginResult>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        //서버 IP만 입력

        private const val BASE_URL = "https://www.blancetnoir.info:443/"

        // 보호 무시
        fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

                }

                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, session -> true }

            return builder
        }

        fun create(): API_Login {
            val gson: Gson = GsonBuilder().setLenient().create()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getUnsafeOkHttpClient().build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(API_Login::class.java)
        }

          // ssl 보호 인증
//        var VERIFY_DOMAIN: String = "*.lottois.info"
//
//        fun create(): API_Login {
//            // Load CAs from an InputStream
//            // (could be from a resource or ByteArrayInputStream or ...)
//            val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
//            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
//            val caInput: InputStream = BufferedInputStream(FileInputStream("load-der.crt"))
//            val ca: X509Certificate = caInput.use {
//                cf.generateCertificate(it) as X509Certificate
//            }
//            System.out.println("ca=" + ca.subjectDN)
//
//            // Create a KeyStore containing our trusted CAs
//            val keyStoreType = KeyStore.getDefaultType()
//            val keyStore = KeyStore.getInstance(keyStoreType).apply {
//                load(null, null)
//                setCertificateEntry("ca", ca)
//            }
//
//            // Create a TrustManager that trusts the CAs inputStream our KeyStore
//            val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
//            val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
//                init(keyStore)
//            }
//
//            // Create an SSLContext that uses our TrustManager
//            val context: SSLContext = SSLContext.getInstance("TLS").apply {
//                init(null, tmf.trustManagers, null)
//            }
//
//            // Tell the URLConnection to use a SocketFactory from our SSLContext
//            val url = URL(BASE_URL)
//            val urlConnection = url.openConnection() as HttpsURLConnection
//            urlConnection.sslSocketFactory = context.socketFactory
//            val inputStream: InputStream = urlConnection.inputStream
//            //copyInputStreamToOutputStream(inputStream, System.out)
//            return
//        }

    }
}