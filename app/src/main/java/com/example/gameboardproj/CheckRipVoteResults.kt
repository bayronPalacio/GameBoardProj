package com.example.gameboardproj

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_check_vote_result.*
import kotlinx.android.synthetic.main.activity_leader_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader

/**
 * @author Daniel Cooper
 *
 * Displays the list of RiPs and their votes
 * A holding area while players wait for all votes to be made
 * Once all players have voted they can proceed to the game
 */
class CheckRipVoteResults : AppCompatActivity() {

    private lateinit var sharedPrefFile : SharedPreferences

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: RecyclerView.Adapter<*>
    private var initialMCVote = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_vote_result)

        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        var url = fileReader.readLine()

        sharedPrefFile = this.getSharedPreferences("sharedPreferences", 0);
        var editor = sharedPrefFile!!.edit()
        initialMCVote = sharedPrefFile.getString("mcVote", "").toString()

        var listOfRips = ArrayList<RiP>()

        getRips(url, listOfRips)

        val timer = object: CountDownTimer(500*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
               var timeLeft = sharedPrefFile.getString("timeLeft", "").toString()
                if(timeLeft.toInt() == 0){
                    val toEndGame = Intent(applicationContext,EndGameActivity::class.java)
                    startActivity(toEndGame)
                }else if(timeLeft.toInt() > 0){
                    timeLeftInCheckVote.setText(timeLeft)
                }
            }
            override fun onFinish() {
                val toEndGame = Intent(applicationContext,EndGameActivity::class.java)
                startActivity(toEndGame)
            }
        }
        timer.start()

        buttonBackToCreateRip.setOnClickListener{
            startActivity(Intent(this, CreateRipActivity::class.java ))
        }

        buttonToGame.setOnClickListener{

            var totalVotes = findWinningRip(url, listOfRips, editor)

            getMCVoteData(url, totalVotes, editor)
        }
    }

    private fun findWinningRip(url : String, listOfRips : ArrayList<RiP>, editor : SharedPreferences.Editor): Int {
        // count votes
        var totalVotes = 0
        var winningRip : RiP
        for(rip : RiP in listOfRips){
            totalVotes += rip.ripVote
            var individualVotes = 0
            individualVotes = rip.ripVote
            for(rip : RiP in listOfRips){
                if(rip.ripVote > individualVotes)
                    winningRip = rip
            }
        }
        Log.d("testing", totalVotes.toString())
        var urlPath = "$url/findWinningRip"
        val requestQueue = Volley.newRequestQueue(this)
        val getRequest = JsonObjectRequest(
            Request.Method.GET,    // How
            urlPath,                // Where
            null,           // What
            Response.Listener { response ->
                var rip : String = response["rip_statement"] as String
                println(rip.toString())
                editor.putString("currentRip", rip)
                editor.apply()
            },
            Response.ErrorListener {
                println("Error from server")
            }


        )
        // put query into request queue and perform
        requestQueue.add(getRequest)

        return totalVotes
    }

    private fun getRips(url: String, listOfRips: ArrayList<RiP>): Array<RiP> {

        // URL Path to return all Rips in DB
        var urlPath = "$url/getAllRips"
        var array: Array<RiP> = emptyArray()

        // Follows format - RIP_TABLE (RIP_ID, RIP_STATEMENT ,RIP_SUBMITTED_BY, RIP_VOTE, MC_ID)
        val requestQueue = Volley.newRequestQueue(this)
        val getRequest = JsonArrayRequest(
            Request.Method.GET,    // How
            urlPath,
            null,// What
            Response.Listener { response ->
                for (i in 0 until response.length()) {
                    // now hold a string of the object
                    var entity: String = response[i] as String
                    var splitString = entity.split(",", ":")

                    val id = splitString[1].filterNot { it == '"' }.trim()
                    val statement = splitString[3].filterNot { it == '"' }.trim()
                    val submittedBy = splitString[5].trim()
                    val vote = Integer.parseInt(splitString[7].trim())
                    val forMC = splitString[9].dropLast(1).trim()
                    var ripObject: RiP = RiP(id, statement, submittedBy, vote, forMC)

                    // Log.d("testing", ripObject.toString())
                    listOfRips.add(ripObject)
                }
                array = listOfRips.toTypedArray()

                layoutManager = LinearLayoutManager(this)
                adapter = ResultAdapter(array)

                recyclerView = findViewById<RecyclerView>(R.id.recyclerViewVote)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter
            },
            Response.ErrorListener {
                println(it.toString())
            }
        )
        // put query into request queue and perform
        requestQueue.add(getRequest)
        return array
    }

    private fun getMCVoteData(url: String, totalVotes : Int, editor : SharedPreferences.Editor){
        var urlPath = "$url/getFinalMCVote"

        var listOfVotes: ArrayList<FinalVoteData> = ArrayList<FinalVoteData>()
        var userEmail = sharedPrefFile.getString("Email", "").toString()

        val user = JSONArray()
        user.put(0, userEmail)

        // Follows format - RIP_TABLE (RIP_ID, RIP_STATEMENT ,RIP_SUBMITTED_BY, RIP_VOTE, MC_ID)
        val requestQueue = Volley.newRequestQueue(this)
        val getRequest = JsonArrayRequest(
            Request.Method.POST,    // How
            urlPath,
            user,// What
            { response ->
                for (i in 0 until response.length()) {
                    // now hold a string of the object
                    Log.d("testing", response[i].toString())

                    var entity = response[i].toString()

                    var splitString = entity.split(",", ":")

                    val email = splitString[1].filterNot { it == '"' }.trim()
                    val name = splitString[3].filterNot { it == '"' }.trim()
                    val vote = splitString[5].filterNot { it == '"' }.dropLast(1).trim()

                    Log.d("testing", email + " " + name + ' ' + vote)
                    var entry : FinalVoteData = FinalVoteData(email, name, vote)
                    listOfVotes.add(entry)
                }

                var forList = ""
                var AgainstList = ""

                for (i in 0 until listOfVotes.size) {
                    var vote = listOfVotes[i].vote
                    if (vote == "Agree") {
                        forList += listOfVotes[i].name + "/"
                    }
                    if (vote == "Disagree") {
                        forList += listOfVotes[i].name + "/"
                    }
                }

                editor.putString("forList", forList)
                editor.putString("againstList", AgainstList)
            },
            {
                println(it.toString())
            }
        )
        // put query into request queue and perform
        requestQueue.add(getRequest)

        var forMCVotes = sharedPrefFile.getString("forList", "").toString()
        var againstMCVotes = sharedPrefFile.getString("againstList", "").toString()

        var newForList = parseString(forMCVotes)
        var newAgainstList = parseString(forMCVotes)

        var totalMCVotes = newForList.size + newAgainstList.size

        if(totalVotes >= totalMCVotes) {
            // Go to game
            if (initialMCVote.compareTo("Agree") == 0)
                startActivity(Intent(this, GameBoardRightActivity::class.java))
            else
                startActivity(Intent(this, GameBoardLeftActivity::class.java))
        }
    }

    private fun parseString(list : String) : Array<String>{
        var newList : Array<String> = list.split("/").toTypedArray()
        return newList
    }

    class ResultAdapter(private val dataSet: Array<RiP>) :
        RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            fun bindItem(rip: RiP) {
                val textViewStatement = view.findViewById<TextView>(R.id.ripResultStatement)
                val textViewVote = view.findViewById<TextView>(R.id.ripResultVote)
                textViewStatement.text = rip.ripStatement
                textViewVote.text = rip.ripVote.toString()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val vh = LayoutInflater.from(parent.context).inflate(R.layout.rip_result, parent, false)
            return ViewHolder(vh)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItem(dataSet[position])
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }
    }

    // Follows format - RIP_TABLE (RIP_ID, RIP_STATEMENT ,RIP_SUBMITTED_BY, RIP_VOTE, MC_ID)
    data class RiP constructor(
        val ripID: String,
        val ripStatement: String,
        val ripSubmittedBy: String,
        val ripVote: Int,
        val ripMainClaimStatement: String
    ) {

        override fun toString(): String {
            return "RiP(ripID='$ripID', ripStatement='$ripStatement', ripSubmittedBy=$ripSubmittedBy, ripVote=$ripVote, ripMainClaimID=$ripMainClaimStatement)"
        }
    }

    // When all RiP votes are cast, game can begin. MC votes for group (Initial) is collected and stored locally.
    data class FinalVoteData constructor(val email : String,
                                         val name : String,
                                         val vote : String)
}