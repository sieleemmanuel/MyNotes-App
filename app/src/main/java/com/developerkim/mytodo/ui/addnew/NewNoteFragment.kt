package com.developerkim.mytodo.ui.addnew

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.developerkim.mytodo.R
import com.developerkim.mytodo.ReminderReceiver
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.databinding.FragmentNewNoteBinding
import com.developerkim.mytodo.ui.MainActivity
import com.developerkim.mytodo.ui.listnotes.ListNotesFragment
import com.developerkim.mytodo.ui.listnotes.MainViewModel
import com.developerkim.mytodo.util.Constants
import com.developerkim.mytodo.util.HideKeyboard.hideKeyboard
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

@AndroidEntryPoint
class NewNoteFragment : Fragment(), AdapterView.OnItemClickListener {
    private lateinit var selCategory: String
    private lateinit var binding: FragmentNewNoteBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val args: NewNoteFragmentArgs by navArgs()
    private var passedCategory: String? = null
    private  var reminderCode:Int? = null

    @SuppressLint("ClickableViewAccessibility")
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
                if (passedCategory != null) {
                    selectCategory.setText(passedCategory, false)
                }
                noteTextEditText.movementMethod = ScrollingMovementMethod()

                btnAddNewNote.setOnClickListener {
                    selCategory = selectCategory.text.toString()
                    reminderCode = Constants.RECEIVER_CODE
                    mainViewModel.noteCategory.observe(viewLifecycleOwner) { noteCategory ->
                        Log.d(TAG, "onCreateView: $noteCategory")
                        if (edtReminder.text.toString() == getString(R.string.set_reminder_label)) {
                            edtReminder.setText(getString(R.string.no_reminder_set_label))
                        }
                        val newNote = Note(
                            noteCategory = selCategory,
                            noteTitle = noteTitleEditText.text.toString(),
                            noteText = noteTextEditText.text.toString(),
                            noteDate = mainViewModel.noteDate,
                            reminderTime = edtReminder.text.toString(),
                            noteColor = noteCategory.categoryColor,
                            reminderCode = reminderCode

                        )
                        if (edtReminder.text.toString() != getString(R.string.set_reminder_label) ||
                            edtReminder.text.toString() != getString(R.string.no_reminder_set_label)
                        ) {
                            setReminderNotification(
                                noteTitleEditText.text.toString(),
                                edtReminder.text.toString(),
                                selectCategory.text.toString(),
                                this@NewNoteFragment,
                                reminderCode!!,
                                requireContext())

                            findNavController().popBackStack()
                        }
                        mainViewModel.insertNewNotes(note = newNote)
                        mainViewModel.getCategories()
                    }

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
                noteTextEditText.setOnTouchListener { v, event ->
                    if (v == noteTextEditText) {
                        v.parent.parent.requestDisallowInterceptTouchEvent(true)
                        when (event.action) {
                            MotionEvent.ACTION_UP -> v.parent.parent.requestDisallowInterceptTouchEvent(
                                false
                            )
                        }
                    }
                    false
                }
            }
        }
        return binding.root
    }

    override fun onItemClick(parent: AdapterView<*>?, p1: View?, position: Int, id: Long) {
        if (position == 0) {
            ListNotesFragment.showNewCategoryDialog(
                mainViewModel,
                viewLifecycleOwner,
                binding.selectCategory,
                requireContext()
            )
        } else {
            selCategory = parent?.getItemAtPosition(position).toString()
            mainViewModel.getCategory(selCategory)
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
        }
    }

    private fun getMilliFromDate(
        reminderDate: String,
        reminderTime: String?,
        context: Context
    ): Long?{
        return if (isValidDate(reminderDate)) {
            val timeFormatter = LocalTime.parse(reminderTime)
            val timeMilliSec = timeFormatter.toSecondOfDay() * 1000L
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val millis = dateFormatter.parse(reminderDate)!!.time.plus(timeMilliSec)
            Log.d(TAG, "getMilliFromDate: $millis")
            millis
        } else {
            Toast.makeText(context, "No reminder set!", Toast.LENGTH_SHORT).show()
           null
        }
    }

    fun getReminderMillis(
        reminderTime: String?,
        context: Context
    ): Long? {
        val selectedDate = reminderTime?.substringBefore(" ")
        val selectedTime = reminderTime?.substringAfter(" ")
        return getMilliFromDate(selectedDate!!, selectedTime, context)!!
    }

    private fun isValidDate(isDate: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false
        try {
            dateFormat.parse(isDate.trim { it <= ' ' })
        } catch (pe: ParseException) {
            return false
        }
        return true
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

        @SuppressLint("UnspecifiedImmutableFlag")
        fun setReminderNotification(
            noteTitle:String,
            edtReminder:String,
            selectCategory:String,
            newNoteFragment:NewNoteFragment,
            receiverCode:Int,
            context: Context
            ) {
            val reminderIntent = Intent(context, ReminderReceiver::class.java)
            reminderIntent.apply {
                putExtra(
                    context.getString(R.string.title_key),
                    "Reminder for $noteTitle"
                )
                putExtra(
                    context.getString(R.string.note_title_arg_key),
                    noteTitle
                )
                putExtra(
                    context.getString(R.string.note_category_arg_key),
                    selectCategory
                )
                putExtra(
                    context.getString(R.string.message_key),
                    "It is time to check out $noteTitle note, Don't forget to do so now!"
                )
            }
            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    receiverCode,
                    reminderIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            val alarmManager =context.getSystemService(ALARM_SERVICE) as AlarmManager
            val reminderMillis =
               newNoteFragment.getReminderMillis(edtReminder, context)!!
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                reminderMillis,
                pendingIntent
            )
        }
    }
}