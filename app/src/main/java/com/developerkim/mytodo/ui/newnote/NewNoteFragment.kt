package com.developerkim.mytodo.ui.newnote

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.database.NoteDatabase
import com.developerkim.mytodo.databinding.FragmentNewNoteBinding
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import kotlinx.android.synthetic.main.fragment_new_note.*

class NewNoteFragment : Fragment(), AdapterView.OnItemClickListener {

    lateinit var selectedCategory: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val binding: FragmentNewNoteBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_new_note,
            container,
            false
        )

        val noteTitle = binding.noteTitleEditText.text
        val noteText = binding.noteTextEditText.text

        binding.btnClose.setOnClickListener {
            this.findNavController().navigate(
                NewNoteFragmentDirections.actionNewNoteFragmentToListNotesFragment()
            )
        }

        val application = requireActivity().application
        val database = NoteDatabase.getInstance(application).notesCategoriesDao

        /*Getting the reference to viewModelFactory and initialize viewModel*/
        val viewModelFactory = NewNoteViewModelFactory(application, database)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(NewNoteViewModel::class.java)


        val adapter =ArrayAdapter(requireContext(), R.layout.categories_menu_item, viewModel.categoryItems)
        binding.selectedCategory.apply {
            setAdapter(adapter)
            onItemClickListener = this@NewNoteFragment
        }

        binding.btnSave.setOnClickListener {
            //Creating a list of notes to store in database based on categories
            val newNote = Note(selectedCategory, noteTitle.toString(), noteText.toString(), viewModel.noteDate)

            Toast.makeText(requireContext(),newNote.noteText,Toast.LENGTH_LONG ).show()
            val noteList = viewModel.createNoteList(newNote)
            val categories = NoteCategory(newNote.noteCategory, noteList)

            //Saving notes to database
            viewModel.updateIfExist(newNote,categories)
            this.findNavController().navigate(
                NewNoteFragmentDirections.actionNewNoteFragmentToListNotesFragment()
            )
        }

        return binding.root
    }

    override fun onItemClick(parent: AdapterView<*>?, p1: View?, position: Int, id: Long) {
        selectedCategory = parent?.getItemAtPosition(position).toString()
    }


}