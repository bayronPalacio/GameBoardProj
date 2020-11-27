package com.example.gameboardproj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_game_board.*
import kotlinx.android.synthetic.main.activity_user_main.*
import java.io.BufferedReader

/**
 * @author Daniel Cooper
 *
 * The main Game Board Activity
 *
 * will allow the user to vote on the MC then depending on their vote open a fragment
 * for the corresponding half of the gameboard.
 */
class GameBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_board)

        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        var url = fileReader.readLine()
        var urlPath = "$url/getMC"

        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET, urlPath,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                Log.i("Res",response)
                textViewMainClaim.text = "$response"
//                        textView.text = "Response is: ${response.substring(0, 500)}"
            },
            Response.ErrorListener { Log.i("res","did not work")})
        queue.add(stringRequest)

        // Switch Game Boards
        // False Board - Left
        falseLayout.setOnClickListener {

        }

        // True Board - Right
        trueLayout.setOnClickListener {

        }
    }
}
