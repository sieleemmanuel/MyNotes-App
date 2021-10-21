package com.developerkim.mytodo.ui.listnotes

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.developerkim.mytodo.R
import com.developerkim.mytodo.database.NoteDatabase
import com.developerkim.mytodo.databinding.FragmentListNotesBinding
import com.developerkim.mytodo.model.Note
import com.developerkim.mytodo.util.ClickListener
import com.developerkim.mytodo.util.LongClickListener


class ListNotesFragment : Fragment(), ClickListener, LongClickListener {

    private lateinit var viewModel:ListNoteViewModel
    private lateinit var binding: FragmentListNotesBinding
    private lateinit var adapter: CategoryAdapter
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
        viewModel = ViewModelProvider(this,viewModelFactory).get(ListNoteViewModel::class.java)

//        reference to adapter with clickListener
        adapter = CategoryAdapter(this,this)
        binding.rv.adapter = adapter

//        Observing the changes on category list and update recycler adapter
        viewModel.categoriesList.observe(viewLifecycleOwner,{
            it?.let {
                adapter.noteCategories = it
            }
        })

        //navigating to add new note
       binding.btnAddNote.setOnClickListener {
                this.findNavController().navigate(
                    ListNotesFragmentDirections.actionListNotesFragmentToNewNoteFragment()
                )}

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_bar_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_all->viewModel.clearAllCategories()
            R.id.hide_private ->{
                viewModel.filteredList()
                viewModel.filteredList.observe(viewLifecycleOwner,{
                    it.let {
                        adapter.noteCategories = it
                    }

                })
            }
        }

        return true
    }

    override fun onClick(view: View, note: Note, position: Int) {
        when(view.id){
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
            R.id.delete_note ->{
                viewModel.updateNote(note,position)
            }
        }
    }

    override fun onLongClick(view: View, note: Note, position: Int, deleteNote: ImageButton): Boolean {
        view.background = resources.getDrawable(R.drawable.rounded_corners_bg_gray)
        deleteNote.visibility = View.VISIBLE
        return true
    }

}