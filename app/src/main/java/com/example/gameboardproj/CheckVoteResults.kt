package com.example.gameboardproj

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_check_vote_result.*
import java.io.BufferedReader

class CheckVoteResults : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: RecyclerView.Adapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_vote_result)

        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        var url = fileReader.readLine()

        var listOfRips = ArrayList<RiP>()

        getRips(url, listOfRips)

        buttonBackToCreateRip.setOnClickListener{
            startActivity(Intent(this, CreateRipActivity::class.java ))
        }
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
}