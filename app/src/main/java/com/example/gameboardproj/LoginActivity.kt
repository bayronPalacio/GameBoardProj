package com.example.gameboardproj

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_login.register


/**
 * @author: Bayron Arturo Palacio
 *
 * This class handles the login function and check that the user exists in the database.
 * Also, it will launch the activity_registration when the text "click here to register" is clicked
 *
 */

class LoginActivity : AppCompatActivity() {

    private var dataBaseGame: GameDatabase? = null
    private var studentDao: StudentDao? = null
    private var studentInfo : Student? = null

    private val sharedPrefFile = "sharedPref"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //When the login button is clicked
        loginBtn.setOnClickListener {
                Observable.fromCallable {

                    //An instance of the Database and studentDao are created
                    dataBaseGame = GameDatabase.getAppDataBase(context = this)
                    studentDao = dataBaseGame?.studentDao()

                    with(studentDao) {
                        //A request is made to the database through the studentDao with the getStudentByEmail() method
                        studentInfo = this?.getStudentByEmail(inputEmail = username.text.toString())!!
                    }
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            Thread.sleep(300)
            //Check if the email and password entered by the user match with the user in the database
            if(studentInfo?.emailDb.toString() == username.text.toString() && studentInfo?.passwordDb.toString() == password.text.toString() )
            {
                Toast.makeText(this,"User Name\n" + studentInfo?.firstNameDb.toString() + " " + studentInfo?.lastNameDb.toString(),Toast.LENGTH_LONG).show()

                // Shared Preferences
                val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor =  sharedPreferences.edit()

                // Store the current user email
                editor.putString("user_email", username.text.toString())
                editor.apply()
                editor.commit()

                if(studentInfo?.roleTypeDb.toString().toInt() == 0)
                {
                    //if the user is a leader, the leader activity will be launched
                    val toLeaderScreen = Intent(this,LeaderMain::class.java)
                    startActivity(toLeaderScreen)
                }
                else{
                    //if the user is a player, the player activity will be launched
                    val toUserScreen = Intent(this,UserMain::class.java)
                    startActivity(toUserScreen)
                }
            }
            //if the information entered does not match with any user in the database, a toast will be shown
            else{
                Toast.makeText(this,"Please enter a valid username and password",Toast.LENGTH_LONG).show()
            }

        }


        //The Register Activity is launched, when the text "click here to register" is clicked
        register.setOnClickListener {
            val toRegister = Intent(this, RegistrationActivity::class.java)
            startActivity(toRegister)
        }
    }
}