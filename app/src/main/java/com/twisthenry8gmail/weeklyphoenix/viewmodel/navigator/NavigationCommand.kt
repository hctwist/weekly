package com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator

interface NavigationCommand {

    fun navigateWith(navController: NavController)

    class To(
        private val resId: Int,
        private val args: Bundle?,
        private val navigatorExtras: FragmentNavigator.Extras?
    ) :
        NavigationCommand {

        override fun navigateWith(navController: NavController) {

            navController.navigate(resId, args, null, navigatorExtras)
        }
    }

    class Back :
        NavigationCommand {

        override fun navigateWith(navController: NavController) {

            navController.popBackStack()
        }
    }
}