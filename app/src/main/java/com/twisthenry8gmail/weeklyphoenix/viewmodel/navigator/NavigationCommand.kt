package com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator

interface NavigationCommand {

    fun navigateWith(navController: NavController)

    fun setNavigatorExtras(extras: FragmentNavigator.Extras) {}

    fun getId(): Int = 0

    class To(
        private val resId: Int,
        private val args: Bundle?,
        private var navigatorExtras: FragmentNavigator.Extras?
    ) :
        NavigationCommand {

        override fun navigateWith(navController: NavController) {

            navController.navigate(resId, args, null, navigatorExtras)
        }

        override fun setNavigatorExtras(extras: FragmentNavigator.Extras) {

            navigatorExtras = extras
        }

        override fun getId(): Int {

            return resId
        }
    }

    class Back :
        NavigationCommand {

        override fun navigateWith(navController: NavController) {

            navController.popBackStack()
        }
    }
}