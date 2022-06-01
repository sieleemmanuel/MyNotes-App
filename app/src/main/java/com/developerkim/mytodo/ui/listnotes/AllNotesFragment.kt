package com.developerkim.mytodo.ui.listnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developerkim.mytodo.adapters.NoteAdapter
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.FragmentAllNotesBinding
import com.developerkim.mytodo.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllNotesFragment : Fragment() {
    private lateinit var binding: FragmentAllNotesBinding
    private lateinit var noteAdapter: NoteAdapter
    private val viewModel: ListNoteViewModel by activityViewModels()
    private val TAG = "AllNotes"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).onBackPressed()
        binding = FragmentAllNotesBinding.inflate(inflater)
        binding.apply {
            tabsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.categoriesList.observe(viewLifecycleOwner) {
            if (it!=null) {
                binding.tabsRecyclerview.visibility = View.VISIBLE
                binding.tvEmptyNotes.visibility = View.GONE
                val allNotes = mutableListOf<Note>()
                it.forEach { noteCategory ->
                    noteCategory.notes?.let { it1 -> allNotes.addAll(it1) }
                }
                noteAdapter = NoteAdapter(
                    allNotes,
                    noteClickListener(),
                    viewClickListener(),
                    requireContext()
                )
                binding.tabsRecyclerview.adapter = noteAdapter
            }else{
                binding.tabsRecyclerview.visibility = View.GONE
                binding.tvEmptyNotes.visibility = View.VISIBLE
            }
        }
        return binding.root
    }

    private fun noteClickListener() = NoteAdapter.NoteClickListener { note ->
        findNavController().navigate(
            AllNotesFragmentDirections.actionAllNotesFragmentToReadNotesFragment(
                note
            )
        )
    }

    private fun viewClickListener() = NoteAdapter.ViewClickListener { note, _, binding ->
        when(view){
            binding.btnFavorite ->{
                if (note.isFavorite){
                    val updatedNote = Note(
                        note.noteCategory,
                        note.noteTitle,
                        note.noteText,
                        note.noteDate,
                        isFavorite = false,
                        note.priority
                    )
                    viewModel.updateNote(updatedNote)
                }
            }
        }
    }

    /*override fun onClick(view: View, note: Note, position: Int, deleteNote: ImageButton) {
        findNavController().navigate(AllNotesFragmentDirections.actionAllNotesFragmentToReadNotesFragment(note))
    }*/
}