package com.developerkim.mytodo.ui.listnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developerkim.mytodo.R
import com.developerkim.mytodo.adapters.NoteAdapter
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.FragmentCategoryNotesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryNotes : Fragment() {
    private lateinit var binding: FragmentCategoryNotesBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val args: CategoryNotesArgs by navArgs()
    private lateinit var notesAdapter: NoteAdapter
    private lateinit var selectedCategory: NoteCategory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryNotesBinding.inflate(inflater)
        selectedCategory = args.category
        notesAdapter = NoteAdapter(
            noteClickListener(),
            viewClickListener(),
            requireContext())

        viewModel.getCategory(selectedCategory.categoryName)
        viewModel.noteCategory.observe(viewLifecycleOwner) { noteCategory ->
            notesAdapter.submitList(noteCategory.notes)
            binding.apply {
                setUpRecyclerView()
                setUpToolbar(selectedCategory)
                performNoteSearch()
                fabAddCategoryNote.setOnClickListener {
                    findNavController().navigate(
                        CategoryNotesDirections.actionCategoryNotesToNewNoteFragment(
                            selectedCategory.categoryName
                        )
                    )
                }
            }
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

    private fun FragmentCategoryNotesBinding.setUpToolbar(selectedCategory: NoteCategory) {
        categoryNotesToolbar.apply {
            title = selectedCategory.categoryName
            setupWithNavController(findNavController())
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.actionDeleteAllNotes -> {
                        Toast.makeText(
                            requireContext(),
                            "ToDo confirm delete",
                            Toast.LENGTH_SHORT
                        ).show()
                        showDeleteAllAlert()
                    }
                }
                false
            }
        }
    }

    private fun FragmentCategoryNotesBinding.performNoteSearch() {
        searchViewNotes.apply {
            isIconfiedByDefault
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    filterNotes(query!!)
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    filterNotes(query = newText!!)
                    return true
                }

            })
        }
    }

    private fun showDeleteAllAlert() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Are you sure you want all items?")
            .setPositiveButton("Delete") { dialog, _ ->
                Toast.makeText(
                    requireContext(),
                    "TODO: delete all Categories nd notes",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun noteClickListener() = NoteAdapter.NoteClickListener { note, position ->
        findNavController().navigate(
            CategoryNotesDirections.actionCategoryNotesToReadNotesFragment(
                note.noteCategory, note.noteTitle
            )
        )
    }

    private fun viewClickListener() = NoteAdapter.ViewClickListener { note, _, view, binding ->
        when (view) {
            binding.btnFavorite -> viewModel.setNoteFavorite(note)
        }
    }

    private fun filterNotes(query: String) {
        viewModel.noteCategory.observe(viewLifecycleOwner) { currentCategory ->
            val filteredNotes = viewModel.noteSearchFilter(currentCategory.notes!!, query)
            if (filteredNotes.isEmpty()) {
                Toast.makeText(requireContext(), "No match found", Toast.LENGTH_SHORT).show()
                notesAdapter.submitList(null)
            } else {
                notesAdapter.submitList(null)
                notesAdapter.submitList(filteredNotes.toMutableList())
            }

        }

    }
}