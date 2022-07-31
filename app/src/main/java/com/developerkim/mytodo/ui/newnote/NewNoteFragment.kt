package com.developerkim.mytodo.ui.newnote

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.databinding.FragmentNewNoteBinding
import com.developerkim.mytodo.ui.MainActivity
import com.developerkim.mytodo.ui.listnotes.MainViewModel
import com.developerkim.mytodo.util.HideKeyboard.hideKeyboard
import com.developerkim.mytodo.util.NoteReminderWorker
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class NewNoteFragment : Fragment(), AdapterView.OnItemClickListener {
    private lateinit var selCategory: String
    private lateinit var binding: FragmentNewNoteBinding
    private val sharedViewModel: MainViewModel by activityViewModels()
    private val TAG = NewNoteFragment::class.simpleName
    private var categoryColor: Int? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewNoteBinding.inflate(inflater)
        sharedViewModel.categoriesNames.observe(viewLifecycleOwner) { categories->
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
                root.setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        hideKeyboard(view, requireActivity())
                    }
                }
                edtReminder.setOnClickListener {
                    showReminderDatePicker()
                }
                /*colorSlider.setListener { _, color ->
                sharedViewModel.getPickedColor(color)
                categoryColor = color
                Log.d(TAG, "string color:${color} ")
            }*/
                setUpToolbar()
            }
        }
        return binding.root
    }

    override fun onItemClick(parent: AdapterView<*>?, p1: View?, position: Int, id: Long) {
        selCategory = parent?.getItemAtPosition(position).toString()
    }

    private fun FragmentNewNoteBinding.setUpToolbar() {
        newNoteToolbar.setupWithNavController(findNavController())
        newNoteToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionDone -> {
                    sharedViewModel.pickedColor.observe(viewLifecycleOwner) { pickedColor: Int ->
                        sharedViewModel.reminderTime.observe(viewLifecycleOwner) { reminder ->
                            selCategory = selectCategory.text.toString()
                            val newNote = Note(
                                selCategory,
                                noteTitleEditText.text.toString(),
                                noteTextEditText.text.toString(),
                                sharedViewModel.noteDate,
                                reminderTime = reminder
                            )
                            val noteList = sharedViewModel.createNoteList(newNote)

                            val categories = NoteCategory(
                                newNote.noteCategory,
                                noteList,
                                pickedColor
                            )
                            sharedViewModel.insertCategoryAndNotes(newNote, categories)
                            sharedViewModel.getCategories()
                            setNoteReminder(newNote.reminderTime,newNote.noteTitle)
                            findNavController().popBackStack()
                        }
                    }

                    sharedViewModel.reminderTime()
                    true

                }
                R.id.actionClose -> {
                    findNavController().popBackStack()
                }
                /*R.id.actionAddReminder -> {
                    showReminderDatePicker()
                    true
                }*/
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
            sharedViewModel.reminderTime(reminder)
        }
    }

    private fun getSelectedDate(selection: Long): String? {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val timeZone = TimeZone.getDefault()
        val offset = timeZone.getOffset(Date().time) * -1
        val selectedDate = Date(selection + offset)
        return formatter.format(selectedDate)
    }

    private fun createWorkerRequest(message: String, noteTitle:String,timeDelayInSec: Long) {
        val reminderWorkRequest = OneTimeWorkRequestBuilder<NoteReminderWorker>()
            .setInitialDelay(timeDelayInSec, TimeUnit.SECONDS)
            .setInputData(
                workDataOf(
                    "title" to "Note Reminder",
                    "message" to message,
                    getString(R.string.note_title_arg_key) to noteTitle
                )
            )
            .build()
        WorkManager.getInstance(requireContext()).enqueue(reminderWorkRequest)
    }

    private fun getMilliFromDate(reminderDate: String, reminderTime: String): Long {
        val timeFormatter = LocalTime.parse(reminderTime)
        val timeMilliSec = timeFormatter.toSecondOfDay() * 1000L
        val dateFormatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return dateFormatter?.parse(reminderDate).time.plus(timeMilliSec)
    }

    private fun setNoteReminder(reminderTime: String, noteTitle: String) {
        val selectedDate = reminderTime.substringBefore(" ")
        val selectedTime = reminderTime.substringAfter(" ")
        Log.d(TAG, "Date and : $selectedDate Time:$selectedTime")
        val selectedTimeInSec = getMilliFromDate(selectedDate, selectedTime)
        val todayTimeInSec = Calendar.getInstance().timeInMillis
        val delayInSeconds = (selectedTimeInSec.minus(todayTimeInSec)).div(1000L)

        createWorkerRequest(
            "Hi, it's time to check this $noteTitle note!!",
            noteTitle,
            delayInSeconds
        )
    }
}