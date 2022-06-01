package com.developerkim.mytodo.ui.listnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.developerkim.mytodo.adapters.NoteAdapter
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.FragmentCategoryNotesBinding
import com.developerkim.mytodo.interfaces.ClickListener
import com.developerkim.mytodo.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryNotes : Fragment() {
    private lateinit var binding: FragmentCategoryNotesBinding
    private val viewModel: ListNoteViewModel by activityViewModels()
    private val args: CategoryNotesArgs by navArgs()
    private lateinit var notesAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ( activity as MainActivity).onBackPressed()
        binding = FragmentCategoryNotesBinding.inflate(inflater)

        val categoryNotes = args.noteList

        notesAdapter = NoteAdapter(categoryNotes.toMutableList(),
            noteClickListener(),viewClickListener(), requireContext())

        binding.apply {
            rvCategoryNotes.layoutManager = LinearLayoutManager(context!!)
            rvCategoryNotes.adapter = notesAdapter
        }

        return binding.root
    }

    private fun noteClickListener() = NoteAdapter.NoteClickListener { note ->
        findNavController().navigate(
            CategoryNotesDirections.actionCategoryNotesToReadNotesFragment(
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
}