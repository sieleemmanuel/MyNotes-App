package com.developerkim.mytodo.ui.addnew

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.developerkim.mytodo.databinding.FragmentCreateCategoryBinding

class CreateCategory : DialogFragment() {
    private lateinit var binding: FragmentCreateCategoryBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateCategoryBinding.inflate(inflater, container, false)
        return binding.root

    }
}