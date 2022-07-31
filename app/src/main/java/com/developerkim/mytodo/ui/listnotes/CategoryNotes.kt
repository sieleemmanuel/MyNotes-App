package com.developerkim.mytodo.ui.listnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developerkim.mytodo.R
import com.developerkim.mytodo.adapters.NoteAdapter
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.FragmentCategoryNotesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryNotes : Fragment() {
    private lateinit var binding: FragmentCategoryNotesBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val args: CategoryNotesArgs by navArgs()
    private lateinit var notesAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryNotesBinding.inflate(inflater)
        val categoryNotes = args.category

        notesAdapter = NoteAdapter(
            categoryNotes.notes!!,
            noteClickListener(), viewClickListener(), requireContext()
        )
        binding.apply {
            setUpRecyclerView()
            setUpToolbar(categoryNotes)
        }
        return binding.root
    }

    private fun FragmentCategoryNotesBinding.setUpRecyclerView() {
        rvCategoryNotes.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = notesAdapter
        }
    }

    private fun FragmentCategoryNotesBinding.setUpToolbar(
        categoryNotes: NoteCategory
    ) {
        categoryNotesToolbar.apply {
            title = categoryNotes.categoryName
            setupWithNavController(findNavController())
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.actionDeleteAll -> {
                        Toast.makeText(
                            requireContext(),
                            "ToDo confirm delete",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                false
            }
        }
    }

    private fun noteClickListener() = NoteAdapter.NoteClickListener { note, position ->
        findNavController().navigate(
            CategoryNotesDirections.actionCategoryNotesToReadNotesFragment(
                note,position
            )
        )
    }

    private fun viewClickListener() = NoteAdapter.ViewClickListener { note, _, view, binding ->
        when (view) {
            binding.btnFavorite -> viewModel.setNoteFavorite(note)

        }
    }
}