package com.developerkim.mytodo.ui.listnotes

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.R
import com.developerkim.mytodo.databinding.ListItemsBinding
import com.developerkim.mytodo.model.Note
import com.developerkim.mytodo.model.NoteCategory
import com.developerkim.mytodo.util.ClickListener
import com.developerkim.mytodo.util.LongClickListener


class NoteAdapter(private val notes: MutableList<Note>,val listener: ClickListener,val longClickListener: LongClickListener) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    class ViewHolder(val binding: ListItemsBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ListItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }

        fun bind(
            note: Note, listener: ClickListener, position: Int, longClickListener: LongClickListener
        ) {
            binding.txtCategory.text = note.noteCategory
            binding.tvNoteTitle.text = note.noteTitle
            binding.txtNotes.text = note.noteText
            binding.txtDate.text = note.noteDate

            binding.tvNoteTitle.setTextColor(when(binding.txtCategory.text){
                "Study" ->Color.GREEN
                "Daily Tasks" ->Color.CYAN
                "Private" ->Color.RED
                "Shopping" -> Color.MAGENTA
                else -> Color.BLACK
            })
            binding.deleteNote.setOnClickListener {
                listener.onClick(it,note,position)
            }
            binding.root.setOnClickListener {
                listener.onClick(it,note,position)
/*
                Toast.makeText(it.context,"${note.noteTitle} at Pos: $position",Toast.LENGTH_SHORT).show()
*/
            }
           binding.root.setOnLongClickListener {
               longClickListener.onLongClick(it,note,position,binding.deleteNote)
           }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note,listener,position,longClickListener)

    }
}


/*Adapter to adapt category notes */
class CategoryAdapter(private val listener: ClickListener,private val longClickListener: LongClickListener) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    var noteCategories = listOf<NoteCategory>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvListCategories: RecyclerView = itemView.findViewById(R.id.rvCategoryList)
        val categoryHeader: TextView = itemView.findViewById(R.id.txtCategoryName)

        companion object {
            fun from(parent: ViewGroup): CategoryViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.list_categories,
                    parent,
                    false
                )
                return CategoryViewHolder(view)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = noteCategories[position]
        holder.rvListCategories.adapter = category.notes?.let {
            NoteAdapter(it,listener,longClickListener)
        }
        holder.categoryHeader.text = category.categoryName
    }
    override fun getItemCount(): Int = noteCategories.size
}

