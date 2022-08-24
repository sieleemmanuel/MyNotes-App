package com.developerkim.mytodo.ui.listnotes

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
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
import com.developerkim.mytodo.databinding.DeleteLayoutBinding
import com.developerkim.mytodo.databinding.FragmentCategoryNotesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryNotes : Fragment() {
    private lateinit var binding: FragmentCategoryNotesBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val args: CategoryNotesArgs by navArgs()
    private lateinit var notesAdapter: NoteAdapter
    private lateinit var selectedCategory: NoteCategory
    private lateinit var deleteDialogBinding: DeleteLayoutBinding
    private lateinit var deleteDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var confirmDeleteDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryNotesBinding.inflate(inflater)
        selectedCategory = args.category
        notesAdapter = NoteAdapter(
            noteClickListener(),
            noteLongClickListener(),
            viewClickListener()
        )

        deleteDialogBinding = DeleteLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        deleteDialogBuilder =
            MaterialAlertDialogBuilder(requireContext())
                .setBackground(ColorDrawable(Color.TRANSPARENT))

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.noteCategory.observe(viewLifecycleOwner) { noteCategory ->
            binding.apply {
                if (noteCategory.notes!!.isNotEmpty()) {
                    tvEmptyNotes.visibility = View.GONE
                    pbLoadingNotes.visibility = View.GONE
                } else {
                    tvEmptyNotes.visibility = View.VISIBLE
                    pbLoadingNotes.visibility = View.GONE
                }
            }
        }
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
                        showRemoveAllNotesAlert()
                    }
                    R.id.actionDeleteCategory -> {
                        showRemoveCurrentCategoryAlert()
                        viewModel.removeCategory(selectedCategory.categoryName)
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

    private fun showRemoveAllNotesAlert() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Are you sure you want remove current category notes?")
            .setPositiveButton("YES") { _, _ ->
                viewModel.deleteAllCategoryNotes(selectedCategory.categoryName)
                Toast.makeText(
                    requireContext(),
                    "Notes deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun noteClickListener() = NoteAdapter.NoteClickListener { note, _ ->
        findNavController().navigate(
            CategoryNotesDirections.actionCategoryNotesToReadNotesFragment(
                note.noteCategory, note.noteTitle
            )
        )
    }

    private fun noteLongClickListener() =
        NoteAdapter.NoteLongClickListener { note, btnDeleteNote ->
            if (!btnDeleteNote.isVisible) {
                btnDeleteNote.visibility = View.VISIBLE
            } else {
                btnDeleteNote.visibility = View.GONE
            }
            btnDeleteNote.setOnClickListener {
                confirmDeleteNote(note, btnDeleteNote)
            }
        }

    private fun viewClickListener() = NoteAdapter.ViewClickListener { note, _, view, binding ->
        when (view) {
            binding.btnFavorite -> viewModel.setNoteFavorite(note)
        }
    }

    private fun showRemoveCurrentCategoryAlert() {
        confirmDeleteDialog = deleteDialogBuilder
            .setView(deleteDialogBinding.root)
            .show()
        deleteDialogBinding.apply {
            tvDeleteCategoryLabel.text =
                getString(R.string.delete_category_dialog_label, selectedCategory.categoryName)
            tvDeleteDescription.text = getString(R.string.delete_all_notes_desc_label)
            btnConfirmDelete.setOnClickListener {
                viewModel.removeCategory(selectedCategory.categoryName)
                confirmDeleteDialog.dismiss()
                Snackbar.make(
                    binding.root,
                    getString(R.string.note_deleted_label, selectedCategory.categoryName),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            btnCancelDelete.setOnClickListener {
                confirmDeleteDialog.dismiss()
            }
        }


    }

    private fun confirmDeleteNote(note: Note, btnDeleteNote: ImageView) {
        confirmDeleteDialog = deleteDialogBuilder
            .setView(deleteDialogBinding.root)
            .show()
        deleteDialogBinding.apply {
            tvDeleteCategoryLabel.text =
                getString(R.string.delete_note_dialog_label, note.noteTitle)
            tvDeleteDescription.text = getString(R.string.delete_all_notes_desc_label)
            btnConfirmDelete.setOnClickListener {
                viewModel.deleteNote(note)
                confirmDeleteDialog.dismiss()
                Snackbar.make(
                    binding.root,
                    getString(R.string.note_deleted_label, note.noteTitle),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            btnCancelDelete.setOnClickListener {
                confirmDeleteDialog.dismiss()
                if (btnDeleteNote.isVisible) {
                    btnDeleteNote.visibility = View.GONE
                }
            }
        }
    }


    private fun filterNotes(query: String) {
        viewModel.noteCategory.observe(viewLifecycleOwner) { currentCategory ->
            val filteredNotes = viewModel.searchNotes(query, currentCategory.notes!!)
            if (filteredNotes.isEmpty()) {
                Toast.makeText(requireContext(), "No match found", Toast.LENGTH_SHORT).show()
                notesAdapter.submitList(null)
            } else {
                notesAdapter.submitList(null)
                notesAdapter.submitList(filteredNotes)
            }

        }

    }
}