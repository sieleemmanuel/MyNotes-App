package com.developerkim.mytodo.ui.listnotes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developerkim.mytodo.R
import com.developerkim.mytodo.adapters.NoteAdapter
import com.developerkim.mytodo.databinding.FragmentAllNotesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllNotesFragment : Fragment() {
    private lateinit var binding: FragmentAllNotesBinding
    private lateinit var noteAdapter: NoteAdapter
    private val viewModel: MainViewModel by activityViewModels()
    private val TAG = "AllNotes"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllNotesBinding.inflate(inflater)
        val arg = arguments
        val allNotes = arg?.getString(getString(R.string.all_note_args_key))
        val reminders = arg?.getString(getString(R.string.reminders_args_key))
        val favorites = arg?.getString(getString(R.string.favorites_args_key))
        binding.apply {
            tabsRecyclerview.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            noteAdapter = NoteAdapter(
                noteClickListener(),
                viewClickListener(),
                requireContext()
            )

            viewModel.allNotes.observe(viewLifecycleOwner) {
                 when {
                   allNotes == getString(R.string.all_note_args_key)-> {
                        noteAdapter.submitList(it)
                    }
                    reminders == getString(R.string.reminders_args_key) ->{
                        Log.d(TAG, "reminders:${viewModel.getReminderNotes(it)}")
                        noteAdapter.submitList(viewModel.getReminderNotes(it).toMutableList())
                    }
                    favorites == getString(R.string.favorites_args_key) ->{
                        noteAdapter.submitList(viewModel.getFavouriteNotes(it).toMutableList())
                    }
                }
                tabsRecyclerview.adapter = noteAdapter
                if (it!!.isNotEmpty()) {
                    pbLoadingNotes.visibility = View.INVISIBLE
                    tabsRecyclerview.visibility = View.VISIBLE
                    tvEmptyNotes.visibility = View.INVISIBLE
                } else {
                    tabsRecyclerview.visibility = View.INVISIBLE
                    tvEmptyNotes.visibility = View.VISIBLE
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

    private fun viewClickListener() = NoteAdapter.ViewClickListener { note, _, view, binding ->
        when (view) {
            binding.btnFavorite -> {
                viewModel.setNoteFavorite(note)
            }
        }
    }

}