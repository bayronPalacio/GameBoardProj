package com.example.gameboardproj

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_game_board_left.*
import kotlinx.android.synthetic.main.activity_group.*
import org.json.JSONObject
import java.io.BufferedReader

/**
 * @author Daniel Cooper
 *
 * TheLeft Game Board Activity
 *
 * User will be redirected here on beginning of the game if they voted against the Main Claim
 * They can change their MC vote by selecting Change MC Vote
 *
 * They can vote for or against the RIP by pressing the Affirm or Refute buttons
 *
 * TODO
 * pressing back should go can be funky if you've switched sides multiple times
 * make changes in DB when voting on Rip
 * Allow MC vote to change
 * make the UI look nicer?
 * Implement Timer
 * when timer ends go to EndGameActivity
 */

class GameBoardLeftActivity : AppCompatActivity() {

    private lateinit var sharedPrefFile : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_board_left)

        sharedPrefFile = this.getSharedPreferences("sharedPreferences", 0);
        var rip = sharedPrefFile.getString("currentRip", "").toString()
        var currentUser = sharedPrefFile.getString("Name", "").toString()
        textViewCurrentRipLeft.text = rip
        Log.d("testing", "The current user is: " + currentUser)
        // Access text file query Server
        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        var url = fileReader.readLine()

        constraintLayoutLeftBoardRips.setOnClickListener{
            startActivity(Intent(this, GameBoardRightActivity::class.java))
        }

        constraintLayoutAffirm.setOnClickListener{
            // The associated URL path to Server
            var urlPath = "$url/placeRipVote"

            val myVote = JSONObject()
            var list = listOf(currentUser)
            // Follows format - RIP_TABLE (RIP_ID, RIP_STATEMENT ,RIP_SUBMITTED_BY, RIP_VOTE, MC_ID)
            // unique ID is pre-generated
            myVote.put("currentRip", textViewCurrentRipLeft.text)
            myVote.put("currentUser", list)
            myVote.put("vote", "True")

            val requestQueue = Volley.newRequestQueue(this)
            val createRequest = JsonObjectRequest(
                Request.Method.POST,    // How
                urlPath,                // Where
                myVote,           // What
                Response.Listener { response ->
                    if (response["responseServer"].toString().equals("VotesFor Updated")) {
                        Toast.makeText(this, "VotesFor Updated", Toast.LENGTH_SHORT).show()
                    }
                    if(response["responseServer"].toString().equals("VotesAgainst Updated")) {
                        Toast.makeText(this, "VotesAgainst Updated", Toast.LENGTH_SHORT).show()
                    }
                    if(response["responseServer"].toString().equals("Update Failed")) {
                        Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener {
                    println("Error from server")
                }
            )
            // put query into request queue and perform
            requestQueue.add(createRequest)
        }

        constraintLayoutRefute.setOnClickListener{

        }
    }
}