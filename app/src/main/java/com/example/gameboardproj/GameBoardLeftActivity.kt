package com.example.gameboardproj

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_check_vote_result.*
import kotlinx.android.synthetic.main.activity_game_board_left.*
import kotlinx.android.synthetic.main.activity_leader_main.*
import kotlinx.android.synthetic.main.activity_user_main.*
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
 */

class GameBoardLeftActivity : AppCompatActivity() {

    private lateinit var sharedPrefFile : SharedPreferences
    var url = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_board_left)

        sharedPrefFile = this.getSharedPreferences("sharedPreferences", 0);
        val editor =  sharedPrefFile!!.edit()
        var rip = sharedPrefFile.getString("currentRip", "Current RiP").toString()
        var currentUser = sharedPrefFile.getString("Name", "").toString()
        var userEmail = sharedPrefFile.getString("Email", "").toString()

        // Set TextViews - Current Rip, Timer?
        textViewGameLeftRIP.setText(rip)

        // Access text file query Server
        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        url = fileReader.readLine()

//        var urlTime = "$url/getTime"
//
//        val requestTime = JSONObject()
//        //requestTime.put("time", "0")
//
//        var timeLeft = ""
//        val queTime = Volley.newRequestQueue(this)
//        val reqTime = JsonObjectRequest(
//            Request.Method.GET, urlTime, requestTime,
//            Response.Listener { response ->
//                timeLeft = response as String
//                println("Response from server -> $response")
//            }, Response.ErrorListener {
//                println("Error from server")
//            }
//        )
//        queTime.add(reqTime)

        var timeLeft = sharedPrefFile.getString("timeLeft", "").toString()
        val timer = object: CountDownTimer(timeLeft.toLong()*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                if(timeLeft.toInt() >= 1){
                    textViewTimerInLeft.setText(timeLeft)

//                    var urlPath = "$url/setTime"
//                    val newTime = JSONObject()
//                    newTime.put("time", timeLeft)
//                    val que = Volley.newRequestQueue(applicationContext)
//                    val req = JsonObjectRequest(
//                        Request.Method.POST, urlPath, newTime,
//                        Response.Listener { response ->
//                            if(response["responseServer"].toString().equals("Yes") ){
//                                Toast.makeText(applicationContext, "Time has been created", Toast.LENGTH_LONG).show()
//                            }
//                            else if(response["responseServer"].toString().equals("Updated")){
//                                //Toast.makeText(applicationContext, "Time has been updated", Toast.LENGTH_LONG).show()
//                            }
//                            println("Response from server -> " + response["responseServer"])
//                        }, Response.ErrorListener {
//                            println("Error from server")
//                        }
//                    )
//                    que.add(req)
                }
                timeLeft = ((timeLeft.toInt()*1000 - 1000)/1000).toString()
            }
            override fun onFinish() {
                val toEndGame = Intent(applicationContext,EndGameActivity::class.java)
                startActivity(toEndGame)
            }
        }
        timer.start()

        // Navigate to the Other side of the Board
        buttonGameLeftChangeMC.setOnClickListener {
            // Change Main Claim Vote to Agree
            castMCVote("Agree", userEmail)
            // save rip that changed users mind
            editor.putString("ripThatChangeUserMind", rip)
            editor.apply()
            // Switch Boards
            startActivity(Intent(this, GameBoardRightActivity::class.java))
        }

        constraintLayoutAffirm.setOnClickListener {
            castRipAffirmVote(currentUser)
        }

        constraintLayoutRefute.setOnClickListener {
            castRipRefuteVote(currentUser)
        }

        // If users no longer wish to discuss current Rip - move back to Rip Selection
        buttonGameLeftEndRound.setOnClickListener{
            endRound(userEmail)
            startActivity(Intent(this, CreateRipActivity::class.java))
        }
    }

    private fun endRound(userEmail: String){
        // The associated URL path to Server
        var urlPath = "$url/resetRipVote"

        val user = JSONObject()
        user.put("userEmail", userEmail)

        val requestQueue = Volley.newRequestQueue(this)
        val createRequest = JsonObjectRequest(
            Request.Method.POST,    // How
            urlPath,                // Where
            user,           // What
            Response.Listener { response ->
                if(response["responseServer"].toString().equals("Rip Vote Tallies have been reset")) {
                    Toast.makeText(this, "RiP Votes reset", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                println("Error from server")
            }
        )
        // put query into request queue and perform
        requestQueue.add(createRequest)
    }

    private fun castRipRefuteVote(currentUser : String){
        // The associated URL path to Server
        var urlPath = "$url/placeRipVote"

        val myVote = JSONObject()
        var list = listOf(currentUser)
        // Follows format - RIP_TABLE (RIP_ID, RIP_STATEMENT ,RIP_SUBMITTED_BY, RIP_VOTE, MC_ID)
        // unique ID is pre-generated
        myVote.put("currentRip", textViewGameLeftRIP.text)
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

    private fun castRipAffirmVote(currentUser : String){
        // The associated URL path to Server
        var urlPath = "$url/placeRipVote"

        val myVote = JSONObject()
        var list = listOf(currentUser)
        // Follows format - RIP_TABLE (RIP_ID, RIP_STATEMENT ,RIP_SUBMITTED_BY, RIP_VOTE, MC_ID)
        // unique ID is pre-generated
        myVote.put("currentRip", textViewGameLeftRIP.text)
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

    private fun castMCVote(vote: String, userEmail: String) {

        var urlPathSubmitVote = "$url/voteMC"

        val voteFromStudent = JSONObject()
        voteFromStudent.put("student",userEmail)
        voteFromStudent.put("vote", vote)

        val que = Volley.newRequestQueue(this)
        val req = JsonObjectRequest(
            Request.Method.POST, urlPathSubmitVote, voteFromStudent,
            Response.Listener { response ->
                if(response["responseServer"].toString().equals("Agree") ){
                    Toast.makeText(this, "Vote has been cast", Toast.LENGTH_LONG).show()
                }
                else if(response["responseServer"].toString().equals("Disagree")){
                    Toast.makeText(this, "Vote has been cast*", Toast.LENGTH_LONG).show()
                }
                println("Response from server -> " + response["responseServer"])
            }, Response.ErrorListener {
                println("Error from server")
            }
        )
        que.add(req)
    }
}