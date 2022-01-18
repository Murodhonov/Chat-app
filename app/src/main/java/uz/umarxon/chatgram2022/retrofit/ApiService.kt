package uz.ilhomjon.notificationfirebase5.rretrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import uz.ilhomjon.notificationfirebase5.models.MyResponse
import uz.ilhomjon.notificationfirebase5.models.Sender

interface ApiService {
    @Headers(
        "Content-type:application/json",
        "Authorization:key=AAAA6V1AUIg:APA91bECq1TmsD_NyVoAwF3YTogkXKOYy2FElqfuh-ypaBoyPWDQPr0tDQpcuC7fVxKxEbF3BoFodKWPB6IUXYd-jeCTobaRNik8Rf9HGdZX3k1XfR7IZmpvNJDMpa401aWhNTHezMuE"
    )
    @POST("fcm/send")
    fun sendNotification(@Body sender: Sender): Call<MyResponse>
}