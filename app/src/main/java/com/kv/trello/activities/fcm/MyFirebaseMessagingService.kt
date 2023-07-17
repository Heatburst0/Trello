package com.kv.trello.activities.fcm
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import com.kv.trello.R
import com.kv.trello.activities.MainActivity
import com.kv.trello.activities.SignInActivity
import com.kv.trello.activities.firebase.FirestoreClass
import com.projemanag.utils.Constants
import kotlinx.coroutines.MainScope

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG,"from ${message.from}")

        message.data.isNotEmpty().let {
            Log.d(TAG,"data ${message.data}")
            val title = message.data[Constants.FCM_KEY_TITLE]!!
            val message = message.data[Constants.FCM_KEY_MESSAGE]!!
            sendNotification(title,message)
        }
        message.notification?.let{
            Log.d(TAG,"notification ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationtToServer(token)
    }
    private fun sendRegistrationtToServer(token : String?){
        //todo
    }
    private fun sendNotification(title : String,message : String){
        val intent= if(FirestoreClass().getCurrentUserID().isNotEmpty()){
            Intent(this, MainActivity::class.java)
        }else{
            Intent(this, SignInActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingintent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        val channelid = this.resources.getString(R.string.channel_id)
        val defaultsounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationbuilder = NotificationCompat.Builder(
            this,channelid
        ).setSmallIcon(R.drawable.ic_notify)
            .setContentTitle(title)
            .setContentText(message)
            .setSound(defaultsounduri)
            .setAutoCancel(true)
            .setContentIntent(pendingintent)
        val notificationmanager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelid,"Channel trello title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationmanager.createNotificationChannel(channel)
        }
        notificationmanager.notify(0,notificationbuilder.build())

    }
    companion object{
        private const val TAG="MyFirebaseMsgService"
    }
}