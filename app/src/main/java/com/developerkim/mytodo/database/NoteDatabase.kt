package com.developerkim.mytodo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.developerkim.mytodo.model.Converter
import com.developerkim.mytodo.model.Note
import com.developerkim.mytodo.model.NoteCategory
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.flow.collect

@Database(entities = [NoteCategory::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract val notesCategoriesDao:NoteCategoriesDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null


        fun getInstance(context: Context): NoteDatabase {
            kotlin.synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        NoteDatabase::class.java,
                        "notes_database_history"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }


    }
}