package com.vibs.weatherapisdk

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient
 */
sealed class RetrofitClient {

    companion object {
        private var retrofitClient: Retrofit? = null

        /**
         * Generates the retrofit and sets the base url.
         */
        fun getInstance(): Retrofit? {
            retrofitClient = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .client(getHttpLoggingClient())
                .build()
            return retrofitClient
        }

        /**
         * Logging interceptor.
         */
        private fun getHttpLoggingClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(Interceptor { chain ->
                    val requestUrl = chain.request()
                        .url
                        .newBuilder()
                        .addQueryParameter("appid", BuildConfig.APPID)
                        .build()

                    val request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .url(requestUrl)
                        .build()
                    return@Interceptor chain.proceed(request)
                })
                .callTimeout(60, TimeUnit.SECONDS)
                .build()
            return client
        }

    }

}
