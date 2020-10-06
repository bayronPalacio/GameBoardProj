package com.example.gameboardproj

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_registration.password
import kotlinx.android.synthetic.main.activity_registration.register


class RegistrationActivity : AppCompatActivity() {

    private var dataBaseGame: GameDatabase? = null
    private var studentDao: StudentDao? = null
    private var role : Int = -1
    private val sharedPrefFile = "sharedPref"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor =  sharedPreferences.edit()

        register.setOnClickListener {
            if(firstName.text.toString() == "" || lastName.text.toString() == "" || email.text.toString() == "" || password.text.toString() == "" || radioGroupRole.checkedRadioButtonId == -1){
                if(radioGroupRole.checkedRadioButtonId == -1){
                    Toast.makeText(this,"Please select a role",Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(this,"Please enter information",Toast.LENGTH_LONG).show()
                }
            }
            else{
                // add info to SharedPreferences
                editor.putString("user_firstName", firstName.text.toString())
                editor.putString("user_lastName", lastName.text.toString())
                editor.apply()
                editor.commit()

                role =
                    if(playerRole.isChecked){
                    1 // One will be assigned for the player user
                } else {
                    0 // Zero will be assigned for the leader user
                }
                Observable.fromCallable {
                    dataBaseGame = GameDatabase.getAppDataBase(context = this)
                    studentDao = dataBaseGame?.studentDao()

                    var student1 = Student(firstNameDb = firstName.text.toString(), lastNameDb = lastName.text.toString(), emailDb = email.text.toString(), passwordDb = password.text.toString(),roleTypeDb = role)

                    with(studentDao) {
                        this?.insertStudent(student1)
                    }
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
                Toast.makeText(this,"A new user has been created",Toast.LENGTH_LONG).show()
                val toLogin = Intent(this,LoginActivity::class.java)
                startActivity(toLogin)
            }
        }
    }
}