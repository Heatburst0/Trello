package com.kv.trello.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kv.trello.R
import com.kv.trello.activities.adapters.MembersListAdapter
import com.kv.trello.activities.firebase.FirestoreClass
import com.kv.trello.activities.model.Board
import com.kv.trello.activities.model.User
import com.projemanag.utils.Constants
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.add_member.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private lateinit var mAssignedMembers : ArrayList<User>
    private var anyChangesMade : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
        setupActionBar()
        if(intent.hasExtra(Constants.board_detail))
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.board_detail)!!

        showProgressDialog("Please wait!")
        FirestoreClass().getmemebersList(mBoardDetails,this)
    }
    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMembersToBoard(this,user,mBoardDetails,false)
    }
    fun removeUser(email : String){
        if(mAssignedMembers[mAssignedMembers.size-1].id!=FirestoreClass().getCurrentUserID()){
            Toast.makeText(this,"Only creator can remove members",Toast.LENGTH_LONG).show()
        }else{
            var user : User
            for(i in mAssignedMembers.indices){
                if(mAssignedMembers[i].email==email){
                    user=mAssignedMembers[i]
                    if(mBoardDetails.createdBy==user.name){
                        Toast.makeText(this,"You cannot remove the creator",Toast.LENGTH_LONG).show()
                    }
                    else{
                        mBoardDetails.assignedTo.remove(mAssignedMembers[i].id)
                        showProgressDialog("Please wait!")
                        mAssignedMembers.removeAt(i)
                        FirestoreClass().assignMembersToBoard(this,user,mBoardDetails,true)
                    }

                    break
                }
            }
        }


    }

    fun setupMembersList(users : ArrayList<User>){

        mAssignedMembers = users
        hideProgressDialog()
        rv_members.layoutManager=LinearLayoutManager(this)
        rv_members.setHasFixedSize(true)
        rv_members.adapter=MembersListAdapter(this,users)

    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_members)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Members"
        }

        toolbar_members.setNavigationOnClickListener { onBackPressed() }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_members-> {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.add_member)
                dialog.setCancelable(true)
                dialog.tv_add_member.setOnClickListener {
                    val email = dialog.et_add_member.text.toString()
                    if(email.isNotEmpty()){
                        var added= false
                        for(i in mAssignedMembers.indices){
                            if(mAssignedMembers[i].email==email) {
                                Toast.makeText(this, "User is already added", Toast.LENGTH_LONG)
                                    .show()
                                added=true
                                break
                            }
                        }
                        if(!added){
                            showProgressDialog("Please Wait")
                            FirestoreClass().getMemberDetails(this,email)
                        }
                        dialog.dismiss()
                    }
                    else{
                        dialog.et_add_member.setError("Please enter an email")
                    }
                }
                dialog.tv_cancel.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun memberAssignSuccess(user : User, remove : Boolean){
        hideProgressDialog()
        if(!remove){
            mAssignedMembers.add(0,user)
        }
        anyChangesMade=true
        setupMembersList(mAssignedMembers)
        if(!remove){
            sendNotifytoUserAsyncTask(mBoardDetails.name,user.fcmToken).execute()
        }
    }

    override fun onBackPressed() {
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }
    private inner class sendNotifytoUserAsyncTask(val boardname : String,val token : String) : AsyncTask<Any,Void,String>(){
        override fun doInBackground(vararg params: Any?): String {
            var result : String
            var connection : HttpURLConnection?=null
            try{
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput =true
                connection.doInput = true
                connection.instanceFollowRedirects = false

                connection.requestMethod ="POST"
                connection.setRequestProperty("Content-Type","application/json")
                connection.setRequestProperty("charset","utf-8")
                connection.setRequestProperty("Accept","application/json")
                connection.setRequestProperty(Constants.FCM_AUTHORIZATION,"${Constants.FCM_KEY}=${Constants.fcm_serverkey}")
                connection.useCaches=false

                val wr = DataOutputStream(connection.outputStream)
                val jsonrequest =JSONObject()
                val dataobject =JSONObject()
                dataobject.put(Constants.FCM_KEY_TITLE,"Assigned to a new board $boardname")
                dataobject.put(Constants.FCM_KEY_MESSAGE,"You have been assigned to s new board by ${mAssignedMembers[0].name}")
                dataobject.put(Constants.FCM_KEY_TO,token)
                wr.writeBytes(jsonrequest.toString())
                wr.flush()
                wr.close()
                val httpResult : Int = connection.responseCode
                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    try{
                        while(reader.readLine().also {line=it} !=null){
                            sb.append(line+"\n")
                        }
                    }catch(e : IOException){
                        e.printStackTrace()
                    }finally {
                        try{
                            inputStream.close()
                        }catch (e : IOException){
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()

                }else{
                    result=connection.responseMessage
                }

            }catch (e : SocketTimeoutException){
                result = "Connection Timeout"
            }catch (e : Exception){
                result="error"+e.message
            }finally {
                connection?.disconnect()
            }

            return result
        }

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog("Please Wait")

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgressDialog()
        }

    }

}