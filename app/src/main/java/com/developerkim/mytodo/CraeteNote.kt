package com.developerkim.mytodo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_craete_note.*
import java.text.SimpleDateFormat

class CraeteNote : AppCompatActivity() {


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_craete_note)

        val actionbar = supportActionBar

        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayShowHomeEnabled(true)
        actionbar.setTitle("Create Note")

        val context = this
        val currentdate = System.currentTimeMillis()
        val date_format = SimpleDateFormat("MMM dd,yyyy")
        txtDate.setText(date_format.format(currentdate))


        btnSave.setOnClickListener {
            if (etxtNotes.text.isEmpty()) {
                Toast.makeText(context, "Please type the note", Toast.LENGTH_SHORT).show()
                etxtTitle.error = "Enter the Not Title!"
                etxtTitle.requestFocus()
            } else {
                val note = Note()
                note.noteTITLE = etxtTitle.text.toString()
                note.noteCATEGORY = spinnerCategory.selectedItem.toString()
                note.noteDATE = txtDate.text.toString()
                if (etxtNotes.text.isEmpty()) {
                    etxtNotes.error = "please type in the note"
                    etxtTitle.requestFocus()
                } else {
                    note.noteNOTES = etxtNotes.text.toString()
                    MainActivity.databaseHandler.addNotes(this, note)
                    setDefaults()
                    finish()
                }
            }
        }
        btnCancel.setOnClickListener {
            setDefaults()
            finish()
        }
    }

    private fun setDefaults() {
        etxtTitle.text.clear()
        etxtNotes.text.clear()
        spinnerCategory.prompt
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
