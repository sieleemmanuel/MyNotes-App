package com.developerkim.mytodo.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.CategoryItemBinding
import com.developerkim.mytodo.interfaces.ClickListener

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NotesCategoriesAdapter(
    private val categoryClickListener: CategoryClickListener,
    val context: Context
) :
    ListAdapter<NoteCategory,NotesCategoriesAdapter.CategoryFolderHolder>(DiffItemCallback()){
    /*init {
        setHasStableIds(true)
    }*/

    inner class CategoryFolderHolder(
        private val binding: CategoryItemBinding
    ): RecyclerView.ViewHolder(binding.root){

        fun folderBinder(category: NoteCategory) {
            binding.apply {
                val folderBackground = root.background as GradientDrawable
                noteCategoryName.text = category.categoryName
                notesCount.text = category.notes?.size.toString()
                folderBackground.color = ColorStateList.valueOf(category.categoryColor!!)

                root.setOnClickListener {
                    categoryClickListener.onCategoryClicked(category)

                    Toast.makeText(context, "category color ${category.categoryColor}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryFolderHolder {
        val binding =  CategoryItemBinding.inflate(
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

    override fun getItemId(position: Int): Long = position.toLong()

    /*fun getItemAtPosition(position: Int): NoteCategory? = currentList[position]

    fun getPosition(categoryName: String): Int = currentList.indexOfFirst { it.categoryName == categoryName }
*/
    class DiffItemCallback :DiffUtil.ItemCallback<NoteCategory>(){
        override fun areItemsTheSame(oldItem: NoteCategory, newItem: NoteCategory): Boolean {
            return oldItem.categoryName == newItem.categoryName
        }

        override fun areContentsTheSame(oldItem: NoteCategory, newItem: NoteCategory): Boolean {
           return oldItem == newItem
        }
    }
    class CategoryClickListener(val categoryClickListener:(category:NoteCategory)->Unit){
        fun onCategoryClicked(category: NoteCategory) = categoryClickListener(category)
    }
}

