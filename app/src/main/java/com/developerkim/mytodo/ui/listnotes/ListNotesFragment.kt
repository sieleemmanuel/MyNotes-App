package com.developerkim.mytodo.ui.listnotes

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import com.developerkim.mytodo.MainActivity
import com.developerkim.mytodo.R
import com.developerkim.mytodo.adapters.CategoryItemLookUp
import com.developerkim.mytodo.adapters.CategoryKeyProvider
import com.developerkim.mytodo.adapters.NoteAdapter
import com.developerkim.mytodo.adapters.NotesCategoriesAdapter
import com.developerkim.mytodo.data.database.NoteDatabase
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.FragmentListNotesBinding
import com.developerkim.mytodo.util.ClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


const val IS_FOLDER_VIEW = "IS_FOLDER"
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ListNotesFragment : Fragment(), ClickListener, ActionMode.Callback {

    private lateinit var viewModel: ListNoteViewModel
    private lateinit var binding: FragmentListNotesBinding
    private lateinit var adapter: NotesCategoriesAdapter
    private lateinit var globalMenu: Menu
    private var layoutManager: GridLayoutManager? = null
    private var isFolder: Boolean? = null
    private var tracker: SelectionTracker<NoteCategory>? = null
    private var actionMode: ActionMode? = null
    private var selectedCategories: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = NotesCategoriesAdapter(this, requireContext())
        val application = requireActivity().application
        val database = NoteDatabase.getInstance(application)
        val viewModelFactory = ListNotesViewModelFactory(database)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ListNoteViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_notes, container, false)
        setHasOptionsMenu(true)

        isFolder = if (savedInstanceState != null) {
            tracker?.onRestoreInstanceState(savedInstanceState)
            savedInstanceState.getBoolean("IS_FOLDER",false)

        }else{
            false
        }

        binding.apply {
            changeViewType()
            layoutManager = GridLayoutManager(context, 1)
            rv.layoutManager = layoutManager
            rv.adapter = adapter

            btnAddNote.setOnClickListener {
                findNavController().navigate(
                    ListNotesFragmentDirections.actionListNotesFragmentToNewNoteFragment()
                )
            }

            notesCategories.setOnClickListener {
                isFolder = !adapter.toggleItemViewType()
                changeViewType()
            }

            recentNotes.setOnClickListener {
                listRecentNotes()
            }

            oldNotes.setOnClickListener {
                listOldNotes()
            }
        }

        tracker = SelectionTracker.Builder(
            "selection-1",
            binding.rv,
            CategoryKeyProvider(adapter),
            CategoryItemLookUp(binding.rv),
            StorageStrategy.createParcelableStorage(NoteCategory::class.java)
        )
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()


        tracker?.addObserver(object : SelectionTracker.SelectionObserver<NoteCategory>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                if (actionMode == null)
                    actionMode =
                        (activity as MainActivity).startSupportActionMode(this@ListNotesFragment)

                selectedCategories = tracker?.selection?.size()
                if (selectedCategories!! > 0)
                    actionMode?.title = "$selectedCategories/${adapter.currentList.size}"
                else
                    actionMode?.finish()
            }
        })
        adapter.setTracker(tracker!!)

        setAdapterData {
            Log.d("ListNotes", "onCreateView: called??? ")
            adapter.submitList(it)
        }

        return binding.root
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_FOLDER_VIEW, isFolder!!)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_bar_menu, menu)
        globalMenu = menu
        val searchItem = globalMenu.findItem(R.id.recycler_search)
        val searchView = searchItem.actionView as SearchView

        performSearch(searchView)
        toggleMenuItemsVisibility()
    }

    private fun toggleMenuItemsVisibility() {
        setAdapterData {
            if (viewModel.isPrivateNotesHidden(it)) {
                globalMenu.findItem(R.id.hide_private).isVisible = false
                globalMenu.findItem(R.id.show_all).isVisible = true
            } else {
                globalMenu.findItem(R.id.show_all).isVisible = false
                globalMenu.findItem(R.id.hide_private).isVisible = true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> {
                confirmDeleteAll()
            }

            R.id.hide_private -> {
                viewModel.hidePrivateCategories()

            }
            R.id.show_all -> {
                viewModel.showAllCategories()
            }
        }
        return true
    }

    override fun onClick(view: View, note: Note, position: Int, deleteNote: ImageButton) {
        when (view.id) {
            R.id.noteItem -> {
                if (NoteAdapter.isSelectedMode) {
                    view.background =  if (NoteAdapter.selectedNotes.contains(note)){
                        NoteAdapter.selectedNotes.remove(note)
                        deleteNote.isVisible = false
                        AppCompatResources.getDrawable(requireContext(), R.drawable.rounded_corners)
                    }else{
                        NoteAdapter.selectedNotes.add(note)
                        deleteNote.visibility = View.VISIBLE
                        AppCompatResources.getDrawable(requireContext(), R.drawable.rounded_corners_bg_gray)
                    }
                    if (NoteAdapter.selectedNotes.size == 0){
                        NoteAdapter.isSelectedMode = false
                    }
                }else{
                    this.findNavController().navigate(
                        ListNotesFragmentDirections.actionListNotesFragmentToReadNotesFragment(
                            position,
                            note.noteCategory,
                            note.noteTitle,
                            note.noteDate,
                            note.noteText,
                        )
                    )
                }

            }
            R.id.delete_note -> {
                confirmDeleteNote(note)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onLongClick(view: View, note: Note, deleteNote: ImageButton): Boolean {
        NoteAdapter.isSelectedMode = true
        view.background =  if (NoteAdapter.selectedNotes.contains(note)){
            NoteAdapter.selectedNotes.remove(note)
            deleteNote.isVisible = false
            AppCompatResources.getDrawable(requireContext(), R.drawable.rounded_corners)

        }else{
            NoteAdapter.selectedNotes.add(note)
            deleteNote.visibility = View.VISIBLE
            AppCompatResources.getDrawable(requireContext(), R.drawable.rounded_corners_bg_gray)

        }
        if (NoteAdapter.selectedNotes.size == 0){
            NoteAdapter.isSelectedMode = false
        }
        return true
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.menu_action_mode, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = true

    override fun onActionItemClicked(mode: ActionMode?, menuItem: MenuItem?): Boolean {
        return when (menuItem?.itemId) {
            R.id.deleteSelected -> {
                tracker!!.selection.forEach {
                    confirmDeleteCategory(it)
                }
                mode?.finish()
                true
            }
            R.id.action_mode_close_button -> {
                mode?.finish()
                tracker?.clearSelection()
                true
            }
            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        tracker?.clearSelection()
        actionMode = null
    }

    private fun setAdapterData(listHandler: (noteCategory: List<NoteCategory>) -> Unit) {
        viewModel.categoriesList.observe(viewLifecycleOwner, {
            listHandler.invoke(it!!)
        })
    }

    private fun changeViewType() {
        if (isFolder!!) {
            layoutManager?.spanCount = when (resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    4
                }
                else -> {
                    2
                }
            }
            binding.apply {
                notesCategories.text = getString(R.string.item_list_notes)
                recentNotes.isVisible = false
            }
        } else {
            layoutManager?.spanCount = 1
            binding.apply{
                notesCategories.text = getString(R.string.item_list_show_categories)
                recentNotes.isVisible = true
            }
        }
    }

    private fun performSearch(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterCategories(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCategories(newText)
                return true
            }
        })
    }

    private fun confirmDeleteAll() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete all categories?")
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("DELETE") { _, _ ->
                viewModel.clearAllCategories()
            }
            .show()
    }

    private fun confirmDeleteCategory(noteCategory: NoteCategory) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete category?")
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.cancel()
                tracker?.clearSelection()
            }
            .setPositiveButton("DELETE") { _, _ ->
                viewModel.clearCategories(noteCategory)
            }.setCancelable(false)
            .show()
    }

    private fun confirmDeleteNote(note: Note) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete the note?")
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("DELETE") { _, _ ->
                viewModel.updateNote(note)
                NoteAdapter.isSelectedMode = false
            }
            .show()
    }

    private fun filterCategories(newText: String?) {
       setAdapterData {noteCategories ->
           val filteredCategories = viewModel.noteCategoryFilter(noteCategories,newText)
            if (filteredCategories.isEmpty()) {
                Toast.makeText(requireContext(), "No match found", Toast.LENGTH_SHORT).show()
                adapter.submitList(null)
            } else {
                adapter.submitList(null)
                adapter.submitList(filteredCategories)
            }
        }

    }

    private fun listRecentNotes() {
        setAdapterData {
            val recent = viewModel.sortNotes(it)
            adapter.submitList(recent)
            binding.recentNotes.visibility = GONE
            binding.oldNotes.visibility = View.VISIBLE
        }
    }

    private fun listOldNotes() {
        setAdapterData {
            val recent = viewModel.sortOldNotes(it)
            adapter.submitList(recent)
            binding.recentNotes.visibility = View.VISIBLE
            binding.oldNotes.visibility = GONE
        }

    }

}
