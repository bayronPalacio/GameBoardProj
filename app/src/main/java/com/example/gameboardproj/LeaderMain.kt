package com.example.gameboardproj

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gameboardproj.data.MainClaim
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_leader_main.*
import kotlinx.android.synthetic.main.activity_registration.*

class LeaderMain : AppCompatActivity() {

    private var dataBaseGame: GameDatabase? = null
    private var mainclaimDao: MainClaimDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_main)

        setMCBtn.setOnClickListener{
            if (setMCEditText.text.toString() == ""){
                Toast.makeText(this,"Please set a main claim", Toast.LENGTH_LONG).show()
            }else{
                Observable.fromCallable {
                    dataBaseGame = GameDatabase.getAppDataBase(context = this)
                    mainclaimDao = dataBaseGame?.mainclaimDao()

                    var mainClaim = MainClaim(mc_id = 1, mc_statement = setMCEditText.text.toString(),mc_votes = 0,mc_professor_id = 1)

                    with(mainclaimDao) {
                        this?.insertMainClaim(mainClaim)
                    }
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
                Toast.makeText(this,"A new main claim has been created",Toast.LENGTH_LONG).show()
            }
        }
    }
}