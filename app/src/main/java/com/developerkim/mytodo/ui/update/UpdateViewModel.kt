package com.developerkim.mytodo.ui.update

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject


@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    @ApplicationContext context: Context
): ViewModel() {
    private val categoryItems: Array<String> = context.resources.getStringArray(R.array.notes_categories)

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

    fun updateNote(note: Note,notePosition:Int) {
        viewModelScope.launch {
            val toUpdateCategory = notesRepository.getCategoryToUpdate(note.noteCategory)
            Log.d("toUpdateCategory", "toUpdateCategory:::$toUpdateCategory ")
            Log.d("NoteToUpdate", "Note:::$note")
            toUpdateCategory.notes!![notePosition] = note
            notesRepository.update(toUpdateCategory)
        }

    }
    fun filterCategories(selectedCategory: String): Array<String> {
        return categoryItems.filter {
            it.contentEquals(selectedCategory)
        }.toTypedArray()
    }


}