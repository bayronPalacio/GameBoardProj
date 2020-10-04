package com.example.gameboardproj

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_login.register

class LoginActivity : AppCompatActivity() {

    private var dataBaseGame: GameDatabase? = null
    private var studentDao: StudentDao? = null
    private var studentInfo : Student? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn.setOnClickListener {

                Observable.fromCallable {
                    dataBaseGame = GameDatabase.getAppDataBase(context = this)
                    studentDao = dataBaseGame?.studentDao()

                    with(studentDao) {
                        studentInfo = this?.getStudentByEmail(username.text.toString())!!
                    }
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            if(studentInfo?.emailDb.toString() == username.text.toString() && studentInfo?.passwordDb.toString() == password.text.toString() )
            {
                Toast.makeText(this,"User Name\n" + studentInfo?.firstNameDb.toString() + " " + studentInfo?.lastNameDb.toString(),Toast.LENGTH_LONG).show()
                if(studentInfo?.roleTypeDb.toString().toInt() == 0)
                {
                    val toLeaderScreen = Intent(this,LeaderMain::class.java)
                    startActivity(toLeaderScreen)
                }
                else{
                    val toUserScreen = Intent(this,UserMain::class.java)
                    startActivity(toUserScreen)
                }
            }
            else{
                Toast.makeText(this,"Please enter a valid username and password",Toast.LENGTH_LONG).show()
            }

        }

        register.setOnClickListener {
            val toRegister = Intent(this, RegistrationActivity::class.java)
            startActivity(toRegister)
        }
    }
}