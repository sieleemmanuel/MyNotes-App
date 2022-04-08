package com.developerkim.mytodo.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.ListItemsBinding
import com.developerkim.mytodo.interfaces.ClickListener

class NoteAdapter(
    notes: MutableList<Note>,
    private val listener: ClickListener
) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private val notesList = notes

    companion object {
        var isSelectedMode = false
        val selectedNotes = mutableListOf<Note>()
    }
    init {
        setHasStableIds(true)
    }

    class ViewHolder(private val binding: ListItemsBinding) : RecyclerView.ViewHolder(binding.root) {
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

        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(
            note: Note, listener: ClickListener, position: Int
        ) {
            binding.apply {
                val context = root.context
                txtCategory.text = note.noteCategory
                tvNoteTitle.text = note.noteTitle
                txtNotes.text = note.noteText
                txtDate.text = note.noteDate

                tvNoteTitle.setTextColor(
                    when (txtCategory.text) {
                        context.getString(R.string.category_study) -> context.getColor(R.color.colorStudy)
                        context.getString(R.string.category_daily_task) -> context.getColor(R.color.colorDailTasks)
                        context.getString(R.string.category_private) -> context.getColor(R.color.colorPrivate)
                        context.getString(R.string.categry_shopping) -> context.getColor(R.color.colorShopping)
                        else -> context.getColor(R.color.colorUncategorized)
                    }
                )
                deleteNote.setOnClickListener {
                    listener.onClick(it,note, position,deleteNote)
                }

                root.setOnLongClickListener {
                    it.parent.requestDisallowInterceptTouchEvent(true)
                   listener.onLongClick(it, note, deleteNote)
                }
                root.setOnClickListener {
                    listener.onClick(it, note,position,deleteNote)
                }
            }
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }
    override fun getItemCount(): Int = notesList.size

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]
        holder.bind(note,listener,position)

    }
}