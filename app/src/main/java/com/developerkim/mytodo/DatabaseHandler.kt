package com.developerkim.mytodo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.lang.Integer.parseInt


class DatabaseHandler(
    context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int
) : SQLiteOpenHelper(
    context, DB_NAME, null, DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_NOTES_TABLE: String = ("CREATE TABLE $NOTES_TABLE_NAME (" +
                "$COLUMN_NOTEID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NOTETITLE TEXT," +
                "$COLUMN_NOTECATEGORY TEXT," +
                "$COLUMN_NOTENOTES TEXT," +
                "$COLUMN_NOTEDATE DATE)")
        db?.execSQL(CREATE_NOTES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newerVersion: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS" + NOTES_TABLE_NAME
        db?.execSQL(DROP_TABLE)
        onCreate(db)
    }

    fun addNotes(context: Context, note: Note) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTETITLE, note.noteTITLE)
        values.put(COLUMN_NOTECATEGORY, note.noteCATEGORY)
        values.put(COLUMN_NOTENOTES, note.noteNOTES)
        values.put(COLUMN_NOTEDATE, note.noteDATE)
        try {
            db.insert(NOTES_TABLE_NAME, null, values)
            Toast.makeText(context, "Note Saved Successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }

        db.close()

    }

/*    fun getNotes(_id: Int): Note {
//        val notes = Note()
//        val db = writableDatabase
//        val selectquery = "SELECT*FROM $NOTES_TABLE_NAME WHERE ${COLUMN_NOTEID} = $_id"
//        val cursor = db.rawQuery(selectquery, null)
//        if (cursor != null) {
//            cursor.moveToFirst()
//            while (cursor.moveToNext()) {
//                notes.noteID = parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_NOTEID)))
//                notes.noteTITLE = cursor.getString(cursor.getColumnIndex(COLUMN_NOTETITLE))
//                notes.noteCATEGORY = cursor.getString(cursor.getColumnIndex(COLUMN_NOTECATEGORY))
//                notes.noteNOTES = cursor.getString(cursor.getColumnIndex(COLUMN_NOTENOTES))
//            }
//        }
//        cursor.close()
//        return notes
//    }*/

    fun showNotes(cxt: Context): ArrayList<Note> {

        val noteslist = ArrayList<Note>()
        val selectquery = "SELECT * FROM $NOTES_TABLE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectquery, null)
        cursor.moveToFirst()
        if (cursor.count == 0) {
            Toast.makeText(cxt, "No notes Saved yet", Toast.LENGTH_SHORT).show()
        } else {
            while (cursor.moveToNext()) {
                val note = Note()
                note.noteID = parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_NOTEID)))
                note.noteTITLE = cursor.getString(cursor.getColumnIndex(COLUMN_NOTETITLE))
                note.noteCATEGORY = cursor.getString(cursor.getColumnIndex(COLUMN_NOTECATEGORY))
                note.noteNOTES = cursor.getString(cursor.getColumnIndex(COLUMN_NOTENOTES))
                note.noteDATE = cursor.getString(cursor.getColumnIndex(COLUMN_NOTEDATE))
                noteslist.add(note)
            }
        }
        cursor.close()
        db.close()
        return noteslist

    }

    //    updating the notes
    fun updateNotes(id:String, update_notes:String, update_title:String, update_category:String, update_date:String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        var result: Boolean
        values.put(COLUMN_NOTENOTES,update_notes)
        values.put(COLUMN_NOTETITLE,update_title)
        values.put(COLUMN_NOTECATEGORY,update_category)
        values.put(COLUMN_NOTEDATE,update_date)
         try{
            db.update(NOTES_TABLE_NAME, values, COLUMN_NOTEID + "=?", arrayOf(id)).toLong()
            db.close()
           result = true

        }catch (e:Exception){
            result = false
        }
        return result

    }

    //function to delete a note from db
    fun deleteNotes(_id: Int): Boolean {
        val db = this.writableDatabase
        val _success = db.delete(NOTES_TABLE_NAME, COLUMN_NOTEID + "=?", arrayOf(_id.toString())).toLong()
        db.close()
        return (parseInt("$_success") != -1)
    }

    companion object {
        private val DB_VERSION: Int = 1
        private val DB_NAME: String = "Notes_db"
        private val COLUMN_NOTEID = "Note_id"
        private val COLUMN_NOTETITLE = "Note_Title"
        private val COLUMN_NOTECATEGORY = "Note_Category"
        private val COLUMN_NOTENOTES = "Notes"
        private val NOTES_TABLE_NAME = "Notes_Table"
        private val COLUMN_NOTEDATE = "Date"
    }
}