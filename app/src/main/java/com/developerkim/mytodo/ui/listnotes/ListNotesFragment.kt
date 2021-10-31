package com.developerkim.mytodo.ui.listnotes

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.developerkim.mytodo.R
import com.developerkim.mytodo.database.NoteDatabase
import com.developerkim.mytodo.databinding.FragmentListNotesBinding
import com.developerkim.mytodo.model.Note
import com.developerkim.mytodo.model.NoteCategory
import com.developerkim.mytodo.util.ClickListener
import com.developerkim.mytodo.util.LongClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.recyclerview.widget.GridLayoutManager

import androidx.recyclerview.widget.LinearLayoutManager





class ListNotesFragment : Fragment(), ClickListener, LongClickListener {

    private lateinit var viewModel: ListNoteViewModel
    private lateinit var binding: FragmentListNotesBinding
    private lateinit var adapter: CategoryAdapter
    private lateinit var globalmenu:Menu
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list_notes,
            container,
            false
        )
        setHasOptionsMenu(true)

        /*getting the reference to application and database*/
        val application = requireActivity().application
        val database = NoteDatabase.getInstance(application).notesCategoriesDao


        val viewModelFactory = ListNotesViewModelFactory(database)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ListNoteViewModel::class.java)

//        reference to adapter with clickListener
        adapter = CategoryAdapter(this, this)
        binding.rv.adapter = adapter

//        Observing the changes on category list and update recycler adapter
        viewModel.privateHiddenCategories .observe(viewLifecycleOwner, {
            it?.let {
                adapter.noteCategories = it
            }
        })

        //navigating to add new note
        binding.btnAddNote.setOnClickListener {
            this.findNavController().navigate(
                ListNotesFragmentDirections.actionListNotesFragmentToNewNoteFragment()
            )
        }
        binding.notesCategories.setOnClickListener {
            val isFolder:Boolean = adapter.toggleItemViewType()
            if (!isFolder) {
                binding.rv.layoutManager = GridLayoutManager(
                    requireContext(),
                    2
                )
                binding.notesCategories.text = getString(R.string.item_list_notes)
            } else {
                binding.rv.layoutManager = LinearLayoutManager(requireContext())
                binding.notesCategories.text = getString(R.string.item_list_show_categories)
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_bar_menu, menu)
        globalmenu = menu
        menu.findItem(R.id.hide_private).isVisible = false
        requireActivity().invalidateOptionsMenu()

        /**
         * Search item functionality
         * **/
        /*val searchItem = globalmenu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
               return false
            }

        })*/


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> {
                confirmAlert()
            }

            R.id.hide_private -> {
                viewModel.privateHiddenCategories.observe(viewLifecycleOwner, {
                    it.let {
                        adapter.noteCategories = it
                    }
                })
                // hide Hide_private menu item and show show_all menu item
                item.isVisible = false
                globalmenu.findItem(R.id.show_all).isVisible = true

            }
            R.id.show_all -> {
                viewModel.categoriesList.observe(viewLifecycleOwner, {
                    it.let {
                        adapter.noteCategories = it
                    }
                })
                //show the item only if hide_private menu item is invisible
                item.isVisible = false
                globalmenu.findItem(R.id.hide_private).isVisible = true

            }
        }

        return true
    }

    private fun confirmAlert() {
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

    private fun confirmNoteAlert(note: Note, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete the note?")
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("DELETE") { dialog, _ ->
                viewModel.updateNote(note, position)
            }
            .show()
    }

    override fun onClick(view: View, note: Note, position: Int) {
        when (view.id) {
            R.id.noteItem -> {
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
            R.id.delete_note -> {
                confirmNoteAlert(note, position)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onLongClick(
        view: View,
        note: Note,
        position: Int,
        deleteNote: ImageButton
    ): Boolean {
        if (deleteNote.visibility != View.VISIBLE) {
            view.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corners_bg_gray)
            deleteNote.visibility = View.VISIBLE
        } else {
            view.background = null
            deleteNote.visibility = View.GONE
        }
        return true
    }

    override fun onCategoryLongClick(
        view: View,
        noteCategory: NoteCategory,
        position: Int,
        deleteNote: ImageButton
    ): Boolean {
        if (deleteNote.visibility != View.VISIBLE) {
            view.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corners_bg_gray)
            deleteNote.visibility = View.VISIBLE
        } else {
            view.background = null
            deleteNote.visibility = View.GONE
        }
        return super.onCategoryLongClick(view, noteCategory, position, deleteNote)
    }

    override fun onClickCategory(view: View, noteCategory: NoteCategory) {
        viewModel.clearCategories(noteCategory)
        Toast.makeText(view.context, "Category deleted Successfully", Toast.LENGTH_SHORT).show()
    }

}