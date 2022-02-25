package com.developerkim.mytodo.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.ListItemsBinding
import com.developerkim.mytodo.util.ClickListener
import com.developerkim.mytodo.util.LongClickListener

class NoteAdapter(
    notes: MutableList<Note>,
    private val listener: ClickListener, private val longClickListener: LongClickListener) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private val notesList = notes

    /*companion object{
        private var noteTracker:SelectionTracker<Note>?=null
    }*/

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
            note: Note, listener: ClickListener, position: Int, longClickListener: LongClickListener
        ) {
            binding.apply {
                val context = root.context
                txtCategory.text = note.noteCategory
                tvNoteTitle.text = note.noteTitle
                txtNotes.text = note.noteText
                txtDate.text = note.noteDate

                tvNoteTitle.setTextColor(
                    when (binding.txtCategory.text) {
                        "Study" -> context.getColor(R.color.colorStudy)
                        "Daily Tasks" -> context.getColor(R.color.colorDailTasks)
                        "Private" -> context.getColor(R.color.colorPrivate)
                        "Shopping" -> context.getColor(R.color.colorShopping)
                        else -> context.getColor(R.color.colorUncategorized)
                    }
                )
                deleteNote.setOnClickListener {
                    listener.onClick(it, note, position)
                }

                root.setOnClickListener {
                    listener.onClick(it, note, position)
                }
                root.setOnLongClickListener {
                    it.parent.requestDisallowInterceptTouchEvent(true)
                    longClickListener.onLongClick(it, note, deleteNote)
                }
                /*if (noteTracker!!.isSelected(note)) {
                    itemView.background =
                        AppCompatResources.getDrawable(context, R.drawable.rounded_corners_bg_gray)
                } else {
                    itemView.background =
                        AppCompatResources.getDrawable(context, R.drawable.rounded_corners)
                }*/
            }

        }
       /* fun getItemDetails():ItemDetailsLookup.ItemDetails<Note> = object:ItemDetailsLookup.ItemDetails<Note>(){
            override fun getPosition(): Int  = bindingAdapterPosition

            override fun getSelectionKey(): Note = (bindingAdapter as NoteAdapter).notesList[position]

        }*/
    }

    override fun getItemId(position: Int): Long = position.toLong()

    /*fun getItemAtPosition(position: Int) = notesList[position]

    fun getPosition(title:String) = notesList.indexOfFirst { it.noteTitle==title }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }
    override fun getItemCount(): Int = notesList.size

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]
        holder.bind(note,listener,position, longClickListener)
    }
    /*fun setTracker(tracker: SelectionTracker<Note>){
        noteTracker = tracker
    }*/
}
/*
class ItemLookUp(private val recyclerView: RecyclerView):ItemDetailsLookup<Note>(){
    override fun getItemDetails(e: MotionEvent): ItemDetails<Note>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view!=null){
            return (recyclerView.getChildViewHolder(view) as NoteAdapter.ViewHolder).getItemDetails()
        }
        return null
    }
}*/