package com.developerkim.mytodo.adapters

import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.NoteItemBinding

class NoteAdapter(
    private val noteClickListener: NoteClickListener,
    private val noteLongClickListener: NoteLongClickListener,
    private val viewClickListener: ViewClickListener
) : ListAdapter<Note, NoteAdapter.ViewHolder>(DiffUtilItem) {
    init {
        setHasStableIds(true)
    }

    inner class ViewHolder(private val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(note: Note, position: Int) {
            binding.apply {
                txtCategory.text = note.noteCategory
                tvNoteTitle.text = note.noteTitle
                txtNotes.text = note.noteText
                txtDate.text = note.noteDate
                ivNoteColor.setBackgroundColor(note.noteColor!!)
                btnFavorite.isSelected = note.isFavorite
                btnFavorite.imageTintList = ColorStateList.valueOf(note.noteColor!!)

                btnFavorite.setOnClickListener {
                    viewClickListener.onViewClicked(note, position, it, binding)
                }
                btnDeleteNote.apply {
                    setOnLongClickListener {
                        if (it.isVisible) {
                            it.visibility = View.GONE
                        }
                        true
                    }
                }

                root.apply {
                    setOnClickListener {
                        noteClickListener.onNoteClicked(note, position)
                    }
                    setOnLongClickListener {
                        noteLongClickListener.onNoteLongClicked(note, btnDeleteNote)
                        true
                    }
                }
            }
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note!!, position)

    }

    class NoteClickListener(val noteClick: (note: Note, position: Int) -> Unit) {
        fun onNoteClicked(note: Note, position: Int) = noteClick(note, position)
    }

    class ViewClickListener(val viewClick: (note: Note, position: Int, view: View, binding: NoteItemBinding) -> Unit) {
        fun onViewClicked(note: Note, position: Int, view: View, binding: NoteItemBinding) =
            viewClick(note, position, view, binding)
    }

    class NoteLongClickListener(val noteLongClickListener: (note: Note, deleteBtn: ImageView) -> Unit) {
        fun onNoteLongClicked(note: Note, deleteBtn: ImageView) =
            noteLongClickListener(note, deleteBtn)
    }


    object DiffUtilItem : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.noteTitle == newItem.noteTitle
        }

    }
}