package com.developerkim.mytodo.interfaces

import androidx.appcompat.widget.SearchView

interface NoteSearchListener {
    fun searchNote(query: String?, searchView: SearchView)
}