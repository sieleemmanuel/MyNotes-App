package com.developerkim.mytodo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.MainActivity
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.ListCategoriesBinding
import com.developerkim.mytodo.databinding.ListCategoriesFolderBinding
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.util.ClickListener
import com.developerkim.mytodo.util.LongClickListener
import com.developerkim.mytodo.util.NoteActionModeCallback

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NotesCategoriesAdapter(private val listener: ClickListener, private val longClickListener:LongClickListener, private val context: Context/*, var noteCallback: ActionMode.Callback, private val activity: MainActivity*/) :
    ListAdapter<NoteCategory,RecyclerView.ViewHolder>(DiffItemCallback()){
    companion object{
        private const val TYPE_CATEGORY_LIST:Int = 1
        private  const val  TYPE_CATEGORY_FOLDER:Int = 2
        var tracker:SelectionTracker<NoteCategory>? = null
        var isFolderView:Boolean = true
        /*var noteActionMode:ActionMode? = null
        var noteTracker:SelectionTracker<Note>? = null*/
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isFolderView) TYPE_CATEGORY_LIST
        else TYPE_CATEGORY_FOLDER
    }

    fun toggleItemViewType(): Boolean {
        isFolderView = !isFolderView
        return isFolderView
    }

    class CategoryViewHolder(val binding: ListCategoriesBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("UseCompatLoadingForDrawables")
        fun binder(
            category: NoteCategory,
            listener: ClickListener,
            longClickListener: LongClickListener,
            context: Context
          /*  activity: MainActivity,
            noteCallback:ActionMode.Callback*/

        ) {
            val noteAdapter = category.notes?.let {
                NoteAdapter(it, listener,longClickListener)
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
               /* noteTracker = SelectionTracker.Builder(
                    "selection_note_id",
                    rvCategoryList,
                    NoteKeyProvider(noteAdapter!!),
                    ItemLookUp(rvCategoryList),
                    StorageStrategy.createParcelableStorage(Note::class.java)
                ).withSelectionPredicate(SelectionPredicates.createSelectAnything())
                    .build()

                noteTracker!!.addObserver(object :SelectionTracker.SelectionObserver<Note>(){
                    override fun onSelectionChanged() {
                        super.onSelectionChanged()
                        if (noteActionMode==null){
                            noteActionMode = activity.startSupportActionMode(noteCallback)
                        }

                        val selectedNotes = noteTracker?.selection?.size()!!
                        if (selectedNotes >0) {
                            noteActionMode?.title = "$selectedNotes/${noteAdapter.itemCount}"
                        }

                        else {
                            noteActionMode?.finish()
                        }
                    }
                })
                txtCategoryName.text = category.categoryName
                btnDelCat.setOnClickListener {
                    listener.onClickCategory(it,category)
                }
                noteAdapter.setTracker(noteTracker!!)*/
                categoryColor.background = when(binding.txtCategoryName.text){
                "Study"->context.getDrawable(R.drawable.category_color_study)
                "Daily Tasks"->context.getDrawable(R.drawable.category_color_daily_tasks)
                "Shopping"->context.getDrawable(R.drawable.category_color_shopping)
                "Private"->context.getDrawable(R.drawable.category_color_private)
                else ->context.getDrawable(R.drawable.category_color_uncategorized)
            }
            }

            if (tracker!!.isSelected(category)) {
                itemView.background = AppCompatResources.getDrawable(context, R.drawable.rounded_corners_bg_gray)
                binding.rvCategoryList.background = AppCompatResources.getDrawable(context, R.drawable.rounded_corners_bg_gray)
            }
            else {
                itemView.background = AppCompatResources.getDrawable(context, R.drawable.rounded_corners)
                binding.rvCategoryList.background = AppCompatResources.getDrawable(context, R.drawable.rounded_corners)
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
                    "Daily Tasks" -> ContextCompat.getColorStateList(
                        context,
                        R.color.colorDailTasks
                    )
                    "Shopping" -> ContextCompat.getColorStateList(context, R.color.colorShopping)
                    "Private" -> ContextCompat.getColorStateList(context, R.color.colorPrivate)
                    else -> ContextCompat.getColorStateList(context, R.color.colorUncategorized)
                }
                    root.apply {
                        setOnClickListener {
                            Toast.makeText(context, "Hello World", Toast.LENGTH_SHORT).show()
                        }
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
                holder.binder(category, listener, longClickListener/*,activity, noteCallback*/,context)
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
        if (view != null && !NotesCategoriesAdapter.isFolderView ){
            return (recyclerView.getChildViewHolder(view) as NotesCategoriesAdapter.CategoryViewHolder).getItemDetails()
        }
        return null
    }
}

