package com.kv.trello.activities.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kv.trello.R
import com.kv.trello.activities.adapters.LabelColorListAdapter
import kotlinx.android.synthetic.main.dialog_list.view.*

abstract class LabelListDialog (
    context : Context,
    private var list : ArrayList<String>,
    private val title : String="",
    private val mSelectedColor : String=""
        ) : Dialog(context){
            private var adapter : LabelColorListAdapter?=null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val view =LayoutInflater.from(context).inflate(R.layout.dialog_list,null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(view)
    }
    private fun setupRecyclerView(view : View){
        view.label_color_list.layoutManager=LinearLayoutManager(context)
        adapter= LabelColorListAdapter(context,list,mSelectedColor)
        view.label_color_list.adapter=adapter
        adapter!!.onClickListener=
            object : LabelColorListAdapter.OnItemClickListener{
                override fun onClick(position: Int, color: String) {
                    dismiss()
                    onItemSelected(color)
                }

            }
    }
    protected abstract fun onItemSelected(color : String)

}