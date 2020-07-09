package com.twisthenry8gmail.weeklyphoenix

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SelectableListChipGroup(context: Context, attrs: AttributeSet) : ChipGroup(context, attrs) {

    var selected: String? = null
        set(value) {

            field = value

            for (i in 0 until childCount) {

                val chip = getChildAt(i) as Chip
                if (chip.text == value) {

                    chip.isChecked = true
                }
            }
        }

    init {

        isSingleSelection = true
        isSelectionRequired = true
    }

    fun setList(list: List<String>) {

        removeAllViews()

        list.forEach {

            val chip = Chip(context)
            chip.text = it

            addView(chip)

            if (it == selected) {

                chip.isChecked = true
            }
        }
    }
}