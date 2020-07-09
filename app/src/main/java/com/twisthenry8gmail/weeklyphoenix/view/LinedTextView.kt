package com.twisthenry8gmail.weeklyphoenix.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.twisthenry8gmail.weeklyphoenix.R

class LinedTextView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    var lines = listOf<String>()
        set(value) {

            field = value
            invalidateLines()
        }

    private val appearanceRes: Int

    init {

        orientation = VERTICAL

        context.obtainStyledAttributes(attrs, R.styleable.LinedTextView).run {

            appearanceRes = getResourceId(R.styleable.LinedTextView_lineTextAppearance, 0)
            recycle()
        }
    }

    private fun invalidateLines() {

        for (i in 0 until (lines.size - childCount)) {

            addView(makeTextView())
        }

        for (i in lines.indices) {

            (getChildAt(i) as TextView).text = lines[i]
        }

        for (i in lines.size until childCount) {

            getChildAt(i).visibility = View.GONE
        }
    }

    private fun makeTextView(): TextView {

        return TextView(context).apply {

            setLines(1)
            ellipsize = TextUtils.TruncateAt.END
            setTextAppearance(appearanceRes)
        }
    }
}