package com.kv.trello.activities.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kv.trello.R
import com.kv.trello.activities.model.SelectedMembers
import kotlinx.android.synthetic.main.item_card_selected_member.view.*

open class CardMembersListAdapter(
    private val context : Context,
    private val list : ArrayList<SelectedMembers>,
    private val assigned : Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card_selected_member,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model= list[position]
        if(holder is MyViewHolder){
            if(position == list.size -1 && assigned){
                holder.itemView.selected_member.visibility = View.GONE
                holder.itemView.add_member_select.visibility=View.VISIBLE
            }else{
                holder.itemView.selected_member.visibility = View.VISIBLE
                holder.itemView.add_member_select.visibility=View.GONE

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.selected_member)
            }
            holder.itemView.add_member_select.setOnClickListener {
                if(onClickListener!= null){
                    onClickListener!!.onClick()
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


    interface OnClickListener {
        fun onClick()
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}