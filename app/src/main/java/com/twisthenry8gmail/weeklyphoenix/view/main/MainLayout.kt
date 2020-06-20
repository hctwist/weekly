package com.twisthenry8gmail.weeklyphoenix.view.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.twisthenry8gmail.weeklyphoenix.R

enum class MainLayout(@DrawableRes val iconRes: Int, @StringRes val titleRes: Int) {

    DEFAULT(R.drawable.round_view_agenda_24, R.string.main_layout_default),
    TASKS(R.drawable.round_view_list_24, R.string.main_layout_tasks),
    GOALS(R.drawable.round_view_carousel_24, R.string.main_layout_goals)
}