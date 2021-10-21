package com.developerkim.mytodo.util

import android.view.View
import android.widget.ImageButton
import com.developerkim.mytodo.model.Note

/*class CategoryDiffCallback: DiffUtil.ItemCallback<NoteCategory>() {
    override fun areItemsTheSame(oldItem: NoteCategory, newItem: NoteCategory): Boolean {
        return oldItem.categoryName == newItem.categoryName
    }

    override fun areContentsTheSame(oldItem: NoteCategory, newItem: NoteCategory): Boolean {
        return oldItem == newItem
    }


}*/
interface ClickListener {
    fun onClick(view: View, note: Note, position: Int)

}
interface LongClickListener {
    fun onLongClick(view: View, note: Note, position: Int, deleteNote: ImageButton): Boolean {
        return true
    }

}
