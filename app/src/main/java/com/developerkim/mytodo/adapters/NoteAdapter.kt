package com.developerkim.mytodo.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.NoteItemBinding

class NoteAdapter(
    private val noteClickListener: NoteClickListener,
    private val viewClickListener: ViewClickListener,
    private val context: Context
) : ListAdapter<Note,NoteAdapter.ViewHolder>(DiffUtilItem) {
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

                when (note.noteCategory) {
                    context.getString(R.string.category_study) -> {
                        //tvNoteTitle.setTextColor(context.getColor(R.color.colorStudy))
                        categoryColor.setBackgroundColor(context.getColor(R.color.colorStudy))
                    }
                    context.getString(R.string.category_daily_task) -> {
                        //tvNoteTitle.setTextColor(context.getColor(R.color.colorDailTasks))
                        categoryColor.setBackgroundColor(context.getColor(R.color.colorDailTasks))
                    }
                    context.getString(R.string.category_private) -> {
                        //tvNoteTitle.setTextColor(context.getColor(R.color.colorPrivate))
                        categoryColor.setBackgroundColor(context.getColor(R.color.colorPrivate))
                    }
                    context.getString(R.string.categry_shopping) -> {
                        tvNoteTitle.setTextColor(context.getColor(R.color.colorShopping))
                        categoryColor.setBackgroundColor(context.getColor(R.color.colorShopping))
                    }
                    else -> {
                        //tvNoteTitle.setTextColor(context.getColor(R.color.colorUncategorized))
                        categoryColor.setBackgroundColor(context.getColor(R.color.colorUncategorized))
                    }
                }

                btnFavorite.isSelected = note.isFavorite

                btnFavorite.setOnClickListener {
                    viewClickListener.onViewClicked(note, position,it, binding)
                    Toast.makeText(context, "Note at $position Favorite:${note.isFavorite}", Toast.LENGTH_SHORT).show()
                }

                root.setOnClickListener {
                    noteClickListener.onNoteClicked(note, position)
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
        val note =getItem(position)
        holder.bind(note!!, position)

    }

    class NoteClickListener(val noteClick: (note: Note, position: Int) -> Unit) {
        fun onNoteClicked(note: Note, position: Int) = noteClick(note, position)
    }

    class ViewClickListener(val viewClick: (note: Note, position:Int,view: View, binding: NoteItemBinding) -> Unit) {
        fun onViewClicked(note: Note, position: Int,view: View, binding: NoteItemBinding) =
            viewClick(note, position,view, binding)
    }

    object DiffUtilItem:DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.noteTitle == newItem.noteTitle
        }

    }
}