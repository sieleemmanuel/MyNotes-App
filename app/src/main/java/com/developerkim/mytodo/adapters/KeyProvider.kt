package com.developerkim.mytodo.adapters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.selection.ItemKeyProvider
import com.developerkim.mytodo.data.model.NoteCategory

/*class NoteKeyProvider(private val noteAdapter: NoteAdapter):ItemKeyProvider<Note>(SCOPE_CACHED) {
    override fun getKey(position: Int): Note = noteAdapter.getItemAtPosition(position)

    override fun getPosition(key: Note): Int = noteAdapter.getPosition(key.noteTitle)
}*/
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CategoryKeyProvider(private val notesCategoriesAdapter: NotesCategoriesAdapter):ItemKeyProvider<NoteCategory>(SCOPE_CACHED) {

    override fun getKey(position: Int): NoteCategory? = notesCategoriesAdapter.getItemAtPosition(position)

    override fun getPosition(key: NoteCategory): Int = notesCategoriesAdapter.getPosition(key.categoryName)
}
