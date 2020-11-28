package com.example.gameboardproj

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_login.register
import org.json.JSONObject
import java.io.BufferedReader

/**
 * @author: Bayron Arturo Palacio
 *
 * This class handles the login function and check that the user exists in the Mongo database.
 * Also, it will launch the activity_registration when the text "click here to register" is clicked
 *
 */

class LoginActivity : AppCompatActivity() {

    private var dataBaseGame: GameDatabase? = null
    private var studentDao: StudentDao? = null
    private var studentInfo : Student? = null

    private var sharedPrefFile : SharedPreferences? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPrefFile = this.getSharedPreferences("sharedPreferences", 0);

        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        var url = fileReader.readLine()

        loginBtn.setOnClickListener {
            if(username.text.toString() == "" || password.text.toString() == ""){
                    Toast.makeText(this, "Please enter information", Toast.LENGTH_LONG).show()
            }
            //If the fields are not empty
            else{
                val user = JSONObject()
                user.put("email", username.text.toString())
                user.put("password", password.text.toString())

                var urlPath = "$url/login"
                val que = Volley.newRequestQueue(this)
                val req = JsonObjectRequest(
                    Request.Method.POST, urlPath, user,
                    Response.Listener { response ->
                        if(response["responseLogin"].toString().equals("Wrong Password")){
                            Toast.makeText(this, "Your account or password is incorrect.", Toast.LENGTH_LONG).show()
                        }
                        else if(response["responseLogin"].toString().equals("Right User")){
                            Toast.makeText(this, "Welcome " + response["name"] + " " + response["last"], Toast.LENGTH_LONG).show()
                            val editor = sharedPrefFile!!.edit()
                            editor.putString("Name", response["name"].toString() + " " + response["last"].toString())
                            editor.putString("Email",username.text.toString())
                            //editor.putInt("Role", response["role"] as Int)
                            editor.apply()
                            if(response["role"].toString().equals("0")){
                                val toLeader = Intent(this,LeaderMain::class.java)
                                startActivity(toLeader)
                            }
                            else if (response["role"].toString().compareTo("1") == 0){
                                val toStudent = Intent(this,UserMain::class.java)
                                startActivity(toStudent)
                            }
                            Log.d("testing", response["role"].toString() )
                        }
                        else if(response["responseLogin"].toString().equals("User does not exist")){
                            Toast.makeText(this, "This account does not exist", Toast.LENGTH_LONG).show()
                        }
                        println("Response from server -> " + response["responseLogin"])
                    }, Response.ErrorListener {
                        println("Error from server")
                    }
                )
                que.add(req)
//                val login = Intent(this, CreateRipActivity::class.java)
//                startActivity(login)
            }
        }

        //The Register Activity is launched, when the text "click here to register" is clicked
        register.setOnClickListener {
            val toRegister = Intent(this, RegistrationActivity::class.java)
            startActivity(toRegister)
        }

        //************************************************ -ROOM DATABASE- *************************************************************//
        //When the login button is clicked
//        loginBtn.setOnClickListener {
//                Observable.fromCallable {
//
//                    //An instance of the Database and studentDao are created
//                    dataBaseGame = GameDatabase.getAppDataBase(context = this)
//                    studentDao = dataBaseGame?.studentDao()
//
//                    with(studentDao) {
//                        //A request is made to the database through the studentDao with the getStudentByEmail() method
//                        studentInfo = this?.getStudentByEmail(inputEmail = username.text.toString())!!
//                    }
//                }.subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe()
//            Thread.sleep(300)
//            //Check if the email and password entered by the user match with the user in the database
//            if(studentInfo?.emailDb.toString() == username.text.toString() && studentInfo?.passwordDb.toString() == password.text.toString() )
//            {
//                Toast.makeText(this,"User Name\n" + studentInfo?.firstNameDb.toString() + " " + studentInfo?.lastNameDb.toString(),Toast.LENGTH_LONG).show()
//
//                // Shared Preferences
//                val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
//                val editor : SharedPreferences.Editor =  sharedPreferences.edit()
//
//                // Store the current user email
//                editor.putString("user_email", username.text.toString())
//                editor.apply()
//                editor.commit()
//
//                if(studentInfo?.roleTypeDb.toString().toInt() == 0)
//                {
//                    //if the user is a leader, the leader activity will be launched
//                    val toLeaderScreen = Intent(this,LeaderMain::class.java)
//                    startActivity(toLeaderScreen)
//                }
//                else{
//                    //if the user is a player, the player activity will be launched
//                    val toUserScreen = Intent(this,UserMain::class.java)
//                    startActivity(toUserScreen)
//                }
//            }
//            //if the information entered does not match with any user in the database, a toast will be shown
//            else{
//                Toast.makeText(this,"Please enter a valid username and password",Toast.LENGTH_LONG).show()
//            }
//        }
    }
}

