package com.developerkim.mytodo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.ListCategoriesBinding
import com.developerkim.mytodo.databinding.ListCategoriesFolderBinding
import com.developerkim.mytodo.interfaces.ClickListener


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NotesCategoriesAdapter(private val listener: ClickListener, private val context: Context) :
    ListAdapter<NoteCategory,RecyclerView.ViewHolder>(DiffItemCallback()){
    companion object{
        private const val TYPE_CATEGORY_LIST:Int = 1
        private  const val  TYPE_CATEGORY_FOLDER:Int = 2
        var tracker:SelectionTracker<NoteCategory>? = null
        var isListView:Boolean = true
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isListView) TYPE_CATEGORY_LIST
        else TYPE_CATEGORY_FOLDER
    }

    fun toggleItemViewType(): Boolean {
        isListView = !isListView
        return isListView
    }

    class CategoryViewHolder(val binding: ListCategoriesBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("UseCompatLoadingForDrawables")
        fun binder(
            category: NoteCategory,
            listener: ClickListener,
            context: Context

        ) {
            val noteAdapter = category.notes?.let {
                NoteAdapter(it, listener)
            }

            binding.apply {
                txtCategoryName.text = category.categoryName
                categoryColor.background = when(txtCategoryName.text){
                    "Study"->context.getDrawable(R.drawable.category_color_study)
                    "Daily Tasks"->context.getDrawable(R.drawable.category_color_daily_tasks)
                    "Shopping"->context.getDrawable(R.drawable.category_color_shopping)
                    "Private"->context.getDrawable(R.drawable.category_color_private)
                    else ->context.getDrawable(R.drawable.category_color_uncategorized)
                }
                rvCategoryList.adapter = noteAdapter
            }

            itemView.background = if (!tracker!!.isSelected(category)) {
                 AppCompatResources.getDrawable(context, R.drawable.rounded_corners)
            } else {
                 AppCompatResources.getDrawable(context, R.drawable.rounded_corners_bg_gray)
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<NoteCategory> = object :ItemDetailsLookup.ItemDetails<NoteCategory>(){
            override fun getPosition(): Int = bindingAdapterPosition

            override fun getSelectionKey(): NoteCategory? = (bindingAdapter as NotesCategoriesAdapter).currentList[position]

        }

        companion object {
            fun from(parent: ViewGroup): CategoryViewHolder {
                val binding = ListCategoriesBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return CategoryViewHolder(binding)
            }
        }

    }

    class CategoryFolderHolder(private val binding: ListCategoriesFolderBinding): RecyclerView.ViewHolder(binding.root){

        fun folderBinder(category: NoteCategory, context: Context) {
            binding.apply {
                val folderBackground = root.background as GradientDrawable
                noteCategoryName.text = category.categoryName
                notesCount.text = category.notes?.size.toString()

                folderBackground.color = when (noteCategoryName.text) {
                    "Study" -> ContextCompat.getColorStateList(context, R.color.colorStudy)
                    "Daily Tasks" -> ContextCompat.getColorStateList(context,R.color.colorDailTasks)
                    "Shopping" -> ContextCompat.getColorStateList(context, R.color.colorShopping)
                    "Private" -> ContextCompat.getColorStateList(context, R.color.colorPrivate)
                    else -> ContextCompat.getColorStateList(context, R.color.colorUncategorized)
                }

            }
        }

        companion object{
            fun from(parent: ViewGroup): CategoryFolderHolder {
                val binding =  ListCategoriesFolderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CategoryFolderHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_CATEGORY_LIST -> CategoryViewHolder.from(parent)
            TYPE_CATEGORY_FOLDER -> CategoryFolderHolder.from(parent)
            else -> throw IllegalArgumentException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val category = getItem(position)
        when(holder) {
            is CategoryViewHolder -> {
                holder.binder(category, listener,context)
            }
            is CategoryFolderHolder -> {
               holder.folderBinder(category,context)
            }
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    fun getItemAtPosition(position: Int): NoteCategory? = currentList[position]

    fun getPosition(categoryName: String): Int = currentList.indexOfFirst { it.categoryName == categoryName }

    fun setTracker(tracker: SelectionTracker<NoteCategory>?){
        NotesCategoriesAdapter.tracker = tracker
    }

    class DiffItemCallback :DiffUtil.ItemCallback<NoteCategory>(){
        override fun areItemsTheSame(oldItem: NoteCategory, newItem: NoteCategory): Boolean {
            return oldItem.categoryName == newItem.categoryName
        }

        override fun areContentsTheSame(oldItem: NoteCategory, newItem: NoteCategory): Boolean {
           return oldItem == newItem
        }
    }

}
class CategoryItemLookUp(private val recyclerView: RecyclerView):ItemDetailsLookup<NoteCategory>(){
    override fun getItemDetails(e: MotionEvent): ItemDetails<NoteCategory>? {
        val view = recyclerView.findChildViewUnder(e.x,e.y)
        if (view != null && NotesCategoriesAdapter.isListView ){
            return (recyclerView.getChildViewHolder(view) as NotesCategoriesAdapter.CategoryViewHolder).getItemDetails()
        }
        return null
    }
}

