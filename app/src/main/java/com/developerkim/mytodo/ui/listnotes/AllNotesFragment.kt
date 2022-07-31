package com.developerkim.mytodo.ui.listnotes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developerkim.mytodo.R
import com.developerkim.mytodo.adapters.NoteAdapter
import com.developerkim.mytodo.data.model.Note
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

            viewModel.allNotes.observe(viewLifecycleOwner) {
                noteAdapter = when {
                   allNotes == getString(R.string.all_note_args_key)-> {
                        NoteAdapter(
                            it,
                            noteClickListener(),
                            viewClickListener(),
                            requireContext()
                        )
                    }
                    reminders == getString(R.string.reminders_args_key) ->{
                        Log.d(TAG, "reminders:${viewModel.getReminderNotes(it)}")
                        NoteAdapter(
                            viewModel.getReminderNotes(it) as MutableList<Note>,
                            noteClickListener(),
                            viewClickListener(),
                            requireContext()
                        )
                    }
                    favorites == getString(R.string.favorites_args_key) ->{
                        NoteAdapter(
                            viewModel.getFavouriteNotes(it) as MutableList<Note>,
                            noteClickListener(),
                            viewClickListener(),
                            requireContext()
                        )
                    }

                    else-> {
                        NoteAdapter(
                            it,
                            noteClickListener(),
                            viewClickListener(),
                            requireContext()
                        )
                    }
                }
                tabsRecyclerview.adapter = noteAdapter
                if (noteAdapter.itemCount==0){
                    pbLoadingNotes.visibility = View.VISIBLE
                }else{
                    pbLoadingNotes.visibility = View.INVISIBLE
                }
                if (it!!.isNotEmpty()) {
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

    private fun noteClickListener() = NoteAdapter.NoteClickListener { note, position ->
        findNavController().navigate(
            ListNotesFragmentDirections.actionListNotesFragmentToReadNotesFragment(
                note, position
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