package com.developerkim.mytodo.ui.listnotes

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.database.NoteDatabase
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.FragmentListNotesBinding
import com.developerkim.mytodo.util.ClickListener
import com.developerkim.mytodo.util.LongClickListener
import com.developerkim.mytodo.util.RecentNotesListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


class ListNotesFragment : Fragment(), ClickListener, LongClickListener {

    private lateinit var viewModel: ListNoteViewModel
    private lateinit var binding: FragmentListNotesBinding
    private lateinit var adapter: CategoryAdapter
    private lateinit var globalmenu: Menu
    private var spanCount: Int = 2
    private var isFolder:Boolean=false

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
        val database = NoteDatabase.getInstance(application)

        val viewModelFactory = ListNotesViewModelFactory(database)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ListNoteViewModel::class.java)

        adapter = CategoryAdapter(this, this)
        // reference to adapter with clickListener

            binding.rv.layoutManager = LinearLayoutManager(context)
            binding.rv.adapter = adapter
            // Observing the changes on category list and update recycler adapter
            viewModel.privateHiddenCategories.observe(viewLifecycleOwner, {
                it?.let {
                    adapter.submitList(it)
                }
            })

        //navigating to add new note
        binding.btnAddNote.setOnClickListener {
            this.findNavController().navigate(
                ListNotesFragmentDirections.actionListNotesFragmentToNewNoteFragment()
            )
        }
        binding.notesCategories.setOnClickListener {
             isFolder = !adapter.toggleItemViewType()
            if (isFolder) {
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    spanCount = 4
                    binding.rv.layoutManager = GridLayoutManager(
                        requireContext(),
                        spanCount
                    )
                } else {
                    binding.rv.layoutManager = GridLayoutManager(
                        requireContext(),
                        spanCount
                    )
                }
                binding.notesCategories.text = getString(R.string.item_list_notes)
            } else {
                binding.rv.layoutManager = LinearLayoutManager(requireContext())
                binding.notesCategories.text = getString(R.string.item_list_show_categories)
            }
        }
        binding.recentNotes.setOnClickListener {
            listRecentNotes()
        }
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
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
        val searchItem = globalmenu.findItem(R.id.recycler_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return false
            }
        })
    }

    private fun filter(newText: String?) {
        viewModel.categoriesList.observe(viewLifecycleOwner, {
            val filteredList: MutableList<NoteCategory> = mutableListOf()
            for (category in it!!) {
                for (note in category.notes!!) {
                    // checking if the entered string matched with any item of our recycler view.
                    if (category.categoryName.lowercase(Locale.getDefault())
                            .contains(newText!!.lowercase(Locale.getDefault())) ||
                        note.noteTitle.lowercase(Locale.getDefault())
                            .contains(newText.lowercase(Locale.getDefault()))
                    ) {
                        filteredList.add(category)

                    }
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(requireContext(), "No match found", Toast.LENGTH_SHORT).show()
                adapter.submitList(filteredList.distinct())
            } else {
                adapter.submitList(filteredList.distinct())
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> {
                confirmAlert()
            }

            R.id.hide_private -> {
                viewModel.privateHiddenCategories.observe(viewLifecycleOwner, {
                    it.let {
                        adapter.submitList(it)
                    }
                })
                // hide Hide_private menu item and show show_all menu item
                item.isVisible = false
                globalmenu.findItem(R.id.show_all).isVisible = true

            }
            R.id.show_all -> {
                viewModel.categoriesList.observe(viewLifecycleOwner, {
                    it.let {
                        adapter.submitList (it)
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
            .setPositiveButton("DELETE") { _, _ ->
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
            view.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corners)
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
            view.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corners)

            deleteNote.visibility = View.GONE
        }
        return super.onCategoryLongClick(view, noteCategory, position, deleteNote)
    }

    override fun onClickCategory(view: View, noteCategory: NoteCategory) {
        viewModel.clearCategories(noteCategory)
        Toast.makeText(view.context, "Category deleted Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun listRecentNotes() {

        viewModel.categoriesList.observe(viewLifecycleOwner, {
            val recentCategoryNotes: MutableList<NoteCategory> = mutableListOf()
            for (category in it) {
                val noteList = category.notes
                noteList?.reversed()
                recentCategoryNotes.add(category)
                adapter.submitList(recentCategoryNotes)
            }
        })

    }


}