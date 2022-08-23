package com.developerkim.mytodo.ui.listnotes

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.developerkim.mytodo.R
import com.developerkim.mytodo.adapters.CreateCategoryAdapter
import com.developerkim.mytodo.adapters.NotesCategoriesAdapter
import com.developerkim.mytodo.adapters.ViewPagerAdapter
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.AddCategoryDialogBinding
import com.developerkim.mytodo.databinding.DeleteLayoutBinding
import com.developerkim.mytodo.databinding.FragmentListNotesBinding
import com.developerkim.mytodo.interfaces.NoteSearchListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListNotesFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentListNotesBinding
    private lateinit var categoriesAdapter: NotesCategoriesAdapter
    private lateinit var createCategoriesAdapter: CreateCategoryAdapter
    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var deleteDialogBinding: DeleteLayoutBinding
    private lateinit var deleteDialogBuilder:MaterialAlertDialogBuilder
    private lateinit var confirmDeleteDialog:AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListNotesBinding.inflate(inflater)

        categoriesAdapter = NotesCategoriesAdapter(
            categoryClickListener(),
            categoryLongClickListener(),
            requireContext()
        )
        createCategoriesAdapter = CreateCategoryAdapter(newCategoryListener())
        concatAdapter = ConcatAdapter(createCategoriesAdapter, categoriesAdapter)
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle, requireContext())
        deleteDialogBinding = DeleteLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        deleteDialogBuilder = MaterialAlertDialogBuilder(requireContext())
                .setBackground(ColorDrawable(Color.TRANSPARENT))


        binding.apply {
            notesPager.adapter = viewPagerAdapter
            val tabsTitles = resources.getStringArray(R.array.notes_tabs)
            TabLayoutMediator(tabLayout, notesPager) { tab, position ->
                tab.text = tabsTitles[position]
            }.attach()
            setUpMainToolbar()
            setUpCategoriesRecycler()
            performSearch()
            setUpFabSpeedDial()
            setUpCategoriesHandler {
                categoriesAdapter.submitList(it)
                if (it!!.isNotEmpty()) {
                    notesPager.visibility = View.VISIBLE
                    pbLoadingCategories.visibility = View.GONE
                    tvEmptyCategories.visibility = View.GONE

                } else {
                    pbLoadingCategories.visibility = View.GONE
                    tvEmptyCategories.visibility = View.VISIBLE
                    notesPager.visibility = View.INVISIBLE
                }
            }
            viewModel.allNotes.observe(viewLifecycleOwner){ noteList ->
                if (noteList.isEmpty()){
                    pbLoadingAllNotes.visibility = View.GONE
                    tvEmptyNotes.visibility = View.VISIBLE
                }else{
                    pbLoadingAllNotes.visibility = View.GONE
                    tvEmptyNotes.visibility = View.GONE
                }
            }
        }

        return binding.root
    }

    private fun setUpCategoriesHandler(listHandler: (noteCategory: List<NoteCategory>?) -> Unit) {
        viewModel.categoriesList.observe(viewLifecycleOwner) {
            listHandler.invoke(it)
        }
    }

    private fun FragmentListNotesBinding.setUpFabSpeedDial() {
        fabSpeedDial.apply {
            inflate(R.menu.fab_menu)
            setOnActionSelectedListener { actionItem ->
                when (actionItem.id) {
                    R.id.actionNewCategeory -> {
                        showNewCategoryDialog(
                            viewModel,
                            viewLifecycleOwner,
                            null,
                            requireContext()
                        )

                        fabSpeedDial.close()
                        return@setOnActionSelectedListener true
                    }
                    R.id.actionNewNote -> {
                        findNavController().navigate(
                            ListNotesFragmentDirections.actionListNotesFragmentToNewNoteFragment(
                                null
                            )
                        )
                        fabSpeedDial.close()
                        return@setOnActionSelectedListener true
                    }
                }
                false
            }
        }
    }

    private fun FragmentListNotesBinding.setUpCategoriesRecycler() {
        rvCategories.apply {
            adapter = concatAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun FragmentListNotesBinding.setUpMainToolbar() {
        mainToolbar.setOnMenuItemClickListener { menuItem ->
            return@setOnMenuItemClickListener when (menuItem.itemId) {
                R.id.actionClearAll -> {
                    confirmClearAll()
                    true
                }
                else -> false
            }
        }
    }

    private fun FragmentListNotesBinding.performSearch() {
        searchView.apply {
            isIconfiedByDefault
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
    }

    private fun categoryClickListener() =
        NotesCategoriesAdapter.CategoryClickListener { category ->
            findNavController().navigate(
                ListNotesFragmentDirections.actionListNotesFragmentToCategoryNotes(category)
            )
        }

    private fun categoryLongClickListener() =
        NotesCategoriesAdapter.CategoryLongClickListener { category , btnDeleteCategory->
          if (!btnDeleteCategory.isVisible){
              btnDeleteCategory.visibility = View.VISIBLE
          }else{
              btnDeleteCategory.visibility = View.GONE
          }
            btnDeleteCategory.setOnClickListener {
                confirmDeleteCategory(category, btnDeleteCategory)
            }
        }

    private fun newCategoryListener() = CreateCategoryAdapter.NewCategoryListener {
        showNewCategoryDialog(viewModel, viewLifecycleOwner, null, requireContext())
    }

    private fun confirmDeleteCategory(category: NoteCategory, btnDelete:ImageView) {
        confirmDeleteDialog = deleteDialogBuilder
            .setView(deleteDialogBinding.root)
            .show()
        deleteDialogBinding.apply {
            tvDeleteCategoryLabel.text = getString(R.string.delete_category_dialog_label, category.categoryName)
            tvDeleteDescription.text = getString(R.string.delete_all_notes_desc_label)
            btnConfirmDelete.setOnClickListener {
                viewModel.removeCategory(categoryName = category.categoryName)
                confirmDeleteDialog.dismiss()
            }
            btnCancelDelete.setOnClickListener {
                confirmDeleteDialog.dismiss()
                if (btnDelete.isVisible) {
                    btnDelete.visibility = View.GONE
                }
            }
        }

    }

    private fun confirmClearAll() {
        confirmDeleteDialog = deleteDialogBuilder
            .setView(deleteDialogBinding.root)
            .show()
        deleteDialogBinding.apply {
            tvDeleteCategoryLabel.text = getString(R.string.delete_all_notes_label)
            tvDeleteDescription.text = getString(R.string.remove_all_notes_label)
            btnConfirmDelete.setOnClickListener {
                viewModel.clearAllCategories()
                confirmDeleteDialog.dismiss()
            }
            btnCancelDelete.setOnClickListener {
                confirmDeleteDialog.dismiss()
            }
        }
    }

    private fun filterCategories(newText: String?) {
        setUpCategoriesHandler { noteCategories ->
            val filteredCategories =
                viewModel.categoryAndNoteSearchFilter(noteCategories!!, newText)

            if (filteredCategories.isEmpty()) {
                Toast.makeText(requireContext(), "No match found", Toast.LENGTH_SHORT).show()
                categoriesAdapter.submitList(null)

            } else {
                categoriesAdapter.submitList(null)
                categoriesAdapter.submitList(filteredCategories)
            }
            if (newText.isNullOrEmpty()){
                viewModel.getAllNotes()
            }

        }
    }

    companion object {
        private val TAG = ListNotesFragment::class.simpleName
        fun showNewCategoryDialog(
            viewModel: MainViewModel,
            viewLifecycleOwner: LifecycleOwner,
            autoCompleteTextView: AutoCompleteTextView?,
            context: Context
        ) {
            val addCategoryDialogBinding = AddCategoryDialogBinding.inflate(
                LayoutInflater.from(context),
                null,
                false
            )

            val createCategoryDialog = BottomSheetDialog(context)
            createCategoryDialog.apply {
                setContentView(addCategoryDialogBinding.root)
                setTitle(context.getString(R.string.add_category_label))
            }
            createCategoryDialog.show()

            val categoryName = addCategoryDialogBinding.tiNewCategoryName.text
            addCategoryDialogBinding.coloSelector.setOnColorSelectedListener { color ->
                viewModel.getPickedColor(color)
                addCategoryDialogBinding.coloSelector.isSelected = true
            }
            addCategoryDialogBinding.btnAddCategory.setOnClickListener {
                if (categoryName!!.isNotEmpty()) {
                    viewModel.pickedColor.observe(viewLifecycleOwner) { color ->
                        if (color != null) {
                            if (addCategoryDialogBinding.coloSelector.isSelected) {
                                val newCategory = NoteCategory(
                                    categoryName = categoryName.toString(),
                                    categoryColor = color,
                                    notes = mutableListOf()
                                )
                                viewModel.insertNewCategory(newCategory)
                                autoCompleteTextView?.setText(categoryName, false)
                                viewModel.getCategory(newCategory.categoryName)
                                addCategoryDialogBinding.coloSelector.isSelected = false
                                createCategoryDialog.dismiss()
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.choose_color_label),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    addCategoryDialogBinding.tiNewCategoryName.error =
                        context.getString(R.string.error_category_empty)
                }

            }

        }
    }

}
