package com.developerkim.mytodo.ui.listnotes

import android.annotation.SuppressLint
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerkim.mytodo.data.database.NoteDatabase
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.data.repository.NotesRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class ListNoteViewModel(
    private val dataSource: NoteDatabase

) : ViewModel() {
    private val TAG = "ListNoteViewModel"
    private val notesRepository = NotesRepository(dataSource)

    private val _categories = MutableLiveData<List<NoteCategory>>()
    val categoriesList: LiveData<List<NoteCategory>> = _categories

    init {
        hidePrivateCategories()
    }

    private suspend fun deleteAllCategories() {
        dataSource.notesCategoriesDao.clearAllCategories()
    }

    private suspend fun deleteCategory(noteCategory: NoteCategory) {
        dataSource.notesCategoriesDao.deleteCategory(noteCategory.categoryName)
    }

    fun clearAllCategories() {
        viewModelScope.launch {
            deleteAllCategories()
        }
    }

    fun clearCategories(noteCategory: NoteCategory) {
        viewModelScope.launch {
            deleteCategory(noteCategory)
        }
    }

    private suspend fun update(noteCategory: NoteCategory) {
        dataSource.notesCategoriesDao.update(noteCategory)
    }

    private suspend fun getCategoryToUpdate(categoryName: String): NoteCategory {
        return dataSource.notesCategoriesDao.getCategory(categoryName)
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            val toUpdateCategory = getCategoryToUpdate(note.noteCategory)
            if (toUpdateCategory.notes!!.contains(note)) {
                toUpdateCategory.notes?.remove(note)
                update(toUpdateCategory)
            }
        }
    }

    fun showAllCategories() {
        notesRepository.categoryNoteList.observeForever {
            _categories.value = it
        }
    }

    fun hidePrivateCategories() {
        notesRepository.privateHiddenNotes.observeForever {
            _categories.value = it
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun sortNotes(noteCategory: List<NoteCategory>): List<NoteCategory> {
        val parser = SimpleDateFormat("yyyy/MM/dd, HH:mm a")
        return noteCategory.map {
            val noteList = it.notes?.sortedWith(compareBy { note ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDateTime.parse(
                        note.noteDate,
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    )
                } else {
                    parser.format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(note.noteDate)!!)
                }
            })
            return@map NoteCategory(
                categoryName = it.categoryName,
                notes = noteList?.reversed() as MutableList<Note>?
            )
        }
    }
    @SuppressLint("SimpleDateFormat")
    fun sortOldNotes(noteCategory: List<NoteCategory>): List<NoteCategory> {
        val parser = SimpleDateFormat("yyyy/MM/dd, HH:mm a")
        return noteCategory.map {
            val noteList = it.notes?.sortedWith(compareBy { note ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDateTime.parse(
                        note.noteDate,
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    )
                } else {
                    parser.format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(note.noteDate)!!)
                }
            })
            return@map NoteCategory(
                categoryName = it.categoryName,
                notes = noteList as MutableList<Note>?
            )
        }
    }
    fun noteCategoryFilter(noteCategories: List<NoteCategory>,newText:String?): List<NoteCategory> {
        return noteCategories.filter { noteCategory->
            noteCategory.categoryName.lowercase(Locale.getDefault())
                .contains(newText!!.lowercase(Locale.getDefault())) ||
                    noteCategory.notes?.any { note->
                        note.noteTitle.lowercase(Locale.getDefault())
                            .contains(newText.lowercase(Locale.getDefault()))
                    } == true
        }
    }
}



