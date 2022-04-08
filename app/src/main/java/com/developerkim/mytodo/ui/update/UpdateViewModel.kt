package com.developerkim.mytodo.ui.update

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.database.NoteCategoriesDao
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class UpdateViewModel(private val datasource:NoteCategoriesDao, application: Application):
    AndroidViewModel(application) {

    private val categoryItems: Array<String> = application.resources.getStringArray(R.array.notes_categories)

    @SuppressLint("SimpleDateFormat")
    private val currentDateTime:LocalDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now()
    } else {
        TODO()
        //SimpleDateFormat("dd/MM/yy, hh:mm a").format(Date())
    }
    @SuppressLint("SimpleDateFormat")
    val updatedNoteDate: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        currentDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
    } else {
        SimpleDateFormat("dd/MM/yy, hh:mm a").format(Date())
    }

    private suspend fun update(noteCategory: NoteCategory) {
        datasource.update(noteCategory)
    }
    private suspend fun getCategoryToUpdate(categoryName: String): NoteCategory {
        return datasource.getCategory(categoryName)
    }

    fun updateNote(note: Note,notePosition:Int) {
        viewModelScope.launch {
            val toUpdateCategory = getCategoryToUpdate(note.noteCategory)
            Log.d("toUpdateCategory", "toUpdateCategory:::$toUpdateCategory ")
            Log.d("NoteToUpdate", "Note:::$note")
            toUpdateCategory.notes!![notePosition] = note
            update(toUpdateCategory)
        }

    }
    fun filterCategories(selectedCategory: String): Array<String> {
        return categoryItems.filter {
            it.contentEquals(selectedCategory)
        }.toTypedArray()

    }


}