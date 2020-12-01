package com.example.gameboardproj

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_end_game.*
import kotlinx.android.synthetic.main.activity_group.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader

/**
 * @author Daniel Cooper
 *
 * The End Game Board Activity
 *
 * User will be redirected here when the game Time ends
 *
 * TODO
 * Populate data into Recycler Views from DB
 * Display the original MC Votes and the Final MC Votes
 *      - To show how many people changed their minds
 */
class EndGameActivity : AppCompatActivity() {

    private lateinit var sharedPrefFile : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_game)

        sharedPrefFile = this.getSharedPreferences("sharedPreferences", 0);
        var currentUser = sharedPrefFile.getString("Name", "").toString()

        // Access text file query Server
        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        var url = fileReader.readLine()

        // Set TextView Values
        setMCText(url)
        initializeFirstVoteRecyclers()

        // Get Votes From DB
        getMCVoteData(url)

        // End Game - Handle DB? navigate back to UserMain?
        buttonEndGame.setOnClickListener{
            finishAffinity() // Close entire application
        }
    }

    private fun setMCText(url : String){
        // Set Path for query
        var urlPath = "$url/getMC"

        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET, urlPath,
            Response.Listener<String> { response ->
                Log.i("Res",response)
                textViewMainClaimEnd.text = "$response"
            },
            Response.ErrorListener { Log.i("res","did not work")})
        queue.add(stringRequest)
    }

    private fun initializeFirstVoteRecyclers(){
        var forString = sharedPrefFile.getString("votesFor", "").toString()
        var againstString = sharedPrefFile.getString("votesAgainst", "").toString()

        var forArray = parseString(forString)
        var againstArray = parseString(againstString)

        var listOfVotes: ArrayList<FinalVoteData> = ArrayList<FinalVoteData>()
        for ( i in forArray){
            var data = FinalVoteData("", i, "Agree")
            listOfVotes.add(data)
        }

        var agreeArray = listOfVotes.toTypedArray()

        listOfVotes.clear()
        for ( i in forArray){
            var data = FinalVoteData("", i, "Disagree")
            listOfVotes.add(data)
        }

        var disagreeArray = listOfVotes.toTypedArray()

        var initialLayoutManagerAgainst = LinearLayoutManager(this)
        var initialAdapterAgainst = FinalMCVoteAdapter(disagreeArray)
        recyclerViewInitialMCVoteFalse.adapter = initialAdapterAgainst
        recyclerViewInitialMCVoteFalse.layoutManager = initialLayoutManagerAgainst

        var initialLayoutManagerFor = LinearLayoutManager(this)
        var initialAdapterFor = FinalMCVoteAdapter(agreeArray)
        recyclerViewInitialMCVoteTrue.adapter = initialAdapterFor
        recyclerViewInitialMCVoteTrue.layoutManager = initialLayoutManagerFor
    }

    private fun parseString(list : String) : Array<String>{
        var newList : Array<String> = list.split("/").toTypedArray()
        return newList
    }

    private fun getMCVoteData(url: String) {
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

                var agreeVotes: ArrayList<FinalVoteData> = ArrayList<FinalVoteData>()
                var refuteVotes: ArrayList<FinalVoteData> = ArrayList<FinalVoteData>()
                for (i in 0 until listOfVotes.size) {
                    var vote = listOfVotes[i].vote
                    if (vote == "Agree") {
                        agreeVotes.add(listOfVotes[i])
                        Log.d("testing", "Agree: " + listOfVotes[i].vote.toString())
                    }
                    if (vote == "Disagree") {
                        refuteVotes.add(listOfVotes[i])
                        Log.d("testing", "Disagree: " + listOfVotes[i].vote.toString())
                    }
                    Log.d("testing", (listOfVotes[i].vote.compareTo("Agree") == 0).toString())
                }

                var agreeArray = agreeVotes.toTypedArray()
                var adapterFor = FinalMCVoteAdapter(agreeArray)

                var refuteArray = refuteVotes.toTypedArray()
                var adapterAgainst = FinalMCVoteAdapter(refuteArray)

                var layoutManagerAgainst = LinearLayoutManager(this)
                recyclerViewFinalMCVoteFalse.adapter = adapterAgainst
                recyclerViewFinalMCVoteFalse.layoutManager = layoutManagerAgainst

                var layoutManagerFor = LinearLayoutManager(this)
                recyclerViewFinalMCVoteTrue.adapter = adapterFor
                recyclerViewFinalMCVoteTrue.layoutManager = layoutManagerFor
            },
            {
                println(it.toString())
            }
        )
        // put query into request queue and perform
        requestQueue.add(getRequest)
    }
}

// Adapter for Recycler View
class FinalMCVoteAdapter(private var dataSet: Array<FinalVoteData>) :
    RecyclerView.Adapter<FinalMCVoteAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(data: FinalVoteData) {
            val textViewName = view.findViewById<TextView>(R.id.ripTextView)
            textViewName.text = data.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = LayoutInflater.from(parent.context).inflate(R.layout.rip_item, parent, false) // make a new layout?
        return ViewHolder(vh)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}

// MC Table follows this Structure... ?
data class FinalVoteData constructor(val email : String,
                                     val name : String,
                                     val vote : String)