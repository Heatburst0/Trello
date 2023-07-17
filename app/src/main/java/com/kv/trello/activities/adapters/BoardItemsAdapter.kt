package com.kv.trello.activities.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kv.trello.R
import com.kv.trello.activities.MainActivity
import com.kv.trello.activities.MembersActivity
import com.kv.trello.activities.model.Board
import kotlinx.android.synthetic.main.item_board.view.*
import kotlinx.android.synthetic.main.item_members.view.*

open class BoardItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Board>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_board,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemView.iv_board_image)

            holder.itemView.tv_name.text = model.name
            holder.itemView.tv_created_by.text = "Created By : ${model.createdBy}"

            holder.itemView.setOnClickListener {

                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
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
        fun onClick(position: Int, model: Board)
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    private inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view),View.OnLongClickListener{
        init {
            view.setOnLongClickListener(this@MyViewHolder)
        }
        override fun onLongClick(v: View?): Boolean {
            val builder = AlertDialog.Builder(context)
            //set title for alert dialog
            builder.setTitle("Alert")
            //set message for alert dialog
            builder.setMessage("Are you sure you want to delete this board ${v?.tv_name!!.text.toString()}.")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            //performing positive action
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                dialogInterface.dismiss() // Dialog will be dismissed

                (context as MainActivity).removeUser(v.tv_name!!.text.toString())
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