package com.alox1d.vkvoicenotes.recorder.notification

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.RemoteException
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.alox1d.vkvoicenotes.R
import com.alox1d.vkvoicenotes.recorder.model.RecordingAudio
import com.alox1d.vkvoicenotes.recorder.service.RecordingService


class RecorderNotificationManager @Throws(RemoteException::class)
constructor(private val mService: RecordingService) : BroadcastReceiver() {


    private var mNotificationManager: NotificationManager? = null
    private val mOpenAppIntent: PendingIntent
    private val mStopIntent: PendingIntent
    private var mCollapsedRemoteViews: RemoteViews? = null
    private var notificationBuilder: NotificationCompat.Builder? = null
    var mStarted = false // To check if notification manager is started or not!


    private fun getPackageName(): String {
        return mService.packageName
    }


    init {
        mNotificationManager =
            mService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val stopIntent = Intent(ACTION_STOP).setPackage(getPackageName()).apply {
            putExtra(RecordingAudio::class.java.name, true)
        }
        mStopIntent = PendingIntent.getBroadcast(
            mService, NOTIFICATION_REQUEST_CODE,
            stopIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("player://")).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        mOpenAppIntent = PendingIntent.getActivity(
            mService,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Cancel all notifications to handle the case where the Service was killed and restarted by the system.
        mNotificationManager?.cancelAll()
    }

    /**
     * To start notification and service
     */
    fun createMediaNotification() {
        // The notification must be updated after setting started to true
        val filter = IntentFilter().apply {
            addAction(ACTION_PAUSE)
            addAction(ACTION_CONTINUE_RECORDING)
            addAction(ACTION_STOP)
        }
        mService.registerReceiver(this, filter)

        if (!mStarted) {
            mStarted = true
            mService.startForeground(NOTIFICATION_ID, generateNotification())
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
        //ACTION_PAUSE -> mService.pause()
        //ACTION_CONTINUE_RECORDING -> mService.playCurrentSong()
            ACTION_STOP -> {
                mService.run {
                    unregisterReceiver(this@RecorderNotificationManager)

                    val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse("player://")).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(RecordingAudio::class.java.name, true)
                    }
                    PendingIntent.getActivity(
                        mService,
                        NOTIFICATION_REQUEST_CODE,
                        activityIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    ).send()
                }
            }
        }
    }


    private fun generateNotification(): Notification? {
        if (notificationBuilder == null) {
            notificationBuilder = NotificationCompat.Builder(mService, CHANNEL_ID)
            notificationBuilder?.setSmallIcon(R.drawable.ic_voice)
                ?.setLargeIcon(BitmapFactory.decodeResource(mService.resources, R.drawable.ic_voice))
                ?.setContentTitle(mService.getString(R.string.app_name))
                ?.setContentText(mService.getString(R.string.app_name))
                ?.setDeleteIntent(mOpenAppIntent)
                ?.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                ?.setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                ?.setOnlyAlertOnce(true)

            // Notification channels are only supported on Android O+.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            }
        }

        mCollapsedRemoteViews =
            RemoteViews(getPackageName(), R.layout.recorder_collapsed_notification)
        notificationBuilder?.setCustomContentView(mCollapsedRemoteViews)
        notificationBuilder?.setContentIntent(createContentIntent())

        // To make sure that the notification can be dismissed by the user when we are not playing.
        notificationBuilder?.setOngoing(true)

        mCollapsedRemoteViews?.let { createCollapsedRemoteViews(it) }
//
//        if (mService.getPlayState() == PlaybackState.STATE_PLAYING ||
//            mService.getPlayState() == PlaybackState.STATE_BUFFERING
//        ) showPauseIcon() else showPlayIcon()

        mNotificationManager?.notify(NOTIFICATION_ID, notificationBuilder?.build())
        return notificationBuilder?.build()
    }


    private fun createContentIntent(): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("player://")).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        return TaskStackBuilder.create(mService).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(NOTIFICATION_REQUEST_INTENT_CODE, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun showPlayIcon() {
        mCollapsedRemoteViews?.setViewVisibility(
            R.id.collapsed_notification_pause_image_view,
            View.GONE
        )
        mCollapsedRemoteViews?.setViewVisibility(
            R.id.collapsed_notification_play_image_view,
            View.VISIBLE
        )
    }

    private fun showPauseIcon() {
        mCollapsedRemoteViews?.setViewVisibility(
            R.id.collapsed_notification_pause_image_view,
            View.VISIBLE
        )
        mCollapsedRemoteViews?.setViewVisibility(
            R.id.collapsed_notification_play_image_view,
            View.GONE
        )
    }

    private fun createCollapsedRemoteViews(collapsedRemoteViews: RemoteViews) {

        collapsedRemoteViews.setOnClickPendingIntent(
            R.id.collapsed_notification_play_image_view,
            mStopIntent
        )
        //TODO Вывести время записи
//        collapsedRemoteViews.setTextViewText(
//            R.id.collapsed_notification_song_name_text_view,
//            mService.getCurrentSong()?.title
//        )

    }


    /**
     * Creates Notification Channel. This is required in Android O+ to display notifications.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        if (mNotificationManager?.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                mService.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description =
                mService.getString(R.string.notification_channel_description_recorder)
            mNotificationManager?.createNotificationChannel(notificationChannel)
        }
    }
    //TODO Как "подхватывать" состояние при переоткрытии активити?
    // Пример: идёт запись -> перезашёл -> показываь, что идёт запись

    companion object {

        private val TAG = RecorderNotificationManager::class.java.name
        private const val ACTION_PAUSE = "app.pause"
        private const val ACTION_CONTINUE_RECORDING = "app.play"
        private const val ACTION_STOP = "recorder.stop"
        private const val CHANNEL_ID = "app.RECORD_CHANNEL_ID"
        private const val NOTIFICATION_ID = 413
        private const val NOTIFICATION_REQUEST_CODE = 101
        private const val NOTIFICATION_REQUEST_INTENT_CODE = 125246
    }
}

