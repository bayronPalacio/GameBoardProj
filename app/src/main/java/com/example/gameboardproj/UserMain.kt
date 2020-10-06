package com.example.gameboardproj

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user_main.*

class UserMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main)


        btn_join_group.setOnClickListener{
            val toCreateRipScreen = Intent(this, CreateRipActivity::class.java)
            startActivity(toCreateRipScreen)
        }
    }
}