package com.developerkim.mytodo.ui.newnote

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.AddCategoryDialogBinding
import com.developerkim.mytodo.databinding.FragmentNewNoteBinding
import com.developerkim.mytodo.ui.MainActivity
import com.developerkim.mytodo.ui.listnotes.MainViewModel
import com.developerkim.mytodo.util.HideKeyboard.hideKeyboard
import com.developerkim.mytodo.util.NoteReminderWorker
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

@AndroidEntryPoint
class NewNoteFragment : Fragment(), AdapterView.OnItemClickListener {
    private lateinit var selCategory: String
    private lateinit var binding: FragmentNewNoteBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val args:NewNoteFragmentArgs by navArgs()
    private var passedCategory:String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewNoteBinding.inflate(inflater)
        passedCategory = args.categoryName
        mainViewModel.categoriesNames.observe(viewLifecycleOwner) { categories ->
            val noteCategoriesAdapter = ArrayAdapter(
                requireContext(),
                R.layout.category_list_layout,
                categories as MutableList<String>
            )
            binding.apply {
                selectCategory.apply {
                    setAdapter(noteCategoriesAdapter)
                    onItemClickListener = this@NewNoteFragment
                    setOnFocusChangeListener { view, hasFocus ->
                        if (hasFocus) {
                            hideKeyboard(view, requireActivity())
                        }
                    }

                }
                if (passedCategory!=null){
                    selectCategory.setText(passedCategory, false)
                }

                btnAddNewNote.setOnClickListener {
                    selCategory = selectCategory.text.toString()
                    val newNote = Note(
                        noteCategory = selCategory,
                        noteTitle = noteTitleEditText.text.toString(),
                        noteText = noteTextEditText.text.toString(),
                        noteDate = mainViewModel.noteDate,
                        reminderTime = edtReminder.text.toString()
                    )

                    mainViewModel.insertNewNotes(note = newNote)
                    mainViewModel.getCategories()

                    if (edtReminder.text.toString() != getString(R.string.set_reminder_label) ||
                        edtReminder.text.toString() != "None"
                    ) {
                        Log.d(TAG, "reminderTime: ${edtReminder.text} ")
                        Log.d(TAG, "NewNote: $newNote")
                        setNoteReminder(
                            newNote.reminderTime,
                            newNote.noteTitle,
                            newNote.noteCategory,
                            requireContext()
                        )
                        findNavController().popBackStack()
                    }

                    mainViewModel.reminderTime()
                }
                root.setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        hideKeyboard(view, requireActivity())
                    }
                }
                tilReminder.editText?.setOnClickListener {
                    showReminderDatePicker()
                }
                setUpToolbar()
            }
        }
        return binding.root
    }

    override fun onItemClick(parent: AdapterView<*>?, p1: View?, position: Int, id: Long) {
        if (position == 0) {
            showNewCategoryDialog()
        } else {
            selCategory = parent?.getItemAtPosition(position).toString()
        }
    }

    private fun FragmentNewNoteBinding.setUpToolbar() {
        newNoteToolbar.setupWithNavController(findNavController())
        newNoteToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionClose -> {
                    findNavController().popBackStack()
                }
                else -> false
            }
        }
    }

    private fun showReminderDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val formattedDate = getSelectedDate(selection)
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
            binding.edtReminder.setText(reminder)
            mainViewModel.reminderTime(reminder)
        }
    }

    private fun showNewCategoryDialog() {
        val addCategoryDialogBinding = AddCategoryDialogBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        )
        val categoryName = addCategoryDialogBinding.tiNewCategoryName.text
        addCategoryDialogBinding.csCategoryColor.setListener { _, color ->
            mainViewModel.getPickedColor(color)
        }
        val categoryDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(addCategoryDialogBinding.root)
            .setMessage("Create Note Category")
            .setCancelable(false)
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("ADD") { _, _ ->
            }
            .show()
        categoryDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (categoryName!!.isNotEmpty() && addCategoryDialogBinding.csCategoryColor.selectedColor != 1) {
                mainViewModel.pickedColor.observe(viewLifecycleOwner) { color ->
                    val newCategory = NoteCategory(
                        categoryName = categoryName.toString(),
                        categoryColor = color,
                        notes = mutableListOf()
                    )
                    mainViewModel.insertNewCategory(newCategory)
                    binding.selectCategory.setText(categoryName,false)
                    categoryDialog.dismiss()
                }
            } else {
                addCategoryDialogBinding.tiNewCategoryName.error =
                    getString(R.string.error_category_empty)
            }
        }
    }

    companion object {
        private val TAG = NewNoteFragment::class.simpleName
        fun getSelectedDate(selection: Long): String? {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val timeZone = TimeZone.getDefault()
            val offset = timeZone.getOffset(Date().time) * -1
            val selectedDate = Date(selection + offset)
            return formatter.format(selectedDate)
        }

        private fun getMilliFromDate(reminderDate: String, reminderTime: String, context: Context): Long? {
            return   if (reminderDate!="None" || reminderDate!="Set Reminder") {
                val timeFormatter = LocalTime.parse(reminderTime)
                val timeMilliSec = timeFormatter.toSecondOfDay() * 1000L
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                dateFormatter.parse(reminderDate).time.plus(timeMilliSec)
            }else{
                Toast.makeText(context, "No reminder set!", Toast.LENGTH_SHORT).show()
                null
            }

        }

        private fun createWorkerRequest(
            message: String,
            noteTitle: String,
            noteCategory: String,
            timeDelayInSec: Long,
            context: Context
        ) {
            val reminderWorkRequest = OneTimeWorkRequestBuilder<NoteReminderWorker>()
                .setInitialDelay(timeDelayInSec, TimeUnit.SECONDS)
                .addTag(noteTitle)
                .setInputData(
                    workDataOf(
                        context.getString(R.string.title_key) to "Note Reminder",
                        context.getString(R.string.message_key) to message,
                        context.getString(R.string.note_title_arg_key) to noteTitle,
                        context.getString(R.string.note_category_arg_key) to noteCategory
                    )
                )
                .build()
            WorkManager.getInstance(context).enqueue(reminderWorkRequest)
        }

        fun setNoteReminder(reminderTime: String, noteTitle: String, noteCategory: String, context: Context) {
            val selectedDate = reminderTime.substringBefore(" ")
            val selectedTime = reminderTime.substringAfter(" ")
            Log.d(TAG, "Date and : $selectedDate Time:$selectedTime")
            val selectedTimeInSec = getMilliFromDate(selectedDate, selectedTime, context)
            val todayTimeInSec = Calendar.getInstance().timeInMillis
            val delayInSeconds = (selectedTimeInSec?.minus(todayTimeInSec))?.div(1000L)
            Log.d(TAG, "DelaySeconds: $delayInSeconds")
            createWorkerRequest(
                "Hi, it's time to check this $noteTitle note!!",
                noteTitle,
                noteCategory,
                delayInSeconds!!,
                context
            )
            Toast.makeText(context, "Setting reminder", Toast.LENGTH_SHORT).show()
        }
    }
}