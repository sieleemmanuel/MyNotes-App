package com.developerkim.mytodo.ui.listnotes

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developerkim.mytodo.R
import com.developerkim.mytodo.adapters.NotesCategoriesAdapter
import com.developerkim.mytodo.adapters.ViewPagerAdapter
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.AddCategoryDialogBinding
import com.developerkim.mytodo.databinding.FragmentListNotesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@AndroidEntryPoint
class ListNotesFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentListNotesBinding
    private lateinit var categoriesAdapter: NotesCategoriesAdapter
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListNotesBinding.inflate(inflater)
        categoriesAdapter = NotesCategoriesAdapter(categoryClickListener(), requireContext())
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle, requireContext())
        binding.apply {
            setUpMainToolbar()
            setUpCategoriesRecycler()
            notesPager.adapter = viewPagerAdapter
            val tabsTitles = resources.getStringArray(R.array.notes_tabs)
            TabLayoutMediator(tabLayout, notesPager) { tab, position ->
                tab.text = tabsTitles[position]
            }.attach()

            performSearch(searchView)
            setUpFabSpeedDial()
            setAdapterData {
                if (!it.isNullOrEmpty()) {
                    categoriesAdapter.submitList(it)
                    rvCategories.visibility = View.VISIBLE
                    tvEmptyCategories.visibility = View.GONE
                    tabLayout.visibility = View.VISIBLE
                    notesPager.visibility = View.VISIBLE
                    pbLoadingCategories.visibility = View.INVISIBLE

                } else {
                    rvCategories.visibility = View.INVISIBLE
                    tvEmptyCategories.visibility = View.VISIBLE
                    tabLayout.visibility = View.INVISIBLE
                    notesPager.visibility = View.INVISIBLE
                }
                if (categoriesAdapter.currentList.isEmpty()) {
                    pbLoadingCategories.visibility = View.VISIBLE
                } else {
                    pbLoadingCategories.visibility = View.INVISIBLE
                }
            }
        }
        return binding.root
    }

    private fun FragmentListNotesBinding.setUpFabSpeedDial() {
        fabSpeedDial.apply {
            inflate(R.menu.fab_menu)
            setOnActionSelectedListener { actionItem ->
                when (actionItem.id) {
                    R.id.actionNewCategeory -> {
                        showNewCategoryDialog()
                        return@setOnActionSelectedListener true
                    }
                    R.id.actionNewNote -> {
                        findNavController().navigate(
                            ListNotesFragmentDirections.actionListNotesFragmentToNewNoteFragment()
                        )
                        return@setOnActionSelectedListener true
                    }
                }
                false
            }
        }
    }

    private fun FragmentListNotesBinding.setUpCategoriesRecycler() {
        rvCategories.apply {
            adapter = categoriesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun FragmentListNotesBinding.setUpMainToolbar() {
        mainToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionDeleteAll -> {
                    confirmDeleteAll()
                    Toast.makeText(requireContext(), "delete all", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun showNewCategoryDialog() {
        val addCategoryDialogBinding = AddCategoryDialogBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        )
        val categoryName = addCategoryDialogBinding.tiNewCategoryName.text
        addCategoryDialogBinding.csCategoryColor.setListener { _, color ->
            viewModel.getPickedColor(color)
            Toast.makeText(
                requireContext(),
                "Name:$categoryName, Color:$color",
                Toast.LENGTH_SHORT
            ).show()
        }

        val categoryDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(addCategoryDialogBinding.root)
            .setMessage("Create Note Category")
            .setCancelable(false)
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("ADD") { _, _ ->
            }
            .show()
        categoryDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (categoryName!!.isNotEmpty() && addCategoryDialogBinding.csCategoryColor.selectedColor != 1) {
                viewModel.pickedColor.observe(viewLifecycleOwner) { color ->
                    val newCategory = NoteCategory(
                        categoryName = categoryName.toString(),
                        categoryColor = color,
                        notes = mutableListOf()
                    )
                    viewModel.insertNewCategory(newCategory)
                    categoryDialog.dismiss()
                }
            } else {
                addCategoryDialogBinding.tiNewCategoryName.error =
                    getString(R.string.error_category_empty)
            }
        }
    }

    private fun categoryClickListener() =
        NotesCategoriesAdapter.CategoryClickListener { category ->
            findNavController().navigate(
                ListNotesFragmentDirections.actionListNotesFragmentToCategoryNotes(category)
            )
        }

    private fun setAdapterData(listHandler: (noteCategory: List<NoteCategory>?) -> Unit) {
        viewModel.categoriesList.observe(viewLifecycleOwner) {
            listHandler.invoke(it)
        }
    }

    private fun performSearch(searchView: SearchView) {
        searchView.isIconfiedByDefault
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
            .setMessage("Are you sure you want to delete all Notes?")
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("DELETE") { _, _ ->
                viewModel.clearAllCategories()
            }
            .show()
    }

    private fun confirmDeleteNote(note: Note) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete the note?")
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("DELETE") { _, _ ->
                viewModel.deleteNote(note)
            }
            .show()
    }

    private fun filterCategories(newText: String?) {
        setAdapterData { noteCategories ->
            val filteredCategories = viewModel.noteCategoryFilter(noteCategories!!, newText)
            if (filteredCategories.isEmpty()) {
                Toast.makeText(requireContext(), "No match found", Toast.LENGTH_SHORT).show()
                categoriesAdapter.submitList(null)
            } else {
                categoriesAdapter.submitList(null)
                categoriesAdapter.submitList(filteredCategories)
            }
        }
    }

}
