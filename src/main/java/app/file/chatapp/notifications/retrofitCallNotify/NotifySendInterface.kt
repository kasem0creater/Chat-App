package app.file.chatapp.notifications.retrofitCallNotify

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotifySendInterface
{
    /*
    "Authorization : key=baAIzaSyB5VVxUdm2mi3UOYEW2N_lKusGAreCno-Q",
     */
    @Headers(value = ["Authorization:key=AAAA2Kwn-eA:APA91bEkDkS29nK0j4DhBwDfjEKCDCw5mA2eTCdH5GH98fvug7FWd6qRWnyryMVI9Hi4ku4YWI1BVii1oe88tGC6hWmv0mTRDJJDNLIcr1a_tN0R0uCF7g2WehRm9WX_ASmMJzURu3-c","Content-type:application/json"])
    @POST("fcm/send")
    fun sendNotify(@Body bodyData:MutableMap<String , Any>):Call<ResponseBody>
}