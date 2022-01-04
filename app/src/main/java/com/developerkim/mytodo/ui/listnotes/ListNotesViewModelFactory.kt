package com.developerkim.mytodo.ui.listnotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.developerkim.mytodo.data.database.NoteDatabase

class ListNotesViewModelFactory(private val dataSource:NoteDatabase):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(ListNoteViewModel::class.java)){
          return ListNoteViewModel(dataSource) as T
      }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}