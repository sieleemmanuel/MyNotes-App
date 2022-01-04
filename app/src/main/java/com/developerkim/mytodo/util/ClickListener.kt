package com.developerkim.mytodo.util

import android.view.View
import android.widget.ImageButton
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory

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

