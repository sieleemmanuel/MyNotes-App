package com.developerkim.mytodo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorSpace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_items.view.*

class NoteAdapter(context: Context, private var notes: ArrayList<Note>) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    val cxt = context


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {


        val txtDate = itemView.txtdate
        val txtTitle = itemView.txttitle
        val txtCategory = itemView.txtcategory
        val txtNote = itemView.txtnotes
        val viewColor = itemView.categoryColor
        var mitemClickListener: ItemClickListener? = null

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            this.mitemClickListener!!.OnItemClickListener(p0!!, adapterPosition)
        }

        fun SetOnItemClickListener(itemClickListener: ItemClickListener) {
            this.mitemClickListener = itemClickListener
        }
    }
    fun removeItem(position: Int){
        val note: Note = notes[position]
        if (MainActivity.databaseHandler.deleteNotes(note.noteID)){
            notes.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, notes.size)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_items, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note: Note = notes[position]
        holder.txtDate.text = note.noteDATE
        holder.txtTitle.text = note.noteTITLE
        holder.txtCategory.text = note.noteCATEGORY
        holder.txtNote.text= note.noteNOTES

        val vCategory = note.noteCATEGORY
        if(vCategory ==R.string._1.toString()){
                holder.viewColor.setBackgroundColor(Color.parseColor("#3b4a6b"))
            }
        if(vCategory ==R.string._2.toString()){
                holder.viewColor.setBackgroundColor(Color.parseColor("#f0d43a"))
            }
        if(vCategory ==R.string._3.toString()){
                holder.viewColor.setBackgroundColor(Color.parseColor("#22b2da"))
            }
        if(vCategory ==R.string._4.toString()){
                holder.viewColor.setBackgroundColor(Color.parseColor("#f23557"))
            }

//        holder.setData(note,position)
        holder.SetOnItemClickListener(object : ItemClickListener {
            override fun OnItemClickListener(view: View, pos: Int) {
                var id = notes[position]
                val openNote = Intent(cxt, ReadNotes::class.java)
                openNote.putExtra("idDetails", id.noteID)
                openNote.putExtra("categoryDetails", id.noteCATEGORY)
                openNote.putExtra("titleDetails", id.noteTITLE)
                openNote.putExtra("notesDetails", id.noteNOTES)
                openNote.putExtra("dateDetails", id.noteDATE)
                cxt.startActivity(openNote)
            }
        })
    }

}