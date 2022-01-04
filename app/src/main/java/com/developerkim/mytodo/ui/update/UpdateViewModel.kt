package com.developerkim.mytodo.ui.update

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.database.NoteCategoriesDao
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@RequiresApi(Build.VERSION_CODES.O)
class UpdateViewModel(private val datasource:NoteCategoriesDao, application: Application):
    AndroidViewModel(application) {

    val categoryItems: Array<String> = application.resources.getStringArray(R.array.notes_categories)


    private val currentDateTime: LocalDateTime = LocalDateTime.now()
    val updatedNoteDate: String = currentDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

    private suspend fun update(noteCategory: NoteCategory) {
        datasource.update(noteCategory)
    }
    private suspend fun getCategoryToUpdate(categoryName: String): NoteCategory {
        return datasource.getCategory(categoryName)
    }

    fun updateNote(note: Note,notePosition:Int) {
        viewModelScope.launch {
            val toUpdateCategory = getCategoryToUpdate(note.noteCategory)
            toUpdateCategory.notes!![notePosition] = note
            update(toUpdateCategory)
        }

    }


}