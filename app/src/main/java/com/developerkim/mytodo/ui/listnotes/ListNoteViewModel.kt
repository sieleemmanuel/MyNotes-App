package com.developerkim.mytodo.ui.listnotes

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

private const val TAG = "ListNoteViewModel"

@HiltViewModel
class ListNoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _categories: MutableLiveData<List<NoteCategory>?> = MutableLiveData<List<NoteCategory>?>()
    val categoriesList: LiveData<List<NoteCategory>?> = _categories

    init {
        hidePrivateCategories()
    }

    fun clearAllCategories() {
        viewModelScope.launch {
            notesRepository.deleteAllCategories()
        }
    }

    fun clearCategories(noteCategory: NoteCategory) {
        viewModelScope.launch {
            notesRepository.deleteCategory(noteCategory)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            val toUpdateCategory = notesRepository.getCategoryToUpdate(note.noteCategory)
            val selected = toUpdateCategory.notes?.filter {
                it.noteTitle == note.noteTitle
            }
            Log.d(TAG, "updateNote: ${selected?.size}")
            toUpdateCategory.notes?.removeAll(selected!!)
            notesRepository.update(toUpdateCategory)
            if (toUpdateCategory.notes!!.isEmpty()) {
                notesRepository.deleteCategory(toUpdateCategory)
            }
        }
    }

    /*fun showAllCategories() {
        viewModelScope.launch {
            _categories.value = notesRepository.getAllNotes()
        }
    }*/

    private fun hidePrivateCategories() {
        viewModelScope.launch { _categories.value = notesRepository.getAllNotesPrivateHidden() }
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

    fun noteCategoryFilter(
        noteCategories: List<NoteCategory>,
        newText: String?
    ): List<NoteCategory> {
        return noteCategories.filter { noteCategory ->
            noteCategory.categoryName.lowercase(Locale.getDefault())
                .contains(newText!!.lowercase(Locale.getDefault())) ||
                    noteCategory.notes?.any { note ->
                        note.noteTitle.lowercase(Locale.getDefault())
                            .contains(newText.lowercase(Locale.getDefault()))
                    } == true
        }
    }
    /*fun isPrivateNotesHidden(categories: List<NoteCategory>): Boolean {
       return categories.none {
           it.categoryName.contains("Private")
       }
    }*/

}



