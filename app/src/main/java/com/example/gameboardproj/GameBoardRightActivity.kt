package com.example.gameboardproj

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_game_board_left.*
import kotlinx.android.synthetic.main.activity_game_board_right.*
import org.json.JSONObject
import java.io.BufferedReader

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
 * Allow Final MC vote to change when switching sides of board
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
        var currentUser = sharedPrefFile.getString("Name", "").toString()

        // Set the TextView text - Rip, timer
        textViewCurrentRipRight.text = rip

        // Access text file query Server
        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        var url = fileReader.readLine()

        constraintLayoutChangeMCVoteRight.setOnClickListener{
            // Query the DB to change current users MC Vote - original is still stored in SharedPreferences until this point


            startActivity(Intent(this, GameBoardLeftActivity::class.java))
        }

        constraintLayoutRightBoardAffirm.setOnClickListener{
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
                        Toast.makeText(this, "Affirm Vote Cast", Toast.LENGTH_SHORT).show()
                    }
                    if(response["responseServer"].toString().equals("Update Failed")) {
                        Toast.makeText(this, "Vote Failed", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener {
                    println("Error from server")
                }
            )
            // put query into request queue and perform
            requestQueue.add(createRequest)
        }

        constraintLayoutRightBoardRefute.setOnClickListener{
            // The associated URL path to Server
            var urlPath = "$url/placeRipVote"

            val myVote = JSONObject()
            var list = listOf(currentUser)
            // Follows format - RIP_TABLE (RIP_ID, RIP_STATEMENT ,RIP_SUBMITTED_BY, RIP_VOTE, MC_ID)
            // unique ID is pre-generated
            myVote.put("currentRip", textViewCurrentRipLeft.text)
            myVote.put("currentUser", list)
            myVote.put("vote", "False")

            val requestQueue = Volley.newRequestQueue(this)
            val createRequest = JsonObjectRequest(
                Request.Method.POST,    // How
                urlPath,                // Where
                myVote,           // What
                Response.Listener { response ->
                    if(response["responseServer"].toString().equals("VotesAgainst Updated")) {
                        Toast.makeText(this, "Refute Vote Cast", Toast.LENGTH_SHORT).show()
                    }
                    if(response["responseServer"].toString().equals("Update Failed")) {
                        Toast.makeText(this, "Vote Failed", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener {
                    println("Error from server")
                }
            )
            // put query into request queue and perform
            requestQueue.add(createRequest)
        }
    }
}