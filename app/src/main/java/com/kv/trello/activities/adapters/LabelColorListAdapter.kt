package com.kv.trello.activities.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.kv.trello.R
import kotlinx.android.synthetic.main.item_label_color.view.*

class LabelColorListAdapter(
    private val context : Context,
    private var list : ArrayList<String>,
    private val mSelectedColor : String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_label_color,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if(holder is MyViewHolder){
            holder.itemView.label_color.setBackgroundColor(Color.parseColor(item))

            if(mSelectedColor==item){
                holder.itemView.label_color_check.visibility = View.VISIBLE
            }else{
                holder.itemView.label_color_check.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if(onClickListener!=null){
                    onClickListener!!.onClick(position,item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


//
//    /**
//     * An interface for onclick items.
//     */
    interface OnItemClickListener {
        fun onClick(position: Int,color : String)
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}