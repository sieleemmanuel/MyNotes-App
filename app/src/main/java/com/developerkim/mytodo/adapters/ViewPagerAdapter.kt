package com.developerkim.mytodo.adapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.developerkim.mytodo.R
import com.developerkim.mytodo.ui.listnotes.AllNotesFragment

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val context: Context
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val args = Bundle()
        return when (position) {
            0 -> {
                args.putString(
                    context.getString(R.string.all_note_args_key),
                    context.getString(R.string.all_note_args_key)
                )
                val allNotes = AllNotesFragment()
                allNotes.arguments = args
                allNotes
            }
            1 -> {
                args.putString(
                    context.getString(R.string.reminders_args_key),
                    context.getString(R.string.reminders_args_key)
                )
                val reminders = AllNotesFragment()
                reminders.arguments = args
                reminders
            }
            else -> {
                args.putString(
                    context.getString(R.string.favorites_args_key),
                    context.getString(R.string.favorites_args_key)
                )
                val favorite = AllNotesFragment()
                favorite.arguments = args
                favorite
            }
        }
    }
}