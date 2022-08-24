package com.developerkim.mytodo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.developerkim.mytodo.databinding.NewCategoryItemBinding

class CreateCategoryAdapter(private val newCategoryListener:NewCategoryListener)
    : RecyclerView.Adapter<CreateCategoryAdapter.CategoryNewFolderHolder>(){

    inner class CategoryNewFolderHolder(
        private val binding: NewCategoryItemBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun newCategoryBinder() {
            binding.apply {
                root.setOnClickListener {
                    newCategoryListener.onNewCategoryClicked()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryNewFolderHolder {
        val bindingNew =  NewCategoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryNewFolderHolder(bindingNew)
    }

    override fun onBindViewHolder(holder: CategoryNewFolderHolder, position: Int) {
        holder.newCategoryBinder()
    }

    override fun getItemCount(): Int {
        return 1
    }

    class NewCategoryListener(val newCategoryListener:()->Unit){
        fun onNewCategoryClicked() = newCategoryListener()
    }
}