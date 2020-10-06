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

class CreateRipActivity : AppCompatActivity() {

    private var dataBaseGame = GameDatabase.getAppDataBase(context = this)
    private var ripDao: ReasonInPlayDao? = null
    private val sharedPrefFile = "sharedPref"
    lateinit var currentStudent : Student

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        var userFirstName = sharedPreferences.getString("user_firstName", "not found")
        var userLastName = sharedPreferences.getString("user_lastName", "not found")

        create_rip.setOnClickListener {
            if (ripInputText.text.toString() != null) {
                Toast.makeText(this, "A RiP has been created", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Please enter your Reason in Play", Toast.LENGTH_LONG).show()
            }
            Observable.fromCallable {
                ripDao = dataBaseGame?.ripDao()

                var ripStatement = ReasonInPlay(
                    rip_statement = create_rip.text.toString(),
                    rip_submitted_by = "$userFirstName $userLastName",
                    rip_vote = -1,
                    mc_id = 1   // need to get current mc
                )

                with(ripDao) {
                    this?.insertRip(ripStatement)
                }
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

            Toast.makeText(this, "RiP has been added to the Database", Toast.LENGTH_LONG).show()

            // Move activity to Game Board
            //val toGameBoard = Intent(this, GameBoardActivity::class.java)
            //startActivity(toGameBoard)
        }
    }
}