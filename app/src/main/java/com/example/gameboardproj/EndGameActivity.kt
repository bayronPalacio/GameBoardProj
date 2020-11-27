package com.example.gameboardproj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * @author Daniel Cooper
 *
 * The Eng Game Board Activity
 *
 * User will be redirected here when the game Time ends
 *
 * TODO
 * Implement Both Recycler Views
 * Pull MC votes from DB
 * Display the original MC Votes and the Final MC Votes
 *      - To show how many people changed their minds
 */
class EndGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_game)
    }
}