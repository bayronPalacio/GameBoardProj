package com.example.gameboardproj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_registration.*


class RegistrationActivity : AppCompatActivity() {

    private var dataBaseGame: GameDatabase? = null
    private var studentDao: StudentDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        register.setOnClickListener {
            if(firstName.text.toString() == "" || lastName.text.toString() == "" || email.text.toString() == "" || password.text.toString() == ""){
                Toast.makeText(this,"Please enter information",Toast.LENGTH_LONG).show()
            }
            else{
                Observable.fromCallable {
                    dataBaseGame = GameDatabase.getAppDataBase(context = this)
                    studentDao = dataBaseGame?.studentDao()

                    var student1 = Student(firstNameDb = firstName.text.toString(), lastNameDb = lastName.text.toString(), emailDb = email.text.toString(), passwordDb = password.text.toString())

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