package com.twisthenry8gmail.weeklyphoenix.viewmodel

import androidx.navigation.NavController

interface NavigationCommand {

    fun navigateWith(navController: NavController)

    class To(private val resId: Int):
        NavigationCommand {

        override fun navigateWith(navController: NavController) {

            navController.navigate(resId)
        }
    }

    class Back: NavigationCommand {

        override fun navigateWith(navController: NavController) {

            navController.popBackStack()
        }
    }
}