package com.developerkim.mytodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_craete_note.*
import kotlinx.android.synthetic.main.activity_read_notes.*

class ReadNotes : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_notes)

        val actionbar = supportActionBar

        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayShowHomeEnabled(true)
        actionbar.setTitle("Reading")

        val details:Intent= getIntent()
        val id = details.getStringExtra("idDetails")
        val category= details.getStringExtra("categoryDetails")
        val title= details.getStringExtra("titleDetails")
        val notes= details.getStringExtra("notesDetails")
        val date= details.getStringExtra("dateDetails")

        txtNoteCategory.setText(category)
        txtNoteDate.setText(date)
        readTitle.setText(title)
        readNotes.setText(notes)
        txtid.setText(id?.toString())

        btnedit.setOnClickListener {
            val readCategory = txtNoteCategory.text
            val readTitle = readTitle.text
            val readDate = txtNoteDate.text
            val readNote = readNotes.text
            val readId = txtid.text.toString()

            val editNote = Intent(this@ReadNotes, UpdateNote::class.java)
            editNote.putExtra("editCategory", readCategory )
            editNote.putExtra("editTitle", readTitle )
            editNote.putExtra("editDate", readDate )
            editNote.putExtra("editNote", readNote )
            editNote.putExtra("editId", readId)
            startActivity(editNote)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
