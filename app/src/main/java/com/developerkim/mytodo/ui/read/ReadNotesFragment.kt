package com.developerkim.mytodo.ui.read

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.developerkim.mytodo.R
import com.developerkim.mytodo.databinding.FragmentReadNoteBinding

class ReadNotesFragment : Fragment() {
    private lateinit var binding: FragmentReadNoteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_read_note,
            container,
            false
        )

        val arguments = requireArguments()

        binding.apply {
            noteDate.text = arguments.getString("note_date")
            openNoteTittle.text = arguments.getString("note_title")
            openNoteNotes.text = arguments.getString("note_texts")
            openNoteNotes.movementMethod = ScrollingMovementMethod()
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.read_note_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home-> requireActivity().onBackPressed()
            R.id.shareNote ->
                shareNote()
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

    private fun shareNote() {
        val shareNote = "${binding.openNoteTittle.text}\n ${binding.openNoteNotes.text}"
        val shareIntent = Intent()
        shareIntent.apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,shareNote)
            type = "text/plain"
        }
        try {
            startActivity(shareIntent)
        }catch (e:ActivityNotFoundException){
            Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
        }
    }
}
