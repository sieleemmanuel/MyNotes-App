package com.developerkim.mytodo.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.developerkim.mytodo.data.model.NoteCategory

@Dao
interface NoteCategoriesDao{
    @Insert
    suspend fun insert(noteCategory: NoteCategory)

    @Update
    suspend fun update(noteCategory: NoteCategory)

    @Query("DELETE FROM notes_categories_table")
    suspend fun clearAllCategories()

    @Query("DELETE FROM notes_categories_table WHERE category_name =:categoryName")
    suspend fun deleteCategory(categoryName:String)

    @Query("SELECT * FROM notes_categories_table WHERE category_name =:categoryName")
    suspend fun getCategory(categoryName:String):NoteCategory

    @Query("SELECT * FROM notes_categories_table")

    fun getAllNoteCategories(): LiveData<List<NoteCategory>>
    @Query("SELECT * FROM notes_categories_table WHERE category_name !=:categoryName ORDER BY category_name")
    fun getCategoriesPrivateHidden(categoryName:String="Private"): LiveData<List<NoteCategory>>

    @Query("SELECT EXISTS(SELECT * FROM notes_categories_table WHERE category_name=:id)")
    suspend fun isCategoryExists(id:String):Boolean


}