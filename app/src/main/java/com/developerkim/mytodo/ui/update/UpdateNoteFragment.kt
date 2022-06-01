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
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.database.NoteDatabase
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.FragmentUpdateNoteBinding
import com.developerkim.mytodo.util.HideKeyboard.hideKeyboard
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateNoteFragment : Fragment(),
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentUpdateNoteBinding
    private val viewModel: UpdateViewModel by activityViewModels()
    private lateinit var updateSelectedCategory: String
    private lateinit var adapter: ArrayAdapter<String>

    lateinit var noteCategory: String
    lateinit var noteTitle: String
    lateinit var noteText: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateNoteBinding.inflate(
            inflater
        )
        val argument = requireArguments()
        noteCategory = argument.getString("update_category")!!
        noteTitle = argument.getString("update_title")!!
        noteText = argument.getString("update_text")!!
        setValuesToUpdate()

        binding.apply {
            val notePosition = requireArguments().getInt("note_position")
            updateNoteCategory.setAdapter(adapter)
            updateNoteCategory.apply {
                onItemSelectedListener = this@UpdateNoteFragment
            }

            updateNoteToolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.actionDone -> {
                        val uNote = Note(
                            updateNoteCategory.text.toString(),
                            updateTitleEditText.text.toString(),
                            updateTextEditText.text.toString(),
                            viewModel.updatedNoteDate
                        )
                        if (noteText != uNote.noteText || noteTitle != uNote.noteTitle) {
                            viewModel.updateNote(uNote, notePosition)
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
                            updateNoteCategory.text.toString(),
                            updateTitleEditText.text.toString(),
                            updateTextEditText.text.toString(),
                            viewModel.updatedNoteDate
                        )
                        // hideKeyboard(it,requireActivity())
                        if (noteText == updateTextEditText.text.toString() &&
                            noteTitle == updateTitleEditText.text.toString()
                        ) {
                            findNavController().navigate(
                                UpdateNoteFragmentDirections.actionUpdateNoteFragmentToListNotesFragment()
                            )
                        } else {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Want to cancel update?")
                                .setCancelable(false)
                                .setPositiveButton(" NO UPDATE") { dialog, _ ->
                                    viewModel.updateNote(uNote, notePosition)
                                    dialog.dismiss()
                                    findNavController().navigate(
                                        UpdateNoteFragmentDirections.actionUpdateNoteFragmentToListNotesFragment()
                                    )
                                }
                                .setNegativeButton("CANCEL") { dialog, _ ->
                                    findNavController().navigate(
                                        UpdateNoteFragmentDirections.actionUpdateNoteFragmentToListNotesFragment()
                                    )
                                    dialog.dismiss()
                                }.show()
                        }
                    }
                }
                false
            }
            clUpdateNote.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    hideKeyboard(view, requireActivity())
                }
            }
            updateNoteCategory.setOnFocusChangeListener { view, hasFocus ->
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

    private fun setValuesToUpdate() {
        binding.apply {
            updateTitleEditText.setText(noteTitle)
            updateTextEditText.setText(noteText)
            updateNoteCategory.setText(noteCategory)
        }
        adapter = ArrayAdapter(
            requireContext(),
            R.layout.category_item,
            viewModel.filterCategories(noteCategory)
        )
    }

}
