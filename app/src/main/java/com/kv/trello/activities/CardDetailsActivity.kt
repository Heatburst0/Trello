package com.kv.trello.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.kv.trello.R
import com.kv.trello.activities.adapters.CardListItemsAdapter
import com.kv.trello.activities.adapters.CardMembersListAdapter
import com.kv.trello.activities.dialogs.LabelListDialog
import com.kv.trello.activities.dialogs.MembersListDialog
import com.kv.trello.activities.firebase.FirestoreClass
import com.kv.trello.activities.model.*
import com.projemanag.utils.Constants
import kotlinx.android.synthetic.main.activity_card_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private var tasklistpos : Int=-1
    private var cardlistpos : Int=-1
    private var mSelectedcolor : String=""
    private lateinit var mAssignedMembers : ArrayList<User>
    private var duedate : Long=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        if(intent.hasExtra(Constants.board_detail)){
            Toast.makeText(this,"done",Toast.LENGTH_LONG).show()
            mBoardDetails= intent.getParcelableExtra(Constants.board_detail)!!
        }
        if(intent.hasExtra(Constants.TASKLIST_POSITION) && intent.hasExtra(Constants.CARDLIST_POSITION)){
            tasklistpos=intent.getIntExtra(Constants.TASKLIST_POSITION,-1)
            cardlistpos=intent.getIntExtra(Constants.CARDLIST_POSITION,-1)
            duedate=mBoardDetails.taskList[tasklistpos].cards[cardlistpos].duedate

            if(duedate>0){
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                tv_select_due_date.setText(sdf.format(Date(duedate)))
            }
        }
        if(intent.hasExtra(Constants.board_members_list)){
            mAssignedMembers = intent.getParcelableArrayListExtra(Constants.board_members_list)!!
        }
        setupActionBar()
        edit_card_name.setText(mBoardDetails.taskList[tasklistpos].cards[cardlistpos].name)
        edit_card_name.setSelection(edit_card_name.text.toString().length)
        mSelectedcolor=mBoardDetails.taskList[tasklistpos].cards[cardlistpos].selectedColor
        if(mSelectedcolor.isNotBlank()){
            setColor()
        }
        btn_update_card_details.setOnClickListener {
            if(edit_card_name.text.toString().isNotBlank())
                updatecarddetails()
        }
        tv_select_label_color.setOnClickListener {
            setupLabelDialog()
        }
        tv_select_members.setOnClickListener {
            cardMembersDialog()
        }
        tv_select_due_date.setOnClickListener {
            setupDateDialog()
        }
        setupSelectedMembers()
    }
    private fun setupDateDialog(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dialog=DatePickerDialog(this,DatePickerDialog.OnDateSetListener{  view, year, monthOfYear, dayOfMonth ->
            val newMonth = monthOfYear+1
                tv_select_due_date.setText("$dayOfMonth/$newMonth/$year")
            val selecteddate = "$dayOfMonth/$newMonth/$year"
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val thedate = sdf.parse(selecteddate)
            duedate=thedate!!.time
        },year,month,day)
        dialog.show()

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_card_details)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title=mBoardDetails.taskList[tasklistpos].cards[cardlistpos].name
        }

        toolbar_card_details.setNavigationOnClickListener { onBackPressed() }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_card-> {
                alertDialogForDeleteCard(edit_card_name.text.toString())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun updatecarddetails(){
        val card = Card(
            edit_card_name.text.toString(),
            mBoardDetails.taskList[tasklistpos].cards[cardlistpos].createdBy,
            mBoardDetails.taskList[tasklistpos].cards[cardlistpos].assignedTo,
            mSelectedcolor,
            duedate
        )

        val tasklist : ArrayList<Task> = mBoardDetails.taskList
        tasklist.removeAt(tasklist.size-1)
        mBoardDetails.taskList[tasklistpos].cards[cardlistpos]=card
        showProgressDialog("Please wait")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun alertDialogForDeleteCard(title: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            deletecard()

        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    private fun deletecard(){
        val cardlist : ArrayList<Card> = mBoardDetails.taskList[tasklistpos].cards
        cardlist.removeAt(cardlistpos)

        val tasklistt : ArrayList<Task> = mBoardDetails.taskList
        tasklistt.removeAt(tasklistt.size-1)

        tasklistt[cardlistpos].cards=cardlist

        mBoardDetails.taskList=tasklistt
        showProgressDialog("Please wait")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    private fun getColors() : ArrayList<String>{
        val colors : ArrayList<String> = ArrayList()
        colors.add("#43C86F")
        colors.add("#0C90F1")
        colors.add("#F72400")
        colors.add("#7A8089")
        colors.add("#D57C1D")
        colors.add("#770000")
        colors.add("#0022F8")
        return colors
    }
    private fun setColor(){
        tv_select_label_color.text=""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mSelectedcolor))
    }

    private fun setupLabelDialog(){
        val colorslist : ArrayList<String> = ArrayList()
        val labels = object : LabelListDialog(
            this,
            getColors(),
            "Select label color"

        ){
            override fun onItemSelected(color: String) {
                mSelectedcolor = color
                setColor()
            }

        }
        labels.show()
    }

    private fun cardMembersDialog(){
        val assignedMembers = mBoardDetails.taskList[tasklistpos].cards[cardlistpos].assignedTo

        if(assignedMembers.size>0){
            for(i in mAssignedMembers.indices){
                for(j in assignedMembers){
                    if(mAssignedMembers[i].id==j){
                        mAssignedMembers[i].Selected=true
                    }
                }
            }
        }else{
            for(i in mAssignedMembers.indices){
                mAssignedMembers[i].Selected=false
            }
        }
        val listdialog = object : MembersListDialog(
            this,
            mAssignedMembers,
            ""
        ){
            override fun onItemSelected(user: User, action: String) {
                if(action.equals(Constants.select)){
                    if(!mBoardDetails.taskList[tasklistpos]
                            .cards[cardlistpos].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[tasklistpos]
                            .cards[cardlistpos].assignedTo.add(user.id)
                    }
                }else{
                    mBoardDetails.taskList[tasklistpos]
                        .cards[cardlistpos].assignedTo.remove(user.id)
                    for(i in mAssignedMembers.indices){
                        if(mAssignedMembers[i].id==user.id){
                            mAssignedMembers[i].Selected=false
                        }
                    }
                }
                setupSelectedMembers()
            }

        }
        listdialog.show()

    }
    private fun setupSelectedMembers(){
        val assignedMembers = mBoardDetails.taskList[tasklistpos].cards[cardlistpos].assignedTo

        val selectedMembers : ArrayList<SelectedMembers> = ArrayList()
        for(i in mAssignedMembers.indices){
            for(j in assignedMembers){
                if(mAssignedMembers[i].id==j){
                    selectedMembers.add(
                        SelectedMembers(mAssignedMembers[i].id,mAssignedMembers[i].image)
                    )
                }
            }
        }

        if(selectedMembers.size>0){
            selectedMembers.add(SelectedMembers("",""))
            tv_select_members.visibility= View.GONE

            rv_selected_members.visibility = View.VISIBLE
            rv_selected_members.layoutManager = GridLayoutManager(this,6)
            val adapter = CardMembersListAdapter(this,selectedMembers,true)
            rv_selected_members.adapter=adapter
            adapter.setOnClickListener(
                object : CardMembersListAdapter.OnClickListener{
                    override fun onClick() {
                        cardMembersDialog()
                    }
                }
            )
        }else{
            tv_select_members.visibility= View.VISIBLE

            rv_selected_members.visibility = View.GONE
        }
    }
}