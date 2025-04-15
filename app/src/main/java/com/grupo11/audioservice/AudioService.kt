package com.grupo11.audioservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log


class AudioService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private val notificationId = 1
    private val handler = Handler()

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
        startForeground(notificationId, createNotification())

        // Start updating UI using handler
        updateSeekBarProgress()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            when (action) {
                "PLAY" -> playAudio()
                "PAUSE" -> pauseAudio()
                "STOP" -> stopAudio()
                "SEEK" -> {
                    val seekPosition = intent.getIntExtra("SEEK_POSITION", 0)
                    mediaPlayer?.seekTo(seekPosition)
                }
            }
        }
        return START_STICKY
    }

    private fun playAudio() {
        try {
            if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
                mediaPlayer!!.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pauseAudio() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
        }
    }

    private fun stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            val fileDescriptor = this.resources.openRawResourceFd(R.raw.instrumental1)
            mediaPlayer!!.setDataSource(fileDescriptor.fileDescriptor, fileDescriptor.startOffset, fileDescriptor.length)
            mediaPlayer!!.prepare()
        }
        stopForeground(true)
        stopSelf()
    }

    private fun updateSeekBarProgress() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    val currentPosition = mediaPlayer!!.currentPosition
                    val duration = mediaPlayer!!.duration
                    Log.d("AudioService", "MediaPlayer is playing. Current Position: $currentPosition, Duration: $duration")

                    val intent = Intent("UPDATE_UI")
                    intent.putExtra("CURRENT_POSITION", currentPosition)
                    intent.putExtra("DURATION", duration)

                    // Enviar o broadcast
                    sendBroadcast(intent)
                    Log.d("AudioService", "Broadcast sent with Current Position: $currentPosition and Duration: $duration")
                } else {
                    Log.d("AudioService", "MediaPlayer is not playing.")
                }

                // Reagendar a próxima execução
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
        mediaPlayer = null
        mediaSession?.release()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Audio Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Audio Player")
            .setContentText("Tocando música...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "AudioServiceChannel"
    }
}
