package com.developerkim.mytodo.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import java.util.*


@Entity(tableName = "notes_categories_table")
@Parcelize
data class NoteCategory (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "category_name")var categoryName: String = "",
    @ColumnInfo(name = "category_notes")var notes:MutableList<Note>? = null
):Parcelable
class Converter{
    @TypeConverter
    fun listToJson(value: MutableList<Note>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String):MutableList<Note> =
        (Gson().fromJson(value, Array<Note>::class.java) as Array<Note>).toMutableList()

}

