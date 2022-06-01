package com.developerkim.mytodo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.developerkim.mytodo.data.model.Converter
import com.developerkim.mytodo.data.model.NoteCategory

@Database(entities = [NoteCategory::class], version = 2)
@TypeConverters(Converter::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract val notesCategoriesDao:NoteCategoriesDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getInstance(context: Context): NoteDatabase {
            synchronized(this) {
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