package com.developerkim.mytodo.ui.update

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.developerkim.mytodo.R
import com.developerkim.mytodo.database.NoteDatabase
import com.developerkim.mytodo.databinding.FragmentUpdateNoteBinding
import com.developerkim.mytodo.model.Note
import kotlinx.android.synthetic.main.fragment_new_note.*



class UpdateNoteFragment : Fragment(),
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentUpdateNoteBinding
    private lateinit var viewModel: UpdateViewModel
    lateinit var updateSelectedCategory: String
    private lateinit var adapter: ArrayAdapter<String>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_update_note,
            container,
            false
        )
        val database = NoteDatabase.getInstance(requireContext()).notesCategoriesDao
        val viewModelFactory = UpdateViewModelFactory(database, requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(UpdateViewModel::class.java)


        adapter = ArrayAdapter(requireContext(), R.layout.categories_menu_item, viewModel.categoryItems)
        binding.updateNoteCategory.setAdapter(adapter)
        binding.updateNoteCategory.apply {
            onItemSelectedListener = this@UpdateNoteFragment
        }

        binding.btnUpdate.setOnClickListener {
            val notePosition = requireArguments().getInt("note_position")
            val uNote = Note(
                binding.updateNoteCategory.text.toString(),
                binding.updateTitleEditText.text.toString(),
                binding.updateTextEditText.text.toString(),
                viewModel.updatedNoteDate
            )
            Toast.makeText(requireContext(),notePosition.toString(),Toast.LENGTH_SHORT).show()
            viewModel.updateNote(uNote,notePosition)
            this.findNavController().navigate(UpdateNoteFragmentDirections.actionUpdateNoteFragmentToListNotesFragment())


        }
        binding.btnClose.setOnClickListener {
            this.findNavController().navigate(
                UpdateNoteFragmentDirections.actionUpdateNoteFragmentToListNotesFragment())
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setValuesToUpdate()

    }

    //method call to set fields with values to be updated
    private fun setValuesToUpdate() {
        val argument = requireArguments()
        binding.updateTitleEditText.setText(argument.getString("update_title"))
        binding.updateTextEditText.setText(argument.getString("update_text"))
        binding.updateNoteCategory.setText(argument.getString("update_category"),false)
    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        updateSelectedCategory = parent?.getItemAtPosition(position).toString()
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {
       Toast.makeText(requireContext(),"no category selected",Toast.LENGTH_SHORT).show()
    }


}
