package com.example.gameboardproj

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_leader_main.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_user_main.*
import org.json.JSONObject
import java.io.BufferedReader

/**
 *  @author: Bayron Arturo Palacio
 *
 *  This Class houses the user's screen upon logging in
 *
 * Currently Used to navigate to RiP Creation
 * Will also be used for MC voting
 */
class UserMain : AppCompatActivity() {

    private var sharedPrefFile : SharedPreferences? = null;

    var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main)

        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        url = fileReader.readLine()

        sharedPrefFile = this.getSharedPreferences("sharedPreferences", 0);
        val editor = sharedPrefFile!!.edit()

        var name = sharedPrefFile?.getString("Name",null)
        var email = sharedPrefFile?.getString("Email",null)

        var urlPath = "$url/getMC"

        title_stud.text = "Welcome to the Game Board \n $name"

        // Vote For MC
        agree.setOnClickListener{
            editor.putString("mcVote", "Agree")
            editor.apply()
            if (email != null) {
                castVote("Agree",email)
            }
        }

        // Vote Against MC
        disagree.setOnClickListener{
            editor.putString("mcVote", "Disagree")
            editor.apply()
            if (email != null) {
                castVote("Disagree",email)
            }
        }

        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
                    Request.Method.GET, urlPath,
                    Response.Listener<String> { response ->
                        // Display the first 500 characters of the response string.
                        Log.i("Res",response)
                        main_claim.text = "THE MAIN CLAIM OF THE DAY IS \n $response"
//                        textView.text = "Response is: ${response.substring(0, 500)}"
                    },
                    Response.ErrorListener { Log.i("res","did not work")})
                queue.add(stringRequest)

        // When the user clicks on the RIP button, the app will fetch data from the user account to obtain the group number and
        //stored in the shared preferences file
        btn_join_group.setOnClickListener{
            var urlGroups = "$url/groupNumber"

            val userEmail = JSONObject()
            userEmail.put("Email", email)

            val que = Volley.newRequestQueue(this)
            val req = JsonObjectRequest(
                Request.Method.POST, urlGroups, userEmail,
                Response.Listener { response ->
                    editor.putString("Group Number",response["responseGroup"].toString())
                    editor.apply()
                    println("Response from server -> " + response["responseGroup"])
                }, Response.ErrorListener {
                    println("Error from server")
                }
            )
            que.add(req)
            if(sharedPrefFile?.getString("Group Number", " ") == " "){
                Toast.makeText(this, "Waiting for Group to be created", Toast.LENGTH_SHORT).show()
            }
            else{
                // Move to Rip Creation
                val toCreateRipScreen = Intent(this, CreateRipActivity::class.java)
                startActivity(toCreateRipScreen)
            }
        }
    }


    //Function to cast the vote of the user
    private fun castVote(vote: String, userEmail: String) {
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
                agree.isEnabled = false
                disagree.isEnabled = false
                println("Response from server -> " + response["responseServer"])
            }, Response.ErrorListener {
                println("Error from server")
            }
        )
        que.add(req)
    }
}