package com.developerkim.mytodo.data.database

import androidx.room.*
import com.developerkim.mytodo.data.model.NoteCategory

@Dao
interface NoteCategoriesDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noteCategory: NoteCategory)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(noteCategory: NoteCategory)

    @Query("DELETE FROM notes_categories_table")
    suspend fun clearAllCategories()

    @Query("DELETE FROM notes_categories_table WHERE category_name =:categoryName")
    suspend fun deleteCategory(categoryName:String)

    @Query("SELECT * FROM notes_categories_table WHERE category_name =:categoryName")
    suspend fun getCategory(categoryName:String):NoteCategory

    @Query("SELECT * FROM notes_categories_table")
    suspend fun getAllNoteCategories():List<NoteCategory>

    @Query("SELECT * FROM notes_categories_table WHERE category_name !=:categoryName")
    suspend fun getCategoriesPrivateHidden(categoryName:String="Private"): List<NoteCategory>

    @Query("SELECT EXISTS(SELECT * FROM notes_categories_table WHERE category_name=:id)")
    suspend fun isCategoryExists(id:String):Boolean


}