@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.developerkim.mytodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_craete_note.*
import kotlinx.android.synthetic.main.activity_update_note.*
import java.text.SimpleDateFormat

class UpdateNote : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_note)

        val actionbar = supportActionBar

        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayShowHomeEnabled(true)
        actionbar.setTitle("Update Note")
        setValues()

        btnCancelUpdate.setOnClickListener {
//            val updateNotes:Boolean = MainActivity.databaseHandler.updateNotes()
            finish()
        }
        btnUpdate.setOnClickListener {
            var adapter:NoteAdapter? = null
            val id = updateId.text.toString()
            val updatedNote = editNote.text.toString()
            val updateTitle = txtnotetitle.text.toString()
            val updateCategory = txtnotecategory.text.toString()
            val updateDate = txtDate.text.toString()
            try{
                MainActivity.databaseHandler.updateNotes(
                    id ,updatedNote,updateTitle, updateCategory,updateDate)
                adapter?.notifyDataSetChanged()
                Toast.makeText(this@UpdateNote,"Note Updated", Toast.LENGTH_SHORT).show()
            }catch (e:Exception){
                e.printStackTrace()
                Toast.makeText(this@UpdateNote,"Error updating", Toast.LENGTH_SHORT).show()
            }
            finish()
            val goHome = Intent(this@UpdateNote,MainActivity::class.java)
            startActivity(goHome)
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setValues() {
        val editSaved: Intent = getIntent()
        val category = editSaved.getStringExtra("editCategory")
        val title = editSaved.getStringExtra("editTitle")
        val notes = editSaved.getStringExtra("editNote")
        val date = editSaved.getStringExtra("editDate")
        val id = editSaved.getStringExtra("editId")
        val date_format = SimpleDateFormat("MMM dd,yyyy")

/*            val categories = arrayListOf<String>(*getResources().getStringArray(R.array.entries))

            if (category != null) {
                for (i in categories.indices) {
                    when (category == categories[i]) {
                        R.string._1.toString() == categories[i] -> {
                            spinnerCategory.setSelection(i)
                        }
                        R.string._2.toString() == categories[i] -> {
                            spinnerCategory.setSelection(i)
                        }
                        R.string._3.toString() == categories[i] -> {
                            spinnerCategory.setSelection(i)
                        }
                        R.string._4.toString() == categories[i] -> {
                            spinnerCategory.setSelection(i)
                        }
                    }
                }


            }*/
        txtnotetitle.setText(title)
        editNote.setText(notes)
        txtnotedate.setText(date_format.format(date))
        txtnotecategory.setText(category)
        updateId.setText(id)

//            btnSave.setText("update")
    }
    }
