package com.developerkim.mytodo.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.NoteItemBinding
import com.developerkim.mytodo.interfaces.ClickListener

class NoteAdapter(
    notes: MutableList<Note>,
    private val noteClickListener: NoteClickListener,
    private val viewClickListener: ViewClickListener,
    private val context: Context
): RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private val notesList = notes

    companion object {
        var isSelectedMode = false
        val selectedNotes = mutableListOf<Note>()
    }

    init {
        setHasStableIds(true)
    }

    inner class ViewHolder(private val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(note: Note, position: Int ) {
            binding.apply {
                txtCategory.text = note.noteCategory
                tvNoteTitle.text = note.noteTitle
                txtNotes.text = note.noteText
                txtDate.text = note.noteDate

                    when (note.noteCategory) {
                        context.getString(R.string.category_study) -> {
                            tvNoteTitle.setTextColor(context.getColor(R.color.colorStudy))
                            categoryColor.setBackgroundColor(context.getColor(R.color.colorStudy))
                        }
                        context.getString(R.string.category_daily_task) -> {
                            tvNoteTitle.setTextColor(context.getColor(R.color.colorDailTasks))
                            categoryColor.setBackgroundColor(context.getColor(R.color.colorDailTasks))
                        }
                        context.getString(R.string.category_private) -> {
                            tvNoteTitle.setTextColor(context.getColor(R.color.colorPrivate))
                            categoryColor.setBackgroundColor(context.getColor(R.color.colorPrivate))
                        }
                        context.getString(R.string.categry_shopping) -> {
                            tvNoteTitle.setTextColor(context.getColor(R.color.colorShopping))
                            categoryColor.setBackgroundColor(context.getColor(R.color.colorShopping))
                        }
                        else -> {
                            tvNoteTitle.setTextColor(context.getColor(R.color.colorUncategorized))
                            categoryColor.setBackgroundColor(context.getColor(R.color.colorUncategorized))
                        }
                    }

                btnFavorite.isSelected = note.isFavorite

                btnFavorite.setOnClickListener {
                    viewClickListener.onNoteClicked(note, position,binding)
                }
                root.setOnClickListener {
                    noteClickListener.onNoteClicked(note)
                }
            }
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }
    override fun getItemCount(): Int = notesList.size

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]
        holder.bind(note, position)

    }
    class NoteClickListener(val noteClick:(note:Note)->Unit){
        fun onNoteClicked(note:Note) = noteClick(note)
    }
    class ViewClickListener(val viewClick:(note:Note, position:Int,binding:NoteItemBinding)->Unit){
        fun onNoteClicked(note:Note, position: Int, binding:NoteItemBinding) = viewClick(note,position,binding)
    }
}