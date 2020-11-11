package com.example.gameboardproj

import android.R.attr.data
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.gameboardproj.data.User
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.android.synthetic.main.activity_registration.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.util.*


/**
 * @author: Bayron Arturo Palacio
 *
 * This class will create a new user with the data entered by the user and add it to the Mongo Database.
 *
 */

class RegistrationActivity : AppCompatActivity() {

    private var dataBaseGame: GameDatabase? = null
    private var studentDao: StudentDao? = null
    private var role : Int = -1

    // Shared Preferences file name
    private val sharedPrefFile = "sharedPref"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        var fileReader: BufferedReader = application.assets.open("url.txt").bufferedReader()
        var url = fileReader.readLine()

        val sharedPreferences : SharedPreferences = this.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )
        val editor : SharedPreferences.Editor =  sharedPreferences.edit()

        register.setOnClickListener {
            if(firstName.text.toString() == "" || lastName.text.toString() == "" || email.text.toString() == "" || password.text.toString() == "" || radioGroupRole.checkedRadioButtonId == -1){
                if(radioGroupRole.checkedRadioButtonId == -1){
                    Toast.makeText(this, "Please select a role", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(this, "Please enter information", Toast.LENGTH_LONG).show()
                }
            }
            //If the fields are not empty
            else{
                role =
                    if(playerRole.isChecked){
                    1 // One will be assigned for the player user
                } else {
                    0 // Zero will be assigned for the leader user
                }
                var urlPath = "$url/adduser"

                val newUser = JSONObject()

                newUser.put("firstName", firstName.text.toString())
                newUser.put("lastName", lastName.text.toString())
                newUser.put("email", email.text.toString())
                newUser.put("password", password.text.toString())
                newUser.put("role", role)

                val que = Volley.newRequestQueue(this)
                val req = JsonObjectRequest(
                    Request.Method.POST, urlPath, newUser,
                    Response.Listener { response ->
                        if(response["responseServer"].toString().equals("Yes")){
                            Toast.makeText(this, "User has been created", Toast.LENGTH_LONG).show()
                            val toLogin = Intent(this,LoginActivity::class.java)
                            startActivity(toLogin)
                        }
                        else{
                            Toast.makeText(this, "User already exits", Toast.LENGTH_LONG).show()
                        }
                        println("Response from server -> " + response["responseServer"])
                    }, Response.ErrorListener {
                        println("Error from server")
                    }
                )
                que.add(req)

//                Log.i("data",dataJson)

//                val queue = Volley.newRequestQueue(this)
//////                val url = "https://www.google.com"
////
////// Request a string response from the provided URL.
//                val stringRequest = StringRequest(
//                    Request.Method.GET, url,
//                    Response.Listener<String> { response ->
//                        // Display the first 500 characters of the response string.
//                        Log.i("Res",response)
////                        textView.text = "Response is: ${response.substring(0, 500)}"
//                    },
//
//                    Response.ErrorListener { Log.i("res","did not work")})
//
//// Add the request to the RequestQueue.
//                queue.add(stringRequest)


            }
        }



        //************************************************ -ROOM DATABASE- *************************************************************//
        //When the user clicks the Register Button: ROOM DATABASE
//        register.setOnClickListener {
////            //The fields are check to make sure that are not empty. If they are empty, a toast will be shown
////            if(firstName.text.toString() == "" || lastName.text.toString() == "" || email.text.toString() == "" || password.text.toString() == "" || radioGroupRole.checkedRadioButtonId == -1){
////                if(radioGroupRole.checkedRadioButtonId == -1){
////                    Toast.makeText(this,"Please select a role",Toast.LENGTH_LONG).show()
////                }
////                else {
////                    Toast.makeText(this,"Please enter information",Toast.LENGTH_LONG).show()
////                }
////            }
////            //If the fields are not empty
////            else{
////                // add info to SharedPreferences - user fName, lName
////                editor.putString("user_firstName", firstName.text.toString())
////                editor.putString("user_lastName", lastName.text.toString())
////                editor.apply()
////                editor.commit()
////
////                role =
////                    if(playerRole.isChecked){
////                    1 // One will be assigned for the player user
////                } else {
////                    0 // Zero will be assigned for the leader user
////                }
////                Observable.fromCallable {
////
////                    //An instance of the Database and studenDao are created
////                    dataBaseGame = GameDatabase.getAppDataBase(context = this)
////                    studentDao = dataBaseGame?.studentDao()
////
////                    //An Student object is created with the information entered by the user
////                    var student1 = Student(firstNameDb = firstName.text.toString(), lastNameDb = lastName.text.toString(), emailDb = email.text.toString(), passwordDb = password.text.toString(),roleTypeDb = role)
////
////                    with(studentDao) {
////                        //The student object is added to the database through the studentDao with the insertStudent() method
////                        this?.insertStudent(student1)
////                    }
////                }.subscribeOn(Schedulers.io())
////                    .observeOn(AndroidSchedulers.mainThread())
////                    .subscribe()
////                //Message to show the new user has been created
////                Toast.makeText(this,"A new user has been created",Toast.LENGTH_LONG).show()
////                //Launch the Login Activity
////                val toLogin = Intent(this,LoginActivity::class.java)
////                startActivity(toLogin)
////            }
//        }
    }
}