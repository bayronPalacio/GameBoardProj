package com.example.gameboardproj

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_leader_main.*
import org.json.JSONObject
import java.io.BufferedReader

/*
Author: Haonan Cao
Purpose: put main claim into mainclaim table, update database
 */
class LeaderMain : AppCompatActivity() {

    private var dataBaseGame: GameDatabase? = null
    private var mainclaimDao: MainClaimDao? = null

    private var sharedPrefFile : SharedPreferences? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_main)

        sharedPrefFile = this.getSharedPreferences("sharedPreferences", 0);

        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        var url = fileReader.readLine()

        setMCBtn.setOnClickListener{
            if (setMCEditText.text.toString() == ""){
                Toast.makeText(this,"Please set a main claim", Toast.LENGTH_LONG).show()
            }else{
                var urlPath = "$url/addMC"

                val newMC = JSONObject()

                newMC.put("mc", setMCEditText.text.toString())

                val que = Volley.newRequestQueue(this)
                val req = JsonObjectRequest(
                    Request.Method.POST, urlPath, newMC,
                    Response.Listener { response ->
                        if(response["responseServer"].toString().equals("Yes") ){
                            Toast.makeText(this, "MC has been created", Toast.LENGTH_LONG).show()
                        }
                        else if(response["responseServer"].toString().equals("Updated")){
                            Toast.makeText(this, "MC has been updated", Toast.LENGTH_LONG).show()
                        }
                        println("Response from server -> " + response["responseServer"])
                    }, Response.ErrorListener {
                        println("Error from server")
                    }
                )
                que.add(req)
//                Observable.fromCallable {
//                    dataBaseGame = GameDatabase.getAppDataBase(context = this)
//                    mainclaimDao = dataBaseGame?.mainclaimDao()
//
//                    var mainClaim = MainClaim(mc_id = 1, mc_statement = setMCEditText.text.toString(),mc_votes = 0,mc_professor_id = 1)
//
//                    with(mainclaimDao) {
//                        this?.insertMainClaim(mainClaim)
//                    }
//                }.subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe()
//                Toast.makeText(this,"A new main claim has been created",Toast.LENGTH_LONG).show()
            }
        }


        val editor = sharedPrefFile!!.edit()


        var time = timerInLearMain.text.toString().toLong()
        var urlPath = "$url/setTime"

        val newTime = JSONObject()
        val timer = object: CountDownTimer(time*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerInLearMain.setText(time.toString())
                newTime.put("time", time)

                editor.putString("timeLeft", time.toString())
                editor.apply()

                val que = Volley.newRequestQueue(applicationContext)
                val req = JsonObjectRequest(
                    Request.Method.POST, urlPath, newTime,
                    Response.Listener { response ->
                        if(response["responseServer"].toString().equals("Yes") ){
                            Toast.makeText(applicationContext, "Time has been created", Toast.LENGTH_LONG).show()
                        }
                        else if(response["responseServer"].toString().equals("Updated")){
                            //Toast.makeText(applicationContext, "Time has been updated", Toast.LENGTH_LONG).show()
                        }
                        println("Response from server -> " + response["responseServer"])
                    }, Response.ErrorListener {
                        println("Error from server")
                    }
                )
                que.add(req)
                time = (time*1000 - 1000)/1000
            }
            override fun onFinish() {
                //val toEndGame = Intent(applicationContext,EndGameActivity::class.java)
                //startActivity(toEndGame)
            }
        }

        startScrimmage.setOnClickListener {
            time = timerInLearMain.text.toString().toLong()
            timer.start()

        }

        endScrimmage.setOnClickListener {
            timer.cancel()
            val toEndGame = Intent(this,EndGameActivity::class.java)
            startActivity(toEndGame)
        }

        checkVote.setOnClickListener{
            var urlPathGetVotes = "$url/voteMC"

            val requestVotes = JSONObject()
            requestVotes.put("vote", "getTotalVotes")
            requestVotes.put("numberOfMembers", 0)

            val queTotalVotes = Volley.newRequestQueue(this)
            val reqVotes = JsonObjectRequest(
                Request.Method.POST, urlPathGetVotes, requestVotes,
                Response.Listener { response ->
                    totalVotes.text = "Agree votes: " +  response["Agree votes"] + "\nDisagree votes: " + response["Disagree votes"]
                    println("Response from server -> $response")
                }, Response.ErrorListener {
                    println("Error from server")
                }
            )
            queTotalVotes.add(reqVotes)
        }

        btnCreateGroups.setOnClickListener {
            if(inputPersonPerGroup.text.toString() == ""){
                Toast.makeText(this, "Please enter a number of students per group", Toast.LENGTH_LONG).show()
            }
            else{
                var urlPathGetVotes = "$url/voteMC"

                val createGroups = JSONObject()
                createGroups.put("vote", "getTotalVotes")
                createGroups.put("numberOfMembers", inputPersonPerGroup.text.toString())

                val queTotalVotes = Volley.newRequestQueue(this)
                val reqVotes = JsonObjectRequest(
                    Request.Method.POST, urlPathGetVotes, createGroups,
                    Response.Listener { response ->
                        println("Response from server -> $response")
                    }, Response.ErrorListener {
                        println("Error from server")
                    }
                )
                queTotalVotes.add(reqVotes)
            }
        }

    }
}