package com.kv.trello.activities.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kv.trello.R
import com.kv.trello.activities.TaskListActivity
import com.kv.trello.activities.model.Card
import com.kv.trello.activities.model.SelectedMembers
import kotlinx.android.synthetic.main.item_card.view.*
import kotlinx.android.synthetic.main.members_list.view.*


// START
open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            if(model.selectedColor.isNotEmpty()){
                holder.itemView.view_label_color.visibility=View.VISIBLE
                holder.itemView.view_label_color.setBackgroundColor(Color.parseColor(model.selectedColor))
            }
            holder.itemView.tv_card_name.text = model.name

            if((context as TaskListActivity).mAssignedMembers.size >0 ){

                val selectedMembers : ArrayList<SelectedMembers> = ArrayList()
                for( i in context.mAssignedMembers.indices){
                    for(j in model.assignedTo){
                        if(context.mAssignedMembers[i].id==j)
                            selectedMembers.add(SelectedMembers(
                                context.mAssignedMembers[i].id,context.
                                mAssignedMembers[i].image))
                    }
                }

                if(selectedMembers.size>0){
                    if(selectedMembers.size == 1 && selectedMembers[0].id== model.createdBy){
                        holder.itemView.rv_card_selected_members.visibility=View.GONE
                    }else{
                        holder.itemView.rv_card_selected_members.visibility=View.VISIBLE
                        holder.itemView.rv_card_selected_members.layoutManager = GridLayoutManager(context,4)
                        val adapter = CardMembersListAdapter(context,selectedMembers,false)
                        holder.itemView.rv_card_selected_members.adapter = adapter


                    }
                }else{
                    holder.itemView.rv_card_selected_members.visibility=View.GONE
                }
            }
            holder.itemView.setOnClickListener {
                if(onClickListener!=null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(position: Int)
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
// END