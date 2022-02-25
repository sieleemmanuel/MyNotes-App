package com.developerkim.mytodo.util

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode

interface NoteActionModeCallback:ActionMode.Callback{
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean =true

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        TODO("Not yet implemented")
    }
}