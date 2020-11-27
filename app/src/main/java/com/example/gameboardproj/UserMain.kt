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
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_user_main.*
import org.json.JSONObject
import java.io.BufferedReader

/**
 *  This Class houses the user's screen upon logging in
 *
 * Currently Used to navigate to RiP Creation
 * Will also be used for MC voting
 */
class UserMain : AppCompatActivity() {

    private var sharedPrefFile : SharedPreferences? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main)

        sharedPrefFile = this.getSharedPreferences("sharedPreferences", 0);
        val editor = sharedPrefFile!!.edit()

        var name = sharedPrefFile?.getString("Name",null)

        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        var url = fileReader.readLine()
        var urlPath = "$url/getMC"

        title_stud.text = "Welcome to the Game Board \n $name"

        val mcTest = JSONObject()
        mcTest.put("mc", "")

        // Vote For MC
        agree.setOnClickListener{
            editor.putString("mcVote", "Agree")
            editor.apply()
        }

        // Vote Against MC
        disagree.setOnClickListener{
            editor.putString("mcVote", "Disagree")
            editor.apply()
        }

//        val que = Volley.newRequestQueue(this)
////        val getMCValue = JsonObjectRequest(
////            Request.Method.GET, urlPath,mcTest,
////            Response.Listener { response ->
////                println("Response from server -> " + response["responseLogin"])
////            }, Response.ErrorListener {
////                println("Error from server")
////            }
////        )
////        que.add(getMCValue)

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

        // Create a RiP
        btn_join_group.setOnClickListener{
            val toCreateRipScreen = Intent(this, CreateRipActivity::class.java)
            startActivity(toCreateRipScreen)
        }
    }
}