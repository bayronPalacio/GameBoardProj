package com.example.gameboardproj

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.gameboardproj.data.MainClaim
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_leader_main.*
import kotlinx.android.synthetic.main.activity_registration.*
import org.json.JSONObject
import java.io.BufferedReader

/*
Author: Haonan Cao
Purpose: put main claim into mainclaim table, update database
 */
class LeaderMain : AppCompatActivity() {

    private var dataBaseGame: GameDatabase? = null
    private var mainclaimDao: MainClaimDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_main)

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
                        if(response["responseServer"].toString().equals("Yes") ||response["responseServer"].toString().equals("Updated")){
                            Toast.makeText(this, "MC has been created", Toast.LENGTH_LONG).show()

                        }
                        else{
                            Toast.makeText(this, "MC already exits", Toast.LENGTH_LONG).show()
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

        checkVote.setOnClickListener{
            //val toCheckVote = Intent(this,CheckVoteResults::class.java)
            //startActivity(toCheckVote)
        }
    }
}