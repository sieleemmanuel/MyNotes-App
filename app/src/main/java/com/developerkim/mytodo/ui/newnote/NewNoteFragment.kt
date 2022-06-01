package com.developerkim.mytodo.ui.newnote

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.FragmentNewNoteBinding
import com.developerkim.mytodo.ui.MainActivity
import com.developerkim.mytodo.util.HideKeyboard.hideKeyboard
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewNoteFragment : Fragment(), AdapterView.OnItemClickListener {
    private lateinit var selCategory: String
    private lateinit var binding: FragmentNewNoteBinding
    private val viewModel: NewNoteViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        binding = FragmentNewNoteBinding.inflate(inflater)
        val noteCategoriesAdapter = ArrayAdapter(
            requireContext(),
            R.layout.category_layout,
            viewModel.noteCategories
        )

        val notePriorityAdapter = ArrayAdapter(
            requireContext(),
            R.layout.priority_layout,
            viewModel.notePriorities
        )

        binding.apply {

            selectCategory.apply {
                setAdapter(noteCategoriesAdapter)
                onItemClickListener = this@NewNoteFragment
                setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        hideKeyboard(view, requireActivity())
                    }
                }
            }
            selectPriority.apply {
                setAdapter(notePriorityAdapter)
                setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        hideKeyboard(view, requireActivity())
                    }
                }
            }
            val noteTitle = noteTitleEditText.text
            val noteText = noteTextEditText.text

            clNewNote.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    hideKeyboard(view, requireActivity())
                }
            }
            menuItemClickListener(noteTitle, noteText)
        }
       ( activity as MainActivity).onBackPressed()

        return binding.root
    }






    override fun onItemClick(parent: AdapterView<*>?, p1: View?, position: Int, id: Long) {
        selCategory = parent?.getItemAtPosition(position).toString()
    }

    private fun FragmentNewNoteBinding.menuItemClickListener(
        noteTitle: Editable?,
        noteText: Editable
    ) {
        newNoteToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionDone -> {
                    selCategory = selectCategory.text.toString()
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
                    true
                }
                R.id.actionClose -> {
                    findNavController().navigateUp()
                }
                R.id.actionAddReminder -> {
                    showReminderDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun showReminderDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.add_reminder_title_label))
            .setPositiveButton(
                "SET"
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}