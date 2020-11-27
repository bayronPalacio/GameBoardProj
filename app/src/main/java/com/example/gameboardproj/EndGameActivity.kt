package com.example.gameboardproj

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_end_game.*
import kotlinx.android.synthetic.main.activity_group.*
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

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: RecyclerView.Adapter<*>

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

        // Get Votes From DB


        // Populate RecyclerViews
        // Test
        var array : Array<FinalVoteData> = emptyArray()
        populateRecycler(array, recyclerViewInitialMCVoteTrue)
        populateRecycler(array, recyclerViewInitialMCVoteFalse)
        populateRecycler(array, recyclerViewFinalMCVoteTrue)
        populateRecycler(array, recyclerViewFinalMCVoteFalse)

        // End Game - Handle DB? navigate back to UserMain?
        buttonEndGame.setOnClickListener{
            Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show()
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


    private fun getMCVoteData(url: String, urlPath: String, array: Array<FinalVoteData>){
        // TODO
    }

    private fun populateRecycler(array: Array<FinalVoteData>, recyclerView: RecyclerView){
        layoutManager = LinearLayoutManager(this)
        adapter = FinalMCVoteAdapter(array)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }
}

// Adapter for Recycler View
class FinalMCVoteAdapter(private var dataSet: Array<FinalVoteData>) :
    RecyclerView.Adapter<FinalMCVoteAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(rip: FinalVoteData) {
            // Create a .xml for data?
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

    fun update(modelList : Array<FinalVoteData>){
        dataSet = modelList
        notifyDataSetChanged()
    }
}

// MC Table follows this Structure... ?
data class FinalVoteData constructor(val mcStatement : String,
                                     val mcInitialVotesFor : List<String>,
                                     val mcInitialVotesAgainst : List<String>,
                                     val mcFinalVotesFor : List<String>,
                                     val mcFinalVotesAgainst : List<String>){}