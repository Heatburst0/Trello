package com.kv.trello.activities.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kv.trello.R
import com.kv.trello.activities.MembersActivity
import com.kv.trello.activities.TaskListActivity
import com.kv.trello.activities.model.Card
import com.kv.trello.activities.model.User
import com.projemanag.utils.Constants
import kotlinx.android.synthetic.main.activity_my_profile.view.*
import kotlinx.android.synthetic.main.item_members.view.*

open class MembersListAdapter(private val context : Context,private val list : ArrayList<User>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_members,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = list[position]
        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.member_image)
            holder.itemView.member_name.text=user.name
            holder.itemView.member_email.text=user.email
        }
        if(user.Selected){
            holder.itemView.member_selected.visibility = View.VISIBLE
        }else{
            holder.itemView.member_selected.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            if(onClickListener!=null){
                if(user.Selected){
                    onClickListener!!.onClick(position,user,Constants.unselect)
                }else{
                    onClickListener!!.onClick(position,user,Constants.select)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(position: Int, user: User,action : String)
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnLongClickListener{
        init {
            view.setOnLongClickListener(this@MyViewHolder)
        }

        override fun onLongClick(v: View?): Boolean {
//            Toast.makeText(context,"Lets Go ${v?.member_email!!.text}",Toast.LENGTH_LONG).show()
            val mail = v?.member_email!!.text.toString()
            val builder = AlertDialog.Builder(context)
            //set title for alert dialog
            builder.setTitle("Alert")
            //set message for alert dialog
            builder.setMessage("Are you sure you want to remove user ${v.member_name.text.toString()}.")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            //performing positive action
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                dialogInterface.dismiss() // Dialog will be dismissed

                (context as MembersActivity).removeUser(mail)
            }

            //performing negative action
            builder.setNegativeButton("No") { dialogInterface, which ->
                dialogInterface.dismiss() // Dialog will be dismissed
            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
            alertDialog.show()

            return true
        }
    }
}