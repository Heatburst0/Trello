package com.kv.trello.activities.dialogs

import android.app.Dialog
import android.content.Context
import android.icu.text.CaseMap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kv.trello.R
import com.kv.trello.activities.adapters.LabelColorListAdapter
import com.kv.trello.activities.adapters.MembersListAdapter
import com.kv.trello.activities.model.User
import kotlinx.android.synthetic.main.dialog_list.view.*
import kotlinx.android.synthetic.main.members_list.view.*

abstract class MembersListDialog(
    context : Context,
    private var list : ArrayList<User>,
    private val title: String

): Dialog(context) {
    private var adapter : MembersListAdapter?=null
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.members_list,null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(view)
    }
    private fun setupRecyclerView(view : View){
        if(list.size > 0){
            view.rv_members_list.layoutManager = LinearLayoutManager(context)
            adapter = MembersListAdapter(context,list)
            view.rv_members_list.adapter=adapter
            adapter!!.setOnClickListener(
                object : MembersListAdapter.OnClickListener{
                    override fun onClick(position: Int, user: User, action: String) {
                        dismiss()
                        onItemSelected(user,action)
                    }

                }
            )
        }
    }
    protected abstract fun onItemSelected(user: User,action : String)
}