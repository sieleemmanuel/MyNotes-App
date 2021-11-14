package com.developerkim.mytodo.util

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import com.developerkim.mytodo.model.Note
import com.developerkim.mytodo.model.NoteCategory

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
    fun onClickCategory(view: View, noteCategory: NoteCategory)

}
interface LongClickListener {
    fun onLongClick(view: View, note: Note, position: Int, deleteNote: ImageButton): Boolean {
        return true
    }
    fun onCategoryLongClick(view: View, noteCategory: NoteCategory, position: Int, deleteNote: ImageButton): Boolean {
        return true
    }
}
interface RecentNotesListener {
    fun onClickRecent(notes:MutableList<Note>,view: View)

}

