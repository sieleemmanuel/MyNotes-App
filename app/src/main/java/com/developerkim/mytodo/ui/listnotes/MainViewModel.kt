package com.developerkim.mytodo.ui.listnotes

import android.content.Context
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

private const val TAG = "ListNoteViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    private val _categories: MutableLiveData<List<NoteCategory>?> =
        MutableLiveData<List<NoteCategory>?>()
    val categoriesList: LiveData<List<NoteCategory>?> = _categories

    private val _categoryNames: MutableLiveData<List<String>?> =
        MutableLiveData<List<String>?>()
    val categoriesNames: LiveData<List<String>?> = _categoryNames

    private val _allNotes = MutableLiveData<MutableList<Note>>(mutableListOf())
    val allNotes: LiveData<MutableList<Note>> = _allNotes

    private val _note = MutableLiveData<Note>()
    val note: LiveData<Note> = _note

    private val currentDateTime: LocalDateTime = LocalDateTime.now()
    val noteDate: String =
        currentDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

    private val _noteCategory = MutableLiveData<NoteCategory?>(null)
    val noteCategory: LiveData<NoteCategory?> = _noteCategory

    private val _pickedColor = MutableLiveData<Int?>(null)
    val pickedColor: LiveData<Int?> = _pickedColor

    private val _categoryExist = MutableLiveData(false)
    val categoryExist: LiveData<Boolean> = _categoryExist

    fun insertNewCategory(newCategory: NoteCategory) {
        viewModelScope.launch {
            notesRepository.insert(newCategory)
            getCategories()
            getAllNotes()
            getCategoryNames()
        }

    }

    fun insertNewNotes(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val categoryToUpdate = notesRepository.getCategory(note.noteCategory)
            categoryToUpdate.notes?.add(note)
            categoryToUpdate.let { notesRepository.updateCategory(it) }
            getCategories()
            getAllNotes()
            getCategory(note.noteCategory)
        }
    }

    fun deleteAllCategoryNotes(categoryName: String) {
        viewModelScope.launch {
            val toUpdateCategory = notesRepository.getCategoryToUpdate(categoryName = categoryName)
            toUpdateCategory.notes?.removeAll(toUpdateCategory.notes!!)
            notesRepository.updateCategory(toUpdateCategory)
            getCategories()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val toUpdateCategory = notesRepository.getCategoryToUpdate(note.noteCategory)
            Log.d(TAG, "deleteNote: ${toUpdateCategory.notes}")
            val noteToDelete = toUpdateCategory.notes?.find {
                it.noteTitle == note.noteTitle
            }
            Log.d(TAG, "updateNote: $noteToDelete")
            toUpdateCategory.notes?.remove(noteToDelete!!)
            notesRepository.updateCategory(toUpdateCategory)
            getCategories()
            getAllNotes()
        }
    }

    fun setNoteFavorite(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val categoryWithNote = notesRepository.getCategory(note.noteCategory)
            Log.d(TAG, "setNoteFavorite: $categoryWithNote")
            val noteToUpdate = categoryWithNote.notes?.find {
                it.noteTitle == note.noteTitle
            }
            if (noteToUpdate!!.isFavorite) {
                categoryWithNote.notes?.find {
                    it.noteTitle == note.noteTitle
                }!!.isFavorite = false
                notesRepository.updateCategory(categoryWithNote)
            } else {
                categoryWithNote.notes?.find {
                    it.noteTitle == note.noteTitle
                }!!.isFavorite = true
                notesRepository.updateCategory(categoryWithNote)
            }
            getNote(note.noteCategory, note.noteTitle)
            getCategory(note.noteCategory)
            getCategories()
            getAllNotes()
        }
    }

    fun editNote(noteToEdit: Note, editedNote: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val categoryWithNote = notesRepository.getCategory(noteToEdit.noteCategory)
            categoryWithNote.notes?.find { it.noteTitle == noteToEdit.noteTitle }
                ?.apply {
                    noteTitle = editedNote.noteTitle
                    noteText = editedNote.noteText
                    reminderTime = editedNote.reminderTime
                }
            notesRepository.updateCategory(categoryWithNote)
            getCategories()
            getAllNotes()
        }
    }

    fun getPickedColor(color: Int?) {
        _pickedColor.value = color
    }

    fun clearAllCategories() {
        viewModelScope.launch {
            notesRepository.deleteAllCategories()
            getCategories()
        }
    }

    private fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            _categories.postValue(notesRepository.getAllNotes())
        }
    }

    fun getCategory(categoryName: String){
        viewModelScope.launch {
            _noteCategory.postValue(notesRepository.getCategory(categoryName))
            _categoryExist.postValue(true)
        }
    }

    private fun getCategoryNames() {
        viewModelScope.launch(Dispatchers.IO) {
            val categories = mutableListOf<String>()
            categories.add(0, "Add New Category")
            val categoryNames = notesRepository.getAllNotes().map {
                it.categoryName
            }.toMutableList()
            categories.addAll(categoryNames)
            _categoryNames.postValue(categories)
        }
    }

    fun getAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            val allNoteCategories = notesRepository.getAllNotes()
            val allNotes = mutableListOf<Note>()
            allNoteCategories.forEach { noteCategory ->
                noteCategory.notes?.let { notes -> allNotes.addAll(notes) }
            }
            _allNotes.postValue(sortNoteList(allNotes))
        }
    }

    fun getNote(categoryName: String, noteTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = notesRepository.getCategory(categoryName).notes?.find {
                it.noteTitle == noteTitle
            }
            _note.postValue(note!!)
        }

    }

    fun getFavouriteNotes(notes: List<Note>): List<Note> {
        return notes.filter { it.isFavorite }
    }

    fun getReminderNotes(notes: List<Note>): List<Note> {
        return notes.filter {
            it.reminderTime.contains("[0-9/:]".toRegex())
        }
    }

    private fun sortNoteList(notes: List<Note>): MutableList<Note> {
        val parser = SimpleDateFormat("yyyy/MM/dd, HH:mm a", Locale.getDefault())
            val noteList = notes.sortedWith(compareBy { note ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDateTime.parse(
                        note.noteDate,
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    )
                } else {
                    parser.format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault()).parse(note.noteDate)!!)
                }
            }).toMutableList().reversed()
            return noteList.toMutableList()
    }

    fun categoryAndNoteSearchFilter(noteCategories: List<NoteCategory>, newText: String?
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

    fun searchNotes(query:String?, notes: MutableList<Note>): MutableList<Note> {
            val foundNotes = notes.filter {
                it.noteTitle.lowercase().contains(query!!) ||
                        it.noteText.lowercase().contains(query) ||
                        it.noteCategory.lowercase().contains(query)
            }
             return foundNotes.toMutableList()
    }

    fun removeCategory(categoryName: String) {
        viewModelScope.launch {
            notesRepository.deleteCategory(categoryName)
            getCategories()
            getAllNotes()
            getCategoryNames()
        }

    }

    init {
        getCategories()
        getAllNotes()
        getCategoryNames()
    }

}



