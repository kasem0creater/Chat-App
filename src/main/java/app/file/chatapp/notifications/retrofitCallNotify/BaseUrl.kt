package app.file.chatapp.notifications.retrofitCallNotify

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BaseUrl
{
    private val baseurl ="https://fcm.googleapis.com/"
    private lateinit var retrofit: Retrofit

    fun getAPIClient():Retrofit
    {
        val client = OkHttpClient.Builder().build()
            retrofit = Retrofit.Builder().baseUrl(baseurl).addConverterFactory(GsonConverterFactory.create()).build()
        return retrofit
    }
}