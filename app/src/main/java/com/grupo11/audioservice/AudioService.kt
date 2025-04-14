package com.grupo11.audioservice


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat



class AudioService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    private var audioManager: AudioManager? = null

    private var mediaSession: MediaSessionCompat? = null

    private val notificationId = 1


    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer()
        mediaPlayer!!.reset()
        val fileDescriptor = this.resources.openRawResourceFd(R.raw.instrumental1)
        mediaPlayer!!.setDataSource(fileDescriptor.fileDescriptor, fileDescriptor.startOffset, fileDescriptor.length)
        mediaPlayer!!.prepare()

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        mediaSession = MediaSessionCompat(this, "AudioService")



        createNotificationChannel()

        startForeground(notificationId, createNotification(), FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action

            // Processar ações como PLAY, PAUSE, STOP, etc.
            if (action != null) when (action) {
                "PLAY" -> playAudio(intent.getStringExtra("path"))
                "PAUSE" -> pauseAudio()
                "STOP" -> stopAudio()
                else -> {}
            }
        }

        return START_STICKY // ou START_NOT_STICKY, dependendo das suas necessidades.
    }


    private fun playAudio(path: String?) {
        try {
            mediaPlayer!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pauseAudio() {
        if (mediaPlayer!!.isPlaying) mediaPlayer!!.pause()
    }


    private fun stopAudio() {
        mediaPlayer!!.stop()
        stopForeground(true)
        stopSelf()
    }


    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer!!.release()
        mediaPlayer = null
        mediaSession?.release()
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    // ... (Métodos para criação e gerenciamento de notificações) ...
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,

                "Audio Service Channel",

                NotificationManager.IMPORTANCE_DEFAULT

            )

            val manager = getSystemService(
                NotificationManager::class.java
            )

            manager.createNotificationChannel(serviceChannel)
        }
    }


    private fun createNotification(): Notification {
        val notificationIntent = Intent(
            this,
            MainActivity::class.java
        ) // Substitua MainActivity pelo nome da sua Activity

        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return Notification.Builder(this, CHANNEL_ID)

            .setContentTitle("Audio Player")

            .setContentText("Tocando música...")

            .setSmallIcon(R.drawable.ic_launcher_foreground) // Substitua pelo seu ícone

            .setContentIntent(pendingIntent)

            .build()
    }

    companion object {
        private const val CHANNEL_ID = "AudioServiceChannel"
    }
}
