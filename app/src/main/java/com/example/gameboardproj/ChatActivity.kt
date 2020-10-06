package com.example.gameboardproj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * @author Daniel Cooper
 *
 * The chat activity
 * allow participants to discuss the debate remotely
 */
class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
    }
}