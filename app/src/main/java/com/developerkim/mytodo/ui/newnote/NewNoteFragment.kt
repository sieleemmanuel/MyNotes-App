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
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.FragmentNewNoteBinding
import com.developerkim.mytodo.util.HideKeyboard.hideKeyboard

class NewNoteFragment : Fragment(), AdapterView.OnItemClickListener {

    lateinit var selCategory: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentNewNoteBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_new_note,
            container,
            false
        )

        val application = requireActivity().application
        val database = NoteDatabase.getInstance(application).notesCategoriesDao
        val viewModelFactory = NewNoteViewModelFactory(application, database)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(NewNoteViewModel::class.java)
        val adapter =
            ArrayAdapter(requireContext(), R.layout.categories_menu_item, viewModel.categoryItems)

        binding.apply {
            val noteTitle = noteTitleEditText.text
            val noteText = noteTextEditText.text
            btnClose.setOnClickListener {
                hideKeyboard(it,requireActivity())
                findNavController().navigate(
                    NewNoteFragmentDirections.actionNewNoteFragmentToListNotesFragment()
                )
            }

            selectedCategory.apply {
                setAdapter(adapter)
                onItemClickListener = this@NewNoteFragment
            }

            btnSave.setOnClickListener {
                hideKeyboard(it,requireActivity())
                selCategory = selectedCategory.text.toString()
                val newNote = Note(
                        selCategory,
                        noteTitle.toString(),
                        noteText.toString(),
                        viewModel.noteDate
                    )
                val noteList = viewModel.createNoteList(newNote)
                val categories = NoteCategory(
                    newNote.noteCategory,
                    noteList
                )

                //Saving notes to database
                viewModel.updateIfExist(newNote, categories)
                findNavController().navigate(
                    NewNoteFragmentDirections.actionNewNoteFragmentToListNotesFragment()
                )
            }
            clNewNote.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    hideKeyboard(view, requireActivity())
                }
            }
            selectedCategory.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    hideKeyboard(view, requireActivity())
                }
            }
        }
        return binding.root
    }

    override fun onItemClick(parent: AdapterView<*>?, p1: View?, position: Int, id: Long) {
        selCategory = parent?.getItemAtPosition(position).toString()
    }


}