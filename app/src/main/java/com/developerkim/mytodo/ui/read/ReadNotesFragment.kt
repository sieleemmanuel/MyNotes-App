package com.developerkim.mytodo.ui.read

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.FragmentReadNoteBinding
import com.developerkim.mytodo.ui.listnotes.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Suppress("DEPRECATION")
class ReadNotesFragment : Fragment() {
    private lateinit var binding: FragmentReadNoteBinding
    private val args: ReadNotesFragmentArgs by navArgs()
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var selectedNote: Note
    private var isFavorite:Boolean=false
    private lateinit var globalMenu: Menu
    private val TAG = ReadNotesFragment::class.simpleName
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReadNoteBinding.inflate(inflater)
        appBarConfiguration = AppBarConfiguration(findNavController().graph)
        selectedNote = args.note
        isFavorite = selectedNote.isFavorite
        globalMenu = binding.readToolbar.menu
        binding.apply {
            setUpNoteValues()
            setUpToolbar()
        }
        return binding.root
    }

    private fun FragmentReadNoteBinding.setUpNoteValues() {
        noteDate.text = selectedNote.noteDate
        openNoteTittle.text = selectedNote.noteTitle
        openNoteNotes.text = selectedNote.noteText
        tvReminderTime.text = selectedNote.reminderTime
        openNoteNotes.movementMethod = ScrollingMovementMethod()
    }

    private fun FragmentReadNoteBinding.setUpToolbar() {
        readToolbar.apply {
            title = selectedNote.noteTitle
            updateFavoriteIcon()
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        requireActivity().onBackPressed()
                        Log.d(TAG, "onOptionsItemSelected: homeClicked")
                        true
                    }
                    R.id.shareNote -> {
                        Log.d(TAG, "onOptionsItemSelected: ShareClicked")
                        shareNote()
                        true
                    }
                    R.id.updateNote -> {
                        Log.d(TAG, "onOptionsItemSelected: Update")
                        Toast.makeText(requireContext(), "updateClicked", Toast.LENGTH_SHORT).show()
                        val notePosition = args.notePosition
                        this.findNavController().navigate(
                            ReadNotesFragmentDirections.actionReadNotesFragmentToUpdateNoteFragment(
                                selectedNote, notePosition
                            )
                        )
                        true
                    }
                    R.id.actionAddToFavorite -> {
                        invalidateMenu()
                        if (isFavorite) {
                            isFavorite = false
                            viewModel.setNoteFavorite(selectedNote)
                        } else {
                            isFavorite = true
                            viewModel.setNoteFavorite(selectedNote)
                        }
                        updateFavoriteIcon()
                        true
                    }
                    R.id.actionDelete ->{
                        viewModel.deleteNote(selectedNote)
                        findNavController().popBackStack()
                        true
                    }

                    else -> false
                }
            }
            setupWithNavController(NavHostFragment.findNavController(this@ReadNotesFragment))
        }
    }

    private fun updateFavoriteIcon() {
        val favoriteItem = globalMenu.findItem(R.id.actionAddToFavorite)
        if (selectedNote.isFavorite) {
            favoriteItem.setIcon(R.drawable.ic_is_favorite)
        } else {
            favoriteItem.setIcon(R.drawable.ic_favorite)
        }
    }

    private fun shareNote() {
        val shareNote = "${binding.openNoteTittle.text}\n ${binding.openNoteNotes.text}"
        val shareIntent = Intent()
        shareIntent.apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareNote)
            type = "text/plain"
        }
        try {
            startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
    }
}
