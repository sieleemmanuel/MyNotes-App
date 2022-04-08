package com.developerkim.mytodo.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputEditText

object HideKeyboard {
    fun hideKeyboard(view: View?, activity:Activity) {
        if (view !is TextInputEditText) {
            val inputMethodManager: InputMethodManager? =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            inputMethodManager?.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        }
    }
}