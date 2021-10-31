package com.developerkim.mytodo.ui.newnote

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.developerkim.mytodo.database.NoteCategoriesDao

@RequiresApi(Build.VERSION_CODES.O)
class NewNoteViewModelFactory(private val application: Application, private val dataSource:NoteCategoriesDao):ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       if (modelClass.isAssignableFrom(NewNoteViewModel::class.java)){
           return NewNoteViewModel(dataSource,application) as T
       }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
