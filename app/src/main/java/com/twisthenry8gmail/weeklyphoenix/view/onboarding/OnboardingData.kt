package com.twisthenry8gmail.weeklyphoenix.view.onboarding

import androidx.annotation.DrawableRes
import com.twisthenry8gmail.weeklyphoenix.R

enum class OnboardingData(
    val titleRes: Int,
    val subtitleRes: Int,
    val imageRes: Int,
    val skipButtonTextRes: Int?,
    val nextButtonTextRes: Int
) {

    WELCOME(
        R.string.onboarding_welcome_title,
        R.string.onboarding_welcome_subtitle,
        R.drawable.filing_cabinet_01,
        R.string.onboarding_welcome_skip,
        R.string.onboarding_welcome_next
    ),

    GOALS(
        R.string.onboarding_goals_title,
        R.string.onboarding_goals_subtitle,
        R.drawable.mountain_01,
        R.string.onboarding_skip,
        R.string.onboarding_next
    ),

    TASKS(
        R.string.onboarding_tasks_title,
        R.string.onboarding_tasks_subtitle,
        R.drawable.laptop_01,
        R.string.onboarding_skip,
        R.string.onboarding_next
    ),

    GO(
        R.string.onboarding_go_title,
        R.string.onboarding_go_subtitle,
        R.drawable.rocket_01,
        null,
        R.string.onboarding_go_next
    )
}