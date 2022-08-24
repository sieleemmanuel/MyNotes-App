package com.developerkim.mytodo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.CategoryItemBinding


class NotesCategoriesAdapter(
    private val categoryClickListener: CategoryClickListener,
    private val categoryLongClickListener: CategoryLongClickListener,
    val context: Context
) :
    ListAdapter<NoteCategory, NotesCategoriesAdapter.CategoryFolderHolder>(DiffItemCallback()) {

    inner class CategoryFolderHolder(
        private val binding: CategoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun folderBinder(category: NoteCategory) {
            binding.apply {
                noteCategoryName.text = category.categoryName
                notesCount.text = category.notes?.size.toString()
                btnDeleteCategory.setOnLongClickListener {
                    if (it.isVisible)
                    it.visibility = View.GONE
                    true
                }
                root.apply {
                    setCardBackgroundColor(category.categoryColor!!)
                    setOnClickListener {
                        categoryClickListener.onCategoryClicked(category)
                    }
                    setOnLongClickListener {
                        categoryLongClickListener.onCategoryLongClicked(category, btnDeleteCategory)
                        true
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryFolderHolder {
        val binding = CategoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryFolderHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryFolderHolder, position: Int) {
        val category = getItem(position)
        holder.folderBinder(category)
    }

    class DiffItemCallback : DiffUtil.ItemCallback<NoteCategory>() {
        override fun areItemsTheSame(oldItem: NoteCategory, newItem: NoteCategory): Boolean {
            return oldItem.categoryName == newItem.categoryName
        }

        override fun areContentsTheSame(oldItem: NoteCategory, newItem: NoteCategory): Boolean {
            return oldItem == newItem
        }
    }

    class CategoryClickListener(val categoryClickListener: (category: NoteCategory) -> Unit) {
        fun onCategoryClicked(category: NoteCategory) = categoryClickListener(category)
    }
    class CategoryLongClickListener(val categoryLongClickListener: (category: NoteCategory, deleteBtn:ImageView) -> Unit) {
        fun onCategoryLongClicked(category: NoteCategory,deleteBtn:ImageView ) = categoryLongClickListener(category, deleteBtn)
    }

}

