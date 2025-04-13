 package com.grupo11.audioservice

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
             // Adicione aqui a lógica para o botão Play
         }

         pauseButton.setOnClickListener {
             Toast.makeText(this, "Pause clicado", Toast.LENGTH_SHORT).show()
             // Adicione aqui a lógica para o botão Pause
         }

         stopButton.setOnClickListener {
             Toast.makeText(this, "Stop clicado", Toast.LENGTH_SHORT).show()
             // Adicione aqui a lógica para o botão Stop
         }
     }
 }
