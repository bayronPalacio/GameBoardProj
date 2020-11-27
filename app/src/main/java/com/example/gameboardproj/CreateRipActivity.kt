package com.example.gameboardproj

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_group.*
import java.io.BufferedReader

import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

/**
 * @author Daniel Cooper
 *
 * Allows for the creation and modification of RiP's
 *
 * Enter Text into the TextBox then Press Create to Create a RIP
 * All RIPS will be displayed within the RecyclerView
 *
 * Select a RIP in the RecyclerView to have its statement autofill the below textbox
 * Modify the Text and press Update to Modify
 * OR
 * Press Vote to vote for the RIP that was selected
 *
 * TODO
 * Make RecyclerView refresh after Create and Update (have to leave page and return atm)
 */

class CreateRipActivity : AppCompatActivity() {

    // DB and Dao
    //private var dataBaseGame = GameDatabase.getAppDataBase(context = this)
    //private var ripDao: ReasonInPlayDao? = null

    private lateinit var sharedPrefFile : SharedPreferences
    private var currentUser = ""
    private var selectedRip = ""
    private var mainClaim = ""

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: RecyclerView.Adapter<*>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        // Get UserName from Shared Preferences
        sharedPrefFile = this.getSharedPreferences("sharedPreferences", 0);
        currentUser = sharedPrefFile.getString("Name", "").toString()

        // Access text file query Server
        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        var url = fileReader.readLine()

        // Get Main Claim from DB
        getMC(url)

        // Create an Array of RiPs, ArrayList used to get data (Size unknown), copied to array
        var listOfRips = ArrayList<RiP>()
        var array = getRips(url, listOfRips)
        // Test for data
        for(entity in array){
            Log.d("testing", entity.toString())
        }

        create_rip.setOnClickListener {
            // If ReasonInPlay Input has text
            if (ripInputText.text.trim().toString() != null) {

                // The associated URL path to Server
                var urlPath = "$url/createRip"

                val reasonInPlay = JSONObject()

                // Follows format - RIP_TABLE (RIP_ID, RIP_STATEMENT ,RIP_SUBMITTED_BY, RIP_VOTE, MC_ID)
                // unique ID is pre-generated
                reasonInPlay.put("ripStatement", ripInputText.text.toString())
                reasonInPlay.put("ripSubmittedBy", currentUser)
                reasonInPlay.put("ripVote", 0)
                reasonInPlay.put("mcStatement", mainClaim)

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

        update_rip.setOnClickListener {
            // URL Path to return all Rips in DB
            var urlPath = "$url/updateRip"

            // Update the RiP
            val reasonInPlay = JSONObject()
            reasonInPlay.put("oldStatement", selectedRip) // will use to find the rip
            reasonInPlay.put("ripStatement", ripInputText.text.toString()) // update this data

            val requestQueue = Volley.newRequestQueue(this)
            val putRequest = JsonObjectRequest(
                Request.Method.POST,
                urlPath,
                reasonInPlay,
                { response ->
                    if (response["responseServer"].toString().equals("Yes")) {
                        Toast.makeText(this, "RiP has been updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "RiP does not exits", Toast.LENGTH_SHORT).show()
                    }
                    println("Response from server -> " + response["responseServer"])
                },
                {
                    println(it.toString())
                }
            )
            // put query into request queue and perform
            requestQueue.add(putRequest)
        }

        vote_rip.setOnClickListener{
            // URL Path to return all Rips in DB
            var urlPath = "$url/incrementRipVote"

            // Update the RiP
            val reasonInPlay = JSONObject()
            reasonInPlay.put("oldStatement", selectedRip) // will use to find the rip
            reasonInPlay.put("ripVote", 1) // update this data

            val requestQueue = Volley.newRequestQueue(this)
            val putRequest = JsonObjectRequest(
                Request.Method.POST,
                urlPath,
                reasonInPlay,
                { response ->
                    if (response["responseServer"].toString().equals("Yes")) {
                        Toast.makeText(this, "Vote is cast", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Vote failed - Please Try Again", Toast.LENGTH_SHORT).show()
                    }
                    println("Response from server -> " + response["responseServer"])
                },
                {
                    println(it.toString())
                }
            )
            // put query into request queue and perform
            requestQueue.add(putRequest)

            // Once Voted - Move to see Results
            startActivity(Intent(this, CheckVoteResults::class.java))
        }
    }

    private fun getRips(url : String, listOfRips :  ArrayList<RiP>) :  Array<RiP>{

        // URL Path to return all Rips in DB
        var urlPath = "$url/getAllRips"
        var array : Array<RiP> = emptyArray()

        // Follows format - RIP_TABLE (RIP_ID, RIP_STATEMENT ,RIP_SUBMITTED_BY, RIP_VOTE, MC_ID)
        val requestQueue = Volley.newRequestQueue(this)
        val getRequest = JsonArrayRequest(
            Request.Method.GET,    // How
            urlPath,
            null,// What
            Response.Listener { response ->
                for(i in 0 until response.length()) {
                    // now hold a string of the object
                    var entity : String = response[i] as String
                    var splitString = entity.split(",",":")

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
                refreshRecycler(array)
            },
            Response.ErrorListener {
                println(it.toString())
            }
        )
        // put query into request queue and perform
        requestQueue.add(getRequest)
        return array
    }

    private fun getMC(url : String) {
        var urlPath = "$url/getMC"

        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET, urlPath,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                Log.i("Res",response)
                mainClaim = "$response"
//                        textView.text = "Response is: ${response.substring(0, 500)}"
            },
            Response.ErrorListener { Log.i("res","did not work")})
        queue.add(stringRequest)
    }

    private fun refreshRecycler(array : Array<RiP>){
        layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(array) { rip: RiP -> itemClicked(rip) }

        recyclerView = findViewById<RecyclerView>(R.id.ripRecyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun itemClicked(rip : RiP) {
        ripInputText.setText(rip.ripStatement)
        selectedRip = rip.ripStatement.toString()
        Log.d("testing", selectedRip)
    }
}

// Adapter for Recycler View
class MyAdapter(private var dataSet: Array<RiP>, val clickListener: (RiP) -> Unit) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(rip: RiP, clickListener: (RiP) -> Unit) {
            val tv = view.findViewById<TextView>(R.id.ripTextView)
            tv.text = rip.ripStatement
            view.setOnClickListener { clickListener(rip) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = LayoutInflater.from(parent.context).inflate(R.layout.rip_item, parent, false)
        return ViewHolder(vh)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(dataSet[position], clickListener)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun update(modelList : Array<RiP>){
        dataSet = modelList
        notifyDataSetChanged()
    }
}

// Follows format - RIP_TABLE (RIP_ID, RIP_STATEMENT ,RIP_SUBMITTED_BY, RIP_VOTE, MC_ID)
data class RiP constructor(val ripID : String, val ripStatement : String, val ripSubmittedBy : String, val ripVote : Int, val ripMainClaimStatement : String){

    override fun toString(): String {
        return "RiP(ripID='$ripID', ripStatement='$ripStatement', ripSubmittedBy=$ripSubmittedBy, ripVote=$ripVote, ripMainClaimID=$ripMainClaimStatement)"
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