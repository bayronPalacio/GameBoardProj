package com.example.gameboardproj

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gameboardproj.data.ReasonInPlay
import com.example.gameboardproj.data.ReasonInPlayDao
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.rip_result.*

/**
 * @author Daniel Cooper
 *
 * Allows for the creation and modification of RiP's
 *
 * has a listener for two buttons that update or create RiPs
 * after RiP creation will navigate to the game board
 *
 */

class CreateRipActivity : AppCompatActivity() {

    // DB and Dao
    private var dataBaseGame = GameDatabase.getAppDataBase(context = this)
    private var ripDao: ReasonInPlayDao? = null

    private val sharedPrefFile = "sharedPref"
    private var ripStatement = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        // Shared Preferences, user data needed to implement RiP
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        var userFirstName = sharedPreferences.getString("user_firstName", "not found")
        var userLastName = sharedPreferences.getString("user_lastName", "not found")
        // var currentMC =    will need to access MC

        // RiP Creation onClicklistener
        create_rip.setOnClickListener {
            // Toast for if input was empty
            if (ripInputText.text.toString() != null) {
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
            } else {
                Toast.makeText(this, "Please enter your Reason in Play", Toast.LENGTH_LONG).show()
            }

        }

        // User wants to Update RiP
        update_rip.setOnClickListener {
            Observable.fromCallable {
                ripDao = dataBaseGame?.ripDao()

                //  change RiP statement and update
                if (ripStatement != "") {
                    val rip: ReasonInPlay = ripDao!!.getRipByStatement(ripStatement)
                    rip.rip_statement = ripStatement
                    ripDao?.updateRip(rip)
                }
            }
        }
    }
}