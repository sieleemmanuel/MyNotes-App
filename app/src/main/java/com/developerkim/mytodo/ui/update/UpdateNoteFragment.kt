package com.developerkim.mytodo.ui.update

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.FragmentUpdateNoteBinding
import com.developerkim.mytodo.ui.listnotes.MainViewModel
import com.developerkim.mytodo.util.HideKeyboard.hideKeyboard
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateNoteFragment : Fragment(),
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentUpdateNoteBinding
    private val viewModel: UpdateViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val args: UpdateNoteFragmentArgs by navArgs()
    private lateinit var noteToUpdate:Note
    private lateinit var updateSelectedCategory: String
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var adapter: ArrayAdapter<String>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateNoteBinding.inflate(inflater)
        appBarConfiguration = AppBarConfiguration(findNavController().graph)
        noteToUpdate = args.note
        val notePosition = requireArguments().getInt("note_position")

        binding.apply {
            setUpNoteValuesToUpdate()
            setUpToolbar(notePosition)
            clUpdateNote.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    hideKeyboard(view, requireActivity())
                }
            }
        }

        return binding.root
    }



    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        updateSelectedCategory = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Toast.makeText(requireContext(), "no category selected", Toast.LENGTH_SHORT).show()
    }

    private fun  FragmentUpdateNoteBinding.setUpNoteValuesToUpdate() {
        updateTitleEditText.setText(noteToUpdate.noteTitle)
        updateTextEditText.setText(noteToUpdate.noteText)
        edReminderTime.setText(noteToUpdate.reminderTime)
        adapter = ArrayAdapter(
            requireContext(),
            R.layout.category_item,
            viewModel.filterCategories(noteToUpdate.noteCategory)
        )
    }

    private fun FragmentUpdateNoteBinding.setUpToolbar(notePosition: Int) {
        updateNoteToolbar.apply {
            setupWithNavController(findNavController(), appBarConfiguration)
            title = "Edit ${noteToUpdate.noteCategory} note"
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.actionDone -> {
                        val uNote = Note(
                            updateTitleEditText.text.toString(),
                            updateTextEditText.text.toString(),
                            viewModel.updatedNoteDate,
                            reminderTime = "None"
                        )
                        if (noteToUpdate.noteText != uNote.noteText || noteToUpdate.noteTitle != uNote.noteTitle) {
                            mainViewModel.editNote(uNote)
                            findNavController()
                                .navigate(UpdateNoteFragmentDirections.actionUpdateNoteFragmentToListNotesFragment())
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Nothing to Update",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(
                                UpdateNoteFragmentDirections.actionUpdateNoteFragmentToListNotesFragment()
                            )
                        }
                    }
                    R.id.actionClose -> {
                        val uNote = Note(
                            updateTitleEditText.text.toString(),
                            updateTextEditText.text.toString(),
                            viewModel.updatedNoteDate,
                            reminderTime = "None"
                        )
                        if (noteToUpdate.noteText == updateTextEditText.text.toString() &&
                            noteToUpdate.noteTitle == updateTitleEditText.text.toString()
                        ) {
                            findNavController().navigate(
                                UpdateNoteFragmentDirections.actionUpdateNoteFragmentToListNotesFragment()
                            )
                        } else {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Want to cancel update?")
                                .setCancelable(false)
                                .setPositiveButton(" OK") { dialog, _ ->
                                    viewModel.updateNote(uNote, notePosition)
                                    dialog.dismiss()
                                    findNavController().popBackStack(R.id.listNotesFragment, true)
                                }
                                .setNegativeButton("CANCEL") { dialog, _ ->
                                    findNavController().popBackStack()
                                    dialog.dismiss()
                                }.show()
                        }
                    }
                }
                false
            }
        }
    }

}
