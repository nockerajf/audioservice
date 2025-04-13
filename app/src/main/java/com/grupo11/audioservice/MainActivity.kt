 package com.grupo11.audioservice

 import android.content.Intent
 import android.os.Bundle
 import android.widget.Button
 import android.widget.Toast
 import androidx.appcompat.app.AppCompatActivity

 class MainActivity : AppCompatActivity() {

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)

         val playButton: Button = findViewById(R.id.button_play)
         val pauseButton: Button = findViewById(R.id.button_pause)
         val stopButton: Button = findViewById(R.id.button_stop)

         playButton.setOnClickListener {
             Toast.makeText(this, "Play clicado", Toast.LENGTH_SHORT).show()
             val intent = Intent(this, AudioService::class.java)
             intent.action = "PLAY" // Replace with the desired action (e.g., "PLAY", "PAUSE", "STOP")
             startService(intent)
         }

         pauseButton.setOnClickListener {
             Toast.makeText(this, "Pause clicado", Toast.LENGTH_SHORT).show()
             val intent = Intent(this, AudioService::class.java)
             intent.action = "PAUSE"
             startService(intent)
         }

         stopButton.setOnClickListener {
             Toast.makeText(this, "Stop clicado", Toast.LENGTH_SHORT).show()
             val intent = Intent(this, AudioService::class.java)
             intent.action = "STOP"
             startService(intent)
         }
     }
 }
