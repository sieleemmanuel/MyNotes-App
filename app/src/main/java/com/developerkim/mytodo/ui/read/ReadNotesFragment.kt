package com.developerkim.mytodo.ui.read

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgument
import androidx.navigation.fragment.findNavController
import com.developerkim.mytodo.R
import com.developerkim.mytodo.databinding.FragmentReadNoteBinding
import kotlin.properties.Delegates


class ReadNotesFragment : Fragment() {
    private lateinit var binding: FragmentReadNoteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_read_note,
            container,
            false
        )

        setHasOptionsMenu(true)
        val arguments = requireArguments()

        binding.noteDate.text = arguments.getString("note_date")
        binding.openNoteTittle.text = arguments.getString("note_title")
        binding.openNoteNotes.text = arguments.getString("note_texts")

        binding.openNoteNotes.movementMethod = ScrollingMovementMethod()



        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.read_note_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.shareNote ->
                Toast.makeText(requireContext(),"Todo Share Note",Toast.LENGTH_SHORT).show()
            R.id.updateNote ->{
                val arguments = requireArguments()
                val notePosition = arguments.getInt("note_position")
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
            else ->super.onOptionsItemSelected(item)
        }
        return  true
    }
}
