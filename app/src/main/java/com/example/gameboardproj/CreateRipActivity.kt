package com.example.gameboardproj

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gameboardproj.data.ReasonInPlay
import com.example.gameboardproj.data.ReasonInPlayDao
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_group.*
import java.io.BufferedReader

import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_leader_main.*

/**
 * @author Daniel Cooper
 *
 * Allows for the creation and modification of RiP's
 *
 * has a listener for two buttons that update or create RiPs
 * after RiP creation will navigate to the game board
 *
 * Must find current user, current RiP, and current MC on activity start
 *
 */

class CreateRipActivity : AppCompatActivity() {

    // DB and Dao
    private var dataBaseGame = GameDatabase.getAppDataBase(context = this)
    private var ripDao: ReasonInPlayDao? = null

    private val sharedPrefFile = "sharedPref"
    private var ripStatement = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        // Access text file query Server
        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        var url = fileReader.readLine()


        // Shared Preferences, user data needed to implement RiP
        //val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        //var userFirstName = sharedPreferences.getString("user_firstName", "not found")
        //var userLastName = sharedPreferences.getString("user_lastName", "not found")
        // var currentMC =    will need to access MC


        create_rip.setOnClickListener {
            // If ReasonInPlay Input has text
            if (ripInputText.text.trim().toString() != null) {

                // The associated URL path to Server
                var urlPath = "$url/createRip"

                val reasonInPlay = JSONObject()

                // Follows format - RIP_TABLE (RIP_ID, RIP_STATEMENT ,RIP_SUBMITTED_BY, RIP_VOTE, MC_ID)
                // unique ID is pre-generated
                reasonInPlay.put("ripStatement", ripInputText.text.toString())
                reasonInPlay.put("ripSubmittedBy", ripInputText.text.toString())
                // reasonInPlay.put("ripVote", ripInputText.text.toString())
                // reasonInPlay.put("ripAssociatedMainClaim", ripInputText.text.toString())

                val requestQueue = Volley.newRequestQueue(this)
                val createRequest = JsonObjectRequest(
                    Request.Method.POST,    // How
                    urlPath,                // Where
                    reasonInPlay,           // What
                    Response.Listener { response ->
                        if (response["responseServer"].toString().equals("Yes")) {
                            Toast.makeText(this, "RiP has been created", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "RiP already exits", Toast.LENGTH_SHORT).show()
                        }
                        println("Response from server -> " + response["responseServer"])
                    },
                    Response.ErrorListener {
                        println("Error from server")
                    }
                )
                // put query into request queue and perform
                requestQueue.add(createRequest)
            } else {
                Toast.makeText(this, "Please enter your Reason in Play", Toast.LENGTH_LONG).show()
            }
        }

        // Update RiP
        // TextBox will contain the current RiP on Start
        // Make Modification to Text, then Update
        // Check that current RiP ID exists, suggest Add
        // If ID exist - change statement
        update_rip.setOnClickListener {

            if (ripInputText.text.trim().toString() != null) {

                // The associated URL path to Server
                var urlPath = "$url/createRip"

                val reasonInPlay = JSONObject()

                reasonInPlay.get("ripID")




                /*
                Observable.fromCallable {
                    ripDao = dataBaseGame?.ripDao()

                    //  change RiP statement and update
                    if (ripStatement != "") {
                        val rip: ReasonInPlay = ripDao!!.getRipByStatement(ripStatement)
                        rip.rip_statement = ripStatement
                        ripDao?.updateRip(rip)
                    }
                }
                 */
            }

            fun GetCurrentUser(){

            }
        }


        /*

        * From Room DB
        Observable.fromCallable {
            ripDao = dataBaseGame?.ripDao()

            // Create RiP statement
            var ripStatement = ReasonInPlay(
                rip_statement = create_rip.text.toString(), // text from input
                rip_submitted_by = "$userFirstName $userLastName", // submitted by will display a full name
                rip_vote = -1,   // set to -1 by default - 0 or 1 for true false - updated later
                mc_id = 1   // need to get current mc - currently dummy var
            )

            // insert to DB
            with(ripDao) {
                this?.insertRip(ripStatement)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        Toast.makeText(this, "RiP has been added to the Database", Toast.LENGTH_LONG).show()
        ripStatement = create_rip.text.toString()

        // Move activity to Game Board
        //val toGameBoard = Intent(this, GameBoardActivity::class.java)
        //startActivity(toGameBoard)

         */
    }
}