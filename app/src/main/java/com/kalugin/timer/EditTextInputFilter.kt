package com.kalugin.timer

import android.text.InputFilter
import android.text.Spanned

class EditTextInputFilter(private val predicate: (String) -> Boolean) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        val newValue = dest?.subSequence(0, dstart).toString() +
                source?.subSequence(start, end).toString() +
                dest?.subSequence(dend, dest.length).toString()

        return if (predicate(newValue)) source ?: "" else ""
    }
}