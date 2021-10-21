package com.developerkim.mytodo.ui.read

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.developerkim.mytodo.R
import com.developerkim.mytodo.databinding.FragmentReadNoteBinding


class ReadNotesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding:FragmentReadNoteBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_read_note,
            container,
            false
        )

        val arguments = requireArguments()
        val notePosition = arguments.getInt("note_position")
        binding.noteDate.text = arguments.getString("note_date")
        binding.openNoteTittle.text = arguments.getString("note_title")
        binding.openNoteNotes.text = arguments.getString("note_texts")


        binding.btnEdit.setOnClickListener {
            this.findNavController().navigate(
                ReadNotesFragmentDirections.actionReadNotesFragmentToUpdateNoteFragment(
                    notePosition,
                    arguments.getString("note_category")!!,
                    binding.openNoteTittle.text.toString(),
                    binding.openNoteNotes.text.toString(),
                    binding.noteDate.text.toString()
                )
            )
        }
        return binding.root
    }
}
