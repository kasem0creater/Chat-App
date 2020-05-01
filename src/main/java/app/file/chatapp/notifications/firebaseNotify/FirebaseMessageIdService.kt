package app.file.chatapp.notifications.firebaseNotify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import app.file.chatapp.MainActivity
import app.file.chatapp.R
import app.file.chatapp.SharedPreference.SharedPreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageIdService : FirebaseMessagingService()
{
    private lateinit var share: SharedPreferenceManager

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)


        //
        val notify = remoteMessage?.notification
        val message = notify?.body.orEmpty()
        val title = notify?.title.orEmpty()
        Log.i("notify",notify?.body.orEmpty())
            chanelNotify(message , title)



    }

    private fun chanelNotify(message: String, title: String)
    {
        val intent = Intent(this , MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this , 0 , intent ,PendingIntent.FLAG_ONE_SHOT)
        val channelId = "channelID"
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        //build notify
        val notifyBuild = NotificationCompat.Builder(this,channelId)
            .setSmallIcon(R.drawable.chat_menu)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        var notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(channelId,"notification on app",NotificationManager.IMPORTANCE_HIGH)
            notifyManager.createNotificationChannel(channel)
        }
        notifyManager.notify(0,notifyBuild.build())

        //setting shared
        share =
            SharedPreferenceManager(this)
        share.writeNotify(true)
    }
}