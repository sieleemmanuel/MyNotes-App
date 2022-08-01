package com.developerkim.mytodo.ui.listnotes

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.FieldPosition
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
    val allNotes:LiveData<MutableList<Note>> = _allNotes

    val noteCategories: Array<String> = context.resources.getStringArray(R.array.notes_categories)


    private val currentDateTime: LocalDateTime = LocalDateTime.now()
    val noteDate: String =
        currentDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

    private val _noteCategory = MutableLiveData<String>()
    val noteCategory: LiveData<String>
        get() = _noteCategory

    private val _pickedColor = MutableLiveData<Int>()
    val pickedColor: LiveData<Int> = _pickedColor

    private val _reminderTime = MutableLiveData("None")
    val reminderTime: LiveData<String> = _reminderTime


    fun insertCategoryAndNotes(note: Note, noteCategory: NoteCategory) {
        viewModelScope.launch(Dispatchers.IO) {
            if (notesRepository.categoryExists(note.noteCategory)) {
                insertNewNotes(note)
            } else {
                insertNewCategory(noteCategory)
            }
        }

    }

    fun insertNewCategory(newCategory: NoteCategory) {
        viewModelScope.launch {
            notesRepository.insert(newCategory)
        }
        getCategories()
        getAllNotes()
        getCategoryNames()
    }

    fun insertNewNotes(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val categoryToUpdate = notesRepository.getCategory(note.noteCategory)
            categoryToUpdate.notes?.add(note)
            categoryToUpdate.let { notesRepository.updateCategory(it) }
            getCategories()
            getAllNotes()
        }
    }

    fun deleteSelectedNotes(note: Note) {
        viewModelScope.launch {
            val toUpdateCategory = notesRepository.getCategoryToUpdate(note.noteCategory)
            val selected = toUpdateCategory.notes?.filter {
                it.noteTitle == note.noteTitle
            }
            Log.d(TAG, "updateNote: $selected")
            toUpdateCategory.notes?.removeAll(selected!!)
            notesRepository.updateCategory(toUpdateCategory)
            if (toUpdateCategory.notes!!.isEmpty()) {
                notesRepository.deleteCategory(toUpdateCategory)
            }
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
            val noteToUpdate = categoryWithNote.notes?.find {
                it.noteTitle==note.noteTitle
            }
            if (noteToUpdate!!.isFavorite) {
                categoryWithNote.notes?.find {
                    it.noteTitle==note.noteTitle
                }!!.isFavorite=false
                notesRepository.updateCategory(categoryWithNote)
            } else {
                categoryWithNote.notes?.find {
                    it.noteTitle==note.noteTitle
                }!!.isFavorite = true
                notesRepository.updateCategory(categoryWithNote)
            }
            getCategories()
            getAllNotes()
        }
    }

    fun editNote(noteToEdit:Note, editedNote: Note) {
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
    fun createNoteList(note: Note): ArrayList<Note> {
        val noteList = ArrayList<Note>()
        noteList.add(note)
        return noteList
    }

    fun getPickedColor(color: Int) {
        _pickedColor.value = color
    }

    fun clearAllCategories() {
        viewModelScope.launch {
            notesRepository.deleteAllCategories()
        }
    }

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) { _categories.postValue( notesRepository.getAllNotes())}
    }
    fun getCategoryNames() {
        viewModelScope.launch(Dispatchers.IO) {
            val categories = mutableListOf<String>()
            categories.add(0, "Add New Category")
            val categoryNames = notesRepository.getAllNotes().map {
                it.categoryName
            }.toMutableList()
            categories.addAll(categoryNames)
            _categoryNames.postValue(categories) }
    }

    private fun getAllNotes() {
        viewModelScope.launch(Dispatchers.IO){
            val allNoteCategories = notesRepository.getAllNotes()
            val allNotes = mutableListOf<Note>()
            allNoteCategories.forEach { noteCategory ->
                noteCategory.notes?.let { it1 -> allNotes.addAll(it1) }
            }
            _allNotes.postValue(allNotes)
            getCategories()
        }
    }
    fun getFavouriteNotes(notes: List<Note>): List<Note> {
        return notes.filter { it.isFavorite}
    }

    fun getReminderNotes(notes: List<Note>): List<Note> {
       return notes.filter { it.reminderTime!="None" && it.reminderTime.isNotEmpty() }
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

    fun reminderTime(reminderTime: String ="None") {
        _reminderTime.value = reminderTime
    }

    init {
        getCategories()
        getAllNotes()
        getCategoryNames()
    }

}



