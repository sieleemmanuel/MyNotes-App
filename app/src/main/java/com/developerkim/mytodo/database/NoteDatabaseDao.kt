/*
package com.developerkim.mytodo.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.developerkim.mytodo.model.Note
import com.developerkim.mytodo.model.NoteCategory

@Dao()
interface NoteDatabaseDao {

    */
/**
     * suspend keyword to mark a function available for coroutine
     * Run in background thread unless otherwise --Main this
     * Room by default does not run main thread
     * *//*

    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Query("DELETE FROM notes_table")
    suspend fun clearAllCategories()

    @Query("DELETE FROM notes_table ")
    suspend fun delete(noteId:Long)

*/
/**
 * Room uses a background thread for that specific @Query which returns LiveData
 * hence no need to suspend function
 * *//*

    @Query("SELECT * FROM notes_table")
    fun getAllNotes():LiveData<List<NoteCategory>>

    @Query("SELECT * FROM notes_table WHERE note_category=:noteCategory")
    fun getNotesByCategory(noteCategory:String):LiveData<List<Note>>package

}
*/
