package com.developerkim.mytodo.ui.listnotes

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developerkim.mytodo.R
import com.developerkim.mytodo.adapters.NoteAdapter
import com.developerkim.mytodo.adapters.NotesCategoriesAdapter
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.DeleteLayoutBinding
import com.developerkim.mytodo.databinding.FragmentAllNotesBinding
import com.developerkim.mytodo.interfaces.NoteSearchListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllNotesFragment : Fragment() {
    private lateinit var binding: FragmentAllNotesBinding
    private lateinit var noteAdapter: NoteAdapter
    private val viewModel: MainViewModel by activityViewModels()
    private val TAG = "AllNotes"
    private var allNotes:String? = null
    private var reminders:String? = null
    private var favorites:String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllNotesBinding.inflate(inflater)
        val arg = arguments
        allNotes = arg?.getString(getString(R.string.all_note_args_key))
        reminders = arg?.getString(getString(R.string.reminders_args_key))
        favorites = arg?.getString(getString(R.string.favorites_args_key))

        binding.apply {
            tabsRecyclerview.apply { layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            noteAdapter = NoteAdapter(
                noteClickListener(),
                noteLongClickListener(),
                viewClickListener(),
            )
            tabsRecyclerview.adapter = noteAdapter

            viewModel.allNotes.observe(viewLifecycleOwner) {
                when {
                    allNotes == getString(R.string.all_note_args_key) -> {
                        if (it!!.isNotEmpty()) {
                            pbLoadingNotes.visibility = View.GONE
                            tvEmptyNotes.visibility = View.GONE
                            noteAdapter.submitList(null)
                            noteAdapter.submitList(it)
                        } else {
                            noteAdapter.submitList(null)
                            tvEmptyNotes.visibility = View.VISIBLE
                            pbLoadingNotes.visibility = View.GONE
                        }
                    }
                    reminders == getString(R.string.reminders_args_key) -> {
                        if (viewModel.getReminderNotes(it).toMutableList().isNotEmpty()) {
                            pbLoadingNotes.visibility = View.GONE
                            tvEmptyNotes. visibility = View.GONE
                            noteAdapter.submitList(null)
                            noteAdapter.submitList(viewModel.getReminderNotes(it).toMutableList())
                            
                        } else {
                            noteAdapter.submitList(null)
                            pbLoadingNotes.visibility = View.GONE
                            tvEmptyNotes.apply {
                                visibility = View.VISIBLE
                                text = getString(R.string.empty_reminder_label)
                            }
                           
                        }
                        
                    }
                    favorites == getString(R.string.favorites_args_key) -> {
                        if (viewModel.getFavouriteNotes(it).toMutableList().isNotEmpty()) {
                            pbLoadingNotes.visibility = View.GONE
                            tvEmptyNotes.visibility = View.GONE
                            noteAdapter.submitList(null)
                            noteAdapter.submitList(viewModel.getFavouriteNotes(it).toMutableList())
                        } else {
                            noteAdapter.submitList(null)
                            pbLoadingNotes.visibility = View.GONE
                            tvEmptyNotes.apply {
                                visibility = View.VISIBLE
                                text = getString(R.string.empty_favorites_label)
                            }

                        }

                    }
                }

            }
        }
        return binding.root
    }

    private fun noteClickListener() = NoteAdapter.NoteClickListener { note, _ ->
        findNavController().navigate(
            ListNotesFragmentDirections.actionListNotesFragmentToReadNotesFragment(
                note.noteCategory, note.noteTitle
            )
        )
    }

    private fun noteLongClickListener() =
        NoteAdapter.NoteLongClickListener { note, btnDeleteNote->
            if (!btnDeleteNote.isVisible){
                btnDeleteNote.visibility = View.VISIBLE
            }else{
                btnDeleteNote.visibility = View.GONE
            }
            btnDeleteNote.setOnClickListener {
                confirmDeleteNote(note, btnDeleteNote)
            }
        }

    private fun confirmDeleteNote(note: Note, btnDeleteNote: ImageView) {
        val deleteDialogBinding = DeleteLayoutBinding.inflate(LayoutInflater.from(requireContext()))
       val deleteNoteDialog =
           MaterialAlertDialogBuilder(requireContext())
               .setView(deleteDialogBinding.root)
               .setBackground(ColorDrawable(Color.TRANSPARENT))
               .show()
        deleteDialogBinding.apply {
            tvDeleteCategoryLabel.text =
                getString(R.string.delete_note_dialog_label, note.noteTitle)
            tvDeleteDescription.text = getString(R.string.delete_all_notes_desc_label)
            btnConfirmDelete.setOnClickListener {
                viewModel.deleteNote(note)
                deleteNoteDialog.dismiss()
                Snackbar.make(binding.root,getString(R.string.note_deleted_label),Snackbar.LENGTH_SHORT).show()
            }
            btnCancelDelete.setOnClickListener {
                deleteNoteDialog.dismiss()
                if (btnDeleteNote.isVisible) {
                    btnDeleteNote.visibility = View.GONE
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun viewClickListener() = NoteAdapter.ViewClickListener { note, _, view, itemBinding ->
        when (view) {
            itemBinding.btnFavorite -> {
                viewModel.setNoteFavorite(note)
            }
        }
    }
}