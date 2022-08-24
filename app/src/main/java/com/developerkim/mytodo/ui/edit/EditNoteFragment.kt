package com.developerkim.mytodo.ui.edit

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkManager
import com.developerkim.mytodo.R
import com.developerkim.mytodo.ReminderReceiver
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.FragmentEditNoteBinding
import com.developerkim.mytodo.ui.MainActivity
import com.developerkim.mytodo.ui.listnotes.MainViewModel
import com.developerkim.mytodo.ui.addnew.NewNoteFragment
import com.developerkim.mytodo.util.Constants
import com.developerkim.mytodo.util.HideKeyboard.hideKeyboard
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime

@AndroidEntryPoint
class EditNoteFragment : Fragment(),
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentEditNoteBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val args: EditNoteFragmentArgs by navArgs()
    private lateinit var noteToEdit: Note
    private lateinit var updateSelectedCategory: String
    private lateinit var appBarConfiguration: AppBarConfiguration

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditNoteBinding.inflate(inflater)
        appBarConfiguration = AppBarConfiguration(findNavController().graph)
        noteToEdit = args.note

        binding.apply {
            setUpNoteValuesToUpdate()
            setUpToolbar()
            clUpdateNote.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    hideKeyboard(view, requireActivity())
                }
            }
            tilReminderTime.editText?.setOnClickListener {
                showReminderDatePicker()
            }
            btnDoneEditing.setOnClickListener {
                val uNote = Note(
                    noteTitle = updateTitleEditText.text.toString(),
                    noteText = updateTextEditText.text.toString(),
                    reminderTime = edReminderTime.text.toString()
                )
                if (noteToEdit!= uNote) {
                    mainViewModel.editNote(noteToEdit, uNote)
                    if (noteToEdit.reminderTime != uNote.reminderTime) {
                        // replaceReminder(uNote.noteTitle, noteToEdit.noteCategory)
                        NewNoteFragment.setReminderNotification(
                            uNote.noteTitle,
                            uNote.reminderTime,
                            uNote.noteCategory,
                            newNoteFragment = NewNoteFragment(),
                            noteToEdit.reminderCode!!,
                            requireContext()
                        )
                    }
                    findNavController()
                        .navigate(EditNoteFragmentDirections.actionUpdateNoteFragmentToListNotesFragment())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Nothing to Update",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            }
            updateTextEditText.setOnTouchListener { v, event ->
                if (v == updateTextEditText){
                    v.parent.parent.requestDisallowInterceptTouchEvent(true)
                    when(event.action /*|| MotionEvent.ACTION_MASK*/){
                        MotionEvent.ACTION_UP -> v.parent.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }
        }
        return binding.root
    }

    private fun replaceReminder(noteTitle:String, selectCategory:String,) {
        val reminderIntent = Intent(context, ReminderReceiver::class.java)
        reminderIntent.apply {
            putExtra(
                getString(R.string.title_key),
                "Reminder for $noteTitle"
            )
            putExtra(
                getString(R.string.note_title_arg_key),
                noteTitle
            )
            putExtra(
                getString(R.string.note_category_arg_key),
                selectCategory
            )
            putExtra(
                getString(R.string.message_key),
                "It is time to check out $noteTitle note, Don't forget to do so now!"
            )
        }
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                noteToEdit.reminderCode!!,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        updateSelectedCategory = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Toast.makeText(requireContext(), "Please  selected a category", Toast.LENGTH_SHORT).show()
    }

    private fun FragmentEditNoteBinding.setUpNoteValuesToUpdate() {
        updateTitleEditText.setText(noteToEdit.noteTitle)
        updateTextEditText.setText(noteToEdit.noteText)
        edReminderTime.setText(noteToEdit.reminderTime)
    }

    private fun FragmentEditNoteBinding.setUpToolbar() {
        updateNoteToolbar.apply {
            setupWithNavController(findNavController(), appBarConfiguration)
            title = "Edit ${noteToEdit.noteCategory} note"
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.actionClose -> {
                        if (noteToEdit.noteText == updateTextEditText.text.toString() &&
                            noteToEdit.noteTitle == updateTitleEditText.text.toString()
                        ) {
                            findNavController().navigate(
                                EditNoteFragmentDirections.actionUpdateNoteFragmentToListNotesFragment()
                            )
                        } else {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Want to cancel update?")
                                .setCancelable(false)
                                .setPositiveButton(" OK") { dialog, _ ->
                                    dialog.dismiss()
                                    findNavController().popBackStack(R.id.listNotesFragment, true)
                                }
                                .setNegativeButton("CANCEL") { dialog, _ ->
                                    findNavController().popBackStack()
                                    dialog.dismiss()
                                }.show()
                        }
                    }
                }
                false
            }
        }
    }

    private fun showReminderDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val formattedDate = NewNoteFragment.getSelectedDate(selection)
            showTimePicker(formattedDate)
        }
        datePicker.isCancelable = false
        datePicker.show(
            (requireActivity() as MainActivity).supportFragmentManager,
            "DATE_PICKER_TAG"
        )
    }
    @SuppressLint("SetTextI18n")
    private fun showTimePicker(formattedDate: String?) {
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText("Select Reminder Time")
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(LocalTime.now().hour)
            .setMinute(LocalTime.now().minute)
            .build()
        timePicker.show(
            (requireActivity() as MainActivity).supportFragmentManager,
            "TIME_PICKER_TAG"
        )
        timePicker.addOnPositiveButtonClickListener {
            val selectedHour = if (timePicker.hour < 10) {
                "0${timePicker.hour}"
            } else timePicker.hour
            val minutes = if (timePicker.minute < 10) {
                "0${timePicker.minute}"
            } else timePicker.minute
            val reminder = "$formattedDate ${selectedHour}:${minutes}:00"
            binding.edReminderTime.setText(reminder)
        }
    }

}
