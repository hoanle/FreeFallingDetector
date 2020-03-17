package com.whoisyari.freefallingdetector

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.whoisyari.freefallingdetectorlibrary.core.MonitoringService
import com.whoisyari.freefallingdetectorlibrary.R


internal class FreeFallingNotificationUlti {

    companion object {
        /**
         * Register action to link with external client
         */
        fun createNotificationChannelOS26(
            context: Context,
            name: String,
            description: String,
            channelId: String
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
                channel.description = description
                channel.setShowBadge(false)
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }

        /**
         * Create local notification, it will activate the app if tapped
         * @param channelId
         */
        fun createLocalNotification(context: Context, channelId: String) {
            val builder = NotificationCompat.Builder(context, channelId)
            builder.setSmallIcon(R.drawable.ic_launcher_background)

            var launchIntent: Intent?
            val apppack = FreeFallingPreferenceUtil.getApplicationComponentName(context)
            val pm = context.packageManager
            launchIntent = pm.getLaunchIntentForPackage(apppack)
            launchIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            launchIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pIntent = PendingIntent.getActivity(context, 0, launchIntent, 0)
            builder.setContentIntent(pIntent)

            builder.setContentTitle(context.getString(R.string.notification_title))
            builder.setContentText(context.getString(R.string.notification_message))
            builder.setAutoCancel(true)

            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.apply {
                notify(1, builder.build())
            }
        }

        /**
         * Create local notification for reboot device event, it will activate the app if tapped
         * resulting in starting the service
         * @param channelId
         */
        fun createNotificationToRestartService(context: Context, channelId: String) {
            val builder = NotificationCompat.Builder(context, channelId)
            builder.setSmallIcon(R.drawable.ic_launcher_background)

            val serviceIntent = Intent(context, MonitoringService::class.java)
            val pendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0)
            builder.setContentIntent(pendingIntent)

            builder.setContentTitle(context.getString(R.string.notification_title))
            builder.setContentText(context.getString(R.string.reboot_device_restart_service))
            builder.setAutoCancel(true)

            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.apply {
                notify(1, builder.build())
            }
        }

        /**
         * Register channel for notification
         * @param channelId
         */
        fun registerNotificationChannel(context: Context) {
            createNotificationChannelOS26(
                context,
                context.getString(R.string.app_name),
                context.getString(R.string.notification_channel_description),
                context.getString(R.string.notification_channel_id)
            )
        }

        /**
         * Create a notification for foreground service. Tapping it will reactivate the app
         * @param channelId
         */
        fun createForegroundNotification(context: Context, channelId: String) : Notification{
            val builder = NotificationCompat.Builder(context, channelId)
            builder.setSmallIcon(R.drawable.ic_launcher_background)

            var launchIntent: Intent?
            val apppack = FreeFallingPreferenceUtil.getApplicationComponentName(context)
            val pm = context.packageManager
            launchIntent = pm.getLaunchIntentForPackage(apppack)
            launchIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            launchIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pIntent = PendingIntent.getActivity(context, 0, launchIntent, 0)
            builder.setContentIntent(pIntent)

            builder.setContentTitle(context.getString(R.string.foreground_service_title))
            builder.setContentText(context.getString(R.string.foreground_service_content))
            builder.setAutoCancel(true)

            return builder.build()
        }
    }
}