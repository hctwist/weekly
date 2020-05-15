package com.twisthenry8gmail.weeklyphoenix.viewmodel

import androidx.navigation.NavController

interface NavigationCommand {

    fun navigate(navController: NavController)

    class To(private val resId: Int):
        NavigationCommand {

        override fun navigate(navController: NavController) {

            navController.navigate(resId)
        }
    }

    class Back(): NavigationCommand {

        override fun navigate(navController: NavController) {

            navController.popBackStack()
        }
    }
}