package com.developerkim.mytodo.ui.listnotes

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.R
import com.developerkim.mytodo.databinding.ListCategoriesBinding
import com.developerkim.mytodo.databinding.ListCategoriesFolderBinding
import com.developerkim.mytodo.databinding.ListItemsBinding
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.util.ClickListener
import com.developerkim.mytodo.util.LongClickListener
import com.developerkim.mytodo.util.RecentNotesListener


class NoteAdapter(notes: MutableList<Note>, private val listener: ClickListener, private val longClickListener: LongClickListener) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private val notesList = notes
    class ViewHolder(private val binding: ListItemsBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ListItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(
            note: Note, listener: ClickListener, position: Int, longClickListener: LongClickListener
        ) {
            binding.txtCategory.text = note.noteCategory
            binding.tvNoteTitle.text = note.noteTitle
            binding.txtNotes.text = note.noteText
            binding.txtDate.text = note.noteDate

            binding.tvNoteTitle.setTextColor(when(binding.txtCategory.text){
                "Study" ->binding.root.context.getColor(R.color.colorStudy)
                "Daily Tasks" ->binding.root.context.getColor(R.color.colorDailTasks)
                "Private" ->binding.root.context.getColor(R.color.colorPrivate)
                "Shopping" -> binding.root.context.getColor(R.color.colorShopping)
                else -> binding.root.context.getColor(R.color.colorUncategorized)
            })
            binding.deleteNote.setOnClickListener {
                listener.onClick(it,note,position)
            }
            binding.root.setOnClickListener {
                listener.onClick(it,note,position)

            }
           binding.root.setOnLongClickListener {
               longClickListener.onLongClick(it,note,position,binding.deleteNote)
           }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int = notesList.size

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]
        holder.bind(note,listener,position,longClickListener)

    }
}


/*Adapter to adapt category notes */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP) // for context to access resource values
class CategoryAdapter(private val listener: ClickListener,private val longClickListener: LongClickListener) :
    ListAdapter<NoteCategory,RecyclerView.ViewHolder>(DiffItemCallback()) {
    companion object{
        private const val TYPE_CATEGORY_LIST:Int = 1
        private  const val  TYPE_CATEGORY_FOLDER:Int = 2

        var isFolderView:Boolean = true
    }
    override fun getItemViewType(position: Int): Int {
        return if (isFolderView) TYPE_CATEGORY_LIST
        else TYPE_CATEGORY_FOLDER
    }
    fun toggleItemViewType(): Boolean {
        isFolderView = !isFolderView
        return isFolderView
    }

    class CategoryViewHolder(val binding: ListCategoriesBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("UseCompatLoadingForDrawables")
        fun binder(
            category: NoteCategory,
            position: Int,
            listener: ClickListener,
            longClickListener: LongClickListener
        ) {
            binding.rvCategoryList.adapter = category.notes?.let {
                NoteAdapter(it, listener, longClickListener)
            }
            binding.txtCategoryName.text = category.categoryName
            binding.root.setOnLongClickListener {
                longClickListener.onCategoryLongClick(it, category, position, binding.btnDelCat)
            }
            binding.btnDelCat.setOnClickListener {
                listener.onClickCategory(it,category)
            }

            val context = binding.root.context //in place  of Resources.getSystem()
            binding.categoryColor.background = when(binding.txtCategoryName.text){
                "Study"->context.getDrawable(R.drawable.category_color_study)
                "Daily Tasks"->context.getDrawable(R.drawable.category_color_daily_tasks)
                "Shopping"->context.getDrawable(R.drawable.category_color_shopping)
                "Private"->context.getDrawable(R.drawable.category_color_private)

                else ->context.getDrawable(R.drawable.category_color_uncategorized)
            }
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
        fun folderBinder(category: NoteCategory) {
            val folderBackground = binding.root.background as GradientDrawable
            binding.noteCategoryName.text = category.categoryName
            binding.notesCount.text = category.notes?.size.toString()

            folderBackground.color = when( binding.noteCategoryName.text){
                "Study"->ContextCompat.getColorStateList(binding.root.context,R.color.colorStudy)
                "Daily Tasks"->ContextCompat.getColorStateList(binding.root.context,R.color.colorDailTasks)
                "Shopping"->ContextCompat.getColorStateList(binding.root.context,R.color.colorShopping)
                "Private"->ContextCompat.getColorStateList(binding.root.context,R.color.colorPrivate)
                else -> ContextCompat.getColorStateList(binding.root.context,R.color.colorUncategorized)
            }
        }

        companion object{
            fun from(parent: ViewGroup):CategoryFolderHolder{
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
                holder.binder(category, position, listener, longClickListener)
            }
            is CategoryFolderHolder -> {
               holder.folderBinder(category)
            }
        }

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

