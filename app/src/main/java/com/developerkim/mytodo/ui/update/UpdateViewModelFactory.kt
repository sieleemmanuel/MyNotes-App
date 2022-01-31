package com.developerkim.mytodo.ui.update

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.developerkim.mytodo.data.database.NoteCategoriesDao
import java.lang.IllegalArgumentException

@RequiresApi(Build.VERSION_CODES.O)
class UpdateViewModelFactory(
    private val dataSource: NoteCategoriesDao,
    private val application: Application
) :ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       if (modelClass.isAssignableFrom(UpdateViewModel::class.java)){
           return   UpdateViewModel(dataSource,application) as T
       }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}