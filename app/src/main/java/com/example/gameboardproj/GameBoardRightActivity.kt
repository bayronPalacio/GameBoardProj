package com.example.gameboardproj

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_game_board_right.*

/**
 * @author Daniel Cooper
 *
 * The Right Game Board Activity
 *
 * User will be redirected here on beginning of the game if they voted For the Main Claim
 * They can change their MC vote by selecting Change MC Vote
 *
 * They can vote for or against the RIP by pressing the Affirm or Refute buttons
 *
 * TODO
 * pressing back should go can be funky if you've switched sides multiple times
 * make changes in DB when voting on Rip
 * Allow MC vote to change
 * make the UI look nicer?
 * Implement Timer?
 * when timer ends go to EndGameActivity
 *
 */
class GameBoardRightActivity : AppCompatActivity() {

    private lateinit var sharedPrefFile : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_board_right)

        sharedPrefFile = this.getSharedPreferences("sharedPreferences", 0);
        var rip = sharedPrefFile.getString("currentRip", "").toString()

        textViewCurrentRipRight.text = rip


        constraintLayoutChangeMCVoteRight.setOnClickListener{
            // Query the DB to change current users MC Vote - original is still stored in SharedPreferences until this point


            startActivity(Intent(this, GameBoardLeftActivity::class.java))
        }
    }
}